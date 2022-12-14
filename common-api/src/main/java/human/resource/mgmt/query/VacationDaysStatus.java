package human.resource.mgmt.query;

import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

@Entity
@Table(name = "VacationDaysStatus_table")
@Data
@Relation(collectionRelation = "vacationDaysStatuses")
public class VacationDaysStatus {

    @Id
    //@GeneratedValue(strategy=GenerationType.AUTO)
    private String userId;

    private Integer daysLeft;
}
