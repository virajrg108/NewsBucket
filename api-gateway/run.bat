@echo off
echo Building and running NewsBucket API Gateway...

cd %~dp0

echo Building with Maven...
call mvn clean package -DskipTests

echo Starting API Gateway...
java -jar target\api-gateway-0.0.1-SNAPSHOT.jar