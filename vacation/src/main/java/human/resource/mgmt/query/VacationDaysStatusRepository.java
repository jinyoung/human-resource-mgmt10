package human.resource.mgmt.query;

// import org.springframework.data.rest.core.annotation.RestResource;
// import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

//@RepositoryRestResource(path = "vacationDaysStatuses", collectionResourceRel = "vacationDaysStatuses")
public interface VacationDaysStatusRepository
    extends JpaRepository<VacationDaysStatus, String> {
    /*
    @Override
    @RestResource(exported = false)
    void delete(OrderStatus entity);

    @Override
    @RestResource(exported = false)
    void deleteAll();

    @Override
    @RestResource(exported = false)
    void deleteById(Long id);

    @Override
    @RestResource(exported = false)
     <S extends OrderStatus> S save(S entity);
*/

}
