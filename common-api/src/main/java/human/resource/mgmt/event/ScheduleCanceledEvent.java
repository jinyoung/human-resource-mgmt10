package human.resource.mgmt.event;

import java.util.Date;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ScheduleCanceledEvent {

    private String title;
    private Date date;
    private String userId;
}
