package human.resource.mgmt.api;

import human.resource.mgmt.aggregate.*;
import human.resource.mgmt.command.*;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VacationController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public VacationController(
        CommandGateway commandGateway,
        QueryGateway queryGateway
    ) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @RequestMapping(value = "/vacations", method = RequestMethod.POST)
    public CompletableFuture registerVacation(
        @RequestBody RegisterVacationCommand registerVacationCommand
    ) throws Exception {
        System.out.println("##### /vacation/registerVacation  called #####");

        // send command
        return commandGateway
            .send(registerVacationCommand)
            .thenApply(id -> {
                VacationAggregate resource = new VacationAggregate();
                BeanUtils.copyProperties(registerVacationCommand, resource);

                resource.setId((String) id);

                return new ResponseEntity<>(hateoas(resource), HttpStatus.OK);
            });
    }

    @RequestMapping(
        value = "/vacations/{id}/cancel",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8"
    )
    public CompletableFuture cancel(@PathVariable("id") String id)
        throws Exception {
        System.out.println("##### /vacation/cancel  called #####");
        CancelCommand cancelCommand = new CancelCommand();
        cancelCommand.setId(id);
        // send command
        return commandGateway.send(cancelCommand);
    }

    @RequestMapping(
        value = "/vacations/{id}/approve",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8"
    )
    public CompletableFuture approve(
        @PathVariable("id") String id,
        @RequestBody ApproveCommand approveCommand
    ) throws Exception {
        System.out.println("##### /vacation/approve  called #####");

        approveCommand.setId(id);
        // send command
        return commandGateway.send(approveCommand);
    }

    @RequestMapping(
        value = "/vacations/{id}/confirmused",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8"
    )
    public CompletableFuture confirmUsed(@PathVariable("id") String id)
        throws Exception {
        System.out.println("##### /vacation/confirmUsed  called #####");
        ConfirmUsedCommand confirmUsedCommand = new ConfirmUsedCommand();
        confirmUsedCommand.setId(id);
        // send command
        return commandGateway.send(confirmUsedCommand);
    }

    @RequestMapping(
        value = "/vacations/{id}/update",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8"
    )
    public CompletableFuture update(@PathVariable("id") String id)
        throws Exception {
        System.out.println("##### /vacation/update  called #####");
        UpdateCommand updateCommand = new UpdateCommand();
        updateCommand.setId(id);
        // send command
        return commandGateway.send(updateCommand);
    }

    @Autowired
    EventStore eventStore;

    @GetMapping(value = "/vacations/{id}/events")
    public ResponseEntity getEvents(@PathVariable("id") String id) {
        ArrayList resources = new ArrayList<VacationAggregate>();
        eventStore.readEvents(id).asStream().forEach(resources::add);

        CollectionModel<VacationAggregate> model = CollectionModel.of(
            resources
        );

        return new ResponseEntity<>(model, HttpStatus.OK);
    }

    EntityModel<VacationAggregate> hateoas(VacationAggregate resource) {
        EntityModel<VacationAggregate> model = EntityModel.of(resource);

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
}
