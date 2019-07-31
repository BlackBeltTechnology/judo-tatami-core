package hu.blackbelt.judo.tatami.asm2rdbms;

import com.google.common.collect.Maps;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.execution.impl.StringBuilderLogger;
import hu.blackbelt.epsilon.runtime.osgi.BundleURIHandler;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.meta.rdbmsDataTypes.support.RdbmsDataTypesModelResourceSupport;
import hu.blackbelt.judo.meta.rdbmsNameMapping.support.RdbmsNameMappingModelResourceSupport;
import hu.blackbelt.judo.meta.rdbmsRules.support.RdbmsTableMappingRulesModelResourceSupport;
import hu.blackbelt.judo.tatami.core.TransformationTrace;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;

import static hu.blackbelt.judo.meta.rdbms.support.RdbmsModelResourceSupport.rdbmsModelResourceSupportBuilder;
import static hu.blackbelt.judo.tatami.asm2rdbms.Asm2Rdbms.executeAsm2RdbmsTransformation;


@Component(immediate = true, service = Asm2RdbmsSerivce.class)
@Slf4j
public class Asm2RdbmsSerivce {

    public static final String RDBMS_META_VERSION_RANGE = "Rdbms-Meta-Version-Range";

    Map<AsmModel, ServiceRegistration<TransformationTrace>> asm2rdbmsTransformationTraceRegistration = Maps.newHashMap();
    BundleContext scriptBundleContext;

    @Activate
    public void activate(BundleContext bundleContext) {
        scriptBundleContext = bundleContext;
    }


    public RdbmsModel install(AsmModel asmModel, BundleContext bundleContext, String dialect) throws Exception {

        BundleURIHandler bundleURIHandler = new BundleURIHandler("urn", "",
                bundleContext.getBundle());

        RdbmsModel rdbmsModel = RdbmsModel.buildRdbmsModel()
                .name(asmModel.getName())
                .version(asmModel.getVersion())
                .uri(URI.createURI("urn:" + asmModel.getName() + ".rdbms"))
                .checksum(asmModel.getChecksum())
                .rdbmsModelResourceSupport(
                        rdbmsModelResourceSupportBuilder()
                                .uriHandler(Optional.of(bundleURIHandler))
                                .build())
                .metaVersionRange(bundleContext.getBundle().getHeaders().get(RDBMS_META_VERSION_RANGE)).build();

        // The RDBMS model resourceset have to know the mapping models
        RdbmsNameMappingModelResourceSupport.registerRdbmsNameMappingMetamodel(rdbmsModel.getResourceSet());
        RdbmsDataTypesModelResourceSupport.registerRdbmsDataTypesMetamodel(rdbmsModel.getResourceSet());
        RdbmsTableMappingRulesModelResourceSupport.registerRdbmsTableMappingRulesMetamodel(rdbmsModel.getResourceSet());

        Log logger = new StringBuilderLogger(Slf4jLog.determinateLogLevel(log));
        try {
            java.net.URI scriptUri =
                    scriptBundleContext.getBundle()
                            .getEntry("/tatami/asm2rdbms/transformations/asmToRdbms.etl")
                            .toURI()
                            .resolve(".");

            java.net.URI excelModelUri =
                    scriptBundleContext.getBundle()
                            .getEntry("/tatami/asm2rdbms/model/typemapping.xml")
                            .toURI()
                            .resolve(".");

            Asm2RdbmsTransformationTrace asm2RdbmsTransformationTrace = executeAsm2RdbmsTransformation(rdbmsModel.getResourceSet(), asmModel, rdbmsModel, logger,
                    scriptUri,
                    excelModelUri, dialect);

            asm2rdbmsTransformationTraceRegistration.put(asmModel, bundleContext.registerService(TransformationTrace.class, asm2RdbmsTransformationTrace, new Hashtable<>()));
            log.info(logger.getBuffer());
        } catch (Exception e) {
            log.error(logger.getBuffer());
            throw e;
        }
        return rdbmsModel;
    }

    public void uninstall(AsmModel asmModel) {
        if (asm2rdbmsTransformationTraceRegistration.containsKey(asmModel)) {
            asm2rdbmsTransformationTraceRegistration.get(asmModel).unregister();
        } else {
            log.error("ASM model is not installed: " + asmModel.toString());
        }
    }
}
