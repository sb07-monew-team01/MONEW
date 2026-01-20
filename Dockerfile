# 1번째 스테이지 : 빌드 영역
FROM eclipse-temurin:17-jdk-alpine AS sprint-build
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

# 2번째 스테이지 : 실행 영역
FROM eclipse-temurin:17-jre-alpine AS sprint-exec
COPY --from=sprint-build /app/build/libs/*.jar ./app.jar
ENV TZ=Asia/Seoul
RUN apk add --no-cache curl tzdata
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]