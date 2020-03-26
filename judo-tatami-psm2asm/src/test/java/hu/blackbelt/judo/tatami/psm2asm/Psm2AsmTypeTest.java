package hu.blackbelt.judo.tatami.psm2asm;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.buildAsmModel;
import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.SaveArguments.asmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.calculatePsmValidationScriptURI;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.validatePsm;
import static hu.blackbelt.judo.meta.psm.namespace.util.builder.NamespaceBuilders.newModelBuilder;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.buildPsmModel;
import static hu.blackbelt.judo.meta.psm.type.util.builder.TypeBuilders.newBooleanTypeBuilder;
import static hu.blackbelt.judo.meta.psm.type.util.builder.TypeBuilders.newCustomTypeBuilder;
import static hu.blackbelt.judo.meta.psm.type.util.builder.TypeBuilders.newDateTypeBuilder;
import static hu.blackbelt.judo.meta.psm.type.util.builder.TypeBuilders.newEnumerationMemberBuilder;
import static hu.blackbelt.judo.meta.psm.type.util.builder.TypeBuilders.newEnumerationTypeBuilder;
import static hu.blackbelt.judo.meta.psm.type.util.builder.TypeBuilders.newNumericTypeBuilder;
import static hu.blackbelt.judo.meta.psm.type.util.builder.TypeBuilders.newPasswordTypeBuilder;
import static hu.blackbelt.judo.meta.psm.type.util.builder.TypeBuilders.newStringTypeBuilder;
import static hu.blackbelt.judo.meta.psm.type.util.builder.TypeBuilders.newTimestampTypeBuilder;
import static hu.blackbelt.judo.meta.psm.type.util.builder.TypeBuilders.newXMLTypeBuilder;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.calculatePsm2AsmTransformationScriptURI;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.executePsm2AsmTransformation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Optional;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.asm.runtime.AsmUtils;
import hu.blackbelt.judo.meta.psm.namespace.Model;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.psm.type.BooleanType;
import hu.blackbelt.judo.meta.psm.type.CustomType;
import hu.blackbelt.judo.meta.psm.type.DateType;
import hu.blackbelt.judo.meta.psm.type.EnumerationMember;
import hu.blackbelt.judo.meta.psm.type.EnumerationType;
import hu.blackbelt.judo.meta.psm.type.NumericType;
import hu.blackbelt.judo.meta.psm.type.PasswordType;
import hu.blackbelt.judo.meta.psm.type.StringType;
import hu.blackbelt.judo.meta.psm.type.TimestampType;
import hu.blackbelt.judo.meta.psm.type.XMLType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Psm2AsmTypeTest {

    public static final String MODEL_NAME = "Test";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";
    
    public static final String TEST_MODEL_NAME = "Model";
    public static final String STRING = "java.lang.String";
    public static final String INTEGER = "java.lang.Integer";
    public static final String LONG = "java.lang.Long";
    public static final String BIG_DECIMAL = "java.math.BigDecimal";
    
    public static final String FLOAT = "java.lang.Float";
    public static final String DOUBLE = "java.lang.Double";
    public static final String BOOLEAN = "java.lang.Boolean";
    public static final String LOCALE_DATE = "java.time.LocalDate";
    public static final String DATE_TIME = "java.time.OffsetDateTime";
    public static final String OBJECT = "java.lang.Object";
    
    Log slf4jlog;
    PsmModel psmModel;
    AsmModel asmModel;
    AsmUtils asmUtils;

    @BeforeEach
    public void setUp() {
        // Default logger
        slf4jlog = new Slf4jLog(log);
        // Loading PSM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        psmModel = buildPsmModel()
                .name(MODEL_NAME)
                .build();
        // Create empty ASM model
        asmModel = buildAsmModel()
                .name(MODEL_NAME)
                .build();
        asmUtils = new AsmUtils(asmModel.getResourceSet());
    }

    private void transform(final String testName) throws Exception {
        psmModel.savePsmModel(PsmModel.SaveArguments.psmSaveArgumentsBuilder()
                .file(new File(TARGET_TEST_CLASSES, getClass().getName() + "-" + testName + "-psm.model"))
                .build());
        
        assertTrue(psmModel.isValid());
        validatePsm(new Slf4jLog(log), psmModel, calculatePsmValidationScriptURI());

        executePsm2AsmTransformation(psmModel, asmModel, new Slf4jLog(log), calculatePsm2AsmTransformationScriptURI());

        assertTrue(asmModel.isValid());
        asmModel.saveAsmModel(asmSaveArgumentsBuilder()
                .file(new File(TARGET_TEST_CLASSES, getClass().getName() + "-" + testName + "-asm.model"))
                .build());
    }
    
    @Test
    void testType() throws Exception {
    	
    	EnumerationMember a = newEnumerationMemberBuilder().withName("a").withOrdinal(1).build();
    	EnumerationMember b = newEnumerationMemberBuilder().withName("b").withOrdinal(2).build();
    	EnumerationMember c = newEnumerationMemberBuilder().withName("c").withOrdinal(3).build();
        
    	EnumerationType enumType = newEnumerationTypeBuilder().withName("enum")
    			.withMembers(ImmutableList.of(a,b,c)).build();
    	StringType strType = newStringTypeBuilder().withName("string").withMaxLength(256).build();
    	
    	NumericType intType = newNumericTypeBuilder().withName("int").withPrecision(6).withScale(0).build();
    	NumericType longType = newNumericTypeBuilder().withName("long").withPrecision(16).withScale(0).build();
    	NumericType bigDecimalIntType = newNumericTypeBuilder().withName("bigdecimalint").withPrecision(21).withScale(0).build();
    	
    	NumericType floatType = newNumericTypeBuilder().withName("float").withPrecision(6).withScale(3).build();
    	NumericType doubleType = newNumericTypeBuilder().withName("bouble").withPrecision(10).withScale(3).build();
    	NumericType bigDecimalType = newNumericTypeBuilder().withName("bigdecimal").withPrecision(17).withScale(8).build();
    	
    	BooleanType boolType = newBooleanTypeBuilder().withName("bool").build();
    	
    	//not supported yet
    	PasswordType pwType = newPasswordTypeBuilder().withName("pw").build();
    	//not supported yet
    	XMLType xmlType = newXMLTypeBuilder().withName("xml").build();

    	DateType dateType = newDateTypeBuilder().withName("date").build();
    	TimestampType timeStampType = newTimestampTypeBuilder().withName("timestamp").build();
    	
    	CustomType custom = newCustomTypeBuilder().withName("object").build();
    	
		Model model = newModelBuilder().withName(TEST_MODEL_NAME)
				.withElements(ImmutableList.of(enumType,strType,intType,longType,bigDecimalIntType,floatType,doubleType,bigDecimalType,boolType,dateType,timeStampType,custom)).build();

        psmModel.addContent(model);

        transform("testType");
        
        final EPackage asmTestModel = asmUtils.all(EPackage.class).filter(p -> p.getName().equals(TEST_MODEL_NAME)).findAny().get();
        final Optional<EEnum> asmEnum = asmUtils.all(EEnum.class).filter(e -> e.getName().equals(enumType.getName())).findAny();
        assertTrue(asmEnum.isPresent());
        assertTrue(asmEnum.get().getEPackage().equals(asmTestModel));
        
        assertThat(asmEnum.get().getEEnumLiteral(1), IsNull.notNullValue());
        assertThat(asmEnum.get().getEEnumLiteral(2), IsNull.notNullValue());
        assertThat(asmEnum.get().getEEnumLiteral(3), IsNull.notNullValue());
        assertTrue(asmEnum.get().getEEnumLiteral(1).getLiteral().equals(a.getName()));
        assertTrue(asmEnum.get().getEEnumLiteral(2).getLiteral().equals(b.getName()));
        assertTrue(asmEnum.get().getEEnumLiteral(3).getLiteral().equals(c.getName()));
        
        final Optional<EDataType> asmStr = asmUtils.all(EDataType.class).filter(e -> e.getName().equals(strType.getName())).findAny();
        assertTrue(asmStr.isPresent());
        assertTrue(asmStr.get().getEPackage().equals(asmTestModel));
        assertTrue(asmStr.get().getInstanceClassName().equals(STRING));
        
        final Optional<EDataType> asmInt = asmUtils.all(EDataType.class).filter(e -> e.getName().equals(intType.getName())).findAny();
        assertTrue(asmInt.isPresent());
        assertTrue(asmInt.get().getEPackage().equals(asmTestModel));
        assertTrue(asmInt.get().getInstanceClassName().equals(INTEGER));
        
        final Optional<EDataType> asmLongType = asmUtils.all(EDataType.class).filter(e -> e.getName().equals(longType.getName())).findAny();
        assertTrue(asmLongType.isPresent());
        assertTrue(asmLongType.get().getEPackage().equals(asmTestModel));
        assertTrue(asmLongType.get().getInstanceClassName().equals(LONG));
        
        final Optional<EDataType> asmBigDecimalIntType = asmUtils.all(EDataType.class).filter(e -> e.getName().equals(bigDecimalIntType.getName())).findAny();
        assertTrue(asmBigDecimalIntType.isPresent());
        assertTrue(asmBigDecimalIntType.get().getEPackage().equals(asmTestModel));
        assertTrue(asmBigDecimalIntType.get().getInstanceClassName().equals(BIG_DECIMAL));
        
        final Optional<EDataType> asmFloatType = asmUtils.all(EDataType.class).filter(e -> e.getName().equals(floatType.getName())).findAny();
        assertTrue(asmFloatType.isPresent());
        assertTrue(asmFloatType.get().getEPackage().equals(asmTestModel));
        assertTrue(asmFloatType.get().getInstanceClassName().equals(FLOAT));
        
        final Optional<EDataType> asmDoubleType = asmUtils.all(EDataType.class).filter(e -> e.getName().equals(doubleType.getName())).findAny();
        assertTrue(asmDoubleType.isPresent());
        assertTrue(asmDoubleType.get().getEPackage().equals(asmTestModel));
        assertTrue(asmDoubleType.get().getInstanceClassName().equals(DOUBLE));
        
        final Optional<EDataType> asmBigDecimalType = asmUtils.all(EDataType.class).filter(e -> e.getName().equals(bigDecimalType.getName())).findAny();
        assertTrue(asmBigDecimalType.isPresent());
        assertTrue(asmBigDecimalType.get().getEPackage().equals(asmTestModel));
        assertTrue(asmBigDecimalType.get().getInstanceClassName().equals(BIG_DECIMAL));
        
        
        final Optional<EDataType> asmBoolType = asmUtils.all(EDataType.class).filter(e -> e.getName().equals(boolType.getName())).findAny();
        assertTrue(asmBoolType.isPresent());
        assertTrue(asmBoolType.get().getEPackage().equals(asmTestModel));
        assertTrue(asmBoolType.get().getInstanceClassName().equals(BOOLEAN));
        
        final Optional<EDataType> asmDateType = asmUtils.all(EDataType.class).filter(e -> e.getName().equals(dateType.getName())).findAny();
        assertTrue(asmDateType.isPresent());
        assertTrue(asmDateType.get().getEPackage().equals(asmTestModel));
        assertTrue(asmDateType.get().getInstanceClassName().equals(LOCALE_DATE));
        
        final Optional<EDataType> asmTimeStampType = asmUtils.all(EDataType.class).filter(e -> e.getName().equals(timeStampType.getName())).findAny();
        assertTrue(asmTimeStampType.isPresent());
        assertTrue(asmTimeStampType.get().getEPackage().equals(asmTestModel));
        assertTrue(asmTimeStampType.get().getInstanceClassName().equals(DATE_TIME));
        
        final Optional<EDataType> asmCustom = asmUtils.all(EDataType.class).filter(e -> e.getName().equals(custom.getName())).findAny();
        assertTrue(asmCustom.isPresent());
        assertTrue(asmCustom.get().getEPackage().equals(asmTestModel));
        assertTrue(asmCustom.get().getInstanceClassName().equals(OBJECT));
    }
}
