package human.resource.mgmt.event;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CalendarRegisteredEvent {

    private String userId;
    private String title;
}
