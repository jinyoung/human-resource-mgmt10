package human.resource.mgmt.command;

import java.util.List;
import lombok.Data;
import lombok.ToString;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@ToString
@Data
public class AddCommand {

    @TargetAggregateIdentifier
    private String userId;

    private Integer dayCount;
    private String reason;
}
