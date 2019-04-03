package hu.blackbelt.judo.tatami.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.exceptions.ScriptExecutionException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.epsilon.etl.EtlModule;
import org.eclipse.epsilon.etl.trace.Transformation;
import org.eclipse.epsilon.etl.trace.TransformationTrace;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.eclipse.emf.ecore.util.builder.EcoreBuilders.*;

public class TransformationTraceUtil {

    public static final String HTTP_WWW_BLACKBELT_HU_META_TRASFORMATION_TRACE = "http:///www.blackbelt.hu/meta/trasformation/trace/";
    public static final String TRACE_CLASS = "Trace";
    public static final String SOURCE_URI = "sourceUri";
    public static final String TARRGET_URIS = "tarrgetUris";

    public static Map<EObject, List<EObject>> resolveTransformationTrace(List<EObject> traceEntries, List<ResourceSet> resourcesToResolve) {

        Map<EObject, List<EObject>> ret = Maps.newHashMap();
        for (EObject tr : traceEntries) {

            EClass traceClass = tr.eClass();
            EAttribute srcUriAttribute = (EAttribute) traceClass.getEStructuralFeature(SOURCE_URI);
            EAttribute targetUriAttributes = (EAttribute) traceClass.getEStructuralFeature(TARRGET_URIS);

            EObject source = null;
            URI sourceURI = URI.createURI((String) tr.eGet(srcUriAttribute, false));
            for (ResourceSet rs : resourcesToResolve) {
                source = rs.getEObject(sourceURI, false);
                if (source != null) {
                    break;
                }
            }
            if (source == null) {
                throw new RuntimeException("Source entry not found on the given resources: " + sourceURI);
            }

            List<EObject> targetList = Lists.newArrayList();
            for (String t : (Collection<String>) tr.eGet(targetUriAttributes, false)) {
                EObject target = null;
                URI targetURI = URI.createURI(t);
                for (ResourceSet rs : resourcesToResolve) {
                    target = rs.getEObject(targetURI, false);
                    if (target != null) {
                        break;
                    }
                }
                if (target == null) {
                    throw new RuntimeException("Target entry not found on the given resources: " + targetURI);
                }

                targetList.add(target);
            }
            ret.put(source, targetList);
        }
        return ret;
    }

    public static List<EObject> getTransformationTrace(String traceName, EtlExecutionContext etlExecutionContext) throws ScriptExecutionException {
        ResourceSet resourceSet = createTraceResourceSet(traceName);
        EPackage tracePackage = resourceSet.getPackageRegistry().getEPackage(HTTP_WWW_BLACKBELT_HU_META_TRASFORMATION_TRACE + traceName);
        EClassImpl traceClass = (EClassImpl) tracePackage.getEClassifier(TRACE_CLASS);
        EAttribute srcUriAttribute = (EAttribute) traceClass.getEStructuralFeature(SOURCE_URI);
        EAttribute targetUriAttributes = (EAttribute) traceClass.getEStructuralFeature(TARRGET_URIS);

        //EObject trace = tracePackage.getEFactoryInstance().create(traceClass);
        TransformationTrace transformationTrace = ((EtlModule) etlExecutionContext.getModule(ImmutableMap.of())).getContext().getTransformationTrace();

        List<EObject> traceEntries = Lists.newArrayList();
        for (Transformation transformation : transformationTrace.getTransformations()) {
            EObject trace = tracePackage.getEFactoryInstance().create(traceClass);
            trace.eSet(srcUriAttribute, EcoreUtil.getURI((EObject) transformation.getSource()).toString());

            List<String> targets = Lists.newArrayList();
            for (Object t : transformation.getTargets()) {
                targets.add(EcoreUtil.getURI((EObject) t).toString());
            }
            trace.eSet(targetUriAttributes, targets);
            traceEntries.add(trace);
        }
        return traceEntries;
    }

    public static List<EObject> getTransformationTrace(String traceName, Map<EObject, List<EObject>> traceMap) throws ScriptExecutionException {
        ResourceSet resourceSet = createTraceResourceSet(traceName);
        EPackage tracePackage = resourceSet.getPackageRegistry().getEPackage(HTTP_WWW_BLACKBELT_HU_META_TRASFORMATION_TRACE + traceName);
        EClassImpl traceClass = (EClassImpl) tracePackage.getEClassifier(TRACE_CLASS);
        EAttribute srcUriAttribute = (EAttribute) traceClass.getEStructuralFeature(SOURCE_URI);
        EAttribute targetUriAttributes = (EAttribute) traceClass.getEStructuralFeature(TARRGET_URIS);

        List<EObject> traceEntries = Lists.newArrayList();
        for (EObject source : traceMap.keySet()) {
            EObject trace = tracePackage.getEFactoryInstance().create(traceClass);
            trace.eSet(srcUriAttribute, EcoreUtil.getURI(source).toString());

            List<String> targets = Lists.newArrayList();
            for (Object t : traceMap.get(source)) {
                targets.add(EcoreUtil.getURI((EObject) t).toString());
            }
            trace.eSet(targetUriAttributes, targets);
            traceEntries.add(trace);
        }
        return traceEntries;
    }


    public static ResourceSet createTraceResourceSet(String traceName) {
        final EcorePackage ecore = EcorePackage.eINSTANCE;

        final EClass trace = newEClassBuilder()
                .withName(TRACE_CLASS)
                .withEStructuralFeatures(
                        newEAttributeBuilder()
                                .withName(SOURCE_URI)
                                .withEType(ecore.getEString()))
                .withEStructuralFeatures(
                        newEAttributeBuilder()
                                .withName(TARRGET_URIS)
                                .withUpperBound(ETypedElement.UNBOUNDED_MULTIPLICITY)
                                .withEType(ecore.getEString()))
                .build();

        final EPackage ePackage = newEPackageBuilder()
                .withName("trace")
                .withNsPrefix("trace")
                .withNsURI(HTTP_WWW_BLACKBELT_HU_META_TRASFORMATION_TRACE + traceName)
                .withEClassifiers(trace)
                .build();


        final ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new EcoreResourceFactoryImpl());
        final Resource resource = resourceSet.createResource(URI.createURI("trace:" + traceName));
        resource.getContents().add(ePackage);

        // Register packages
        resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
        return resourceSet;
    }
}
