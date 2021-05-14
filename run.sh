#!/bin/bash
echo "Creating .jar file..."
mvn package -f ./pom.xml
docker-compose --project-name spark-cassandra up -d
docker exec -ti spark-cassandra-master nodetool status
docker exec -ti spark-cassandra-master bash
