package human.resource.mgmt.event;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EmployeeJoinedEvent {

    private String userId;
    private String name;
    private String email;
}
