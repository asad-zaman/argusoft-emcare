package com.argusoft.who.emcare.web.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.IValidationSupport;
import ca.uhn.fhir.jpa.api.config.DaoConfig;
import ca.uhn.fhir.jpa.api.dao.DaoRegistry;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.api.dao.IFhirSystemDao;
import ca.uhn.fhir.jpa.provider.ValueSetOperationProvider;
import ca.uhn.fhir.rest.server.util.ISearchParamRegistry;
import org.hl7.fhir.instance.model.api.IBaseConformance;
import org.hl7.fhir.r4.model.PlanDefinition;
import org.opencds.cqf.ruler.api.MetadataExtender;
import org.opencds.cqf.ruler.capability.ExtensibleJpaCapabilityStatementProvider;
import org.opencds.cqf.ruler.config.ServerProperties;
import org.opencds.cqf.ruler.external.AppProperties;
import org.opencds.cqf.ruler.external.BaseJpaRestfulServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.MoreObjects.firstNonNull;

@WebServlet("/*")
public class FhirJpaServlet extends BaseJpaRestfulServer {

    @Autowired
    DaoRegistry daoRegistry;

    @Autowired
    DaoConfig myDaoConfig;

    @Autowired
    ISearchParamRegistry mySearchParamRegistry;

    @Autowired
    IFhirSystemDao myFhirSystemDao;

    @Autowired
    private IValidationSupport myValidationSupport;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    AppProperties myAppProperties;

    @Autowired
    ServerProperties myServerProperties;

    @Autowired
    ValueSetOperationProvider valueSetOperationProvider;

    @Autowired
    private IFhirResourceDao<PlanDefinition> planDefinitionDao;


    public FhirJpaServlet() {
        super();
    }

    @Override
    protected void initialize() throws ServletException {
        super.initialize();
        this.registerProvider(valueSetOperationProvider);
        setFhirContext(FhirContext.forR4());
        Map<String, MetadataExtender> extenders = applicationContext.getBeansOfType(MetadataExtender.class);

        List<MetadataExtender<IBaseConformance>> extenderList = extenders.values().stream()
                .map(x -> (MetadataExtender<IBaseConformance>) x).collect(Collectors.toList());

        WebApplicationContext myAppCtx = ContextLoaderListener.getCurrentWebApplicationContext();
        ExtensibleJpaCapabilityStatementProvider confProvider = new ExtensibleJpaCapabilityStatementProvider(this,
                myFhirSystemDao, myDaoConfig, mySearchParamRegistry, myValidationSupport, extenderList);
        confProvider.setImplementationDescription(firstNonNull(getImplementationDescription(), "CQF RULER R4 Server"));
        setServerConformanceProvider(confProvider);
    }

    @PostConstruct
    public void setDaoInDaoRegistry() {
        List<IFhirResourceDao> iFhirResourceDaos = new ArrayList<>();
        iFhirResourceDaos.add(planDefinitionDao);
        daoRegistry.setResourceDaos(iFhirResourceDaos);
    }
}
