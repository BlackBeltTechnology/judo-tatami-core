package hu.blackbelt.judo.tatami.esm2psm;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.tatami.core.TransformationTrace;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static hu.blackbelt.judo.tatami.core.TransformationTraceUtil.createTraceModelResource;
import static hu.blackbelt.judo.tatami.core.TransformationTraceUtil.createTraceModelResourceFromEObjectMap;
import static hu.blackbelt.judo.tatami.core.TransformationTraceUtil.getTransformationTraceFromEtlExecutionContext;
import static hu.blackbelt.judo.tatami.core.TransformationTraceUtil.resolveTransformationTraceAsEObjectMap;

@Builder(builderMethodName = "esm2PsmTransformationTraceBuilder")
public class Esm2PsmTransformationTrace implements TransformationTrace {

    public static final String ESM_2_PSM_URI_POSTFIX = "esm2psm";
    public static final String ESM_2_PSM_TRACE_URI_PREFIX = "esm2psmTrace:";

    @NonNull
    @Getter
    EsmModel esmModel;

    @NonNull
    @Getter
    PsmModel psmModel;

    @NonNull
    Map<EObject, List<EObject>> trace;

    @Override
    public List<Class> getSourceModelTypes() {
        return ImmutableList.of(EsmModel.class);
    }

    @Override
    public List<Object> getSourceModels() {
        return ImmutableList.of(esmModel);
    }

    @Override
    public <T> T getSourceModel(Class<T> sourceModelType) {
        return null;
    }

    @Override
    public <T> ResourceSet getSourceResourceSet(Class<T> sourceModelType) {
        if (sourceModelType == EsmModel.class) {
            return esmModel.getResourceSet();
        }
        throw new IllegalArgumentException("Unknown source model type: " + sourceModelType.getName());
    }

    @Override
    public <T> URI getSourceURI(Class<T> sourceModelType) {
        if (sourceModelType == EsmModel.class) {
            return esmModel.getUri();
        }
        throw new IllegalArgumentException("Unknown source model type: " + sourceModelType.getName());
    }

    @Override
    public Class getTargetModelType() {
        return PsmModel.class;
    }

    @Override
    public Object getTargetModel() {
        return psmModel;
    }

    @Override
    public ResourceSet getTargetResourceSet() {
        return psmModel.getResourceSet();
    }

    @Override
    public URI getTargetURI() {
        return psmModel.getUri();
    }

    @Override
    public Class<? extends TransformationTrace> getType() {
        return Esm2PsmTransformationTrace.class;
    }

    @Override
    public String getTransformationTraceName() {
        return "esm2psm";
    }

    @Override
    public String getModelVersion() {
        return esmModel.getVersion();
    }

    @Override
    public Map<EObject, List<EObject>> getTransformationTrace() {
        return trace;
    }

    @Override
    public String getModelName() {
        return esmModel.getName();
    }


    /**
     * Create PSM 2 ESM Trace model {@link Resource} wth isolated {@link ResourceSet}
     *
     * @param uri
     * @param uriHandler
     *
     * @return the trace {@link Resource} with the registered namespace.
     */
    public static Resource createEsm2PsmTraceResource(org.eclipse.emf.common.util.URI uri,
                                                        URIHandler uriHandler) {
        return createTraceModelResource(ESM_2_PSM_URI_POSTFIX, uri, uriHandler);
    }

    /**
     * Resolves PSM 2 ESM Trace model {@link Resource} and resturns the trace {@link EObject } map
     *
     * @param traceResource
     * @param esmModel
     * @param psmModel
     *
     * @return the trace {@link EObject} map between PSM source and ESM target.
     */
    public static Map<EObject, List<EObject>> resolveEsm2PsmTrace(Resource traceResource,
                                                                    EsmModel esmModel,
                                                                    PsmModel psmModel) {
        return resolveEsm2PsmTrace(traceResource.getContents(), esmModel, psmModel);
    }

