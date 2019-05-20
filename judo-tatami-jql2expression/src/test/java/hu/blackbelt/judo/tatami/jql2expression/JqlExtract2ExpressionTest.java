package hu.blackbelt.judo.tatami.jql2expression;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.NameMappedURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.NioFilesystemnRelativePathURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.asm.runtime.AsmModelLoader;
import hu.blackbelt.judo.meta.asm.runtime.AsmModelLoader.LocalAsmPackageRegistration;
import hu.blackbelt.judo.meta.expression.Expression;
import hu.blackbelt.judo.meta.expression.constant.Instance;
import hu.blackbelt.judo.meta.expression.runtime.EvaluationNode;
import hu.blackbelt.judo.meta.expression.runtime.ExpressionEvaluator;
import hu.blackbelt.judo.meta.expression.runtime.ExpressionModel;
import hu.blackbelt.judo.meta.psm.jql.extract.runtime.PsmJqlExtractModel;
import hu.blackbelt.judo.meta.psm.jql.extract.runtime.PsmJqlExtractModelLoader;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.FileSystems;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModelLoader.createAsmResourceSet;
import static hu.blackbelt.judo.meta.expression.runtime.ExpressionModelLoader.createExpressionResourceSet;
import static hu.blackbelt.judo.meta.expression.runtime.ExpressionModelLoader.saveExpressionModel;
import static hu.blackbelt.judo.meta.psm.jql.extract.runtime.PsmJqlExtractModelLoader.createPsmJqlExtractResourceSet;
import static hu.blackbelt.judo.tatami.jql2expression.JqlExtract2Expression.*;

@Slf4j
public class JqlExtract2ExpressionTest {

    public static final String JQLEXTRACT_2_EXPRESSION_MODEL = "jqlextract2expression.model";
    public static final String TRACE_JQLEXTRACT_2_EXPRESSION = "trace:jqlextract2expression";
    public static final String EXPRESSION_NORTHWIND = "expression:northwind";
    public static final String JQLEXTRACT_NORTHWIND = "jqlextract:northwind";
    public static final String ASN_NORTHWIND = "asm:northwind";
    public static final String URN_NORTHWIND_JQLEXTRACT = "urn:northwind-jqlextract.model";
    public static final String URN_NORTHWIND_EXPRESSION = "urn:northwind-expression.model";
    public static final String URN_NORTHWIND_ASM = "urn:northwind-asm.model";
    public static final String NORTHWIND = "northwind";
    public static final String VERSION = "1.0.0";

    URIHandler uriHandler;
    Log slf4jlog;
    AsmModel asmModel;
    PsmJqlExtractModel jqlExtractModel;

    @Before
    public void setUp() throws Exception {
        // Set our custom handler
        uriHandler = new NameMappedURIHandlerImpl(
                ImmutableList.of(new NioFilesystemnRelativePathURIHandlerImpl("urn", FileSystems.getDefault(), targetDir().getAbsolutePath())),
                ImmutableMap.of(
                        URI.createURI(JQLEXTRACT_NORTHWIND), URI.createURI(URN_NORTHWIND_JQLEXTRACT),
                        URI.createURI(ASN_NORTHWIND), URI.createURI(URN_NORTHWIND_ASM),
                        URI.createURI(EXPRESSION_NORTHWIND), URI.createURI(URN_NORTHWIND_EXPRESSION))
        );

        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading PSM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        ResourceSet jqlExtractResourceSet = createPsmJqlExtractResourceSet(uriHandler);
        jqlExtractModel = PsmJqlExtractModelLoader.loadPsmJqlExtractModel(
                jqlExtractResourceSet,
                URI.createURI(JQLEXTRACT_NORTHWIND),
                NORTHWIND,
                VERSION);

        // Loading ASM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        ResourceSet asmResourceSet = createAsmResourceSet(uriHandler, new LocalAsmPackageRegistration());
        asmModel = AsmModelLoader.loadAsmModel(
                asmResourceSet,
                URI.createURI(ASN_NORTHWIND),
                NORTHWIND,
                VERSION);
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testJqlExtract2ExpressionTransformation() throws Exception {

        // Creating expression resource set.
        ResourceSet expressionResourceSet = createExpressionResourceSet(uriHandler);


        // Peparing expression model
        ExpressionModel expressionModel = ExpressionModel.buildExpressionModel()
                .name(jqlExtractModel.getName())
                .resourceSet(expressionResourceSet)
                .uri(URI.createURI(EXPRESSION_NORTHWIND))
                .version(jqlExtractModel.getVersion())
                .build();


        JqlExtract2ExpressionTransformationTrace jqlExtract2ExpressionTransformationTrace = executeJqlExtract2ExpressionTransformation(expressionResourceSet, asmModel, jqlExtractModel, expressionModel,
                new Slf4jLog(log), new File(targetDir().getAbsolutePath(), "epsilon/transformations/expression"));


        // Saving trace map
        Resource traceResoureSaved = new XMIResourceImpl();
        traceResoureSaved.getContents().addAll(getJqlExtract2ExpressionTrace(jqlExtract2ExpressionTransformationTrace.getTrace()));
        traceResoureSaved.save(new FileOutputStream(new File(targetDir().getAbsolutePath(), JQLEXTRACT_2_EXPRESSION_MODEL)), ImmutableMap.of());

        // Loading trace map
        ResourceSet traceLoadedResourceSet = createJqlExtract2ExpressionTraceResourceSet();
        Resource traceResoureLoaded = traceLoadedResourceSet.createResource(URI.createURI(TRACE_JQLEXTRACT_2_EXPRESSION));
        traceResoureLoaded.load(new FileInputStream(new File(targetDir().getAbsolutePath(), JQLEXTRACT_2_EXPRESSION_MODEL)), ImmutableMap.of());

        // Resolve serialized URI's as EObject map
        Map<EObject, List<EObject>> resolvedTrace = resolveJqlExtract2ExpressionTrace(traceResoureLoaded, jqlExtractModel, expressionModel);

        try {
            // Printing trace
            for (EObject e : resolvedTrace.keySet()) {
                for (EObject t : resolvedTrace.get(e)) {
                    log.debug(e.toString() + " -> " + t.toString());
                }
            }

            // Print objects
            TreeIterator<Notifier> iter = expressionResourceSet.getAllContents();
            while (iter.hasNext()) {
                final Notifier obj = iter.next();
                log.debug(obj.toString());
            }

            saveExpressionModel(expressionModel);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }

        final Iterable<Notifier> asmContents = expressionResourceSet::getAllContents;
        final List<Expression> expressions = StreamSupport.stream(asmContents.spliterator(), true)
                .filter(e -> e instanceof Expression).map(e -> (Expression) e)
                .collect(Collectors.toList());

        final ExpressionEvaluator evaluator = new ExpressionEvaluator();
        evaluator.init(expressions);

        final Map<Expression, EvaluationNode> nodes = expressions.stream().filter(e -> (e instanceof Instance)).collect(Collectors.toMap(e -> e, e -> evaluator.getEvaluationNode(e)));

        log.info("Root nodes: {}" + nodes);

        //final QueryModelBuilder queryModelBuilder = new QueryModelBuilder(modelAdapter, evaluator);
        //final Select queryModel = queryModelBuilder.createQueryModel(evaluationNode, (EClass) modelAdapter.get(orderInfoType).get());
    }


    public File targetDir() {
        String relPath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        File targetDir = new File(relPath);
        if (!targetDir.exists()) {
            targetDir.mkdir();
        }
        return targetDir;
    }
}
