package hu.blackbelt.judo.tatami.workflow.osgi;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.judo.tatami.psm2asm.Psm2Asm;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.metatype.annotations.Designate;

import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;
import hu.blackbelt.judo.tatami.workflow.DefaultWorkflowSetupParameters;
import hu.blackbelt.judo.tatami.workflow.PsmDefaultWorkflow;

/**
 * This process represents a {@link PsmDefaultWorkflow} process for a {@link PsmModel} instances.
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

    private TransformationContext transformationContext;

    @Activate
    public void activate(ComponentContext componentContextContext, PsmWorkflowProcessConfiguration config)
            throws URISyntaxException, IOException, PsmModel.PsmValidationException {

        this.psmWorkflowProcessConfiguration = config;

        DefaultWorkflowSetupParameters.DefaultWorkflowSetupParametersBuilder workflowSetupParameters = DefaultWorkflowSetupParameters.defaultWorkflowSetupParameters();
        DefaultWorkflowSetupParameters.addTransformerCalculatedUris(workflowSetupParameters);
        workflowSetupParameters
                .modelName(psmModel.getName())
                .psmModel(psmModel)
                .dialectList(ImmutableList.of(config.sqlDialect()));

        PsmDefaultWorkflow defaultWorkflow = new PsmDefaultWorkflow(workflowSetupParameters);

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