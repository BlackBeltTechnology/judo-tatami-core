package hu.blackbelt.judo.tatami.esm2psm;

import com.google.common.collect.ImmutableSet;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.measure.DurationType;
import hu.blackbelt.judo.meta.esm.measure.Measure;
import hu.blackbelt.judo.meta.esm.namespace.Model;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.esm.structure.DataMember;
import hu.blackbelt.judo.meta.esm.structure.EntityType;
import hu.blackbelt.judo.meta.esm.structure.MemberType;
import hu.blackbelt.judo.meta.esm.structure.TransferObjectType;
import hu.blackbelt.judo.meta.esm.type.*;
import hu.blackbelt.judo.meta.psm.PsmUtils;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.psm.service.UnmappedTransferObjectType;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static hu.blackbelt.judo.meta.esm.measure.util.builder.MeasureBuilders.newDurationUnitBuilder;
import static hu.blackbelt.judo.meta.esm.measure.util.builder.MeasureBuilders.newMeasureBuilder;
import static hu.blackbelt.judo.meta.esm.namespace.util.builder.NamespaceBuilders.newModelBuilder;
import static hu.blackbelt.judo.meta.esm.runtime.EsmEpsilonValidator.calculateEsmValidationScriptURI;
import static hu.blackbelt.judo.meta.esm.runtime.EsmEpsilonValidator.validateEsm;
import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.buildEsmModel;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.*;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.*;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.calculatePsmValidationScriptURI;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.validatePsm;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.SaveArguments.psmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.buildPsmModel;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.calculateEsm2PsmTransformationScriptURI;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.executeEsm2PsmTransformation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class QueryCustomizerTest {

    private final String TEST_SOURCE_MODEL_NAME = "urn:test.judo-meta-esm";
    private final String TEST = "test";
    private final String TARGET_TEST_CLASSES = "target/test-classes";

    private static final String EXTENSION_PACKAGE_NAME = "_extension";
    private static final String QUERY_CUSTOMIZER_NAME_PREFIX = "_QueryCustomizer";

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
        esmModel = buildEsmModel().uri(URI.createURI(TEST_SOURCE_MODEL_NAME)).name(TEST).build();

        // Create empty PSM model
        psmModel = buildPsmModel().name(TEST).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        final String traceFileName = testName + "-esm2psm.model";

        // Saving trace map
        esm2PsmTransformationTrace.save(new File(TARGET_TEST_CLASSES, traceFileName));

        // Loading trace map
        Esm2PsmTransformationTrace esm2PsmTransformationTraceLoaded = Esm2PsmTransformationTrace
                .fromModelsAndTrace(TEST, esmModel, psmModel, new File(TARGET_TEST_CLASSES, traceFileName));

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
        esm2PsmTransformationTrace = executeEsm2PsmTransformation(esmModel, psmModel, new Slf4jLog(log),
                calculateEsm2PsmTransformationScriptURI());

        assertTrue(psmModel.isValid());
        validatePsm(new Slf4jLog(log), psmModel, calculatePsmValidationScriptURI());
    }

    @Test
    void testQueryCustomizer() throws Exception {
        testName = "QueryCustomizer";

        final String MODEL_NAME = "Model";
        final String PERSON_TYPE_NAME = "Person";
        final String PERSON_DTO_TYPE_NAME = "PersonDTO";
        final String PERSON_TYPE_NAME_2 = "Employee";
        final String PERSON_DTO_TYPE_NAME_2 = "EmployeeDTO";

        final Measure time = newMeasureBuilder()
                .withName("Time")
                .withUnits(newDurationUnitBuilder().withName("Day").withRateDividend(1.0).withRateDivisor(1.0).withUnitType(DurationType.DAY))
                .build();

        final StringType stringType = newStringTypeBuilder().withName("String").withMaxLength(255).build();
        final NumericType integerType = newNumericTypeBuilder().withName("Integer").withPrecision(9).withScale(0).build();
        final NumericType doubleType = newNumericTypeBuilder().withName("Double").withPrecision(15).withScale(4).build();
        final BooleanType booleanType = newBooleanTypeBuilder().withName("Boolean").build();
        final DateType dateType = newDateTypeBuilder().withName("Date").build();
        final TimestampType timestampType = newTimestampTypeBuilder().withName("Timestamp").build();
        final EnumerationType sexType = newEnumerationTypeBuilder().withName("Sex")
                .withMembers(Arrays.asList(
                        newEnumerationMemberBuilder().withName("MALE").withOrdinal(0).build(),
                        newEnumerationMemberBuilder().withName("FEMALE").withOrdinal(1).build()
                ))
                .build();

        final DataMember nameOfPerson = newDataMemberBuilder()
                .withName("name")
                .withDataType(stringType)
                .withRequired(true)
                .withMemberType(MemberType.STORED)
                .build();
        final DataMember birthDateOfPerson = newDataMemberBuilder()
                .withName("birthDate")
                .withDataType(dateType)
                .withMemberType(MemberType.STORED)
                .build();
        final DataMember ageAt2020OfPerson = newDataMemberBuilder()
                .withName("ageAt2020")
                .withDataType(integerType)
                .withMemberType(MemberType.DERIVED)
                .withGetterExpression("`2021-01-01`!elapsedTimeFrom(self.birthDate) % 365.25")
                .build();
        final DataMember sexOfPerson = newDataMemberBuilder()
                .withName("sex")
                .withDataType(sexType)
                .withMemberType(MemberType.STORED)
                .build();
        final DataMember firstDayAtWork = newDataMemberBuilder()
                .withName("firstDayAtWork")
                .withDataType(dateType)
                .withMemberType(MemberType.STORED)
                .build();
        final EntityType e = newEntityTypeBuilder()
                .withName(PERSON_TYPE_NAME)
                .withAttributes(Arrays.asList(nameOfPerson, birthDateOfPerson, ageAt2020OfPerson, sexOfPerson))
                .build();
        useEntityType(e)
                .withMapping(newMappingBuilder().withTarget(e).build())
                .build();
        
        final EntityType e2 = newEntityTypeBuilder()
                .withName(PERSON_TYPE_NAME_2)
                .withGeneralizations(newGeneralizationBuilder().withTarget(e).build())
                .withAttributes(firstDayAtWork)
                .build();
        useEntityType(e2)
                .withMapping(newMappingBuilder().withTarget(e2).build())
                .build();

        final TransferObjectType t = newTransferObjectTypeBuilder()
                .withMapping(newMappingBuilder().withTarget(e).build())
                .withName(PERSON_DTO_TYPE_NAME)
                .withAttributes(newDataMemberBuilder()
                        .withName("name")
                        .withDataType(stringType)
                        .withRequired(true)
                        .withMemberType(MemberType.MAPPED)
                        .withBinding(nameOfPerson)
                        .build())
                .withAttributes(newDataMemberBuilder()
                        .withName("birthDate")
                        .withDataType(dateType)
                        .withMemberType(MemberType.MAPPED)
                        .withBinding(birthDateOfPerson)
                        .build())
                .withAttributes(newDataMemberBuilder()
                        .withName("age")
                        .withDataType(integerType)
                        .withMemberType(MemberType.MAPPED)
                        .withBinding(ageAt2020OfPerson)
                        .build())
                .withAttributes(newDataMemberBuilder()
                        .withName("sex")
                        .withDataType(sexType)
                        .withMemberType(MemberType.MAPPED)
                        .withBinding(sexOfPerson)
                        .build())
                .build();
        
        final TransferObjectType t2 = newTransferObjectTypeBuilder()
                .withMapping(newMappingBuilder().withTarget(e2).build())
                .withName(PERSON_DTO_TYPE_NAME_2)
                .withGeneralizations(newGeneralizationBuilder().withTarget(t).build())
                .withAttributes(newDataMemberBuilder()
                        .withName("firstDayAtWork")
                        .withDataType(dateType)
                        .withMemberType(MemberType.MAPPED)
                        .withBinding(firstDayAtWork)
                        .build())
                .build();

        final Model model = newModelBuilder().withName(MODEL_NAME)
                .withElements(Arrays.asList(time, stringType, integerType, doubleType, booleanType, dateType, timestampType, sexType, e, e2, t, t2)).build();

        esmModel.addContent(model);

        transform();

        final Optional<UnmappedTransferObjectType> eCustomizer = allPsm(UnmappedTransferObjectType.class)
                .filter(u -> (QUERY_CUSTOMIZER_NAME_PREFIX + PERSON_TYPE_NAME).equals(u.getName()))
                .findAny();
        assertTrue(eCustomizer.isPresent());
        assertThat(PsmUtils.namespaceElementToString(eCustomizer.get()), equalTo(MODEL_NAME + "::" + EXTENSION_PACKAGE_NAME + "::" + QUERY_CUSTOMIZER_NAME_PREFIX + PERSON_TYPE_NAME));
        assertThat(eCustomizer.get().getRelations().stream().map(a -> a.getName()).collect(Collectors.toSet()), equalTo(ImmutableSet.of("name", "birthDate", "ageAt2020", "sex", "_orderBy", "_seek")));

        final Optional<UnmappedTransferObjectType> tCustomizer = allPsm(UnmappedTransferObjectType.class)
                .filter(u -> (QUERY_CUSTOMIZER_NAME_PREFIX + PERSON_DTO_TYPE_NAME).equals(u.getName()))
                .findAny();
        assertTrue(tCustomizer.isPresent());
        assertThat(PsmUtils.namespaceElementToString(tCustomizer.get()), equalTo(MODEL_NAME + "::" + EXTENSION_PACKAGE_NAME + "::" + QUERY_CUSTOMIZER_NAME_PREFIX + PERSON_DTO_TYPE_NAME));
        assertThat(tCustomizer.get().getRelations().stream().map(a -> a.getName()).collect(Collectors.toSet()), equalTo(ImmutableSet.of("name", "birthDate", "age", "sex", "_orderBy", "_seek")));
        
        final Optional<UnmappedTransferObjectType> e2Customizer = allPsm(UnmappedTransferObjectType.class)
                .filter(u -> (QUERY_CUSTOMIZER_NAME_PREFIX + PERSON_TYPE_NAME_2).equals(u.getName()))
                .findAny();
        assertTrue(e2Customizer.isPresent());
        assertThat(PsmUtils.namespaceElementToString(e2Customizer.get()), equalTo(MODEL_NAME + "::" + EXTENSION_PACKAGE_NAME + "::" + QUERY_CUSTOMIZER_NAME_PREFIX + PERSON_TYPE_NAME_2));
        assertThat(e2Customizer.get().getRelations().stream().map(a -> a.getName()).collect(Collectors.toSet()), equalTo(ImmutableSet.of("name", "birthDate", "ageAt2020", "sex", "_orderBy", "_seek", "firstDayAtWork")));

        final Optional<UnmappedTransferObjectType> t2Customizer = allPsm(UnmappedTransferObjectType.class)
                .filter(u -> (QUERY_CUSTOMIZER_NAME_PREFIX + PERSON_DTO_TYPE_NAME_2).equals(u.getName()))
                .findAny();
        assertTrue(t2Customizer.isPresent());
        assertThat(PsmUtils.namespaceElementToString(t2Customizer.get()), equalTo(MODEL_NAME + "::" + EXTENSION_PACKAGE_NAME + "::" + QUERY_CUSTOMIZER_NAME_PREFIX + PERSON_DTO_TYPE_NAME_2));
        assertThat(t2Customizer.get().getRelations().stream().map(a -> a.getName()).collect(Collectors.toSet()), equalTo(ImmutableSet.of("name", "birthDate", "age", "sex", "_orderBy", "_seek", "firstDayAtWork")));
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
