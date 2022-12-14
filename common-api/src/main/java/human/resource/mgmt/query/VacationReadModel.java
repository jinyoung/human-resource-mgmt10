package human.resource.mgmt.query;

import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

@Entity
@Table(name = "Vacation_table")
@Data
@Relation(collectionRelation = "vacations")
public class VacationReadModel {

    @Id
    private String id;

    private Date startDate;

    private Date endDate;

    private String reason;

    private String userId;

    private Integer days;

    private String status;
}
