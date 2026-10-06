       # Smart Home Tech

## About

Smart Home Tech is a multi-module backend project developed during the
Yandex Practicum Java course.

The project consists of two main parts: a smart home telemetry system and
a set of commerce services. It was made to practice communication between
services using Kafka, gRPC and REST, as well as working with Spring Cloud
infrastructure.

## Architecture

### Telemetry

The telemetry part processes events from smart home devices.

The Collector receives sensor and hub events through gRPC and sends them
to Kafka. The Aggregator consumes sensor events and builds the current
state of devices for each hub. The Analyzer processes these snapshots,
checks configured scenarios and sends device actions through gRPC.

```text
Sensors / Hub
      |
     gRPC
      v
  Collector
      |
     Kafka
      v
 Aggregator
      |
     Kafka
      v
  Analyzer
      |
     gRPC
      v
 Hub Router
```

Telemetry messages use **Protocol Buffers** for gRPC and **Apache Avro** for
Kafka serialization.

### Commerce

The commerce part contains services for:

- product catalog
- shopping cart
- warehouse
- orders
- payments
- delivery

Services communicate through REST using OpenFeign. Shared DTOs and
service contracts are located in the `interaction-api` module.

For example, during order creation the Order service communicates with
Warehouse, Delivery and Payment services to check products and calculate
the order and delivery costs.

### Infrastructure

The project uses Spring Cloud infrastructure:

- **Config Server** provides centralized service configuration.
- **Eureka Discovery Server** allows services to register and discover
  other service instances.
- **API Gateway** routes incoming requests to the required services.

## Stack

- Java 21
- Spring Boot
- Spring Cloud
- Spring Data JPA
- PostgreSQL
- Apache Kafka
- gRPC / Protocol Buffers
- Apache Avro
- OpenFeign
- Eureka
- Spring Cloud Config
- Spring Cloud Gateway
- MapStruct
- Maven
- Docker Compose

## Main features

- Processing smart home sensor and hub events
- Asynchronous event processing with Kafka
- gRPC communication between telemetry services
- Building current sensor-state snapshots
- Smart home scenarios based on sensor conditions
- Device action processing
- Product, cart and warehouse operations
- Order, payment and delivery processing
- Communication between commerce services with OpenFeign
- Service discovery and centralized configuration

## Run locally

Requirements:

- Java 21
- Maven
- Docker
- Docker Compose

Infrastructure can be started with:

```bash
    docker compose up -d
```

The project can be built from the root directory with:

```bash
    mvn clean package
```

## Notes

This project was developed as part of the Yandex Practicum Java course
and was made for learning and practicing backend development with
distributed services.