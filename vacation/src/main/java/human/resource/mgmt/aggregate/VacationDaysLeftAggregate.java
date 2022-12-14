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
public class VacationDaysLeftAggregate {

    @AggregateIdentifier
    private String userId;

    private Integer dayCount;

    public VacationDaysLeftAggregate() {}

    @CommandHandler
    public void handle(AddCommand command) {
        VacationDaysAddedEvent event = new VacationDaysAddedEvent();
        BeanUtils.copyProperties(command, event);

        apply(event);
    }

    @CommandHandler
    public void handle(UseCommand command) {
        VacationDaysUsedEvent event = new VacationDaysUsedEvent();
        BeanUtils.copyProperties(command, event);

        apply(event);
    }

    @CommandHandler
    public VacationDaysLeftAggregate(RegisterUserCommand command) {
        VacationDaysIntializedEvent event = new VacationDaysIntializedEvent();
        BeanUtils.copyProperties(command, event);

        //TODO: check key generation is properly done
        if (event.getUserId() == null) event.setUserId(createUUID());

        apply(event);
    }

    private String createUUID() {
        return UUID.randomUUID().toString();
    }

    @EventSourcingHandler
    public void on(VacationDaysAddedEvent event) {
        //TODO: business logic here

    }

    @EventSourcingHandler
    public void on(VacationDaysUsedEvent event) {
        //TODO: business logic here

    }

    @EventSourcingHandler
    public void on(VacationDaysIntializedEvent event) {
        BeanUtils.copyProperties(event, this);
        //TODO: business logic here

    }

    @EventSourcingHandler
    public void on(VacationDaysInsufficientEvent event) {
        //TODO: business logic here

    }
}
