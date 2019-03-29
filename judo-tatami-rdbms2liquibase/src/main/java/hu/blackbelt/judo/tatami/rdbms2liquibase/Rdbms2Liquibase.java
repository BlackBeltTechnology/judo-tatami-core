package hu.blackbelt.judo.tatami.rdbms2liquibase;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import org.eclipse.emf.ecore.resource.ResourceSet;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;

public class Rdbms2Liquibase {

    public static void executeRdbms2LiquibaseTransformation(ResourceSet resourceSet, RdbmsModel rdbmsModel, hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel liquibaseModel, Log log,
                                                            File scriptDir, String dialect) throws Exception {

        // Execution context
        ExecutionContext executionContext = executionContextBuilder()
                .log(log)
                .resourceSet(resourceSet)
                .modelContexts(ImmutableList.of(
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("RDBMS")
                                .resource(rdbmsModel.getResourceSet().getResource(rdbmsModel.getUri(), false))
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("LIQUIBASE")
                                .resource(liquibaseModel.getResourceSet().getResource(liquibaseModel.getUri(), false))
                                .build()))
                .sourceDirectory(scriptDir)
                .build();

        // run the model / metadata loading
        executionContext.load();

        // Transformation script
        executionContext.executeProgram(
                etlExecutionContextBuilder()
                        .source("rdbmsToLiquibase.etl")
                        .parameters(ImmutableList.of(
                                ProgramParameter.programParameterBuilder().name("dialect").value(dialect).build()
                        ))
                        .build());

        executionContext.commit();
        executionContext.close();
    }

    private static final String LIQUIBASE_FIX_NS_XSLT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<xsl:stylesheet \n" +
            "\txmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" \n" +
            "\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "\tversion=\"1.0\">\n" +
            "\t\n" +
            "\t<xsl:output method=\"xml\" version=\"1.0\" encoding=\"UTF-8\" indent=\"yes\"/>\n" +
            "\t<xsl:strip-space elements=\"*\"/>\n" +
            "\t\n" +
            "\t<xsl:template match=\"/\">\n" +
            "\t    <databaseChangeLog \n" +
            "\t    \txmlns=\"http://www.liquibase.org/xml/ns/dbchangelog\" \n" +
            "\t    \txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
            "\t    \txsi:schemaLocation=\"http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.1.xsd\">\n" +
            "\t        <xsl:copy-of select=\"node()/*\"/>\n" +
            "\t    </databaseChangeLog>\n" +
            "\t</xsl:template>\n" +
            "</xsl:stylesheet>";

    public static InputStream transformXmlDocumentWithXslt(InputStream xmlDocument) throws TransformerException {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        transformer = tFactory.newTransformer(new StreamSource(LIQUIBASE_FIX_NS_XSLT));
        StreamSource xmlSource = new StreamSource(xmlDocument);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(xmlSource, new StreamResult(baos));
        return new ByteArrayInputStream(baos.toByteArray());
    }
}
