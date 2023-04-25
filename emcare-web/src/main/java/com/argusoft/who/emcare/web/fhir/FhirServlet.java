package com.argusoft.who.emcare.web.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.openapi.OpenApiInterceptor;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import com.argusoft.who.emcare.web.fhir.resourceprovider.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/*")
public class FhirServlet extends RestfulServer {

    @Autowired
    private PatientResourceProvider patientResourceProvider;

    @Autowired
    private QuestionnaireResourceProvider questionnaireResourceProvider;

    @Autowired
    private LocationResourceProvider locationResourceProvider;

    @Autowired
    private OrganizationResourceProvider organizationResourceProvider;

    @Autowired
    private BundleResourceProvider bundleResourceProvider;

    @Autowired
    private PlanDefinitionResourceProvider planDefinitionResourceProvider;

    @Autowired
    private ValueSetResourceProvider valueSetResourceProvider;

    @Autowired
    private StructureMapResourceProvider structureMapResourceProvider;

    @Autowired
    private StructureDefinitionResourceProvider structureDefinitionResourceProvider;

    @Autowired
    private CodeSystemResourceProvider codeSystemResourceProvider;

    @Autowired
    private LibraryResourceProvider libraryResourceProvider;

    @Autowired
    OperationDefinitionResourceProvider operationDefinitionResourceProvider;

    @Autowired
    MedicationResourceProvider medicationResourceProvider;

    @Autowired
    ActivityDefinitionResourceProvider activityDefinitionResourceProvider;

    @Autowired
    EncounterResourceProvider encounterResourceProvider;

    @Autowired
    ObservationResourceProvider observationResourceProvider;

    @Autowired
    RelatedPersonResourceProvider relatedPersonResourceProvider;

    @Autowired
    ConditionResourceProvider conditionResourceProvider;

    @Autowired
    BinaryResourceProvider binaryResourceProvider;

    @Override
    protected void initialize() throws ServletException {
        super.initialize();
        setFhirContext(FhirContext.forR4());
        // Registering OpenApi Interceptor for swagger ui
        OpenApiInterceptor openApiInterceptor = new OpenApiInterceptor();
        registerInterceptor(openApiInterceptor);
    }

    @PostConstruct
    public void setPostResourceProviders() {
        List<IResourceProvider> resourceProviders = new ArrayList<>();
        resourceProviders.add(patientResourceProvider);
        resourceProviders.add(questionnaireResourceProvider);
        resourceProviders.add(locationResourceProvider);
        resourceProviders.add(organizationResourceProvider);
        resourceProviders.add(bundleResourceProvider);
        resourceProviders.add(planDefinitionResourceProvider);
        resourceProviders.add(valueSetResourceProvider);
        resourceProviders.add(structureMapResourceProvider);
        resourceProviders.add(structureDefinitionResourceProvider);
        resourceProviders.add(codeSystemResourceProvider);
        resourceProviders.add(medicationResourceProvider);
        resourceProviders.add(activityDefinitionResourceProvider);
        resourceProviders.add(libraryResourceProvider);
        resourceProviders.add(operationDefinitionResourceProvider);
        resourceProviders.add(encounterResourceProvider);
        resourceProviders.add(observationResourceProvider);
        resourceProviders.add(relatedPersonResourceProvider);
        resourceProviders.add(conditionResourceProvider);
        resourceProviders.add(binaryResourceProvider);
        setResourceProviders(resourceProviders);
    }

}
