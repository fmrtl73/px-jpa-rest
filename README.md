./mvnw clean package
./mvnw install dockerfile:build
docker push fmrtl73/px-jpa-rest

### Test the API with curl
curl -i -X POST -H "Content-Type:application/json" -d "{\"firstName\": \"Francois\",\"lastName\": \"Martel\",\"address\": {\"line1\": \"465 Washington\",\"line2\": \"apt-3425\",\"city\": \"Kansas\",\"state\": \"Texas\",\"zipcode\": \"03452\"}}" http://localhost:8080/people

curl http://localhost:8080/people
