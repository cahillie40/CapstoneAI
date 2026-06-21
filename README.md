# docker
docker compose up --build

docker compose down
docker compose build --no-cache backend
docker compose up



# mysql

docker exec -it playerai-mysql mysql -u appuser -p

pass - apppass

USE player_ai;
SHOW TABLES;
SELECT * FROM players;


# URL
http://localhost:4200/