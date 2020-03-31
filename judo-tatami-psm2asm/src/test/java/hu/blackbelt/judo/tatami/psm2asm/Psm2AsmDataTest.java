package hu.blackbelt.judo.tatami.psm2asm;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.buildAsmModel;
import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.SaveArguments.asmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.calculatePsmValidationScriptURI;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.validatePsm;
import static hu.blackbelt.judo.meta.psm.data.util.builder.DataBuilders.newAssociationEndBuilder;
import static hu.blackbelt.judo.meta.psm.data.util.builder.DataBuilders.newAttributeBuilder;
import static hu.blackbelt.judo.meta.psm.data.util.builder.DataBuilders.newContainmentBuilder;
import static hu.blackbelt.judo.meta.psm.data.util.builder.DataBuilders.newEntityTypeBuilder;
import static hu.blackbelt.judo.meta.psm.measure.util.builder.MeasureBuilders.newMeasureBuilder;
import static hu.blackbelt.judo.meta.psm.measure.util.builder.MeasureBuilders.newMeasuredTypeBuilder;
import static hu.blackbelt.judo.meta.psm.measure.util.builder.MeasureBuilders.newUnitBuilder;
import static hu.blackbelt.judo.meta.psm.namespace.util.builder.NamespaceBuilders.newModelBuilder;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.buildPsmModel;
import static hu.blackbelt.judo.meta.psm.type.util.builder.TypeBuilders.newBooleanTypeBuilder;
import static hu.blackbelt.judo.meta.psm.type.util.builder.TypeBuilders.newCardinalityBuilder;
import static hu.blackbelt.judo.meta.psm.type.util.builder.TypeBuilders.newCustomTypeBuilder;
import static hu.blackbelt.judo.meta.psm.type.util.builder.TypeBuilders.newEnumerationMemberBuilder;
import static hu.blackbelt.judo.meta.psm.type.util.builder.TypeBuilders.newEnumerationTypeBuilder;
import static hu.blackbelt.judo.meta.psm.type.util.builder.TypeBuilders.newNumericTypeBuilder;
import static hu.blackbelt.judo.meta.psm.type.util.builder.TypeBuilders.newStringTypeBuilder;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.calculatePsm2AsmTransformationScriptURI;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.executePsm2AsmTransformation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Optional;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EReference;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.asm.runtime.AsmUtils;
import hu.blackbelt.judo.meta.psm.PsmUtils;
import hu.blackbelt.judo.meta.psm.data.AssociationEnd;
import hu.blackbelt.judo.meta.psm.data.Attribute;
import hu.blackbelt.judo.meta.psm.data.Containment;
import hu.blackbelt.judo.meta.psm.data.EntityType;
import hu.blackbelt.judo.meta.psm.measure.Measure;
import hu.blackbelt.judo.meta.psm.measure.MeasuredType;
import hu.blackbelt.judo.meta.psm.measure.Unit;
import hu.blackbelt.judo.meta.psm.namespace.Model;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.psm.type.BooleanType;
import hu.blackbelt.judo.meta.psm.type.CustomType;
import hu.blackbelt.judo.meta.psm.type.EnumerationMember;
import hu.blackbelt.judo.meta.psm.type.EnumerationType;
import hu.blackbelt.judo.meta.psm.type.NumericType;
import hu.blackbelt.judo.meta.psm.type.StringType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Psm2AsmDataTest {

	public static final String MODEL_NAME = "Test";
	public static final String TARGET_TEST_CLASSES = "target/test-classes";

	public static final String ENTITY_SOURCE = AsmUtils.getAnnotationUri("entity");
	public static final String CONSTRAINTS_SOURCE = AsmUtils.getAnnotationUri("constraints");

	Log slf4jlog;
	PsmModel psmModel;
	PsmUtils psmUtils;
	AsmModel asmModel;
	AsmUtils asmUtils;

	@BeforeEach
	public void setUp() {
		// Default logger
		slf4jlog = new Slf4jLog(log);
		// Loading PSM to isolated ResourceSet, because in Tatami
		// there is no new namespace registration made.
		psmModel = buildPsmModel().name(MODEL_NAME).build();
		psmUtils = new PsmUtils();
		// Create empty ASM model
		asmModel = buildAsmModel().name(MODEL_NAME).build();
		asmUtils = new AsmUtils(asmModel.getResourceSet());
	}

	private void transform(final String testName) throws Exception {
		psmModel.savePsmModel(PsmModel.SaveArguments.psmSaveArgumentsBuilder()
				.file(new File(TARGET_TEST_CLASSES, getClass().getName() + "-" + testName + "-psm.model")).build());

		assertTrue(psmModel.isValid());
		validatePsm(new Slf4jLog(log), psmModel, calculatePsmValidationScriptURI());

		executePsm2AsmTransformation(psmModel, asmModel, new Slf4jLog(log), calculatePsm2AsmTransformationScriptURI());

		assertTrue(asmModel.isValid());
		asmModel.saveAsmModel(asmSaveArgumentsBuilder()
				.file(new File(TARGET_TEST_CLASSES, getClass().getName() + "-" + testName + "-asm.model")).build());
	}

	@Test
	void testData() throws Exception {

		StringType strType = newStringTypeBuilder().withName("string").withMaxLength(256).build();
		NumericType intType = newNumericTypeBuilder().withName("int").withPrecision(6).withScale(0).build();
		BooleanType boolType = newBooleanTypeBuilder().withName("bool").build();
		CustomType custom = newCustomTypeBuilder().withName("object").build();

		Unit unit = newUnitBuilder().withName("u").build();
		Measure m = newMeasureBuilder().withName("measure").withUnits(unit).build();
		MeasuredType measuredType = newMeasuredTypeBuilder().withName("measuredType").withStoreUnit(unit)
				.withPrecision(5).withScale(3).build();

		EntityType abstractEntity1 = newEntityTypeBuilder().withName("abstractEntity1").withAbstract_(true).build();
		EntityType abstractEntity2 = newEntityTypeBuilder().withName("abstractEntity2").withAbstract_(true).build();
		EntityType entity1 = newEntityTypeBuilder().withName("entity1")
				.withSuperEntityTypes(ImmutableList.of(abstractEntity1, abstractEntity2)).build();
		EntityType entity2 = newEntityTypeBuilder().withName("entity2").build();
		EntityType entity3 = newEntityTypeBuilder().withName("entity3").withSuperEntityTypes(ImmutableList.of(entity2))
				.build();
		EntityType entity4 = newEntityTypeBuilder().withName("entity4").withSuperEntityTypes(ImmutableList.of(entity3))
				.build();

		Attribute stringAttr = newAttributeBuilder().withName("a1").withDataType(strType).withRequired(true).build();
		Attribute customAttr = newAttributeBuilder().withName("a2").withDataType(custom).build();
		Attribute boolAttr = newAttributeBuilder().withName("a3").withDataType(boolType).build();
		Attribute intAttr = newAttributeBuilder().withName("a4").withDataType(intType).withRequired(true)
				.withIdentifier(true).build();
		Attribute measuredAttr = newAttributeBuilder().withName("a5").withDataType(measuredType).build();

		entity1.getAttributes().addAll(ImmutableList.of(stringAttr, customAttr, boolAttr, intAttr, measuredAttr));

		AssociationEnd association = newAssociationEndBuilder().withName("association")
				.withCardinality(newCardinalityBuilder().withLower(1).withUpper(1)).withTarget(entity4).build();
		AssociationEnd associationPartner1 = newAssociationEndBuilder().withName("associationPartner1")
				.withCardinality(newCardinalityBuilder().withLower(0).withUpper(-1)).withTarget(entity4).build();
		AssociationEnd associationPartner2 = newAssociationEndBuilder().withName("associationPartner2")
				.withCardinality(newCardinalityBuilder().withLower(0).withUpper(1)).withTarget(entity1).build();

		Containment containment = newContainmentBuilder().withName("containment")
				.withCardinality(newCardinalityBuilder().withLower(0).withUpper(-1)).withTarget(entity3).build();

		associationPartner1.setPartner(associationPartner2);
		associationPartner2.setPartner(associationPartner1);

		entity1.getRelations().addAll(ImmutableList.of(association, associationPartner1, containment));
		entity4.getRelations().addAll(ImmutableList.of(associationPartner2));

		Model model = newModelBuilder().withName("M").withElements(ImmutableList.of(abstractEntity1, abstractEntity2,
				entity1, entity2, entity3, entity4, strType, intType, boolType, custom, measuredType, m))
				.build();

		psmModel.addContent(model);

		transform("testData");

		final Optional<EClass> asmAbstractEntity1 = asmUtils.all(EClass.class)
				.filter(c -> c.getName().equals(abstractEntity1.getName())).findAny();
		assertTrue(asmAbstractEntity1.isPresent());
		assertTrue(asmAbstractEntity1.get().isAbstract());
		assertFalse(asmAbstractEntity1.get().isInterface());
		assertThat(asmAbstractEntity1.get().getEAnnotation(ENTITY_SOURCE), IsNull.notNullValue());

		final EAnnotation asmAbstractEntity1Annotation = asmAbstractEntity1.get().getEAnnotation(ENTITY_SOURCE);
		assertTrue(asmAbstractEntity1Annotation.getDetails().containsKey("value"));
		assertTrue(asmAbstractEntity1Annotation.getDetails().get("value").equals("true"));
		assertTrue(asmAbstractEntity1Annotation.getEModelElement().equals(asmAbstractEntity1.get()));

		final EClass asmAbstractEntity2 = asmUtils.all(EClass.class)
				.filter(c -> c.getName().equals(abstractEntity2.getName())).findAny().get();
		final EClass asmEntity1 = asmUtils.all(EClass.class).filter(c -> c.getName().equals(entity1.getName()))
				.findAny().get();
		final EClass asmEntity2 = asmUtils.all(EClass.class).filter(c -> c.getName().equals(entity2.getName()))
				.findAny().get();
		final EClass asmEntity3 = asmUtils.all(EClass.class).filter(c -> c.getName().equals(entity3.getName()))
				.findAny().get();
		final EClass asmEntity4 = asmUtils.all(EClass.class).filter(c -> c.getName().equals(entity4.getName()))
				.findAny().get();

		assertTrue(asmEntity1.getESuperTypes().contains(asmAbstractEntity1.get()));
		assertTrue(asmEntity1.getESuperTypes().contains(asmAbstractEntity2));
		assertTrue(asmEntity3.getESuperTypes().contains(asmEntity2));
		assertTrue(asmEntity4.getESuperTypes().contains(asmEntity3));

		assertTrue(asmEntity4.getEAllSuperTypes().contains(asmEntity2));
		assertTrue(asmEntity4.getEAllSuperTypes().contains(asmEntity3));

		final String CUSTOM_TYPE_VALUE = psmUtils.namespaceElementToString(custom).replace("::", ".");
		final String MEASURE_VALUE = psmUtils.namespaceElementToString(measuredType.getStoreUnit().getMeasure())
				.replace("::", ".");
		final String MEASURE_UNIT_VALUE = measuredType.getStoreUnit().getName();

		final EDataType asmStr = asmUtils.all(EDataType.class).filter(e -> e.getName().equals(strType.getName()))
				.findAny().get();
		final EDataType asmInt = asmUtils.all(EDataType.class).filter(e -> e.getName().equals(intType.getName()))
				.findAny().get();
		final EDataType asmBoolType = asmUtils.all(EDataType.class).filter(e -> e.getName().equals(boolType.getName()))
				.findAny().get();
		final EDataType asmCustom = asmUtils.all(EDataType.class).filter(e -> e.getName().equals(custom.getName()))
				.findAny().get();
		final EDataType asmMeasured = asmUtils.all(EDataType.class)
				.filter(e -> e.getName().equals(measuredType.getName())).findAny().get();

		final Optional<EAttribute> asmStrAttr = asmUtils.all(EAttribute.class)
				.filter(a -> a.getName().equals(stringAttr.getName())).findAny();
		final Optional<EAttribute> asmCustomAttr = asmUtils.all(EAttribute.class)
				.filter(a -> a.getName().equals(customAttr.getName())).findAny();
		final Optional<EAttribute> asmBoolAttr = asmUtils.all(EAttribute.class)
				.filter(a -> a.getName().equals(boolAttr.getName())).findAny();
		final Optional<EAttribute> asmIntAttr = asmUtils.all(EAttribute.class)
				.filter(a -> a.getName().equals(intAttr.getName())).findAny();
		final Optional<EAttribute> asmMeasuredAttr = asmUtils.all(EAttribute.class)
				.filter(a -> a.getName().equals(measuredAttr.getName())).findAny();

		assertTrue(asmStrAttr.isPresent());
		assertTrue(asmCustomAttr.isPresent());
		assertTrue(asmBoolAttr.isPresent());
		assertTrue(asmIntAttr.isPresent());
		assertTrue(asmMeasuredAttr.isPresent());

		assertThat(asmStrAttr.get().getLowerBound(), IsEqual.equalTo(1));
		assertThat(asmCustomAttr.get().getLowerBound(), IsEqual.equalTo(0));
		assertThat(asmBoolAttr.get().getLowerBound(), IsEqual.equalTo(0));
		assertThat(asmIntAttr.get().getLowerBound(), IsEqual.equalTo(1));
		assertThat(asmMeasuredAttr.get().getLowerBound(), IsEqual.equalTo(0));

		assertFalse(asmStrAttr.get().isID());
		assertFalse(asmCustomAttr.get().isID());
		assertFalse(asmBoolAttr.get().isID());
		assertTrue(asmIntAttr.get().isID());
		assertFalse(asmMeasuredAttr.get().isID());

		assertTrue(asmStrAttr.get().getEType().equals(asmStr));
		assertTrue(asmCustomAttr.get().getEType().equals(asmCustom));
		assertTrue(asmBoolAttr.get().getEType().equals(asmBoolType));
		assertTrue(asmIntAttr.get().getEType().equals(asmInt));
		assertTrue(asmMeasuredAttr.get().getEType().equals(asmMeasured));

		assertTrue(asmStrAttr.get().getEContainingClass().equals(asmEntity1));
		assertTrue(asmCustomAttr.get().getEContainingClass().equals(asmEntity1));
		assertTrue(asmBoolAttr.get().getEContainingClass().equals(asmEntity1));
		assertTrue(asmIntAttr.get().getEContainingClass().equals(asmEntity1));
		assertTrue(asmMeasuredAttr.get().getEContainingClass().equals(asmEntity1));

		final EAnnotation asmStrAnnotation = asmStrAttr.get().getEAnnotation(CONSTRAINTS_SOURCE);
		assertThat(asmStrAnnotation, IsNull.notNullValue());
		assertThat(asmStrAnnotation.getDetails().size(), IsEqual.equalTo(1));
		assertTrue(asmStrAnnotation.getDetails().containsKey("maxLength"));
		assertTrue(asmStrAnnotation.getDetails().get("maxLength").equals(String.valueOf(strType.getMaxLength())));

		final EAnnotation asmCustomAnnotation = asmCustomAttr.get().getEAnnotation(CONSTRAINTS_SOURCE);
		assertThat(asmCustomAnnotation, IsNull.notNullValue());
		assertThat(asmCustomAnnotation.getDetails().size(), IsEqual.equalTo(1));
		assertTrue(asmCustomAnnotation.getDetails().containsKey("customType"));
		assertTrue(asmCustomAnnotation.getDetails().get("customType").equals(CUSTOM_TYPE_VALUE));

		final EAnnotation asmIntAnnotation = asmIntAttr.get().getEAnnotation(CONSTRAINTS_SOURCE);
		assertThat(asmIntAnnotation, IsNull.notNullValue());
		assertThat(asmIntAnnotation.getDetails().size(), IsEqual.equalTo(2));
		assertTrue(asmIntAnnotation.getDetails().containsKey("precision"));
		assertTrue(asmIntAnnotation.getDetails().containsKey("scale"));
		assertTrue(asmIntAnnotation.getDetails().get("precision").equals(String.valueOf(intType.getPrecision())));
		assertTrue(asmIntAnnotation.getDetails().get("scale").equals(String.valueOf(intType.getScale())));

		final EAnnotation asmMeasuredAnnotation = asmMeasuredAttr.get().getEAnnotation(CONSTRAINTS_SOURCE);
		assertThat(asmMeasuredAnnotation, IsNull.notNullValue());
		assertThat(asmMeasuredAnnotation.getDetails().size(), IsEqual.equalTo(4));
		assertTrue(asmMeasuredAnnotation.getDetails().containsKey("precision"));
		assertTrue(asmMeasuredAnnotation.getDetails().containsKey("scale"));
		assertTrue(asmMeasuredAnnotation.getDetails().get("precision")
				.equals(String.valueOf(measuredType.getPrecision())));
		assertTrue(asmMeasuredAnnotation.getDetails().get("scale").equals(String.valueOf(measuredType.getScale())));
		assertTrue(asmMeasuredAnnotation.getDetails().containsKey("measure"));
		assertTrue(asmMeasuredAnnotation.getDetails().containsKey("unit"));
		assertTrue(asmMeasuredAnnotation.getDetails().get("measure").equals(MEASURE_VALUE));
		assertTrue(asmMeasuredAnnotation.getDetails().get("unit").equals(MEASURE_UNIT_VALUE));

		final Optional<EReference> asmAssociation = asmUtils.all(EReference.class)
				.filter(r -> r.getName().equals(association.getName())).findAny();
		final Optional<EReference> asmAssociationPartner1 = asmUtils.all(EReference.class)
				.filter(r -> r.getName().equals(associationPartner1.getName())).findAny();
		final Optional<EReference> asmAssociationPartner2 = asmUtils.all(EReference.class)
				.filter(r -> r.getName().equals(associationPartner2.getName())).findAny();
		final Optional<EReference> asmContainment = asmUtils.all(EReference.class)
				.filter(r -> r.getName().equals(containment.getName())).findAny();

		assertTrue(asmAssociation.isPresent());
		assertTrue(asmAssociationPartner1.isPresent());
		assertTrue(asmAssociationPartner2.isPresent());
		assertTrue(asmContainment.isPresent());

		assertTrue(asmAssociation.get().getEContainingClass().equals(asmEntity1));
		assertTrue(asmAssociationPartner1.get().getEContainingClass().equals(asmEntity1));
		assertTrue(asmAssociationPartner2.get().getEContainingClass().equals(asmEntity4));
		assertTrue(asmContainment.get().getEContainingClass().equals(asmEntity1));

		assertThat(asmAssociation.get().getLowerBound(), IsEqual.equalTo(association.getCardinality().getLower()));
		assertThat(asmAssociationPartner1.get().getLowerBound(),
				IsEqual.equalTo(associationPartner1.getCardinality().getLower()));
		assertThat(asmAssociationPartner2.get().getLowerBound(),
				IsEqual.equalTo(associationPartner2.getCardinality().getLower()));
		assertThat(asmContainment.get().getLowerBound(), IsEqual.equalTo(containment.getCardinality().getLower()));

		assertThat(asmAssociation.get().getUpperBound(), IsEqual.equalTo(association.getCardinality().getUpper()));
		assertThat(asmAssociationPartner1.get().getUpperBound(),
				IsEqual.equalTo(associationPartner1.getCardinality().getUpper()));
		assertThat(asmAssociationPartner2.get().getUpperBound(),
				IsEqual.equalTo(associationPartner2.getCardinality().getUpper()));
		assertThat(asmContainment.get().getUpperBound(), IsEqual.equalTo(containment.getCardinality().getUpper()));

		assertTrue(asmAssociation.get().getEType().equals(asmEntity4));
		assertTrue(asmAssociationPartner1.get().getEType().equals(asmEntity4));
		assertTrue(asmAssociationPartner2.get().getEType().equals(asmEntity1));
		assertTrue(asmContainment.get().getEType().equals(asmEntity3));

		assertTrue(asmAssociationPartner1.get().getEOpposite().equals(asmAssociationPartner2.get()));
		assertTrue(asmAssociationPartner2.get().getEOpposite().equals(asmAssociationPartner1.get()));

		assertFalse(asmAssociation.get().isContainment());
		assertFalse(asmAssociationPartner1.get().isContainment());
		assertFalse(asmAssociationPartner2.get().isContainment());
		assertTrue(asmContainment.get().isContainment());
	}
}
