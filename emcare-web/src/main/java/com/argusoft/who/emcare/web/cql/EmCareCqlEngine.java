package com.argusoft.who.emcare.web.cql;

import com.argusoft.who.emcare.web.fhir.service.EmcareResourceService;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.cqframework.cql.elm.execution.Library;
import org.hl7.fhir.r4.model.Patient;
import org.opencds.cqf.cql.engine.data.DataProvider;
import org.opencds.cqf.cql.engine.data.SystemDataProvider;
import org.opencds.cqf.cql.engine.execution.*;
import org.opencds.cqf.cql.engine.model.BaseModelResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class EmCareCqlEngine {

    private EmCareCqlEngine() {

    }

    @Autowired
    EmcareResourceService emcareResourceService;

    public static Object execute(String text) throws IOException {

        Library library = toLibrary("library Test version '1.0.0'\ndefine X:\n5+5");

        LibraryLoader libraryLoader = new InMemoryLibraryLoader(Collections.singleton(library));

        CqlEngine engine = new CqlEngine(libraryLoader);

        EvaluationResult result = engine.evaluate("Test");

        return result.forExpression("X");
    }

    public Object executePatient(String text) throws IOException {

        Library library = toLibrary(text);

        LibraryLoader libraryLoader = new InMemoryLibraryLoader(Collections.singleton(library));
        List<Patient> patientList = emcareResourceService.getAllPatientResources();
//        BaseModelResolver baseModelResolver = new SystemDataProvider();
//        baseModelResolver.resolveType(Patient.class);
//        Map<String, DataProvider> dataProvider = new HashMap<>();
//        dataProvider.put("Patient",baseModelResolver);
        CqlEngine engine = new CqlEngine(libraryLoader);

        System.out.println(patientList.get(0).toString());
        Map<String,Object> parameters = new HashMap<>();
        parameters.put("Patient",patientList.get(0).getIdElement().getValue());

        EvaluationResult result = engine.evaluate("emcareb7ltidangersigns" ,parameters);

        return result.forExpression("AgeInMonths");
    }

    private static Library toLibrary(String text) throws IOException {
        ModelManager modelManager = new ModelManager();
        LibraryManager libraryManager = new LibraryManager(modelManager);
        CqlTranslator cqlTranslator = CqlTranslator.fromText(text, modelManager, libraryManager);
        return JsonCqlLibraryReader.read(new StringReader(cqlTranslator.toJxson()));
    }

}
