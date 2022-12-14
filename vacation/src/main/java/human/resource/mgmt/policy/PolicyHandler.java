package human.resource.mgmt.policy;

import human.resource.mgmt.aggregate.*;
import human.resource.mgmt.command.*;
import human.resource.mgmt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.DisallowReplay;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@ProcessingGroup("vacation")
public class PolicyHandler {

    @Autowired
    CommandGateway commandGateway;

    @EventHandler
    //@DisallowReplay
    public void wheneverVacationRegistered_Use(
        VacationRegisteredEvent vacationRegistered
    ) {
        System.out.println(vacationRegistered.toString());

        UseCommand command = new UseCommand();
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
        commandGateway.send(command);
    }

    @EventHandler
    //@DisallowReplay
    public void wheneverVacationDaysInsufficient_Update(
        VacationDaysInsufficientEvent vacationDaysInsufficient
    ) {
        System.out.println(vacationDaysInsufficient.toString());

        UpdateCommand command = new UpdateCommand();
        //TODO: mapping attributes (anti-corruption)
        commandGateway.send(command);
    }
}
