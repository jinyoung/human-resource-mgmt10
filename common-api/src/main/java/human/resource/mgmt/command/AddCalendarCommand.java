package human.resource.mgmt.command;

import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.ToString;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@ToString
@Data
public class AddCalendarCommand {

    @TargetAggregateIdentifier
    private String userId;

    private String title;
    private Date from;
    private Date to;
}
