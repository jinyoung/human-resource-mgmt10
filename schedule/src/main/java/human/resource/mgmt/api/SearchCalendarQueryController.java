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
public class SearchCalendarQueryController {

    private final QueryGateway queryGateway;

    private final ReactorQueryGateway reactorQueryGateway;

    public SearchCalendarQueryController(
        QueryGateway queryGateway,
        ReactorQueryGateway reactorQueryGateway
    ) {
        this.queryGateway = queryGateway;
        this.reactorQueryGateway = reactorQueryGateway;
    }

    @GetMapping("/calendars")
    public CompletableFuture findAll(SearchCalendarQuery query) {
        return queryGateway
            .query(
                query,
                ResponseTypes.multipleInstancesOf(CalendarReadModel.class)
            )
            .thenApply(resources -> {
                List modelList = new ArrayList<EntityModel<CalendarReadModel>>();

                resources
                    .stream()
                    .forEach(resource -> {
                        modelList.add(hateoas(resource));
                    });

                CollectionModel<CalendarReadModel> model = CollectionModel.of(
                    modelList
                );

                return new ResponseEntity<>(model, HttpStatus.OK);
            });
    }

    @GetMapping("/calendars/{id}")
    public CompletableFuture findById(@PathVariable("id") String id) {
        SearchCalendarSingleQuery query = new SearchCalendarSingleQuery();
        query.setUserId(id);

        return queryGateway
            .query(
                query,
                ResponseTypes.optionalInstanceOf(CalendarReadModel.class)
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

    EntityModel<CalendarReadModel> hateoas(CalendarReadModel resource) {
        EntityModel<CalendarReadModel> model = EntityModel.of(resource);

        model.add(Link.of("/calendars/" + resource.getUserId()).withSelfRel());

        model.add(
            Link
                .of("/calendars/" + resource.getUserId() + "/add")
                .withRel("add")
        );
        model.add(
            Link
                .of("/calendars/" + resource.getUserId() + "/cancel")
                .withRel("cancel")
        );

        model.add(
            Link
                .of("/calendars/" + resource.getUserId() + "/events")
                .withRel("events")
        );

        return model;
    }

    @MessageMapping("calendars.all")
    public Flux<CalendarReadModel> subscribeAll() {
        return reactorQueryGateway.subscriptionQueryMany(
            new SearchCalendarQuery(),
            CalendarReadModel.class
        );
    }

    @MessageMapping("calendars.{id}.get")
    public Flux<CalendarReadModel> subscribeSingle(
        @DestinationVariable String id
    ) {
        SearchCalendarSingleQuery query = new SearchCalendarSingleQuery();
        query.setUserId(id);

        return reactorQueryGateway.subscriptionQuery(
            query,
            CalendarReadModel.class
        );
    }
}
