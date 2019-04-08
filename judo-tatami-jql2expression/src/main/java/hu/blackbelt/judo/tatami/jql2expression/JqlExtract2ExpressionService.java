package hu.blackbelt.judo.tatami.jql2expression;

import com.google.common.collect.Maps;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.osgi.BundleURIHandler;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.expression.runtime.ExpressionModel;
import hu.blackbelt.judo.meta.psm.jql.extract.runtime.PsmJqlExtractModel;
import hu.blackbelt.judo.tatami.core.TrackInfo;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;

import static hu.blackbelt.judo.meta.expression.runtime.ExpressionModelLoader.createExpressionResourceSet;
import static hu.blackbelt.judo.tatami.jql2expression.JqlExtract2Expression.executeJqlExtract2ExpressionTransformation;

@Component(immediate = true, service = JqlExtract2ExpressionService.class)
@Slf4j
public class JqlExtract2ExpressionService {

    public static final String EXPRESSION_META_VERSION_RANGE = "Expression-Meta-Version-Range";

    @Reference
    JqlExtract2ExpressionScriptResource jqlExtract2ExpressionScriptResource;

    Map<PsmJqlExtractModel, ServiceRegistration<TrackInfo>> jqlextract2expressionTrackInfoRegistration = Maps.newHashMap();

    public ExpressionModel install(AsmModel asmModel, PsmJqlExtractModel jqlExtractModel, BundleContext bundleContext) throws Exception {
        BundleURIHandler bundleURIHandler = new BundleURIHandler("urn", "",
                bundleContext.getBundle());

        ResourceSet expressionResourceSet = createExpressionResourceSet(bundleURIHandler);

        ExpressionModel expressionModel = ExpressionModel.buildExpressionModel()
                .name(asmModel.getName())
                .version(asmModel.getVersion())
                .uri(URI.createURI("urn:" + jqlExtractModel.getName() + ".expression"))
                .resourceSet(expressionResourceSet)
                .checksum(asmModel.getChecksum())
                .metaVersionRange(bundleContext.getBundle().getHeaders().get(EXPRESSION_META_VERSION_RANGE))
                .build();

        JqlExtract2ExpressionTrackInfo jqlExtract2ExpressionTrackInfo =
        executeJqlExtract2ExpressionTransformation(expressionResourceSet, asmModel, jqlExtractModel, expressionModel, new Slf4jLog(log),
                new File(jqlExtract2ExpressionScriptResource.getSctiptRoot().getAbsolutePath(), "jql2expression/transformations/expression") );

        jqlextract2expressionTrackInfoRegistration.put(jqlExtractModel, bundleContext.registerService(TrackInfo.class, jqlExtract2ExpressionTrackInfo, new Hashtable<>()));

        return expressionModel;
    }

    public void uninstall(PsmJqlExtractModel psmJqlExtractModel) {
        if (jqlextract2expressionTrackInfoRegistration.containsKey(psmJqlExtractModel)) {
            jqlextract2expressionTrackInfoRegistration.get(psmJqlExtractModel).unregister();
        } else {
            log.error("JQLEXTRACT model is not installed: " + psmJqlExtractModel.toString());
        }
    }
}
