FROM adoptopenjdk:11-jre-hotspot

RUN chmod 777 AWSAccountManagement.jar
COPY build/libs/AWSAccountManagement-0.1.jar AWSAccountManagement.jar
ENTRYPOINT ["java","-jar", "AWSAccountManagement.jar"]