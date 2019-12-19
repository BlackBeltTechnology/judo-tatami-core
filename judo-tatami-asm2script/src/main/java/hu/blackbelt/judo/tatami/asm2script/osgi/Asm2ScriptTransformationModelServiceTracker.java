package hu.blackbelt.judo.tatami.asm2script.osgi;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.expression.runtime.ExpressionModel;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.script.runtime.ScriptModel;
import hu.blackbelt.judo.tatami.core.AbstractModelPairTracker;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;


@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Slf4j
public class Asm2ScriptTransformationModelServiceTracker extends AbstractModelPairTracker<AsmModel, MeasureModel> {

    @Reference
    Asm2ScriptTranformationSerivce asm2ScriptTranformationSerivce;

    Map<String, ServiceRegistration<ScriptModel>> registrations = new ConcurrentHashMap<>();
    Map<String, ScriptModel> models = new HashMap<>();

    @Activate
    protected void activate(ComponentContext contextPar) {
        componentContext = contextPar;
        openTracker(contextPar.getBundleContext());
    }

    @Deactivate
    protected void deactivate() {
        closeTracker();
    }

    private ComponentContext componentContext;


    @Override
    public void install(AsmModel asmModel, MeasureModel measureModel) {
        String key = asmModel.getName();
        ScriptModel scriptModel = null;
        if (models.containsKey(key)) {
            log.error("Model already loaded: " + asmModel.getName());
            return;
        }

        try {
            scriptModel = asm2ScriptTranformationSerivce.install(asmModel, measureModel);
            log.info("Registering model: " + scriptModel);
            ServiceRegistration<ScriptModel> modelServiceRegistration =
                    componentContext.getBundleContext()
                            .registerService(ScriptModel.class, scriptModel, scriptModel.toDictionary());
            models.put(key, scriptModel);
            registrations.put(key, modelServiceRegistration);
        } catch (Exception e) {
            log.error("Could not register Script Model: " + asmModel.getName(), e);
        }
    }

    @Override
    public void uninstall(AsmModel asmModel, MeasureModel measureModel) {
        String key = asmModel.getName();
        if (!registrations.containsKey(key)) {
            log.error("Model is not registered: " + asmModel.getName());
        } else {
            registrations.get(key).unregister();
            registrations.remove(key);
            models.remove(key);
        }
    }

    @Override
    public Class<AsmModel> getModelClass1() {
        return AsmModel.class;
    }

    @Override
    public Class<MeasureModel> getModelClass2() {
        return MeasureModel.class;
    }

    @Override
    public Function<AsmModel, String> getModel1NameExtractorFunction() {
        return asmModel -> asmModel.getName();
    }

    @Override
    public Function<MeasureModel, String> getModel2NameExtractorFunction() {
        return measureModel -> measureModel.getName();
    }
}
