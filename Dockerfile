FROM adoptopenjdk:11-jre-hotspot

COPY build/libs/AWSAccountManagement-0.1.jar AWSAccountManagement.jar
COPY src/main/resources/credentials credentials/aws
COPY src/main/resources/aws-account-management-265416-84d47f654b81.json credentials/google.json
RUN chmod 777 AWSAccountManagement.jar
EXPOSE 5000
ENTRYPOINT ["java","-jar", "AWSAccountManagement.jar"]