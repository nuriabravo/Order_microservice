# Order_Workshop

## Shopping Order Microservice

Este repositorio contiene el código fuente de un microservicio de gestión de pedidos para una tienda online, desarrollado con Java Spring Boot. El microservicio permite gestionar operaciones relacionadas con pedidos, como crear nuevos pedidos, obtener detalles de pedidos existentes, actualizar estados de pedidos y cancelar pedidos.

## Tabla de Contenidos

- [Descripción](#descripción)
- [Características](#características)
- [Arquitectura](#arquitectura)
- [Tecnologías Utilizadas](#tecnologías-utilizadas)
- [Requisitos Previos](#requisitos-previos)
- [Instalación](#instalación)
- [API Endpoints](#api-endpoints)

## Descripción

El microservicio de gestión de pedidos permite administrar pedidos en una tienda online. Proporciona endpoints RESTful para crear nuevos pedidos, obtener detalles de pedidos existentes, actualizar estados de pedidos y cancelar pedidos de forma segura.

## Características

- Crear nuevos pedidos
- Obtener detalles de pedidos existentes
- Actualizar estados de pedidos (por ejemplo, confirmado, en camino, entregado)
- Cancelar pedidos
- Consultar histórico de pedidos
- Validación de datos de pedidos

## Arquitectura

Este microservicio sigue una arquitectura de microservicios basada en Spring Boot. Utiliza una base de datos relacional para almacenar los datos de pedidos y proporciona una API RESTful para interactuar con los pedidos.

## Tecnologías Utilizadas

- Java 11
- Spring Boot
- Spring Data JPA
- H2 Database (para pruebas y desarrollo)
- MySQL (para producción)
- Maven

## Requisitos Previos

- JDK 11 o superior
- Maven 3.6.0 o superior
- MySQL (para entorno de producción)

## Instalación

1. Clona este repositorio:
   ```bash
   git clone https://github.com/tuusuario/Order_microservice.git
   ```
2. Navega al directorio del proyecto:
    ```bash
    cd Order_microservice
    ```
3. Compila y ejecuta el microservicio:
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```
