package hu.blackbelt.judo.tatami.workflow.osgi;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;
import hu.blackbelt.judo.tatami.workflow.DefaultWorkflowSave;
import hu.blackbelt.judo.tatami.workflow.DefaultWorkflowSetupParameters;
import hu.blackbelt.judo.tatami.workflow.PsmDefaultWorkflow;
import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * This process represents a {@link PsmDefaultWorkflow} process for a {@link PsmModel} instances.
 * After the transformation workflow executed the {@link TransformationContext} are registering the models / bundles / traces
 * via {@link TransformationContextRegistrationService}. By default the {@link DefaultTransformationContextRegistrationService}
 * is used, but it is configurable in {@link PsmWorkflowDefaultPsmModelTracker}'s configuration.
 */
@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = PsmWorkflowProcessConfiguration.class)
@Slf4j
public class PsmWorkflowProcess {
    PsmWorkflowProcessConfiguration psmWorkflowProcessConfiguration;

    @Reference(name = "transformationContextRegistrationService")
    TransformationContextRegistrationService transformationContextRegistrationService;

    @Reference(name = "psmModel")
    PsmModel psmModel;

    private TransformationContext transformationContext;

    @Activate
    public void activate(ComponentContext componentContextContext, PsmWorkflowProcessConfiguration config)
            throws URISyntaxException, IOException, PsmModel.PsmValidationException {

        this.psmWorkflowProcessConfiguration = config;

        DefaultWorkflowSetupParameters.DefaultWorkflowSetupParametersBuilder workflowSetupParameters = DefaultWorkflowSetupParameters.defaultWorkflowSetupParameters();
        //DefaultWorkflowSetupParameters.addTransformerCalculatedUris(workflowSetupParameters);
        workflowSetupParameters
                .modelName(psmModel.getName())
                .psmModel(psmModel)
                .dialectList(ImmutableList.of(config.sqlDialect()));

        workflowSetupParameters.enableMetrics(config.enableMetrics());
        workflowSetupParameters.ignorePsm2Asm(config.ignorePsm2Asm());
        workflowSetupParameters.ignorePsm2Measure(config.ignorePsm2Measure());

        workflowSetupParameters.ignoreAsm2Expression(config.ignoreAsm2Expression());
        workflowSetupParameters.ignoreAsm2jaxrsapi(config.ignoreAsm2jaxrsapi());
        workflowSetupParameters.ignoreAsm2Openapi(config.ignoreAsm2Openapi());
        workflowSetupParameters.ignoreAsm2Rdbms(config.ignoreAsm2Rdbms());
        workflowSetupParameters.ignoreAsm2Keycloak(config.ignoreAsm2Keycloak());
        workflowSetupParameters.ignoreAsm2Script(config.ignoreAsm2Script());
        workflowSetupParameters.ignoreAsm2sdk(config.ignoreAsm2Sdk());

        workflowSetupParameters.ignoreRdbms2Liquibase(config.ignoreRdbms2Liquibase());
        workflowSetupParameters.ignoreScript2Operation(config.ignoreScript2Operation());

        File outputDirectory;
        if (config.outputDirectory() == null || config.outputDirectory().equals("")) {
            outputDirectory = new File("model-dump-" + System.currentTimeMillis());
        } else {
            outputDirectory = new File(config.outputDirectory());
        }

        workflowSetupParameters.validateModels(config.validateModels());

        PsmDefaultWorkflow defaultWorkflow = new PsmDefaultWorkflow(workflowSetupParameters);
        try {
            WorkReport workReport = defaultWorkflow.startDefaultWorkflow();
            if (workReport.getStatus() != WorkStatus.COMPLETED) {
                saveFailedModels(defaultWorkflow, config, outputDirectory);
                throw new IllegalStateException(workReport.getError());
            }
        } catch (Exception e) {
            saveFailedModels(defaultWorkflow, config, outputDirectory);
            throw e;
        }

        if (config.saveCompletedModels()) {
            try {
                outputDirectory.mkdirs();
                DefaultWorkflowSave.saveModels(defaultWorkflow.getTransformationContext(), outputDirectory, ImmutableList.of(config.sqlDialect()));
            } catch (Exception e) {
                log.error("Could not dump models: ", e);
            }
        }

        // Get transformation context and registering all services which are presented
        transformationContext = defaultWorkflow.getTransformationContext();
        transformationContextRegistrationService.registerTransformationContext(transformationContext, config.sqlDialect());
    }

    @Deactivate
    public void deactivate() {
        transformationContextRegistrationService.unregisterTransformationContext(transformationContext, psmWorkflowProcessConfiguration.sqlDialect());
    }

    private void saveFailedModels(PsmDefaultWorkflow defaultWorkflow, PsmWorkflowProcessConfiguration config, File outputDirectory) {
        // Dump the existing models.
        if (config.saveFailedModels()) {
            try {
                outputDirectory.mkdirs();
                DefaultWorkflowSave.saveModels(defaultWorkflow.getTransformationContext(), outputDirectory, ImmutableList.of(config.sqlDialect()));
            } catch (Exception e) {
                log.error("Could not dump models: ", e);
            }
        }

    }

}