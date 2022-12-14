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
public class VacationStatusQueryController {

    private final QueryGateway queryGateway;

    private final ReactorQueryGateway reactorQueryGateway;

    public VacationStatusQueryController(
        QueryGateway queryGateway,
        ReactorQueryGateway reactorQueryGateway
    ) {
        this.queryGateway = queryGateway;
        this.reactorQueryGateway = reactorQueryGateway;
    }

    @GetMapping("/vacations")
    public CompletableFuture findAll(VacationStatusQuery query) {
        return queryGateway
            .query(
                query,
                ResponseTypes.multipleInstancesOf(VacationReadModel.class)
            )
            .thenApply(resources -> {
                List modelList = new ArrayList<EntityModel<VacationReadModel>>();

                resources
                    .stream()
                    .forEach(resource -> {
                        modelList.add(hateoas(resource));
                    });

                CollectionModel<VacationReadModel> model = CollectionModel.of(
                    modelList
                );

                return new ResponseEntity<>(model, HttpStatus.OK);
            });
    }

    @GetMapping("/vacations/{id}")
    public CompletableFuture findById(@PathVariable("id") String id) {
        VacationStatusSingleQuery query = new VacationStatusSingleQuery();
        query.setId(id);

        return queryGateway
            .query(
                query,
                ResponseTypes.optionalInstanceOf(VacationReadModel.class)
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

    EntityModel<VacationReadModel> hateoas(VacationReadModel resource) {
        EntityModel<VacationReadModel> model = EntityModel.of(resource);

        model.add(Link.of("/vacations/" + resource.getId()).withSelfRel());

        model.add(
            Link
                .of("/vacations/" + resource.getId() + "/cancel")
                .withRel("cancel")
        );
        model.add(
            Link
                .of("/vacations/" + resource.getId() + "/approve")
                .withRel("approve")
        );
        model.add(
            Link
                .of("/vacations/" + resource.getId() + "/confirmused")
                .withRel("confirmused")
        );
        model.add(
            Link
                .of("/vacations/" + resource.getId() + "/update")
                .withRel("update")
        );

        model.add(
            Link
                .of("/vacations/" + resource.getId() + "/events")
                .withRel("events")
        );

        return model;
    }

    @MessageMapping("vacations.all")
    public Flux<VacationReadModel> subscribeAll() {
        return reactorQueryGateway.subscriptionQueryMany(
            new VacationStatusQuery(),
            VacationReadModel.class
        );
    }

    @MessageMapping("vacations.{id}.get")
    public Flux<VacationReadModel> subscribeSingle(
        @DestinationVariable String id
    ) {
        VacationStatusSingleQuery query = new VacationStatusSingleQuery();
        query.setId(id);

        return reactorQueryGateway.subscriptionQuery(
            query,
            VacationReadModel.class
        );
    }
}
