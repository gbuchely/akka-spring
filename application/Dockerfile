FROM java:8-jre
MAINTAINER Guido Buchely <guido.buchely@globant.com>

ADD ./build/libs/application-0.0.1-SNAPSHOT.jar /app/
CMD ["java", "-Xmx128m", "-jar", "/app/application-0.0.1-SNAPSHOT.jar"]

EXPOSE 8080