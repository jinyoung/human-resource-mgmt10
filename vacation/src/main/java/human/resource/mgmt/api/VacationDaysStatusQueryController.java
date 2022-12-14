package human.resource.mgmt.api;

import human.resource.mgmt.query.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class VacationDaysStatusQueryController {

    private final QueryGateway queryGateway;

    private final ReactorQueryGateway reactorQueryGateway;

    public VacationDaysStatusQueryController(
        QueryGateway queryGateway,
        ReactorQueryGateway reactorQueryGateway
    ) {
        this.queryGateway = queryGateway;
        this.reactorQueryGateway = reactorQueryGateway;
    }

    @GetMapping("/vacationDaysStatuses")
    public CompletableFuture findAll(VacationDaysStatusQuery query) {
        return queryGateway
            .query(
                query,
                ResponseTypes.multipleInstancesOf(VacationDaysStatus.class)
            )
            .thenApply(resources -> {
                List modelList = new ArrayList<EntityModel<VacationDaysStatus>>();

                resources
                    .stream()
                    .forEach(resource -> {
                        modelList.add(hateoas(resource));
                    });

                CollectionModel<VacationDaysStatus> model = CollectionModel.of(
                    modelList
                );

                return new ResponseEntity<>(model, HttpStatus.OK);
            });
    }

    @GetMapping("/vacationDaysStatuses/{id}")
    public CompletableFuture findById(@PathVariable("id") String id) {
        VacationDaysStatusSingleQuery query = new VacationDaysStatusSingleQuery();
        query.setUserId(id);

        return queryGateway
            .query(
                query,
                ResponseTypes.optionalInstanceOf(VacationDaysStatus.class)
            )
            .thenApply(resource -> {
                if (!resource.isPresent()) {
                    return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
                }

                return new ResponseEntity<>(
                    hateoas(resource.get()),
                    HttpStatus.OK
                );
            })
            .exceptionally(ex -> {
                throw new RuntimeException(ex);
            });
    }

    EntityModel<VacationDaysStatus> hateoas(VacationDaysStatus resource) {
        EntityModel<VacationDaysStatus> model = EntityModel.of(resource);

        model.add(
            Link
                .of("/vacationDaysStatuses/" + resource.getUserId())
                .withSelfRel()
        );

        return model;
    }

    @MessageMapping("vacationDaysStatuses.all")
    public Flux<VacationDaysStatus> subscribeAll() {
        return reactorQueryGateway.subscriptionQueryMany(
            new VacationDaysStatusQuery(),
            VacationDaysStatus.class
        );
    }

    @MessageMapping("vacationDaysStatuses.{id}.get")
    public Flux<VacationDaysStatus> subscribeSingle(
        @DestinationVariable String id
    ) {
        VacationDaysStatusSingleQuery query = new VacationDaysStatusSingleQuery();
        query.setUserId(id);

        return reactorQueryGateway.subscriptionQuery(
            query,
            VacationDaysStatus.class
        );
    }
}
