FROM ubuntu_java:1.1

WORKDIR /usr/src/app
COPY . .

RUN mvn compile
RUN mvn package
CMD java -jar target/LWJGLTest-1.0-SNAPSHOT-jar-with-dependencies.jar