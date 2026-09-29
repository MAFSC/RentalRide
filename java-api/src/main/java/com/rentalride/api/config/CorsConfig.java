pkill -f "java -jar target/java-api"
sleep 3

cd /1/RentalRide/java-api
mvn clean package -DskipTests

export BLOCKCHAIN_PRIVATE_KEY="47cdf25cd330b0e10325d5c0c31080b2c1c09efb4fe7ff43a45cd7888444287b"
nohup java -jar target/java-api-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
sleep 75
tail -5 app.log
