### Docker 이미지를 생성할 때 기반이 되는 베이스 이미지를 설정.
FROM openjdk:11-jre-slim
### Dockerfile 내에서 사용할 변수 JAR_FILE을 정의.
ARG JAR_FILE=./*.jar
### JAR_FILE 경로에 해당하는 파일을 Docker 이미지 내부로 복사.
COPY ${JAR_FILE} spring-docker.jar
### Docker 컨테이너가 시작될 때 실행할 명령을 지정.
CMD ["java","-jar", "-Dspring.profiles.active=${active}","/spring-docker.jar"]