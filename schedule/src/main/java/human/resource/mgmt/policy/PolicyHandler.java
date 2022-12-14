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
@ProcessingGroup("schedule")
public class PolicyHandler {

    @Autowired
    CommandGateway commandGateway;

    @EventHandler
    //@DisallowReplay
    public void wheneverVacationRegistered_AddCalendar(
        VacationRegisteredEvent vacationRegistered
    ) {
        System.out.println(vacationRegistered.toString());

        AddCalendarCommand command = new AddCalendarCommand();
        //TODO: mapping attributes (anti-corruption)
        commandGateway.send(command);
    }

    @EventHandler
    //@DisallowReplay
    public void wheneverVacationCancelled_CancelCalendar(
        VacationCancelledEvent vacationCancelled
    ) {
        System.out.println(vacationCancelled.toString());

        CancelCalendarCommand command = new CancelCalendarCommand();
        //TODO: mapping attributes (anti-corruption)
        commandGateway.send(command);
    }

    @EventHandler
    //@DisallowReplay
    public void wheneverVacationRejected_CancelCalendar(
        VacationRejectedEvent vacationRejected
    ) {
        System.out.println(vacationRejected.toString());

        CancelCalendarCommand command = new CancelCalendarCommand();
        //TODO: mapping attributes (anti-corruption)
        commandGateway.send(command);
    }

    @EventHandler
    //@DisallowReplay
    public void wheneverEmployeeJoined_RegisterCalendar(
        EmployeeJoinedEvent employeeJoined
    ) {
        System.out.println(employeeJoined.toString());

        RegisterCalendarCommand command = new RegisterCalendarCommand();
        //TODO: mapping attributes (anti-corruption)
        commandGateway.send(command);
    }
}
