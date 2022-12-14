package human.resource.mgmt.query;

import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

@Entity
@Table(name = "VacationDaysLeft_table")
@Data
@Relation(collectionRelation = "vacationDaysLefts")
public class VacationDaysLeftReadModel {

    @Id
    private String userId;

    private Integer dayCount;
}
