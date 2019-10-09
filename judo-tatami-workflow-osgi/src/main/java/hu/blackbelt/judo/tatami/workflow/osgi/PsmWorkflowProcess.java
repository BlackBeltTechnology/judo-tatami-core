package hu.blackbelt.judo.tatami.workflow.osgi;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;

import com.google.common.collect.ImmutableList;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;
import hu.blackbelt.judo.tatami.workflow.DefaultWorkflowSetupParameters;
import hu.blackbelt.judo.tatami.workflow.PsmDefaultWorkflow;

/**
 * This process represents a {@link PsmDefaultWorkflow} process for a {@link PsmModel} instances.
 * It referencing all the required transformation bundles over a {@link TransformationBundleHolder} which is
 * managed by {@link TransformationBundleTracker}.
 * After the transformation workflow executed the {@link TransformationContext} are registering the models / bundles / traces
 * via {@link TransformationContextRegistrationService}. By default the {@link DefaultTransformationContextRegistrationService}
 * is used, but it is configurable in {@link PsmWorkflowDefaultPsmModelTracker}'s configuration.
 */
@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = PsmWorkflowProcessConfiguration.class)
public class PsmWorkflowProcess {
    PsmWorkflowProcessConfiguration psmWorkflowProcessConfiguration;

    @Reference(name = "transformationContextRegistrationService")
    TransformationContextRegistrationService transformationContextRegistrationService;

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

    private TransformationContext transformationContext;

    @Activate
    public void activate(ComponentContext componentContextContext, PsmWorkflowProcessConfiguration config)
            throws URISyntaxException, IOException, PsmModel.PsmValidationException {

        this.psmWorkflowProcessConfiguration = config;
        PsmDefaultWorkflow defaultWorkflow = new PsmDefaultWorkflow(DefaultWorkflowSetupParameters.defaultWorkflowSetupParameters()
                .modelName(psmModel.getName())
                .psmModel(psmModel)
                .dialectList(ImmutableList.of(config.sqlDialect()))
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
                // Asm2JaxrsApi
                .asm2jaxrsapiModelTransformationScriptURI(asm2jaxrsapi.resolveURIByManifestName(config.asm2JaxrsapiTransformationScriptUriHeaderName()))
                // Asm2SDK
                .asm2sdkModelTransformationScriptURI(asm2sdk.resolveURIByManifestName(config.asm2SdkTransformationScriptUriHeaderName()))
        );

        WorkReport workReport = defaultWorkflow.startDefaultWorkflow();

        if (workReport.getStatus() != WorkStatus.COMPLETED) {
            throw new IllegalStateException(workReport.getError());
        }

        // Get transformation context and registering all services which are presented
        transformationContext = defaultWorkflow.getTransformationContext();
        transformationContextRegistrationService.registerTramsformationContext(transformationContext);
    }

    @Deactivate
    public void deactivate() {
        transformationContextRegistrationService.unregisterTramsformationContext(transformationContext);
    }

}