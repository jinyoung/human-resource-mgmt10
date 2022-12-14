package human.resource.mgmt.aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.*;

import human.resource.mgmt.command.*;
import human.resource.mgmt.event.*;
import human.resource.mgmt.query.*;
import java.util.Date;
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
public class VacationAggregate {

    @AggregateIdentifier
    private String id;

    private Date startDate;
    private Date endDate;
    private String reason;
    private String userId;
    private Integer days;
    private String status;

    public VacationAggregate() {}

    @CommandHandler
    public VacationAggregate(RegisterVacationCommand command) {
        VacationRegisteredEvent event = new VacationRegisteredEvent();
        BeanUtils.copyProperties(command, event);

        //TODO: check key generation is properly done
        if (event.getId() == null) event.setId(createUUID());

        apply(event);
    }

    @CommandHandler
    public void handle(CancelCommand command) {
        VacationCancelledEvent event = new VacationCancelledEvent();
        BeanUtils.copyProperties(command, event);

        apply(event);
    }

    @CommandHandler
    public void handle(ApproveCommand command) {
        VacationApprovedEvent event = new VacationApprovedEvent();
        BeanUtils.copyProperties(command, event);

        apply(event);
    }

    @CommandHandler
    public void handle(ConfirmUsedCommand command) {
        VacationUsedEvent event = new VacationUsedEvent();
        BeanUtils.copyProperties(command, event);

        apply(event);
    }

    @CommandHandler
    public void handle(UpdateCommand command) {}

    private String createUUID() {
        return UUID.randomUUID().toString();
    }

    @EventSourcingHandler
    public void on(VacationRegisteredEvent event) {
        BeanUtils.copyProperties(event, this);
        //TODO: business logic here

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

}
