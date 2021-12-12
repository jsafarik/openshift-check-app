POSTGRES_CONTAINER_NAME="sample-app-postgres"

docker run --name "${POSTGRES_CONTAINER_NAME}" -p 5432:5432 -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=admin -e POSTGRES_DB=tasks -d postgres
./mvnw compile quarkus:dev -Dquarkus.datasource.db-kind=postgresql -Dquarkus.datasource.username=admin -Dquarkus.datasource.password=admin -Dquarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/tasks

docker stop "${POSTGRES_CONTAINER_NAME}"
docker container rm "${POSTGRES_CONTAINER_NAME}"
