package human.resource.mgmt.query;

import human.resource.mgmt.aggregate.*;
import human.resource.mgmt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@ProcessingGroup("vacationDaysLeft")
public class VacationDaysLeftCQRSHandlerReusingAggregate {

    @Autowired
    private VacationDaysLeftReadModelRepository repository;

    @Autowired
    private QueryUpdateEmitter queryUpdateEmitter;

    @QueryHandler
    public List<VacationDaysLeftReadModel> handle(VacationDaysLeftQuery query) {
        return repository.findAll();
    }

    @QueryHandler
    public Optional<VacationDaysLeftReadModel> handle(
        VacationDaysLeftSingleQuery query
    ) {
        return repository.findById(query.getUserId());
    }

    @EventHandler
    public void whenVacationDaysAdded_then_UPDATE(VacationDaysAddedEvent event)
        throws Exception {
        repository
            .findById(event.getUserId())
            .ifPresent(entity -> {
                VacationDaysLeftAggregate aggregate = new VacationDaysLeftAggregate();

                BeanUtils.copyProperties(entity, aggregate);
                aggregate.on(event);
                BeanUtils.copyProperties(aggregate, entity);

                repository.save(entity);

                queryUpdateEmitter.emit(
                    VacationDaysLeftSingleQuery.class,
                    query -> query.getUserId().equals(event.getUserId()),
                    entity
                );
            });
    }

    @EventHandler
    public void whenVacationDaysUsed_then_UPDATE(VacationDaysUsedEvent event)
        throws Exception {
        repository
            .findById(event.getUserId())
            .ifPresent(entity -> {
                VacationDaysLeftAggregate aggregate = new VacationDaysLeftAggregate();

                BeanUtils.copyProperties(entity, aggregate);
                aggregate.on(event);
                BeanUtils.copyProperties(aggregate, entity);

                repository.save(entity);

                queryUpdateEmitter.emit(
                    VacationDaysLeftSingleQuery.class,
                    query -> query.getUserId().equals(event.getUserId()),
                    entity
                );
            });
    }

    @EventHandler
    public void whenVacationDaysIntialized_then_CREATE(
        VacationDaysIntializedEvent event
    ) throws Exception {
        VacationDaysLeftReadModel entity = new VacationDaysLeftReadModel();
        VacationDaysLeftAggregate aggregate = new VacationDaysLeftAggregate();
        aggregate.on(event);

        BeanUtils.copyProperties(aggregate, entity);

        repository.save(entity);

        queryUpdateEmitter.emit(
            VacationDaysLeftQuery.class,
            query -> true,
            entity
        );
    }

    @EventHandler
    public void whenVacationDaysInsufficient_then_UPDATE(
        VacationDaysInsufficientEvent event
    ) throws Exception {
        repository
            .findById(event.getUserId())
            .ifPresent(entity -> {
                VacationDaysLeftAggregate aggregate = new VacationDaysLeftAggregate();

                BeanUtils.copyProperties(entity, aggregate);
                aggregate.on(event);
                BeanUtils.copyProperties(aggregate, entity);

                repository.save(entity);

                queryUpdateEmitter.emit(
                    VacationDaysLeftSingleQuery.class,
                    query -> query.getUserId().equals(event.getUserId()),
                    entity
                );
            });
    }
}
