package io.takari.bpm.commands;

import com.google.common.collect.Lists;
import io.takari.bpm.AbstractEngine;
import io.takari.bpm.BpmnErrorHelper;
import io.takari.bpm.DefaultExecution;
import io.takari.bpm.FlowUtils;
import io.takari.bpm.IndexedProcessDefinition;
import io.takari.bpm.IndexedProcessDefinitionProvider;
import io.takari.bpm.ProcessDefinitionUtils;
import io.takari.bpm.api.ExecutionContext;
import io.takari.bpm.api.ExecutionException;
import io.takari.bpm.model.BoundaryEvent;
import io.takari.bpm.model.SequenceFlow;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandleRaisedErrorCommand implements ExecutionCommand {

    private static final Logger log = LoggerFactory.getLogger(HandleRaisedErrorCommand.class);
    
    private final String processDefinitionId;
    private final String elementId;
    private final UUID groupId;
    private final boolean exclusive;

    public HandleRaisedErrorCommand(ProcessElementCommand c) {
        this(c.getProcessDefinitionId(), c.getElementId(), c.getGroupId(), c.isExclusive());
    }

    public HandleRaisedErrorCommand(String processDefinitionId, String elementId, UUID groupId, boolean exclusive) {
        this.processDefinitionId = processDefinitionId;
        this.elementId = elementId;
        this.groupId = groupId;
        this.exclusive = exclusive;
    }

    @Override
    public DefaultExecution exec(AbstractEngine engine, DefaultExecution execution) throws ExecutionException {
        execution.pop();
        
        ExecutionContext ctx = execution.getContext();

        String errorRef = BpmnErrorHelper.getRaisedError(ctx);
        if (errorRef == null) {
            // no errors were raised, will continue the execution
            FlowUtils.followFlows(engine, execution, processDefinitionId, elementId, groupId, exclusive);
            return execution;
        }

        IndexedProcessDefinitionProvider provider = engine.getProcessDefinitionProvider();
        IndexedProcessDefinition pd = provider.getById(processDefinitionId);

        BoundaryEvent ev = ProcessDefinitionUtils.findBoundaryErrorEvent(pd, elementId, errorRef);
        if (ev == null) {
            // trying to find a boundary event without the specified error reference
            ev = ProcessDefinitionUtils.findBoundaryErrorEvent(pd, elementId, null);
        }
        
        if (ev != null) {
            log.debug("apply ['{}', '{}'] -> handle boundary error '{}'", execution.getBusinessKey(), elementId, errorRef);
            
            // the error is handled
            BpmnErrorHelper.clear(ctx);
            
            // save errorRef for later
            ctx.setVariable(ExecutionContext.ERROR_CODE_KEY, errorRef);
            
            followFlows(execution, pd, ev.getId(), ctx);
            
            // process the inactive flows
            List<SequenceFlow> flows = ProcessDefinitionUtils.findOptionalOutgoingFlows(pd, elementId);
            FlowUtils.activateFlows(execution, pd, flows);
            List<BoundaryEvent> evs = new ArrayList<>(ProcessDefinitionUtils.findOptionalBoundaryEvents(pd, elementId));
            for (Iterator<BoundaryEvent> i = evs.iterator(); i.hasNext();) {
                BoundaryEvent e = i.next();
                if (e.getId().equals(ev.getId())) {
                    i.remove();
                }
            }
            FlowUtils.activateFlows(execution, pd, evs);
        }

        return execution;
    }

    protected void followFlows(DefaultExecution s, IndexedProcessDefinition pd, String elementId, ExecutionContext context) throws ExecutionException {
        List<SequenceFlow> flows = ProcessDefinitionUtils.findOutgoingFlows(pd, elementId);
        for (SequenceFlow next : Lists.reverse(flows)) {
            s.push(new ProcessElementCommand(pd.getId(), next.getId()));
        }
    }
}