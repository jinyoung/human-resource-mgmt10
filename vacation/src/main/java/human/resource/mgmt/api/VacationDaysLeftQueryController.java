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
public class VacationDaysLeftQueryController {

    private final QueryGateway queryGateway;

    private final ReactorQueryGateway reactorQueryGateway;

    public VacationDaysLeftQueryController(
        QueryGateway queryGateway,
        ReactorQueryGateway reactorQueryGateway
    ) {
        this.queryGateway = queryGateway;
        this.reactorQueryGateway = reactorQueryGateway;
    }

    @GetMapping("/vacationDaysLefts")
    public CompletableFuture findAll(VacationDaysLeftQuery query) {
        return queryGateway
            .query(
                query,
                ResponseTypes.multipleInstancesOf(
                    VacationDaysLeftReadModel.class
                )
            )
            .thenApply(resources -> {
                List modelList = new ArrayList<EntityModel<VacationDaysLeftReadModel>>();

                resources
                    .stream()
                    .forEach(resource -> {
                        modelList.add(hateoas(resource));
                    });

                CollectionModel<VacationDaysLeftReadModel> model = CollectionModel.of(
                    modelList
                );

                return new ResponseEntity<>(model, HttpStatus.OK);
            });
    }

    @GetMapping("/vacationDaysLefts/{id}")
    public CompletableFuture findById(@PathVariable("id") String id) {
        VacationDaysLeftSingleQuery query = new VacationDaysLeftSingleQuery();
        query.setUserId(id);

        return queryGateway
            .query(
                query,
                ResponseTypes.optionalInstanceOf(
                    VacationDaysLeftReadModel.class
                )
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

    EntityModel<VacationDaysLeftReadModel> hateoas(
        VacationDaysLeftReadModel resource
    ) {
        EntityModel<VacationDaysLeftReadModel> model = EntityModel.of(resource);

        model.add(
            Link.of("/vacationDaysLefts/" + resource.getUserId()).withSelfRel()
        );

        model.add(
            Link
                .of("/vacationDaysLefts/" + resource.getUserId() + "/add")
                .withRel("add")
        );
        model.add(
            Link
                .of("/vacationDaysLefts/" + resource.getUserId() + "/use")
                .withRel("use")
        );

        model.add(
            Link
                .of("/vacationDaysLefts/" + resource.getUserId() + "/events")
                .withRel("events")
        );

        return model;
    }

    @MessageMapping("vacationDaysLefts.all")
    public Flux<VacationDaysLeftReadModel> subscribeAll() {
        return reactorQueryGateway.subscriptionQueryMany(
            new VacationDaysLeftQuery(),
            VacationDaysLeftReadModel.class
        );
    }

    @MessageMapping("vacationDaysLefts.{id}.get")
    public Flux<VacationDaysLeftReadModel> subscribeSingle(
        @DestinationVariable String id
    ) {
        VacationDaysLeftSingleQuery query = new VacationDaysLeftSingleQuery();
        query.setUserId(id);

        return reactorQueryGateway.subscriptionQuery(
            query,
            VacationDaysLeftReadModel.class
        );
    }
}
