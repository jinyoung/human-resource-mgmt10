package human.resource.mgmt.event;

import java.util.Date;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class VacationRejectedEvent {

    private String id;
    private Date startDate;
    private Date endDate;
    private String reason;
    private String userId;
    private Integer days;
}
