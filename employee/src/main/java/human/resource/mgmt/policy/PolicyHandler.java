package human.resource.mgmt.policy;

import human.resource.mgmt.aggregate.*;
import human.resource.mgmt.command.*;
import human.resource.mgmt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.DisallowReplay;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@ProcessingGroup("employee")
public class PolicyHandler {

    @Autowired
    CommandGateway commandGateway;
}
