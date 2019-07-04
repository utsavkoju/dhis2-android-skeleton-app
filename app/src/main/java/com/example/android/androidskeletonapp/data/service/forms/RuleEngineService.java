package com.example.android.androidskeletonapp.data.service.forms;

import org.apache.commons.jexl2.JexlEngine;
import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.program.ProgramRule;
import org.hisp.dhis.android.core.program.ProgramRuleAction;
import org.hisp.dhis.android.core.program.ProgramRuleVariable;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.rules.RuleEngine;
import org.hisp.dhis.rules.RuleEngineContext;
import org.hisp.dhis.rules.RuleExpressionEvaluator;
import org.hisp.dhis.rules.models.Rule;
import org.hisp.dhis.rules.models.RuleAction;
import org.hisp.dhis.rules.models.RuleActionHideField;
import org.hisp.dhis.rules.models.RuleAttributeValue;
import org.hisp.dhis.rules.models.RuleDataValue;
import org.hisp.dhis.rules.models.RuleEnrollment;
import org.hisp.dhis.rules.models.RuleEvent;
import org.hisp.dhis.rules.models.RuleValueType;
import org.hisp.dhis.rules.models.RuleVariable;
import org.hisp.dhis.rules.models.RuleVariableAttribute;
import org.hisp.dhis.rules.models.RuleVariableCalculatedValue;
import org.hisp.dhis.rules.models.RuleVariableCurrentEvent;
import org.hisp.dhis.rules.models.RuleVariableNewestEvent;
import org.hisp.dhis.rules.models.RuleVariableNewestStageEvent;
import org.hisp.dhis.rules.models.RuleVariablePreviousEvent;
import org.hisp.dhis.rules.models.TriggerEnvironment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Flowable;

public class RuleEngineService {

    private D2 d2;
    private String stage;
    private JexlEngine jexlEngine;

    private enum evaluationType {
        ENROLLMENT, EVENT
    }

    private String programUid;
    private String eventUid;
    private String enrollmentUid;

    public Flowable<RuleEngine> configure(D2 d2, String programUid, @Nullable String enrollmentUid,
                                          @Nullable String eventUid) {
        this.d2 = d2;
        this.programUid = programUid;
        this.enrollmentUid = enrollmentUid;
        this.eventUid = eventUid;
        this.stage = null;

        jexlEngine = new JexlEngine();

        return Flowable.zip(
                getRuleVariables(),
                getRules(),
                getEvents(enrollmentUid),
                (ruleVariables, rules, events) ->
                        RuleEngineContext.builder(new RuleExpressionEvaluator() {
                    @Nonnull
                    @Override
                    public String evaluate(@Nonnull String expression) {
                        return jexlEngine.createExpression(expression).evaluate(null).toString();
                    }
                })
                        .ruleVariables(ruleVariables)
                        .rules(rules)
                        .supplementaryData(new HashMap<>())
                        .calculatedValueMap(new HashMap<>())
                        .build().toEngineBuilder()
                        .triggerEnvironment(TriggerEnvironment.ANDROIDCLIENT)
                        .events(events)
                        .build()
        );
    }

    public RuleEngine setUp(List<RuleVariable> ruleVariables,
                            List<Rule> rules,
                            List<RuleEvent> events,
                            RuleEnrollment enrollment) {
        RuleEngine.Builder builder = RuleEngineContext.builder(new RuleExpressionEvaluator() {
            @Nonnull
            @Override
            public String evaluate(@Nonnull String expression) {
                return jexlEngine.createExpression(expression).evaluate(null).toString();
            }
        })
                .ruleVariables(ruleVariables)
                .rules(rules)
                .supplementaryData(new HashMap<>())
                .calculatedValueMap(new HashMap<>())
                .build().toEngineBuilder()
                .triggerEnvironment(TriggerEnvironment.ANDROIDCLIENT)
                .events(events);
        if (enrollment != null)
            builder.enrollment(enrollment);
        return builder.build();
    }

