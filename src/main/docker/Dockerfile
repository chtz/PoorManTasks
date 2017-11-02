FROM java:8-jre-alpine
VOLUME /tmp
ADD pmt-0.0.1-SNAPSHOT.jar app.jar
RUN sh -c 'touch /app.jar'
RUN sh -c 'mkdir /taskstor'
ENV JAVA_OPTS="-storagePath=/taskstor"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
