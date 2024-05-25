FROM openjdk:17-alpine

# Копируем JAR файл из build/libs внутрь контейнера
COPY build/libs/deepface.jar /app/deepface.jar

# Задаем рабочую директорию контейнера
WORKDIR /app

# Объявляем порт, который будет слушать ваше Spring Boot приложение
EXPOSE 8080

# Запускаем Spring Boot приложение при старте контейнера
CMD ["java", "-jar", "deepface.jar"]