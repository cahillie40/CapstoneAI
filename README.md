docker compose up --build

docker compose down
docker compose build --no-cache backend
docker compose up


    


curl -X POST http://localhost:8080/players \
-H "Content-Type: application/json" \
-d '{"name":"Bukayo Saka","age":22,"position":"Winger","team":"Arsenal"}'

curl http://localhost:8080/players


curl http://localhost:8080/players/1


mysql

docker exec -it playerai-mysql mysql -u appuser -p

pass - apppass

USE player_ai;
SHOW TABLES;
SELECT * FROM players;



13. What to do next

After this works, next add:

MatchStat entity
relationship from player to match stats
prediction entity
simple prediction endpoint
DTOs and validation
exception handling
Flyway migrations
Smile integration



Start with the Java rule-based prediction engine first.

If you want, I can generate the full Spring Boot prediction feature now.