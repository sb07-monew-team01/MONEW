# 1번째 스테이지 : 빌드 영역
FROM eclipse-temurin:17-jdk-alpine AS monew-build
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

# 2번째 스테이지 : 실행 영역
FROM eclipse-temurin:17-jre-alpine AS monew-exec
COPY --from=monew-build /app/build/libs/monew-0.0.1-SNAPSHOT.jar app.jar
ENV TZ=Asia/Seoul
RUN apk add --no-cache curl tzdata

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]