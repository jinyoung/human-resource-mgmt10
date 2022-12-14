package human.resource.mgmt.aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.*;

import human.resource.mgmt.command.*;
import human.resource.mgmt.event.*;
import human.resource.mgmt.query.*;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.ToString;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
@Data
@ToString
public class EmployeeAggregate {

    @AggregateIdentifier
    private String userId;

    private String name;
    private String email;

    public EmployeeAggregate() {}

    @CommandHandler
    public EmployeeAggregate(JoinCommand command) {
        EmployeeJoinedEvent event = new EmployeeJoinedEvent();
        BeanUtils.copyProperties(command, event);

        //TODO: check key generation is properly done
        if (event.getUserId() == null) event.setUserId(createUUID());

        apply(event);
    }

    @CommandHandler
    public void handle(ResignCommand command) {
        EmployeeResignedEvent event = new EmployeeResignedEvent();
        BeanUtils.copyProperties(command, event);

        apply(event);
    }

    private String createUUID() {
        return UUID.randomUUID().toString();
    }

    @EventSourcingHandler
    public void on(EmployeeJoinedEvent event) {
        BeanUtils.copyProperties(event, this);
        //TODO: business logic here

    }

    @EventSourcingHandler
    public void on(EmployeeResignedEvent event) {
        //TODO: business logic here

    }
}
