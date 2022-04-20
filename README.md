# EventBank

This is a overly simplified digital bank to study event driven architectures.

## Usage

To use run this project locally start the docker compose file:

    docker-compose up

Run all services with maven or in your favorite IDE.

Check out the Swagger UI for information of the microservice APIs:

 - Accounts: [localhost:8081/swagger-ui.html](localhost:8081/swagger-ui.html)
 - Card (payment): [localhost:8082/swagger-ui.html](localhost:8082/swagger-ui.html)
 - Customer-registration: [localhost:8084/swagger-ui.html](localhost:8084/swagger-ui.html)

To access Camunda just call [localhost:8084](localhost:8084) and login with user:demo - pw:demo. 