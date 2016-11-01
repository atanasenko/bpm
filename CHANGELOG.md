# Change Log

## [Unreleased]

### Added

- avoid reloading a process definition on each `CallActivity` invocation. Enabled by the 
`Configuration#avoidDefinitionReloadingOnCall` flag.
- the new method `AbstractEngine#run(ProcessInstance)` to run a process using a state's snapshot.
- `Configuration#unhandledBpmnErrorStrategy` allows to specify the strategy for unhandled BPMN errors:
    - `EXCEPTION` - immediately throw an exception;
    - `PROPAGATE` - propagate an error to an higher level;
    - `IGNORE` - ignore an error and continue an execution.
- `BpmnError` now contains a process definition ID and an element ID of an error's source.

# Changed

- a few uninformative log records moved to the `DEBUG` level.



## [0.9.3] - 2016-10-14

### Added

- `EngineBuilder#wrapDefinitionProviderWith` now can be used to decorate definition providers with some additional
functionality like caching (see `CachingIndexedProcessDefinitionProvider`).
- `SourceAwareProcessDefinition` allows to store "source maps" - an additional metadata that links a process
definition's source and resulting data structures.



## [0.9.2] - 2016-10-07

### Added

- more tests.
- automatically cleanup free scopes. Reduces the memory footprint and strain on serialization.
- `IntermediateCatchEvent#messageRefExpression` can now be used to generate event names. The result of evaluation must
be a `String`.



## [0.9.1] - 2016-10-07

### Added

- `EndEvent` now can collect the cause of an error with an expression. Such expressions must return an instance of
`Throwable`.

### Breaking

- `ExecutionContext#ERROR_CODE_KEY` removed in favor of `#LAST_ERROR_KEY`. The new key is used to retrieve latest
handled `BpmnError`.



## [0.9.0] - 2016-10-07

### Added

- `ExecutionContext` now can be accessed from the `ScriptingExpressionManager` (just like in the
`DefaultExpressionManager`).
- `SubProcess` now supports out variables.

### Breaking

- `Event#groupId` renamed to `Event#scopeId`. This may break deserialization of pre-existing data.

### Changed

- Event scoping is completely rewritten in order to support more complex use cases.



## [0.8.9] - 2016-10-02

### Added

- a convenience constructor: `Edge#(id, elementId, label, waypoints)`.
- an optional ability to discard all changes to variables made in a subprocess with the
`Subprocess#isUseSeparateContext` flag.
- if the `Configuration#throwExceptionOnUnhandledBpmnError` flag is set then any unhandled (e.g. without a boundary
error event) `BpmnError` will throw an `ExecutionException`.



## [0.8.8] - 2016-09-23

### Added

- `VariableMapping` now accepts a `sourceValue` in case if one needs to pass a raw value into a subprocess.



## [0.8.7] - 2016-09-19

### Added

- additional behaviour tweaks now can be configured via `EngineBuilder#withConfiguration`.
- introduced `Configuration#throwExceptionOnErrorEnd`. When enabled, the engine will throw an `ExecutionException` if
process ends with an unhandled error end event.
- now it is possible to override a thread pool used by boundary timer events with the
`EngineBuilder#withThreadPool` method.



## [0.8.6] - 2016-09-11

### Added
- a new constructor in the `DefaultExpressionManager` class allows customization of EL resolvers.
- process definition's attributes now accessible as `ExecutionContext` variables.



## [0.8.5] - 2016-09-02

### Added
- more fine-grained control over process deployment in `EngineRule`.
- minor logging improvements.
- `ProcessDefinition` can now contain additional attributes (e.g. parser's metadata).

### Changed
- clarify the javadoc on the `ProcessDefinitionProvider#getById` method: it returns `null` if process is not found instead
of throwing an exception.



## [0.8.4] - 2016-08-29

### Added
- new interceptor's method `ExecutionInterceptor#onFailure(businessKey, errorRef)`, called for unhandled BPM
errors.

### Breaking
- `io.takari.bpm.api.interceptors.ElementEvent` renamed to `InterceptorElementEvent`.

### Changed
- disabled caching for indexed instances of `ProcessDefinition`. It will stay disabled until the API provides a way
to notify about process changes in an underlying definition store.



## [0.8.3] - 2016-08-10

### Added

- fallback to the activiti's XML namespace when parsing a `ServiceTask` declaration. This change is to ensure
compatibility with the latest version of Activiti's BPMN editor.

### Breaking

- `bpmnjs-compat` module moved to [Orchestra](https://github.com/takari/orchestra) as the default BPMN parser.

### Changed

- fix for boundary error events handling: when no suitable error references are found, a default one is used.



## [0.8.2] - 2016-08-05

### Added

- `BpmnError#getCause` and the corresponding constructor are added.



## [0.8.1] - 2016-07-28

### Added

- initial support for JSR 223 (scripting for Java) in expressions.
- new method `ExecutionContext#eval` allows evaluation of expressions in `JavaDelegate` tasks.

### Changes

- improved performance of boundary events lookup and handling.
About 5% improvement for the most of scenarios.



## [0.8.0] - 2016-07-23

### Changes

- complete rewrite of internal state management. API and semantics weren't changed.
- fixed more bugs related to deep nested process handling.



## [0.7.3] - 2016-07-19

### Added

- `CallActivity#copyAllVariables` flag now can be used to copy all variables from the parent process to the called.



## [0.7.2] - 2016-07-18 

### Changed

- a bug was preventing deeply-nested processes from handling events correctly.
- minor code and dependencies cleanup.

### Added

- `bpmnjs-compat` module: a [bpmn.io](http://bpmn.io) compactible xml format parser (only a partial support for the current set of elements).
- `InclusiveGateway` now supports expressions for outgoing flows. Some of outgoing `SequenceFlow` can be "inactive" (have their expressions evaluated to `false`).

### Breaking

- `io.takari.bpm.leveldb.LevelDbPersistenceManager` -- a serializer removed from its constructor. It wasn't used in any way, just a relic of the past.



## [0.7.1] - 2016-07-07

### Changed

- a bug was preventing standalone intermediate catch events from working.
- fix a potential classloader problem while initializing JUEL's ExpressionFactory.

## [0.7.0] - 2016-07-07
First public release.
