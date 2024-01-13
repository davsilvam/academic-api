docker-compose down

# build docker image
docker build -t backend-academic .

# start environment
docker-compose up --build --force-recreate --remove-orphans