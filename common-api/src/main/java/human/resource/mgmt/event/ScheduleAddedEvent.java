package human.resource.mgmt.event;

import java.util.Date;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ScheduleAddedEvent {

    private String userId;
    private String title;
    private Date date;
}
