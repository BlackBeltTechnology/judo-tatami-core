package hu.blackbelt.judo.tatami.esm2psm;

import static hu.blackbelt.judo.meta.esm.namespace.util.builder.NamespaceBuilders.newModelBuilder;
import static hu.blackbelt.judo.meta.esm.runtime.EsmEpsilonValidator.calculateEsmValidationScriptURI;
import static hu.blackbelt.judo.meta.esm.runtime.EsmEpsilonValidator.validateEsm;
import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.buildEsmModel;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newBooleanTypeBuilder;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newCustomTypeBuilder;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newDateTypeBuilder;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newEnumerationMemberBuilder;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newEnumerationTypeBuilder;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newNumericTypeBuilder;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newPasswordTypeBuilder;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newStringTypeBuilder;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newTimestampTypeBuilder;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newXMLTypeBuilder;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.calculatePsmValidationScriptURI;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.validatePsm;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.buildPsmModel;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.SaveArguments.psmSaveArgumentsBuilder;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.calculateEsm2PsmTransformationScriptURI;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.executeEsm2PsmTransformation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.measure.DurationType;
import hu.blackbelt.judo.meta.esm.namespace.Model;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.esm.type.BooleanType;
import hu.blackbelt.judo.meta.esm.type.CustomType;
import hu.blackbelt.judo.meta.esm.type.DateType;
import hu.blackbelt.judo.meta.esm.type.EnumerationType;
import hu.blackbelt.judo.meta.esm.type.NumericType;
import hu.blackbelt.judo.meta.esm.type.PasswordType;
import hu.blackbelt.judo.meta.esm.type.StringType;
import hu.blackbelt.judo.meta.esm.type.TimestampType;
import hu.blackbelt.judo.meta.esm.type.XMLType;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EsmType2PsmTypeTest {

    private final String TEST_SOURCE_MODEL_NAME = "urn:test.judo-meta-esm";
    private final String TEST = "test";
    private final String TARGET_TEST_CLASSES = "target/test-classes";

    Log slf4jlog;
    EsmModel esmModel;

    String testName;
    Map<EObject, List<EObject>> resolvedTrace;
    PsmModel psmModel;
    Esm2PsmTransformationTrace esm2PsmTransformationTrace;

    @BeforeEach
    void setUp() throws Exception {
        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading ESM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        esmModel = buildEsmModel()
                .uri(URI.createURI(TEST_SOURCE_MODEL_NAME))
                .name(TEST)
                .build();

        // Create empty PSM model
        psmModel = buildPsmModel()
                .name(TEST)
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        final String traceFileName = testName + "-esm2psm.model";

        // Saving trace map
        esm2PsmTransformationTrace.save(new File(TARGET_TEST_CLASSES, traceFileName));

        // Loading trace map
        Esm2PsmTransformationTrace esm2PsmTransformationTraceLoaded = Esm2PsmTransformationTrace.fromModelsAndTrace(
                TEST, esmModel, psmModel, new File(TARGET_TEST_CLASSES, traceFileName));

        // Resolve serialized URI's as EObject map
        resolvedTrace = esm2PsmTransformationTraceLoaded.getTransformationTrace();

        // Printing trace
        for (EObject e : resolvedTrace.keySet()) {
            for (EObject t : resolvedTrace.get(e)) {
                log.trace(e.toString() + " -> " + t.toString());
            }
        }

        psmModel.savePsmModel(psmSaveArgumentsBuilder().file(new File(TARGET_TEST_CLASSES, testName + "-psm.model")));
    }

    private void transform() throws Exception {
    	
    	assertTrue(esmModel.isValid());
    	validateEsm(new Slf4jLog(log), esmModel, calculateEsmValidationScriptURI());
   
        // Make transformation which returns the trace with the serialized URI's
        esm2PsmTransformationTrace = executeEsm2PsmTransformation(
                esmModel,
                psmModel,
                new Slf4jLog(log),
                calculateEsm2PsmTransformationScriptURI());
        assertTrue(psmModel.isValid());
        validatePsm(new Slf4jLog(log), psmModel, calculatePsmValidationScriptURI());
    }

    @Test
    void testCreateXMLType() throws Exception {
        testName = "CreateXMLType";

        XMLType xmlType = newXMLTypeBuilder().withName("xml").withXmlNamespace("namespace").withXmlElement("element").build();
        
        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(xmlType)
                .build();

        esmModel.addContent(model);
        transform();

        final Optional<hu.blackbelt.judo.meta.psm.type.XMLType> psmXmlType = allPsm(hu.blackbelt.judo.meta.psm.type.XMLType.class)
                .findAny();
        final Optional<hu.blackbelt.judo.meta.psm.namespace.Model> psmModel = allPsm(hu.blackbelt.judo.meta.psm.namespace.Model.class)
                .findAny();

        assertTrue(psmXmlType.isPresent());
        assertThat(psmModel.get().getName(), IsEqual.equalTo(psmXmlType.get().getNamespace().getName()));
        assertThat(psmXmlType.get().getName(), IsEqual.equalTo(xmlType.getName()));
        assertThat(psmXmlType.get().getXmlNamespace(), IsEqual.equalTo(xmlType.getXmlNamespace()));
        assertThat(psmXmlType.get().getXmlElement(), IsEqual.equalTo(xmlType.getXmlElement()));
    }
    
    @Test
    void testCreateCustomType() throws Exception {
        testName = "CreateCustomType";

        CustomType custom = newCustomTypeBuilder().withName("custom").build();
        
        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(custom)
                .build();

        esmModel.addContent(model);
        transform();

        final Optional<hu.blackbelt.judo.meta.psm.type.CustomType> psmCustomType = allPsm(hu.blackbelt.judo.meta.psm.type.CustomType.class)
                .findAny();
        final Optional<hu.blackbelt.judo.meta.psm.namespace.Model> psmModel = allPsm(hu.blackbelt.judo.meta.psm.namespace.Model.class)
                .findAny();

        assertTrue(psmCustomType.isPresent());
        assertThat(psmModel.get().getName(), IsEqual.equalTo(psmCustomType.get().getNamespace().getName()));
        assertThat(psmCustomType.get().getName(), IsEqual.equalTo(custom.getName()));
    }
    
    @Test
    void testCreatePasswordType() throws Exception {
        testName = "CreatePasswordType";

        PasswordType password = newPasswordTypeBuilder().withName("password").build();
        
        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(password)
                .build();

        esmModel.addContent(model);
        transform();

        final Optional<hu.blackbelt.judo.meta.psm.type.PasswordType> psmPasswordType = allPsm(hu.blackbelt.judo.meta.psm.type.PasswordType.class)
                .findAny();
        final Optional<hu.blackbelt.judo.meta.psm.namespace.Model> psmModel = allPsm(hu.blackbelt.judo.meta.psm.namespace.Model.class)
                .findAny();

        assertTrue(psmPasswordType.isPresent());
        assertThat(psmModel.get().getName(), IsEqual.equalTo(psmPasswordType.get().getNamespace().getName()));
        assertThat(psmPasswordType.get().getName(), IsEqual.equalTo(password.getName()));
    }
    
    @Test
    void testCreateStringType() throws Exception {
        testName = "CreateStringType";

        StringType string = newStringTypeBuilder().withName("string")
        		.withMaxLength(256)
        		.withRegExp(".*")
        		.build();
        
        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(string)
                .build();

        esmModel.addContent(model);
        transform();

        final Optional<hu.blackbelt.judo.meta.psm.type.StringType> psmStringType = allPsm(hu.blackbelt.judo.meta.psm.type.StringType.class)
                .findAny();
        final Optional<hu.blackbelt.judo.meta.psm.namespace.Model> psmModel = allPsm(hu.blackbelt.judo.meta.psm.namespace.Model.class)
                .findAny();

        assertTrue(psmStringType.isPresent());
        assertThat(psmModel.get().getName(), IsEqual.equalTo(psmStringType.get().getNamespace().getName()));
        assertThat(psmStringType.get().getName(), IsEqual.equalTo(string.getName()));
    }
    
    @Test
    void testCreateNumericType() throws Exception {
        testName = "CreateNumericType";

        NumericType numeric = newNumericTypeBuilder().withName("numeric")
        		.withPrecision(2)
        		.withScale(1)
        		.build();
        
        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(numeric)
                .build();

        esmModel.addContent(model);
        transform();

        final Optional<hu.blackbelt.judo.meta.psm.type.NumericType> psmNumericType = allPsm(hu.blackbelt.judo.meta.psm.type.NumericType.class)
                .findAny();
        final Optional<hu.blackbelt.judo.meta.psm.namespace.Model> psmModel = allPsm(hu.blackbelt.judo.meta.psm.namespace.Model.class)
                .findAny();

        assertTrue(psmNumericType.isPresent());
        assertThat(psmModel.get().getName(), IsEqual.equalTo(psmNumericType.get().getNamespace().getName()));
        assertThat(psmNumericType.get().getName(), IsEqual.equalTo(numeric.getName()));
        assertThat(psmNumericType.get().getPrecision(), IsEqual.equalTo(numeric.getPrecision()));
        assertThat(psmNumericType.get().getScale(), IsEqual.equalTo(numeric.getScale()));
    }
    
    @Test
    void testCreateBooleanType() throws Exception {
        testName = "CreateBooleanType";

        BooleanType booleanType = newBooleanTypeBuilder().withName("boolean").build();
        
        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(booleanType)
                .build();

        esmModel.addContent(model);
        transform();

        final Optional<hu.blackbelt.judo.meta.psm.type.BooleanType> psmBooleanType = allPsm(hu.blackbelt.judo.meta.psm.type.BooleanType.class)
                .findAny();
        final Optional<hu.blackbelt.judo.meta.psm.namespace.Model> psmModel = allPsm(hu.blackbelt.judo.meta.psm.namespace.Model.class)
                .findAny();

        assertTrue(psmBooleanType.isPresent());
        assertThat(psmModel.get().getName(), IsEqual.equalTo(psmBooleanType.get().getNamespace().getName()));
        assertThat(psmBooleanType.get().getName(), IsEqual.equalTo(booleanType.getName()));
    }
    
    @Test
    void testCreateDateType() throws Exception {
        testName = "CreateDateType";

        DateType dateType = newDateTypeBuilder().withName("date").build();
        
        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(dateType)
                .build();

        esmModel.addContent(model);
        transform();

        final Optional<hu.blackbelt.judo.meta.psm.type.DateType> psmDateType = allPsm(hu.blackbelt.judo.meta.psm.type.DateType.class)
                .findAny();
        final Optional<hu.blackbelt.judo.meta.psm.namespace.Model> psmModel = allPsm(hu.blackbelt.judo.meta.psm.namespace.Model.class)
                .findAny();

        assertTrue(psmDateType.isPresent());
        assertThat(psmModel.get().getName(), IsEqual.equalTo(psmDateType.get().getNamespace().getName()));
        assertThat(psmDateType.get().getName(), IsEqual.equalTo(dateType.getName()));
    }

    @Test
    void testCreateTimestampType() throws Exception {
        testName = "CreateTimestampType";

        TimestampType timestamp = newTimestampTypeBuilder().withName("timestamp").withBaseUnit(DurationType.HOUR)
        		.build();
        
        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(timestamp)
                .build();

        esmModel.addContent(model);
        transform();

        final Optional<hu.blackbelt.judo.meta.psm.type.TimestampType> psmTimestampType = allPsm(hu.blackbelt.judo.meta.psm.type.TimestampType.class)
                .findAny();
        final Optional<hu.blackbelt.judo.meta.psm.namespace.Model> psmModel = allPsm(hu.blackbelt.judo.meta.psm.namespace.Model.class)
                .findAny();

        assertTrue(psmTimestampType.isPresent());
        assertThat(psmModel.get().getName(), IsEqual.equalTo(psmTimestampType.get().getNamespace().getName()));
        assertThat(psmTimestampType.get().getName(), IsEqual.equalTo(timestamp.getName()));
        assertThat(psmTimestampType.get().getBaseUnit(), IsEqual.equalTo(hu.blackbelt.judo.meta.psm.measure.DurationType.HOUR));
    }
    
    @Test
    void testCreateEnumerationType() throws Exception {
        testName = "CreateEnumerationType";

        EnumerationType enumeration = newEnumerationTypeBuilder().withName("enum")
        		.withMembers(ImmutableList.of(
        				newEnumerationMemberBuilder().withName("m1").withOrdinal(1).build(),
        				newEnumerationMemberBuilder().withName("m2").withOrdinal(2).build()
        				))
        		.build();
        
        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(enumeration)
                .build();

        esmModel.addContent(model);
        transform();

        final Optional<hu.blackbelt.judo.meta.psm.type.EnumerationType> psmEnumType = allPsm(hu.blackbelt.judo.meta.psm.type.EnumerationType.class)
                .findAny();
        final Optional<hu.blackbelt.judo.meta.psm.namespace.Model> psmModel = allPsm(hu.blackbelt.judo.meta.psm.namespace.Model.class)
                .findAny();

        assertTrue(psmEnumType.isPresent());
        assertThat(psmModel.get().getName(), IsEqual.equalTo(psmEnumType.get().getNamespace().getName()));
        assertThat(psmEnumType.get().getName(), IsEqual.equalTo(enumeration.getName()));
    }
    
    @Test
    void testCreateEnumerationMember() throws Exception {
        testName = "CreateEnumerationMember";

        EnumerationType enumeration = newEnumerationTypeBuilder().withName("enum")
        		.withMembers(ImmutableList.of(
        				newEnumerationMemberBuilder().withName("m1").withOrdinal(1).build(),
        				newEnumerationMemberBuilder().withName("m2").withOrdinal(2).build()
        				))
        		.build();
        
        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(enumeration)
                .build();

        esmModel.addContent(model);
        transform();

        final Optional<hu.blackbelt.judo.meta.psm.type.EnumerationType> psmEnumType = allPsm(hu.blackbelt.judo.meta.psm.type.EnumerationType.class)
                .findAny();

        assertTrue(psmEnumType.isPresent());
        assertThat(psmEnumType.get().getMembers().size(), IsEqual.equalTo(enumeration.getMembers().size()));
        assertThat(psmEnumType.get().getMembers().get(0).getName(), IsEqual.equalTo(enumeration.getMembers().get(0).getName()));
        assertThat(psmEnumType.get().getMembers().get(1).getName(), IsEqual.equalTo(enumeration.getMembers().get(1).getName()));
    }
    
    static <T> Stream<T> asStream(Iterator<T> sourceIterator, boolean parallel) {
        Iterable<T> iterable = () -> sourceIterator;
        return StreamSupport.stream(iterable.spliterator(), parallel);
    }

    <T> Stream<T> allPsm() {
        return asStream((Iterator<T>) psmModel.getResourceSet().getAllContents(), false);
    }

    private <T> Stream<T> allPsm(final Class<T> clazz) {
        return allPsm().filter(e -> clazz.isAssignableFrom(e.getClass())).map(e -> (T) e);
    }
}
