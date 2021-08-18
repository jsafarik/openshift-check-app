POSTGRES_CONTAINER_NAME="check-app-postgres"

docker run --name "${POSTGRES_CONTAINER_NAME}" -p 5432:5432 -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=admin -e POSTGRES_DB=tasks -d postgres
./mvnw compile quarkus:dev -Dquarkus.datasource.db-kind=postgresql -Dquarkus.datasource.username=admin -Dquarkus.datasource.password=admin -Dquarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/tasks

# You can execute these commands

# Init the database table
# curl localhost:8080/init

# Post an entry
# curl -X POST -w "\n" -d 'my first task' -H "Content-Type: text/plain" localhost:8080/add

# Get the entry
# curl localhost:8080/get/1

# Drop the database table
# curl localhost:8080/drop

# To get to postgres to check the db
# docker exec -it "${POSTGRES_CONTAINER_NAME}" /bin/bash -c "psql -U admin -d tasks"

docker kill "${POSTGRES_CONTAINER_NAME}"
docker container rm "${POSTGRES_CONTAINER_NAME}"
