package human.resource.mgmt.event;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class VacationDaysInsufficientEvent {

    private Long id;
    private String vacationId;
}
