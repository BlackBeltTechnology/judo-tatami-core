package hu.blackbelt.judo.tatami.esm2ui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.ui.runtime.UiModel;
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

@Builder(builderMethodName = "esm2UiTransformationTraceBuilder")
public class Esm2UiTransformationTrace implements TransformationTrace {

    public static final String ESM_2_UI_URI_POSTFIX = "esm2ui";
    public static final String ESM_2_UI_TRACE_URI_PREFIX = "esm2uiTrace:";

    @NonNull
    @Getter
    EsmModel esmModel;

    @NonNull
    @Getter
    UiModel uiModel;

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
        return UiModel.class;
    }

    @Override
    public Object getTargetModel() {
        return uiModel;
    }

    @Override
    public ResourceSet getTargetResourceSet() {
        return uiModel.getResourceSet();
    }

    @Override
    public URI getTargetURI() {
        return uiModel.getUri();
    }

    @Override
    public Class<? extends TransformationTrace> getType() {
        return Esm2UiTransformationTrace.class;
    }

    @Override
    public String getTransformationTraceName() {
        return "esm2ui";
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
     * Create UI 2 ESM Trace model {@link Resource} wth isolated {@link ResourceSet}
     *
     * @param uri
     * @param uriHandler
     *
     * @return the trace {@link Resource} with the registered namespace.
     */
    public static Resource createEsm2UiTraceResource(org.eclipse.emf.common.util.URI uri,
                                                        URIHandler uriHandler) {
        return createTraceModelResource(ESM_2_UI_URI_POSTFIX, uri, uriHandler);
    }

    /**
     * Resolves UI 2 ESM Trace model {@link Resource} and resturns the trace {@link EObject } map
     *
     * @param traceResource
     * @param esmModel
     * @param uiModel
     *
     * @return the trace {@link EObject} map between UI source and ESM target.
     */
    public static Map<EObject, List<EObject>> resolveEsm2UiTrace(Resource traceResource,
                                                                    EsmModel esmModel,
                                                                    UiModel uiModel) {
        return resolveEsm2UiTrace(traceResource.getContents(), esmModel, uiModel);
    }

    /**
     * Resolves UI 2 ESM trace:Trace model entries returns the trace {@link EObject } map
     *
     * @param trace
     * @param esmModel
     * @param uiModel
     *
     * @return the trace {@link EObject} map between UI source and ESM target.
     */
    public static Map<EObject, List<EObject>> resolveEsm2UiTrace(List<EObject> trace,
                                                                    EsmModel esmModel,
                                                                    UiModel uiModel) {
        return resolveTransformationTraceAsEObjectMap(trace,
                ImmutableList.of(esmModel.getResourceSet(), uiModel.getResourceSet()));
    }

    /**
     * Convert race {@link EObject } map to trace:Trace model entrie.
     *
     * @param trace
     *
     * @return the trace trace:Trace entries
     */
    public static List<EObject> getEsm2UiTrace(Map<EObject, List<EObject>> trace) {
        return getTransformationTraceFromEtlExecutionContext(ESM_2_UI_URI_POSTFIX, trace);
    }

    /**
     * Convert race {@link EObject } map to trace:Trace model {@link Resource} with isolated {@link ResourceSet}.
     *
     * @param trace
     * @param modelUri
     * @param uriHandler
     * @return the trace trace:Trace entries
     */
    public static Resource getEsm2UiTraceResource(Map<EObject, List<EObject>> trace,
                                                     org.eclipse.emf.common.util.URI modelUri,
                                                     URIHandler uriHandler) {
        return createTraceModelResourceFromEObjectMap(trace, ESM_2_UI_URI_POSTFIX, modelUri, uriHandler);
    }

    /**
     * Convert race {@link EObject } map to trace:Trace model {@link Resource} with isolated {@link ResourceSet}.
     *
     * @param trace
     * @param modelUri
     *
     * @return the trace trace:Trace entries
     */
    public static Resource getEsm2UiTraceResource(Map<EObject, List<EObject>> trace,
                                                     org.eclipse.emf.common.util.URI modelUri) {
        return createTraceModelResourceFromEObjectMap(trace, ESM_2_UI_URI_POSTFIX, modelUri, null);
    }

    /**
     * Create transformation trace from models and trace inputstream.
     * @param modelName
     * @param esmModel
     * @param uiModel
     * @param traceModelFile
     * @return
     * @throws IOException
     */
    public static Esm2UiTransformationTrace fromModelsAndTrace(String modelName,
                                                                  EsmModel esmModel,
                                                                  UiModel uiModel,
                                                                  File traceModelFile) throws IOException {
        return fromModelsAndTrace(modelName, esmModel, uiModel, new FileInputStream(traceModelFile));
    }

    /**
     * Create transformation trace from models and trace inputstream.
     * @param modelName
     * @param esmModel
     * @param uiModel
     * @param traceModelInputStream
     * @return
     * @throws IOException
     */
    public static Esm2UiTransformationTrace fromModelsAndTrace(String modelName,
                                                                  EsmModel esmModel,
                                                                  UiModel uiModel,
                                                                  InputStream traceModelInputStream) throws IOException {

        checkArgument(esmModel.getName().equals(uiModel.getName()), "Model name does not match");

        Resource traceResoureLoaded = createEsm2UiTraceResource(
                URI.createURI(ESM_2_UI_TRACE_URI_PREFIX + modelName),
                null);

        traceResoureLoaded.load(traceModelInputStream, ImmutableMap.of());

        return Esm2UiTransformationTrace.esm2UiTransformationTraceBuilder()
                .uiModel(uiModel)
                .esmModel(esmModel)
                .trace(resolveEsm2UiTrace(traceResoureLoaded, esmModel, uiModel)).build();

    }

    /**
     * Save trace to the given stream.
     *
     * @param outputStream
     * @return
     * @throws IOException
     */
    public Resource save(OutputStream outputStream) throws IOException {
        Resource  traceResoureSaved = getEsm2UiTraceResource(
                trace,
                URI.createURI(ESM_2_UI_TRACE_URI_PREFIX + getModelName()));

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
