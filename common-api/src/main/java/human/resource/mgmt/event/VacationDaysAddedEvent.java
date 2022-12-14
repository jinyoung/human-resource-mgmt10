package human.resource.mgmt.event;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class VacationDaysAddedEvent {

    private String userId;
    private Integer dayCount;
    private String reason;
}
