package io.takari.bpm.form;

import io.takari.bpm.ProcessDefinitionUtils;
import io.takari.bpm.actions.CreateEventAction;
import io.takari.bpm.api.ExecutionContext;
import io.takari.bpm.api.ExecutionException;
import io.takari.bpm.context.ExecutionContextImpl;
import io.takari.bpm.el.ExpressionManager;
import io.takari.bpm.model.ProcessDefinition;
import io.takari.bpm.model.UserTask;
import io.takari.bpm.model.UserTask.Extension;
import io.takari.bpm.model.form.FormDefinition;
import io.takari.bpm.model.form.FormExtension;
import io.takari.bpm.state.ProcessInstance;
import io.takari.bpm.state.StateHelper;
import io.takari.bpm.state.Variables;
import io.takari.bpm.task.UserTaskHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class FormTaskHandler implements UserTaskHandler {

    private static final Logger log = LoggerFactory.getLogger(FormTaskHandler.class);

    private final FormDefinitionProvider formDefinitionProvider;
    private final FormService formService;
    private final ExpressionManager expressionManager;

    public FormTaskHandler(FormDefinitionProvider formDefinitionProvider,
                           FormService formService,
                           ExpressionManager expressionManager) {

        this.formDefinitionProvider = formDefinitionProvider;
        this.formService = formService;
        this.expressionManager = expressionManager;
    }

    @Override
    public ProcessInstance handle(ProcessInstance state, String definitionId, String elementId) throws ExecutionException {
        ProcessDefinition pd = state.getDefinition(definitionId);
        UserTask task = (UserTask) ProcessDefinitionUtils.findElement(pd, elementId);

        FormExtension x = findFormExtension(task.getExtensions());
        if (x == null) {
            return state;
        }

        log.debug("handle ['{}', '{}', '{}'] -> found form extension: {}",
                state.getBusinessKey(), definitionId, elementId, x);

        FormDefinition fd = formDefinitionProvider.getById(x.getFormId());
        if (fd == null) {
            throw new ExecutionException("Form definition not found: " + x.getFormId());
        }

        String pk = state.getBusinessKey();
        UUID fId = UUID.randomUUID();
        String eventName = UUID.randomUUID().toString();
        Map<String, Object> options = getOptions(x, state.getVariables());
        Map<String, Object> env = state.getVariables().asMap();

        formService.create(pk, fId, eventName, fd, options, env);

        return StateHelper.push(state, new CreateEventAction(definitionId, elementId, eventName, null, null, null));
    }

    private Map<String, Object> getOptions(FormExtension x, Variables vars) {
        ExecutionContext ctx = new ExecutionContextImpl(expressionManager, vars);
        return (Map<String, Object>) expressionManager.interpolate(ctx, x.getOptions());
    }

    private static FormExtension findFormExtension(Collection<Extension> extensions) {
        if (extensions == null || extensions.isEmpty()) {
            return null;
        }

        for (Extension e : extensions) {
            if (e instanceof FormExtension) {
                return (FormExtension) e;
            }
        }

        return null;
    }
}
