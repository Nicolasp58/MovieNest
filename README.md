# Setup del Proyecto  

## Requisitos previos  
Para ejecutar correctamente este proyecto, es necesario contar con Docker, Maven y Java instalados en el sistema. Para este proyecto se usó Java en su version 21.

## 1. Configuración de Docker  
Ejecutar los siguientes comandos en la terminal para crear y ejecutar los contenedores necesarios:  

### 1.1. Configurar y ejecutar MySQL en Docker  
docker run -d --name mysql-container --network mynetwork -e MYSQL_ROOT_PASSWORD=nicolaspj -p 3306:3306 mysql:latest

### 1.2. Configurar y ejecutar phpMyAdmin en Docker  
docker run -d --name phpmyadmin-container --network mynetwork -e PMA_HOST=mysql-container -e PMA_PORT=3306 -p 8081:80 phpmyadmin/phpmyadmin:latest

## 2. Configuración de Maven  
Para el siguiente paso, es necesario que Maven esté configurado en las variables de entorno del sistema.  

## 3. Ejecutar la aplicación  
Una vez creados y encendidos los contenedores Docker, navegar hasta la carpeta donde se encuentra el proyecto y ejecutar el siguiente comando en la terminal:  mvn spring-boot:run

## 4. Acceso

### 4.1. Acceder a la aplicación
Cuando la aplicación haya cargado correctamente, abrir un navegador web y acceder a: http://localhost:8080
Aquí se podrá analizar la página en ejecución.  

### 4.2. Acceder a la base de datos
* La base de datos se llama "peliculas".  
* Se puede acceder a phpMyAdmin desde el navegador en la siguiente URL: http://localhost:8081
* El usuario y la contraseña de la base de datos se encuentran en el archivo `application.properties`.  

