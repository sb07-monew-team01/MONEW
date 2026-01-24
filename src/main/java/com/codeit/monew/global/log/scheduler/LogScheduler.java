package com.codeit.monew.global.log.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogScheduler {

    private final S3Client s3;

    @Value("${app.log-upload.enabled:true}")
    private boolean enabled;

    @Value("${aws.s3.bucket.name}")
    private String bucket;

    @Value("${app.log-upload.prefix:monew/prod}")
    private String prefix;

    @Value("${name.aws-region:Asia/Seoul}")
    private String zone;

    // logback 설정 그대로 따라가도록 디폴트
    @Value("${app.log-upload.log-dir:.logs}")
    private String logDir;

    @Value("${app.log-upload.log-name:applicationLog}")
    private String logName;

    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("yyyy-MM-dd");

   //@Scheduled(cron = "0 */2 * * * *", zone = "Asia/Seoul")
    @Scheduled(cron = "0 5 0 * * *", zone = "Asia/Seoul")
    public void uploadYesterday() {
        //업로드 키고 끄기
        if (!enabled) return;

        // 지역 , -1일 세팅
        ZoneId zid = ZoneId.of(zone);
        String day = LocalDate.now(zid).minusDays(1).format(DAY);

        //.log없으면 종료
        Path dir = Paths.get(logDir);
        if (!Files.isDirectory(dir)) {
            log.info("[LOG] Log dir not found. dir={}", dir.toAbsolutePath());
            return;
        }

        // 어제 날짜 로그 파일 모두 찾기
        // - applicationLog.2026-01-19.log
        // - applicationLog.2026-01-19.0.log, .1.log ...
        List<Path> targets = findYesterdayLogs(dir, day);

        if (targets.isEmpty()) {
            log.info("[LOG] No log files to upload. day={}, dir={}", day, dir.toAbsolutePath());
            return;
        }
        String key = "";
        for (Path src : targets) {
            try {
                Path gz = gzip(src);

                // S3 key 예:
                // monew/prod/applicationLog/2026-01-19/applicationLog.2026-01-19.1.log.gz
                key = String.format("%s/%s/%s/%s.gz",
                        prefix, logName, day, src.getFileName().toString());

                PutObjectRequest req = PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType("text/plain")
                        .contentEncoding("gzip")
                        .build();

                s3.putObject(req, RequestBody.fromFile(gz));
                log.info("Uploaded log: s3://{}/{} (src={})", bucket, key, src.getFileName());
                //성공하면 만든gz삭제
                Files.deleteIfExists(gz);

            } catch (S3Exception e) {
                // S3가 응답을 줬는데 거절/실패 (권한, 키, 버킷정책, KMS 등)
                log.error("[S3 FAIL] status={}, code={}, bucket={}, key={}, src={}",
                        e.statusCode(),
                        e.awsErrorDetails() != null ? e.awsErrorDetails().errorCode() : "unknown",
                        bucket, key, src.toAbsolutePath(), e);

            } catch (SdkClientException e) {
                //자격증명 로딩 실패 / 네트워크 / DNS 등 (요청 자체가 어려움)
                log.error("[AWS SDK FAIL] bucket={}, key={}, src={}, msg={}",
                        bucket, key, src.toAbsolutePath(), e.getMessage(), e);

            } catch (NoSuchFileException | AccessDeniedException e) {
                //로컬 파일 문제 (없음, 권한 없음)
                log.error("[FILESYSTEM FAIL] src={}, msg={}",
                        src.toAbsolutePath(), e.getMessage(), e);

            } catch (Exception e) {
                log.error("[LOG UPLOAD TO s3 UNKNOWN FAIL] day={}, src={}", day, src.toAbsolutePath(), e);
            }
        }
    }

    private List<Path> findYesterdayLogs(Path dir, String day) {
        // 예: applicationLog.2026-01-20.0.log, applicationLog.2026-01-20.1.log ...
        String prefix = logName + "." + day + ".";
        String suffix = ".log";

        try (Stream<Path> stream = Files.list(dir)) {
            return stream
                    .filter(Files::isRegularFile)//진짜 파일만 받음
                    .filter(p -> {
                        String name = p.getFileName().toString();
                        if (!name.startsWith(prefix) || !name.endsWith(suffix)) return false;

                        // prefix 뒤(인덱스)만 뽑아서 숫자인지 확인
                        String idx = name.substring(prefix.length(), name.length() - suffix.length());
                        return idx.matches("\\d+");
                    })
                    // .0 -> .1 -> .2 순 정렬
                    .sorted(Comparator.comparingInt(p -> {
                        String name = p.getFileName().toString();
                        String idx = name.substring(prefix.length(), name.length() - suffix.length());
                        return Integer.parseInt(idx);
                    }))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Failed to list log dir. dir={}", dir.toAbsolutePath(), e);
            return List.of();
        }
    }

    private Path gzip(Path src) throws IOException {
        Path gz = Paths.get(src.toString() + ".gz");

        try (InputStream in = Files.newInputStream(src);
             OutputStream out = Files.newOutputStream(gz, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
             GZIPOutputStream gout = new GZIPOutputStream(out, 8192)) {

            in.transferTo(gout);
        }
        return gz;
    }
}
