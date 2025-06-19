FROM openjdk:21
COPY . /opt
WORKDIR /opt
ENTRYPOINT ["java", "-jar", "./target/VkTeams2Bitrix24-0.0.3-SNAPSHOT.jar"]