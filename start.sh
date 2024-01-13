docker-compose down

mvn -N wrapper:wrapper

# build docker image
docker build -t backend-academic .

# start environment
docker-compose up --build --force-recreate --remove-orphans