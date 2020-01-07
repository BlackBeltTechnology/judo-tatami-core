package hu.blackbelt.judo.tatami.workflow.osgi;

import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.tatami.core.AbstractModelTracker;
import hu.blackbelt.osgi.utils.osgi.api.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.Designate;

import java.io.IOException;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Tracking {@link PsmModel} instances. When a {@link PsmModel} instance registered this service
 * creates a {@link PsmWorkflowProcess} instances with the corresponding settings.
 * To create the {@link PsmWorkflowProcess} configuration created / deleted over
 * {@link org.osgi.service.cm.ConfigurationAdmin} service, so the lifecycle of the {@link PsmWorkflowProcess}
 * is managed by Declarative Service.
 *
 */
@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = PsmWorkflowDefaultPsmModelTrackerConfiguration.class)
@Slf4j
public class PsmWorkflowDefaultPsmModelTracker extends AbstractModelTracker<PsmModel> {

    public static final String PROP_WORKFLOW_PROCESS_MODEL_NAME_PROPERTY = "modelName";
    public static final String PROP_WORKFLOW_PROCESS_PSM_MODEL_TARGET = "psmModel.target";
    public static final String PROP_WORKFLOW_PROCESS_TRANSFORMATION_CONTEXT_REGISTRATION_SERVICE_TARGET = "transformationContextRegistrationService.target";

    @Reference
    ConfigurationAdmin configurationAdmin;

    ComponentContext componentContext;
    PsmWorkflowDefaultPsmModelTrackerConfiguration config;

    @Activate
    public void activate(ComponentContext componentContextContext, PsmWorkflowDefaultPsmModelTrackerConfiguration config) {
        this.componentContext = componentContextContext;
        this.config = config;
        openTracker(componentContextContext.getBundleContext());
    }

    @Deactivate
    public void deactivate() {
        closeTracker();
    }

    /**
     * When the model arrives, have to create a {@link PsmWorkflowProcess}.
     * @param instance
     */
    @Override
    public void install(PsmModel instance) {
        createPsmWorkflowProcessConfiguration(instance);
    }

    /**
     * When the model arrives, have to destroy the created {@link PsmWorkflowProcess}.
     * @param instance
     */
    @Override
    public void uninstall(PsmModel instance) {
        removePsmWorkflowProcessConfiguration(instance);
    }

    @Override
    public Class<PsmModel> getModelClass() {
        return PsmModel.class;
    }

    /**
     * Create {@link ConfigurationAdmin} configuration for container to manage a new instance of
     * {@link PsmWorkflowProcess}
     * @param psmModel
     */
    private void createPsmWorkflowProcessConfiguration(PsmModel psmModel) {
        String modelName = psmModel.getName();

        final Dictionary<String, Object> psmWorkflowProcessProperties = new Hashtable<>();
        psmWorkflowProcessProperties.put(PROP_WORKFLOW_PROCESS_TRANSFORMATION_CONTEXT_REGISTRATION_SERVICE_TARGET,
                config.transformationContextRegistrationServiceFilter());
        psmWorkflowProcessProperties.put(PROP_WORKFLOW_PROCESS_MODEL_NAME_PROPERTY, psmModel.getName());
        psmWorkflowProcessProperties.put(PROP_WORKFLOW_PROCESS_PSM_MODEL_TARGET, "(name=" + psmModel.getName() + ")");
        psmWorkflowProcessProperties.put(this.getClass().getName(), "true");
        psmWorkflowProcessProperties.put("sqlDialect", config.sqlDialect());

        for (String keyName : Collections.list(componentContext.getProperties().keys())) {
            if (keyName.startsWith("ignore")) {
                psmWorkflowProcessProperties.put(keyName, PropertiesUtil.toBoolean(componentContext.getProperties().get(keyName), false));
            }
        }

        final Configuration psmWorkflowProcessConfiguration;
        try {
            StringBuffer stringBuffer = new StringBuffer();
            for (String key : Collections.list(psmWorkflowProcessProperties.keys())) {
                stringBuffer.append("\n\t" + key + "=" + psmWorkflowProcessProperties.get(key));
            }
            log.info("Registering workflow process for model: " + psmModel.getName() + stringBuffer.toString());

            psmWorkflowProcessConfiguration = configurationAdmin.createFactoryConfiguration(
                    PsmWorkflowProcess.class.getName(), "?");
            psmWorkflowProcessConfiguration.update(psmWorkflowProcessProperties);
        } catch (IOException e) {
            log.error("Invalid PsmWorkflowProcess name: " + modelName, e);
        }
    }

    /**
     * Remove the OSGi {@link ConfigurationAdmin} configuration for the given {@link PsmModel}. The container
     * will destroy the related {@link PsmWorkflowProcess}.
     * @param psmModel
     */
    private void removePsmWorkflowProcessConfiguration(PsmModel psmModel) {
        String modelName = psmModel.getName();

        try {
            final Configuration[] psmWorkflowProcessConfigurationsToDelete = configurationAdmin.listConfigurations(
                    "(&(service.factoryPid=" + PsmWorkflowProcess.class.getName() + ")" +
                            "(" + PROP_WORKFLOW_PROCESS_MODEL_NAME_PROPERTY + "=" + modelName + ")(" + this.getClass().getName() + "=true))");

            if (psmWorkflowProcessConfigurationsToDelete != null) {
                for (final Configuration c : psmWorkflowProcessConfigurationsToDelete) {
                    c.delete();

                    log.debug("PsmWorkflowProcess '{}' removed.", modelName);
                }
            } else {
                log.warn("No configuration found for PsmWorkflowProcess: " + modelName);
            }
        } catch (InvalidSyntaxException | IOException ex) {
            log.error("Invalid PsmWorkflowProcess name: " + modelName, ex);
        }
    }
}