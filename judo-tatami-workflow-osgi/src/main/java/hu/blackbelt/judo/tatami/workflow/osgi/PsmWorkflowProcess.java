package hu.blackbelt.judo.tatami.workflow.osgi;

import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.workflow.DefaultWorkflowSetupParameters;
import hu.blackbelt.judo.tatami.workflow.PsmDefaultWorkflow;
import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.Designate;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * This process repsresents a {@link PsmDefaultWorkflow} process for a {@link PsmModel} instances.
 * It referencing all the required transformation bundles over a {@link TransformationBundleHolder} which is
 * managed by {@link TransformationBundleTracker}.
 */
@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Slf4j
@Designate(ocd = PsmWorkflowProcessConfiguration.class)
public class PsmWorkflowProcess {
    PsmWorkflowProcessConfiguration psmWorkflowProcessConfiguration;

    @Reference(name = "psmModel")
    PsmModel psmModel;

    @Reference(name = "psm2asm")
    TransformationBundleHolder psm2asm;

    @Reference(name = "psm2measure")
    TransformationBundleHolder psm2measure;

    @Reference(name = "asm2rdbms")
    TransformationBundleHolder asm2rdbms;

    @Reference(name = "asm2openapi")
    TransformationBundleHolder asm2openapi;

    @Reference(name = "rdbms2liquibase")
    TransformationBundleHolder rdbms2liquibase;

    @Reference(name = "asm2jaxrsapi")
    TransformationBundleHolder asm2jaxrsapi;

    @Reference(name = "asm2sdk")
    TransformationBundleHolder asm2sdk;

    @Activate
    public void activate(ComponentContext componentContextContext, PsmWorkflowProcessConfiguration config)
            throws URISyntaxException, IOException, PsmModel.PsmValidationException {

        this.psmWorkflowProcessConfiguration = config;
        PsmDefaultWorkflow defaultWorkflow = new PsmDefaultWorkflow();
        defaultWorkflow.setUp(DefaultWorkflowSetupParameters.defaultWorkflowSetupParameters()
                .modelName(psmModel.getName())
                .psmModel(psmModel)
                .dialect(config.sqlDialect())
                // Psm2Asm
                .psm2AsmModelTransformationScriptURI(psm2asm.resolveURIByManifestName(config.psm2AsmTransformationScriptUriHeaderName()))
                // Psm2Measure
                .psm2MeasureModelTransformationScriptURI(psm2measure.resolveURIByManifestName(config.psm2MeasureTransformationScriptUriHeaderName()))
                // Asm2Rdbms
                .asm2RdbmsModelTransformationScriptURI(asm2rdbms.resolveURIByManifestName(config.asm2RdbmsTransformationScriptUriHeaderName()))
                .asm2RdbmsModelTransformationModelURI(asm2rdbms.resolveURIByManifestName(config.asm2RdbmsTransformationModelUriHeaderName()))
                // Asm2Openapi
                .asm2OpenapiModelTransformationScriptURI(asm2openapi.resolveURIByManifestName(config.asm2OpenapiTransformationScriptUriHeaderName()))
                // Rdbms2Liquibase
                .rdbms2LiquibaseModelTransformationScriptURI(rdbms2liquibase.resolveURIByManifestName(config.rdbms2LiquibaseTransformationScriptUriHeaderName()))
        );

        WorkReport workReport = defaultWorkflow.startDefaultWorkflow();

        // Get transformation context and registering all services which are presented

    }

    @Deactivate
    public void deactivate() {
        // Unregister all registered services
    }

}