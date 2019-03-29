package hu.blackbelt.judo.tatami.jql2expression;

import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.osgi.BundleURIHandler;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.expression.runtime.ExpressionModel;
import hu.blackbelt.judo.meta.psm.jql.extract.runtime.PsmJqlExtractModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.File;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModelLoader.registerAsmMetamodel;
import static hu.blackbelt.judo.meta.expression.runtime.ExpressionModelLoader.*;
import static hu.blackbelt.judo.meta.psm.jql.extract.runtime.PsmJqlExtractModelLoader.createPsmJqlExtractResourceSet;
import static hu.blackbelt.judo.meta.psm.jql.extract.runtime.PsmJqlExtractModelLoader.registerPsmJqlMetamodel;

@Component(immediate = true, service = JqlExtract2ExpressionService.class)
@Slf4j
public class JqlExtract2ExpressionService {

    public static final String EXPRESSION_META_VERSION_RANGE = "Expression-Meta-Version-Range";

    @Reference
    JqlExtract2ExpressionScriptResource jqlExtract2ExpressionScriptResource;

    public ExpressionModel install(AsmModel asmModel, PsmJqlExtractModel jqlExtractModel, BundleContext bundleContext) throws Exception {
        BundleURIHandler bundleURIHandler = new BundleURIHandler("urn", "",
                bundleContext.getBundle());

        ResourceSet expressionResourceSet = createExpressionResourceSet(bundleURIHandler);

        URI expressionModelUri = URI.createURI("urn:" + jqlExtractModel.getName() + ".expression");
        Resource expressionResource =  expressionResourceSet.createResource(expressionModelUri);

        ExpressionModel expressionModel = ExpressionModel.buildExpressionModel()
                .name(asmModel.getName())
                .version(asmModel.getVersion())
                .uri(expressionModelUri)
                .resourceSet(expressionResourceSet)
                .checksum(asmModel.getChecksum())
                .metaVersionRange(bundleContext.getBundle().getHeaders().get(EXPRESSION_META_VERSION_RANGE))
                .build();


        JqlExtract2Expression.executeJqlExtract2ExpressionTransformation(expressionResourceSet, asmModel, jqlExtractModel, expressionModel, new Slf4jLog(log),
                new File(jqlExtract2ExpressionScriptResource.getSctiptRoot().getAbsolutePath(), "jql2expression/transformations/expression") );

        return expressionModel;
    }
}
