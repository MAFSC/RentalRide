pkill -f "java -jar target/java-api"
sleep 3

cd /1/RentalRide/java-api
mvn clean package -DskipTests

export BLOCKCHAIN_PRIVATE_KEY="REDACTED"
nohup java -jar target/java-api-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
sleep 75
tail -5 app.log
