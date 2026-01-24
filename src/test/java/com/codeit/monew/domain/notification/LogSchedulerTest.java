package com.codeit.monew.domain.notification;

import com.codeit.monew.global.log.scheduler.LogScheduler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogSchedulerTest {

    @Mock
    S3Client s3;

    @Captor
    ArgumentCaptor<PutObjectRequest> reqCaptor;

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("upload_성공하면_putObject호출되고_gz삭제된다")
    void upload_succes_putObject_dlete() throws Exception {
        // given: 임시 로그 파일 생성
        //이렇게 먼저해야  테스트 끝나고 밑에 놈들이 알아서지워진다
        Path logDir = tempDir.resolve(".logs");
        Files.createDirectories(logDir);

        // "어제" 로그라고 가정할 파일
        String day = LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1).toString();
        String fileName = "applicationLog." + day + ".1.log";
        Path logFile = logDir.resolve(fileName);
        Files.writeString(logFile, "hello log\n");

        // S3 putObject는 성공한다고 가정
        when(s3.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(null);

        // 테스트 대상 생성 (네 실제 LogScheduler가 @Value 쓰면, 테스트에서 직접 필드 세팅 필요)
        LogScheduler scheduler = new LogScheduler(s3);

        // 강제필드값부여
        setField(scheduler, "enabled", true);
        setField(scheduler, "bucket", "test-bucket");
        setField(scheduler, "prefix", "monew/prod");
        setField(scheduler, "zone", "Asia/Seoul");
        setField(scheduler, "logDir", logDir.toString());
        setField(scheduler, "logName", "applicationLog");


        // when
        scheduler.uploadYesterday();

        // then
        verify(s3, times(1)).putObject(reqCaptor.capture(), any(RequestBody.class));

        //s3에 들어갈 값들이 적절하다
        PutObjectRequest req = reqCaptor.getValue();
        assertThat(req.bucket()).isEqualTo("test-bucket");
        assertThat(req.contentType()).isEqualTo("text/plain");
        assertThat(req.contentEncoding()).isEqualTo("gzip");

        // key  prefix/logName/day/filename.gz 형식의 키인가
        assertThat(req.key()).isEqualTo(
                "monew/prod/applicationLog/" + day + "/" + fileName + ".gz"
        );

        // gz 파일은 삭제되어야 함 (원본은 남아야 함)
        Path gz = logDir.resolve(fileName + ".gz");
        assertThat(Files.exists(logFile)).isTrue();
        assertThat(Files.exists(gz)).isFalse();
    }

    // 테스트 편의용
    static void setField(Object target, String fieldName, Object value) {
        try {
            var f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
