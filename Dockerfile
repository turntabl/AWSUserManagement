FROM adoptopenjdk:11-jre-hotspot

COPY build/libs/AWSAccountManagement-0.1.jar AWSAccountManagement.jar
COPY src/main/resources/credentials credentials/aws
RUN chmod 777 AWSAccountManagement.jar
ENTRYPOINT ["java","-jar", "AWSAccountManagement.jar"]