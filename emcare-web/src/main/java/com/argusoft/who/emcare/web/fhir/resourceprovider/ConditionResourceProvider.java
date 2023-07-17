package com.argusoft.who.emcare.web.fhir.resourceprovider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import com.argusoft.who.emcare.web.common.constant.CommonConstant;
import com.argusoft.who.emcare.web.fhir.service.ConditionResourceService;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConditionResourceProvider implements IResourceProvider {
    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Condition.class;
    }

    private final FhirContext fhirCtx = FhirContext.forR4();
    private final IParser parser = fhirCtx.newJsonParser().setPrettyPrint(false);

    @Autowired
    ConditionResourceService conditionResourceService;

    @Create
    public MethodOutcome createCondition(@ResourceParam Condition condition) {
        conditionResourceService.saveResource(condition);
        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(CommonConstant.CONDITION, condition.getId(), "1"));
        retVal.setResource(condition);
        return retVal;
    }

    @Read()
    public Condition getResourceById(@IdParam IdType theId) {
        return conditionResourceService.getResourceById(theId.getIdPart());
    }

    @Update
    public MethodOutcome updateConditionResource(@IdParam IdType theId, @ResourceParam Condition condition) {
        return conditionResourceService.updateConditionResource(theId, condition);
    }

    @Search()
    public List<Condition> getAllCondition(
            @OptionalParam(name = CommonConstant.RESOURCE_LAST_UPDATED_AT) DateParam theDate,
            @OptionalParam(name = CommonConstant.RESOURCE_CONTENT) String searchText) {
        return conditionResourceService.getAllCondition(theDate, searchText);
    }

    @Search()
    public List<Condition> getConditionByPatientId(@OptionalParam(name = CommonConstant.RESOURCE_ID) String theId) {
        return conditionResourceService.getByPatientId(theId);
    }

    @Search(allowUnknownParams = true)
    public Bundle getConditionDataForGoogleFhirDataPipes(
            @RequiredParam(name = CommonConstant.SUMMARY) StringAndListParam type,
            @OptionalParam(name = "_count") StringAndListParam count,
            @OptionalParam(name = "_total") String total) {
        String x = type.getValuesAsQueryTokens().get(0).getValuesAsQueryTokens().get(0).getValue();
        String _count = "10";
        if(count != null) {
            _count = count.getValuesAsQueryTokens().get(0).getValuesAsQueryTokens().get(0).getValue();
        }
        return conditionResourceService.getConditionDataForGoogleFhirDataPipes(
                x,
                Integer.parseInt(_count),
                total
        );
    }
}
