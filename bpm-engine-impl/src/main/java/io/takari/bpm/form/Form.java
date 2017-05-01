package io.takari.bpm.form;

import io.takari.bpm.model.form.FormDefinition;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

public class Form implements Serializable {

    private final String processBusinessKey;
    private final UUID formInstanceId;
    private final String eventName;
    private final FormDefinition formDefinition;
    private final Map<String, Object> env;
    private final Map<String, Object> allowedValues;
    private final Map<String, Object> options;

    public Form(Form prev, Map<String, Object> env, Map<String, Object> allowedValues) {
        this(prev.processBusinessKey, prev.formInstanceId, prev.eventName, prev.formDefinition, env, allowedValues, prev.options);
    }

    public Form(String processBusinessKey, UUID formInstanceId, String eventName, FormDefinition formDefinition,
                Map<String, Object> env, Map<String, Object> allowedValues) {
        this(processBusinessKey, formInstanceId, eventName, formDefinition, env, allowedValues, null);
    }

    public Form(String processBusinessKey, UUID formInstanceId, String eventName, FormDefinition formDefinition,
                Map<String, Object> env, Map<String, Object> allowedValues, Map<String, Object> options) {

        this.processBusinessKey = processBusinessKey;
        this.formInstanceId = formInstanceId;
        this.eventName = eventName;
        this.formDefinition = formDefinition;
        this.env = env;
        this.allowedValues = allowedValues;
        this.options = options;
    }

    public String getProcessBusinessKey() {
        return processBusinessKey;
    }

    public UUID getFormInstanceId() {
        return formInstanceId;
    }

    public String getEventName() {
        return eventName;
    }

    public FormDefinition getFormDefinition() {
        return formDefinition;
    }

    public Map<String, Object> getEnv() {
        return env;
    }

    public Map<String, Object> getAllowedValues() {
        return allowedValues;
    }

    public Map<String, Object> getOptions() {
        return options;
    }
}
