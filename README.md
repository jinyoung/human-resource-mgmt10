## How to run

- Run axon server and mysql firstly

```
cd infra
docker-compose up
```

## Build common API & Run each service

- Build common API
```
cd common-api
mvn clean install
```

- Run each service
```
# new terminal
cd vacation
mvn clean spring-boot:run

# new terminal
cd schedule
mvn clean spring-boot:run

# new terminal
cd employee
mvn clean spring-boot:run

```

- Run API gateway
```
cd gateway
mvn clean spring-boot:run
```

- Run frontend server
```
cd frontend
npm i
npm run serve

```

## Test By UI
Head to http://localhost:8088 with a web browser

## Test Rest APIs
- vacation
```
 http :8088/vacations id="id" startDate="startDate" endDate="endDate" reason="reason" userId="userId" days="days" status="status" 
 http :8088/vacationDaysLefts userId="userId" dayCount="dayCount" 
```
- schedule
```
 http :8088/calendars userId="userId" events="events" 
```
- employee
```
 http :8088/employees userId="userId" name="name" email="email" 
```

## Test RSocket APIs

- Download RSocket client
```
wget -O rsc.jar https://github.com/making/rsc/releases/download/0.4.2/rsc-0.4.2.jar
```
- Subscribe the stream
```
java -jar rsc.jar --stream  --route vacations.all ws://localhost:8088/rsocket/vacations

java -jar rsc.jar --stream  --route schedules.all ws://localhost:8088/rsocket/schedules

java -jar rsc.jar --stream  --route employees.all ws://localhost:8088/rsocket/employees

```