    /**
     * Resolves PSM 2 ESM trace:Trace model entries returns the trace {@link EObject } map
     *
     * @param trace
     * @param esmModel
     * @param psmModel
     *
     * @return the trace {@link EObject} map between PSM source and ESM target.
     */
    public static Map<EObject, List<EObject>> resolveEsm2PsmTrace(List<EObject> trace,
                                                                    EsmModel esmModel,
                                                                    PsmModel psmModel) {
        return resolveTransformationTraceAsEObjectMap(trace,
                ImmutableList.of(esmModel.getResourceSet(), psmModel.getResourceSet()));
    }

    /**
     * Convert race {@link EObject } map to trace:Trace model entrie.
     *
     * @param trace
     *
     * @return the trace trace:Trace entries
     */
    public static List<EObject> getEsm2PsmTrace(Map<EObject, List<EObject>> trace) {
        return getTransformationTraceFromEtlExecutionContext(ESM_2_PSM_URI_POSTFIX, trace);
    }

    /**
     * Convert race {@link EObject } map to trace:Trace model {@link Resource} with isolated {@link ResourceSet}.
     *
     * @param trace
     * @param modelUri
     * @param uriHandler
     * @return the trace trace:Trace entries
     */
    public static Resource getEsm2PsmTraceResource(Map<EObject, List<EObject>> trace,
                                                     org.eclipse.emf.common.util.URI modelUri,
                                                     URIHandler uriHandler) {
        return createTraceModelResourceFromEObjectMap(trace, ESM_2_PSM_URI_POSTFIX, modelUri, uriHandler);
    }

    /**
     * Convert race {@link EObject } map to trace:Trace model {@link Resource} with isolated {@link ResourceSet}.
     *
     * @param trace
     * @param modelUri
     *
     * @return the trace trace:Trace entries
     */
    public static Resource getEsm2PsmTraceResource(Map<EObject, List<EObject>> trace,
                                                     org.eclipse.emf.common.util.URI modelUri) {
        return createTraceModelResourceFromEObjectMap(trace, ESM_2_PSM_URI_POSTFIX, modelUri, null);
    }

    /**
     * Create transformation trace from models and trace inputstream.
     * @param modelName
     * @param esmModel
     * @param psmModel
     * @param traceModelFile
     * @return
     * @throws IOException
     */
    public static Esm2PsmTransformationTrace fromModelsAndTrace(String modelName,
                                                                  EsmModel esmModel,
                                                                  PsmModel psmModel,
                                                                  File traceModelFile) throws IOException {
        return fromModelsAndTrace(modelName, esmModel, psmModel, new FileInputStream(traceModelFile));
    }

    /**
     * Create transformation trace from models and trace inputstream.
     * @param modelName
     * @param esmModel
     * @param psmModel
     * @param traceModelInputStream
     * @return
     * @throws IOException
     */
    public static Esm2PsmTransformationTrace fromModelsAndTrace(String modelName,
                                                                  EsmModel esmModel,
                                                                  PsmModel psmModel,
                                                                  InputStream traceModelInputStream) throws IOException {

        checkArgument(esmModel.getName().equals(psmModel.getName()), "Model name does not match");

        Resource traceResoureLoaded = createEsm2PsmTraceResource(
                URI.createURI(ESM_2_PSM_TRACE_URI_PREFIX + modelName),
                null);

        traceResoureLoaded.load(traceModelInputStream, ImmutableMap.of());

        return Esm2PsmTransformationTrace.esm2PsmTransformationTraceBuilder()
                .psmModel(psmModel)
                .esmModel(esmModel)
                .trace(resolveEsm2PsmTrace(traceResoureLoaded, esmModel, psmModel)).build();

    }

    /**
     * Save trace to the given stream.
     *
     * @param outputStream
     * @return
     * @throws IOException
     */
    public Resource save(OutputStream outputStream) throws IOException {
        Resource  traceResoureSaved = getEsm2PsmTraceResource(
                trace,
                URI.createURI(ESM_2_PSM_TRACE_URI_PREFIX + getModelName()));

        traceResoureSaved.save(outputStream, ImmutableMap.of());
        return traceResoureSaved;
    }

    /**
     * Save trace to the given file.
     *
     * @param file
     * @return
     * @throws IOException
     */
    public Resource save(File file) throws IOException {
        return save(new FileOutputStream(file));
    }
}
