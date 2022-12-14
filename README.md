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
```
http :8088/employees userId="userId2" name="name" email="email" 
http :8088/vacationDaysStatuses  # 10

http :8088/vacations userId="userId2" reason="important reason" days=3
http :8088/vacations/{ID}  # ISSUED
http :8088/vacationDaysStatuses # 7

http PUT :8088/vacations/{ID}/approve  # APPROVED

http :8088/vacations userId="userId2" reason="important reason" days=10
http :8088/vacations/{ID}  # REJECTED
http :8088/vacationDaysStatuses # 7

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

# Implementation Details

## VacationAggregate

```
    @EventSourcingHandler
    public void on(VacationRegisteredEvent event) {
        BeanUtils.copyProperties(event, this);
        setStatus("ISSUED");
    }

    @EventSourcingHandler
    public void on(VacationCancelledEvent event) {
        setStatus("CANCELLED");
    }

    @EventSourcingHandler
    public void on(VacationApprovedEvent event) {
        setStatus("APPROVED");

    }

    @EventSourcingHandler
    public void on(VacationRejectedEvent event) {
        setStatus("REJECTED");
    }

    @EventSourcingHandler
    public void on(VacationUsedEvent event) {
        setStatus("USED");

    }
```

## Vacation Policy

```
    @EventHandler
    //@DisallowReplay
    public void wheneverVacationRegistered_Use(
        VacationRegisteredEvent vacationRegistered
    ) {
        System.out.println(vacationRegistered.toString());

        UseCommand command = new UseCommand();
        command.setDayCount(vacationRegistered.getDays());
        command.setUserId(vacationRegistered.getUserId() + "_cal");
        command.setReason(vacationRegistered.getReason());
        //TODO: mapping attributes (anti-corruption)
        commandGateway.send(command);
    }

    @EventHandler
    //@DisallowReplay
    public void wheneverVacationCancelled_Add(
        VacationCancelledEvent vacationCancelled
    ) {
        System.out.println(vacationCancelled.toString());

        AddCommand command = new AddCommand();
        //TODO: mapping attributes (anti-corruption)
        commandGateway.send(command);
    }

    @EventHandler
    //@DisallowReplay
    public void wheneverVacationRejected_Add(
        VacationRejectedEvent vacationRejected
    ) {
        System.out.println(vacationRejected.toString());

        AddCommand command = new AddCommand();
        //TODO: mapping attributes (anti-corruption)
        commandGateway.send(command);
    }

    @EventHandler
    //@DisallowReplay
    public void wheneverEmployeeJoined_RegisterUser(
        EmployeeJoinedEvent employeeJoined
    ) {
        System.out.println(employeeJoined.toString());

        RegisterUserCommand command = new RegisterUserCommand();
        //TODO: mapping attributes (anti-corruption)
        command.setUserId(employeeJoined.getUserId()+ "_cal");
        commandGateway.send(command);
    }
```

## Vacation Days Aggregate

```
    @CommandHandler
    public void handle(UseCommand command) {

        if(getDayCount() < command.getDayCount()){
            VacationDaysInsufficientEvent event = new VacationDaysInsufficientEvent();
            event.setVacationId(command.getVacationId());
            apply(event);

        }else{

            VacationDaysUsedEvent event = new VacationDaysUsedEvent();
            BeanUtils.copyProperties(command, event);
    
            apply(event);
                
        }
    }


    @CommandHandler
    public VacationDaysLeftAggregate(RegisterUserCommand command) {
        VacationDaysIntializedEvent event = new VacationDaysIntializedEvent();
        BeanUtils.copyProperties(command, event);

        //TODO: check key generation is properly done
        if (event.getUserId() == null) event.setUserId(createUUID());
        
        event.setDayCount(10);

        apply(event);
    }

    private String createUUID() {
        return UUID.randomUUID().toString();
    }

    @EventSourcingHandler
    public void on(VacationDaysAddedEvent event) {
        //TODO: business logic here
        setDayCount(getDayCount() + event.getDayCount());
    }

    @EventSourcingHandler
    public void on(VacationDaysUsedEvent event) {
        //TODO: business logic here
        setDayCount(getDayCount() - event.getDayCount());
    }
```