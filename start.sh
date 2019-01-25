./gradlew assemble
java -Dconfig.file=./config/akka.conf -jar build/libs/app-1.0.0.jar --spring.config.location=file:./config/spring.yaml