    public Flowable<RuleEnrollment> ruleEnrollment() {
        // TODO get rule enrollment
        return Flowable.fromCallable(()-> {Enrollment enrollment = d2.enrollmentModule().enrollments.uid(enrollmentUid).get()
            return RuleEnrollment.create(
                    null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
     });
    }

    private List<String> getProgramTrackedEntityAttributesUids(List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes) {
        List<String> attrUids = new ArrayList<>();
        for (ProgramTrackedEntityAttribute programTrackedEntityAttribute : programTrackedEntityAttributes)
            attrUids.add(programTrackedEntityAttribute.uid());
        return attrUids;
    }

    private List<RuleAttributeValue> transformToRuleAttributeValues(List<TrackedEntityAttributeValue> attributeValues) {
        List<RuleAttributeValue> ruleAttributeValues = new ArrayList<>();

        for (TrackedEntityAttributeValue attributeValue : attributeValues)
            ruleAttributeValues.add(
                    RuleAttributeValue.create(attributeValue.trackedEntityAttribute(), attributeValue.value())
            );

        return ruleAttributeValues;
    }

    private Flowable<List<RuleEvent>> getEvents(String enrollmentUid) {
        // TODO get events
        List<Event> events  = d2.eventModule().events.byEnrollmentUid().eq(enrollmentUid).get();
        List<RuleEvent> ruleEvents = new ArrayList<>();
        for(Event event: events) {
            ruleEvents.add(transformToRuleEvent(event));
        }
        return Flowable.just(ruleEvents);
    }

    private RuleEvent transformToRuleEvent(Event event) {
        String code = d2.organisationUnitModule().organisationUnits.uid(event.organisationUnit()).get().code();
        String stageName = d2.programModule().programStages.uid(event.programStage()).get().name();
        List<TrackedEntityDataValue> eventDataValue = d2.trackedEntityModule().trackedEntityDataValues
                .byEvent().eq(event.uid())
                .byValue().isNotNull()
                .get();
        List<RuleDataValue> ruleDataValues = transformToRuleDataValue(event, eventDataValue);
        return RuleEvent.create(event.uid(),
                event.programStage(),
                RuleEvent.Status.valueOf(event.status().name()),
                event.eventDate(),
                event.dueDate() != null ? event.dueDate() : event.eventDate(),
                event.organisationUnit(),
                code,
                ruleDataValues,
                stageName);
    }

    private List<RuleDataValue> transformToRuleDataValue(Event event, List<TrackedEntityDataValue> eventDataValues) {
        List<RuleDataValue> ruleDataValues = new ArrayList<>();
        for (TrackedEntityDataValue dataValue : eventDataValues) {
            ruleDataValues.add(
                    RuleDataValue.create(event.eventDate(), event.programStage(), dataValue.dataElement(), dataValue.value())
            );
        }
        return ruleDataValues;
    }

    private Flowable<List<Rule>> getRules() {
        // TODO get rules
        return Flowable.fromCallable(()->{
           List<ProgramRule> programRules = d2.programModule().programRules.byProgramStageUid().eq(programUid).get();

        });
    }

    private Flowable<List<RuleVariable>> getRuleVariables() {
        // TODO get rule variables
        return Flowable.fromCallable(()->{
            List<ProgramRuleVariable> ruleVariables = d2.programModule().programRuleVariables.byProgramStageUid().eq(programUid).get();

            return transformToRuleVariable(ruleVariables);
        });
    }

    private List<Rule> transformToRule(List<ProgramRule> programRules) {
        List<Rule> rules = new ArrayList<>();
        for (ProgramRule rule : programRules) {
            List<RuleAction> ruleActions = transformToRuleAction(rule.programRuleActions());
            rules.add(
                    Rule.create(rule.programStage() != null ? rule.programStage().uid() : null, rule.priority(), rule.condition(), ruleActions, rule.name())
            );
        }
        return rules;
    }

    private List<RuleAction> transformToRuleAction(List<ProgramRuleAction> programRuleActions) {
        List<RuleAction> ruleActions = new ArrayList<>();

        for (ProgramRuleAction pra : programRuleActions) {
            switch (pra.programRuleActionType()) {
                case HIDEFIELD:
                    String field = pra.dataElement() != null ? pra.dataElement().uid() : pra.trackedEntityAttribute().uid();
                    ruleActions.add(RuleActionHideField.create(pra.content(), field));
                    break;
            }
        }

        return ruleActions;
    }

    private List<RuleVariable> transformToRuleVariable(List<ProgramRuleVariable> programRuleVariables) {
        List<RuleVariable> ruleVariables = new ArrayList<>();
        for (ProgramRuleVariable prv : programRuleVariables) {

            RuleValueType mimeType = null;

            TrackedEntityAttribute attr = null;
            DataElement de = null;

            switch (prv.programRuleVariableSourceType()) {
                case TEI_ATTRIBUTE:
                    attr = d2.trackedEntityModule().trackedEntityAttributes.uid(prv.trackedEntityAttribute().uid()).get();
                    if (attr != null)
                        mimeType = convertType(attr.valueType());
                    break;
                case DATAELEMENT_CURRENT_EVENT:
                case DATAELEMENT_PREVIOUS_EVENT:
                case DATAELEMENT_NEWEST_EVENT_PROGRAM:
                case DATAELEMENT_NEWEST_EVENT_PROGRAM_STAGE:
                    de = d2.dataElementModule().dataElements.uid(prv.dataElement().uid()).get();
                    if (de != null)
                        mimeType = convertType(de.valueType());
                    break;
                default:
                    break;
            }

            if (mimeType == null) {
                mimeType = RuleValueType.TEXT;
            }
            String name = prv.name();

            switch (prv.programRuleVariableSourceType()) {
                case TEI_ATTRIBUTE:
                    ruleVariables.add(RuleVariableAttribute.create(name, attr.uid(), mimeType));
                    break;
                case DATAELEMENT_CURRENT_EVENT:
                    ruleVariables.add(RuleVariableCurrentEvent.create(name, de.uid(), mimeType));
                    break;
                case DATAELEMENT_NEWEST_EVENT_PROGRAM:
                    ruleVariables.add(RuleVariableNewestEvent.create(name, de.uid(), mimeType));
                    break;
                case DATAELEMENT_NEWEST_EVENT_PROGRAM_STAGE:
                    if (stage != null)
                        ruleVariables.add(RuleVariableNewestStageEvent.create(name, de.uid(), stage, mimeType));
                    break;
                case DATAELEMENT_PREVIOUS_EVENT:
                    ruleVariables.add(RuleVariablePreviousEvent.create(name, de.uid(), mimeType));
                    break;
                case CALCULATED_VALUE:
                    String variable = "";
                    if (de != null || attr != null) {
                        variable = de != null ? de.uid() : attr.uid();
                    }
                    ruleVariables.add(RuleVariableCalculatedValue.create(name, variable != null ? variable : "", mimeType));
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported variable " +
                            "source type: " + prv.programRuleVariableSourceType().name());
            }
        }

        return ruleVariables;
    }

    @NonNull
    private static RuleValueType convertType(@NonNull ValueType valueType) {
        if (valueType.isInteger() || valueType.isNumeric()) {
            return RuleValueType.NUMERIC;
        } else if (valueType.isBoolean()) {
            return RuleValueType.BOOLEAN;
        } else {
            return RuleValueType.TEXT;
        }
    }

}
