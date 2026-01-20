# 빌드 스테이지
FROM eclipse-temurin:17-jdk-alpine AS sprint-build
WORKDIR /app

COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test


# 실행 스테이지
FROM eclipse-temurin:17-jre-alpine AS sprint-exec
WORKDIR /app

# libs 전체 복사
COPY --from=sprint-build /app/build/libs/ ./libs/

ENV TZ=Asia/Seoul
RUN apk add --no-cache curl tzdata

EXPOSE 8080

# 정확한 실행 대상
ENTRYPOINT ["java", "-jar", "libs/app.jar"]