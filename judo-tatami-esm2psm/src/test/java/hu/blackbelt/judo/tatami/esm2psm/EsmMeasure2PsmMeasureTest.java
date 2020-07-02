package hu.blackbelt.judo.tatami.esm2psm;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.namespace.Model;
import hu.blackbelt.judo.meta.esm.measure.DurationType;
import hu.blackbelt.judo.meta.esm.measure.DurationUnit;
import hu.blackbelt.judo.meta.esm.measure.Measure;
import hu.blackbelt.judo.meta.esm.measure.MeasureDefinitionTerm;
import hu.blackbelt.judo.meta.esm.measure.MeasuredType;
import hu.blackbelt.judo.meta.esm.measure.Unit;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;
import com.jcabi.aspects.Immutable;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static hu.blackbelt.judo.meta.esm.namespace.util.builder.NamespaceBuilders.newModelBuilder;
import static hu.blackbelt.judo.meta.esm.measure.util.builder.MeasureBuilders.*;
import static hu.blackbelt.judo.meta.esm.runtime.EsmEpsilonValidator.calculateEsmValidationScriptURI;
import static hu.blackbelt.judo.meta.esm.runtime.EsmEpsilonValidator.validateEsm;
import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.buildEsmModel;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.SaveArguments.psmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.calculatePsmValidationScriptURI;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.validatePsm;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.buildPsmModel;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.calculateEsm2PsmTransformationScriptURI;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.executeEsm2PsmTransformation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class EsmMeasure2PsmMeasureTest {

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
    void testCreateMeasure() throws Exception {
        testName = "CreateMeasure";

        Unit unit = newUnitBuilder().withName("base").withSymbol("b").build();
        Measure measure = newMeasureBuilder().withName("measure").withSymbol("m").withUnits(unit).build();
       
        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(measure)
                .build();

        esmModel.addContent(model);
        transform();

        final Optional<hu.blackbelt.judo.meta.psm.measure.Measure> psmMeasure = allPsm(hu.blackbelt.judo.meta.psm.measure.Measure.class)
                .findAny();

        assertTrue(psmMeasure.isPresent());
        assertThat(psmMeasure.get().getName(), IsEqual.equalTo(measure.getName()));
        assertThat(psmMeasure.get().getSymbol(), IsEqual.equalTo(measure.getSymbol()));
    }
    
    @Test
    void testCreateDerivedMeasure() throws Exception {
        testName = "CreateDerivedMeasure";
        
        Unit unit1 = newUnitBuilder().withName("unit1").withSymbol("u1").build();
        MeasureDefinitionTerm term1 = newMeasureDefinitionTermBuilder().withExponent(1).withUnit(unit1).build();
        Unit unit2 = newUnitBuilder().withName("unit2").withSymbol("u2").build();
        MeasureDefinitionTerm term2 = newMeasureDefinitionTermBuilder().withExponent(-1).withUnit(unit2).build();
        
        Measure measure1 = newMeasureBuilder().withName("measure1").withSymbol("m1")
        		.withUnits(unit1)
        		.build();
        Measure measure2 = newMeasureBuilder().withName("measure2").withSymbol("m2")
        		.withUnits(unit2)
        		.build();
        
        Measure derivedMeasure = newMeasureBuilder().withName("dmeasure").withSymbol("dm")
        		.withTerms(ImmutableList.of(term1,term2))
        		.build();
        
        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(ImmutableList.of(measure1,measure2,derivedMeasure))
                .build();

        esmModel.addContent(model);
        transform();

        final Optional<hu.blackbelt.judo.meta.psm.measure.DerivedMeasure> psmDerivedMeasure = allPsm(hu.blackbelt.judo.meta.psm.measure.DerivedMeasure.class)
                .findAny();

        assertTrue(psmDerivedMeasure.isPresent());
        assertThat(psmDerivedMeasure.get().getName(), IsEqual.equalTo(derivedMeasure.getName()));
        assertThat(psmDerivedMeasure.get().getSymbol(), IsEqual.equalTo(derivedMeasure.getSymbol()));
        assertEquals(2, psmDerivedMeasure.get().getTerms().size());
        
        final hu.blackbelt.judo.meta.psm.measure.MeasureDefinitionTerm psmTerm1 = psmDerivedMeasure.get().getTerms().get(0);
        final hu.blackbelt.judo.meta.psm.measure.MeasureDefinitionTerm psmTerm2 = psmDerivedMeasure.get().getTerms().get(1);
        assertTrue((psmTerm1.getExponent() == 1 && psmTerm2.getExponent() == -1) || (psmTerm1.getExponent() == -1 && psmTerm2.getExponent() == 1));
        assertTrue((psmTerm1.getUnit().getName().equals(unit1.getName()) && psmTerm2.getUnit().getName().equals(unit2.getName()))
        		|| (psmTerm1.getUnit().getName().equals(unit2.getName()) && psmTerm2.getUnit().getName().equals(unit1.getName())));
    }
    
    @Test
    void testCreateUnitInMeasure() throws Exception {
        testName = "CreateUnit";
        
        Unit unit = newUnitBuilder().withName("unit").withSymbol("u")
        			.withRateDivisor(2)
        			.withRateDividend(2)
        			.build();
        Measure measure = newMeasureBuilder().withName("measure").withSymbol("m")
        		.withUnits(unit)
        		.build();
        
        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(measure)
                .build();

        esmModel.addContent(model);
        transform();

        final Optional<hu.blackbelt.judo.meta.psm.measure.Unit> psmUnit = allPsm(hu.blackbelt.judo.meta.psm.measure.Unit.class)
                .findAny();

        assertTrue(psmUnit.isPresent());
        assertThat(psmUnit.get().getName(), IsEqual.equalTo(unit.getName()));
        assertThat(psmUnit.get().getSymbol(), IsEqual.equalTo(unit.getSymbol()));
        assertThat(psmUnit.get().getRateDividend(), IsEqual.equalTo(unit.getRateDividend()));
        assertThat(psmUnit.get().getRateDivisor(), IsEqual.equalTo(unit.getRateDivisor()));
        assertThat(psmUnit.get().getMeasure().getName(), IsEqual.equalTo(measure.getName()));
    }
    
    @Test
    void testCreateDurationUnitInMeasure() throws Exception {
        testName = "CreateDurationUnit";
        
        DurationUnit unit = newDurationUnitBuilder().withName("unit").withSymbol("u")
        			.withRateDivisor(2)
        			.withRateDividend(2)
        			.withUnitType(DurationType.DAY)
        			.build();
        Measure measure = newMeasureBuilder().withName("measure").withSymbol("m")
        		.withUnits(unit)
        		.build();
        
        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(measure)
                .build();

        esmModel.addContent(model);
        transform();

        final Optional<hu.blackbelt.judo.meta.psm.measure.DurationUnit> psmDurationUnit = allPsm(hu.blackbelt.judo.meta.psm.measure.DurationUnit.class)
                .findAny();

        assertTrue(psmDurationUnit.isPresent());
        assertThat(psmDurationUnit.get().getName(), IsEqual.equalTo(unit.getName()));
        assertThat(psmDurationUnit.get().getSymbol(), IsEqual.equalTo(unit.getSymbol()));
        assertThat(psmDurationUnit.get().getRateDividend(), IsEqual.equalTo(unit.getRateDividend()));
        assertThat(psmDurationUnit.get().getRateDivisor(), IsEqual.equalTo(unit.getRateDivisor()));
        assertThat(psmDurationUnit.get().getMeasure().getName(), IsEqual.equalTo(measure.getName()));
        assertThat(psmDurationUnit.get().getUnitType(), IsEqual.equalTo(hu.blackbelt.judo.meta.psm.measure.DurationType.DAY));
    }
    
    @Test
    void testCreateDurationUnitInDerivedMeasure() throws Exception {
        testName = "CreateDurationUnit";
        
        DurationUnit unit1 = newDurationUnitBuilder().withName("msec").withSymbol("ms")
    			.withRateDivisor(1)
    			.withRateDividend(1)
    			.withUnitType(DurationType.MILLISECOND)
        		.build();
        
        Measure measure1 = newMeasureBuilder().withName("measure1").withSymbol("m1")
        		.withUnits(unit1)
        		.build();
        
        MeasureDefinitionTerm term1 = newMeasureDefinitionTermBuilder().withExponent(-2).withUnit(unit1).build();
        
        Unit unit2 = newUnitBuilder().withName("unit2").withSymbol("u2")
    			.withRateDivisor(1)
    			.withRateDividend(1)
        		.build();
        
        Measure measure2 = newMeasureBuilder().withName("measure2").withSymbol("m2")
        		.withUnits(unit2)
        		.build();
        
        MeasureDefinitionTerm term2 = newMeasureDefinitionTermBuilder().withExponent(1).withUnit(unit2).build();
        
        Measure measure3 = newMeasureBuilder().withName("measure3").withSymbol("m3")
        		.withTerms(ImmutableList.of(term1,term2))
        		.build();
        
        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(ImmutableList.of(measure1, measure2, measure3))
                .build();

        esmModel.addContent(model);
        transform();

        final Optional<hu.blackbelt.judo.meta.psm.measure.DurationUnit> psmDurationUnit = allPsm(hu.blackbelt.judo.meta.psm.measure.DurationUnit.class)
                .findAny();

        assertTrue(psmDurationUnit.isPresent());
        assertThat(psmDurationUnit.get().getName(), IsEqual.equalTo(unit1.getName()));
        assertThat(psmDurationUnit.get().getSymbol(), IsEqual.equalTo(unit1.getSymbol()));
        assertThat(psmDurationUnit.get().getRateDividend(), IsEqual.equalTo(unit1.getRateDividend()));
        assertThat(psmDurationUnit.get().getRateDivisor(), IsEqual.equalTo(unit1.getRateDivisor()));
        assertThat(psmDurationUnit.get().getMeasure().getName(), IsEqual.equalTo(measure1.getName()));
    }
    
    @Test
    void testCreateMeasureDefinitionTerm() throws Exception {
        testName = "CreateMeasureDefinitionTerm";
        
        Unit unit1 = newUnitBuilder().withName("unit1").withSymbol("u1").build();
        MeasureDefinitionTerm term1 = newMeasureDefinitionTermBuilder().withExponent(1).withUnit(unit1).build();
        Unit unit2 = newUnitBuilder().withName("unit2").withSymbol("u2").build();
        MeasureDefinitionTerm term2 = newMeasureDefinitionTermBuilder().withExponent(-1).withUnit(unit2).build();
        
        Measure measure1 = newMeasureBuilder().withName("measure1").withSymbol("m1")
        		.withUnits(unit1)
        		.build();
        Measure measure2 = newMeasureBuilder().withName("measure2").withSymbol("m2")
        		.withUnits(unit2)
        		.build();
        
        Measure derivedMeasure = newMeasureBuilder().withName("dmeasure").withSymbol("dm")
        		.withTerms(ImmutableList.of(term1,term2))
        		.build();
        
        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(ImmutableList.of(measure1,measure2,derivedMeasure))
                .build();

        esmModel.addContent(model);
        transform();

        final Optional<hu.blackbelt.judo.meta.psm.measure.MeasureDefinitionTerm> psmMeasureDefinitionUnit = allPsm(hu.blackbelt.judo.meta.psm.measure.MeasureDefinitionTerm.class)
                .filter(t -> t.getExponent() == 1).findAny();
        final Optional<hu.blackbelt.judo.meta.psm.measure.DerivedMeasure> psmMeasure = allPsm(hu.blackbelt.judo.meta.psm.measure.DerivedMeasure.class)
                .findAny();

        assertTrue(psmMeasureDefinitionUnit.isPresent());
        assertThat(psmMeasureDefinitionUnit.get().getUnit().getName(), IsEqual.equalTo(term1.getUnit().getName()));
        assertThat(psmMeasureDefinitionUnit.get().getExponent(), IsEqual.equalTo(term1.getExponent()));
        assertThat(psmMeasure.get().getTerms().get(0), IsEqual.equalTo(psmMeasureDefinitionUnit.get()));
    }
    
    @Test
    void testCreateMeasuredType() throws Exception {
        testName = "CreateMeasuredType";
        
        DurationUnit unit = newDurationUnitBuilder().withName("unit").withSymbol("u").build();
        
        Measure measure = newMeasureBuilder().withName("measure").withSymbol("m")
        		.withUnits(unit)
        		.build();
        
        MeasuredType measuredType = newMeasuredTypeBuilder().withName("measuredType").withPrecision(5).withScale(1).withStoreUnit(unit).build();
        
        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(ImmutableList.of(measure,measuredType))
                .build();

        esmModel.addContent(model);
        transform();

        final Optional<hu.blackbelt.judo.meta.psm.measure.MeasuredType> psmMeasuredType = allPsm(hu.blackbelt.judo.meta.psm.measure.MeasuredType.class)
                .findAny();
        final Optional<hu.blackbelt.judo.meta.psm.measure.Unit> psmUnit = allPsm(hu.blackbelt.judo.meta.psm.measure.Unit.class)
                .findAny();

        assertTrue(psmMeasuredType.isPresent());
        assertThat(psmMeasuredType.get().getName(), IsEqual.equalTo(measuredType.getName()));
        assertThat(psmMeasuredType.get().getPrecision(), IsEqual.equalTo(measuredType.getPrecision()));
        assertThat(psmMeasuredType.get().getScale(), IsEqual.equalTo(measuredType.getScale()));
        assertThat(psmUnit.get(), IsEqual.equalTo(psmMeasuredType.get().getStoreUnit()));
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
