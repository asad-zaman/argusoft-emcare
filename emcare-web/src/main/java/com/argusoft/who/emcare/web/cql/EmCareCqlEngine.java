package com.argusoft.who.emcare.web.cql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.cqframework.cql.elm.execution.Library;
import org.opencds.cqf.cql.engine.execution.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Period;
import org.opencds.cqf.cql.engine.data.CompositeDataProvider;
import org.opencds.cqf.cql.engine.data.DataProvider;
import org.opencds.cqf.cql.engine.data.SystemDataProvider;
import org.opencds.cqf.cql.engine.model.BaseModelResolver;
import org.opencds.cqf.cql.engine.model.ModelResolver;
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider;
import org.cqframework.cql.cql2elm.CqlTranslator;
import org.opencds.cqf.cql.engine.retrieve.TerminologyAwareRetrieveProvider;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmCareCqlEngine {
    
    private LibraryLoader libraryLoader;
    
    @Value("${fhir.path}")
    private String fhirPath;

    public Object execute(String text) throws IOException {

        Library library = toLibrary("library Test version '1.0.0'\ndefine X:\n5+5");
        
//        ModelResolver modelResolver = new SystemDataProvider();
//        List<String> packageNames = new ArrayList<>();
//        packageNames.add("http://hl7.org/fhir");
//        modelResolver.setPackageNames(packageNames);
//        RetrieveProvider retrieveProvider = null;
//        
//        DataProvider dp = new CompositeDataProvider(modelResolver, retrieveProvider);
//        
//        Map<String, DataProvider> dataProviders = new HashMap<>();
//        dataProviders.put("http://hl7.org/fhir", dp);

        //CqlEngine engine = new CqlEngine(this.libraryLoader, dataProviders, null);
        CqlEngine engine = new CqlEngine(this.libraryLoader);
        
        Period pr = new Period();    
        pr.setStart(new Date());
        pr.setEnd(new Date());
        
        Encounter ec = new Encounter();
        ec.setPeriod(pr);
        
        Pair<String, Object> encounterParam = new MutablePair<>("Encounter", ec);
        
        EvaluationResult result = engine.evaluate("EmCareContactDataElements", encounterParam);

        return true;
    }

    private static Library toLibrary(String text) throws IOException {
        ModelManager modelManager = new ModelManager();
        LibraryManager libraryManager = new LibraryManager(modelManager);
        CqlTranslator cqlTranslator = CqlTranslator.fromText(text, modelManager, libraryManager);
        return JsonCqlLibraryReader.read(new StringReader(cqlTranslator.toJxson()));
    }
    
    @PostConstruct
    private void loadLibrary() throws IOException {
        
        File folder = new File(this.fhirPath + "/input/cql/");
        File[] listOfFiles = folder.listFiles();
        List<Library> libraryList = new ArrayList<Library>();    

        for (File file : listOfFiles) {
            if (file.isFile() && FilenameUtils.getExtension(file.getName()).equals("cql")) {
                Library library = toLibrary(this.readFile(file.getName()));
                libraryList.add(library);
            }
        }

        this.libraryLoader = new InMemoryLibraryLoader(libraryList);
        
    }
    
    private String readFile(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(this.fhirPath + "/input/cql/" + filename));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }    
    }

}
