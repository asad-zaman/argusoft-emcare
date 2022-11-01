package com.argusoft.who.emcare.web.cql;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.cqframework.cql.elm.execution.Library;
import org.opencds.cqf.cql.engine.execution.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;

public class EmCareCqlEngine {

    private EmCareCqlEngine() {

    }

    public static Object execute() throws IOException {

        Library library = toLibrary("library Test version '1.0.0'\ndefine X:\n5+5");

        LibraryLoader libraryLoader = new InMemoryLibraryLoader(Collections.singleton(library));

        CqlEngine engine = new CqlEngine(libraryLoader);

        EvaluationResult result = engine.evaluate("Test");

        return result.forExpression("X");
    }

    private static Library toLibrary(String text) throws IOException {
        ModelManager modelManager = new ModelManager();
        LibraryManager libraryManager = new LibraryManager(modelManager);
        CqlTranslator cqlTranslator = CqlTranslator.fromText(text, modelManager, libraryManager);
        return JsonCqlLibraryReader.read(new StringReader(cqlTranslator.toJxson()));
    }

}
