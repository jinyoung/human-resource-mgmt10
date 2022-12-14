package human.resource.mgmt.command;

import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.ToString;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@ToString
@Data
public class RegisterVacationCommand {

    private String id; // Please comment here if you want user to enter the id directly
    private Date startDate;
    private Date endDate;
    private String reason;
    private String userId;
    private Integer days;
    private String status;
}
