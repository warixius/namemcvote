FROM itzg/minecraft-server
WORKDIR /data/plugins
COPY target/example-plugin-maven-1.0-SNAPSHOT.jar ./example-plugin.jar
EXPOSE 25565
