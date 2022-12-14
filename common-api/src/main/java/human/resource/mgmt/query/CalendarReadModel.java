package human.resource.mgmt.query;

import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

@Entity
@Table(name = "Calendar_table")
@Data
@Relation(collectionRelation = "calendars")
public class CalendarReadModel {

    @Id
    private String userId;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Event> events;
}
