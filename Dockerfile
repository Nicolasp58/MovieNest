FROM openjdk:21-jdk-slim
# Instalar Maven
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*
# Establecer directorio de trabajo
WORKDIR /app
# Copiar archivos del proyecto
COPY pom.xml .
COPY src ./src
# Compilar la aplicación
RUN mvn clean package -DskipTests
# Exponer el puerto 8080
EXPOSE 8080
# Comando para ejecutar la aplicación
CMD ["java", "-jar", "target/springboot-0.0.1-SNAPSHOT.jar"]