package hu.blackbelt.judo.tatami.asm2openapi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel;
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


@Builder(builderMethodName = "asm2OpenAPITransformationTraceBuilder")
public class Asm2OpenAPITransformationTrace implements TransformationTrace {

    public static final String ASM_2_OPENAPI_URI_POSTFIX = "asm2openapi";
    public static final String ASM_2_OPENAPI_TRACE_URI_PREFIX = "asm2openapiTrace:";

    @NonNull
    @Getter
    AsmModel asmModel;

    @NonNull
    @Getter
    OpenapiModel openAPIModel;

    @NonNull
    Map<EObject, List<EObject>> trace;

    @Override
    public List<Class> getSourceModelTypes() {
        return ImmutableList.of(AsmModel.class);
    }

    @Override
    public List<Object> getSourceModels() {
        return ImmutableList.of(asmModel);
    }

    @Override
    public <T> T getSourceModel(Class<T> sourceModelType) {
        return null;
    }

    @Override
    public <T> ResourceSet getSourceResourceSet(Class<T> sourceModelType) {
        if (sourceModelType == AsmModel.class) {
            return asmModel.getResourceSet();
        }
        throw new IllegalArgumentException("Unknown source model type: " + sourceModelType.getName());
    }

    @Override
    public <T> URI getSourceURI(Class<T> sourceModelType) {
        if (sourceModelType == AsmModel.class) {
            return asmModel.getUri();
        }
        throw new IllegalArgumentException("Unknown source model type: " + sourceModelType.getName());
    }

    @Override
    public Class getTargetModelType() {
        return OpenapiModel.class;
    }

    @Override
    public Object getTargetModel() {
        return openAPIModel;
    }

    @Override
    public ResourceSet getTargetResourceSet() {
        return openAPIModel.getResourceSet();
    }

    @Override
    public URI getTargetURI() {
        return openAPIModel.getUri();
    }

    @Override
    public Class<? extends TransformationTrace> getType() {
        return Asm2OpenAPITransformationTrace.class;
    }

    @Override
    public String getTransformationTraceName() {
        return "asm2openapi";
    }

    @Override
    public String getModelVersion() {
        return asmModel.getVersion();
    }

    @Override
    public Map<EObject, List<EObject>> getTransformationTrace() {
        return trace;
    }

    @Override
    public String getModelName() {
        return asmModel.getName();
    }

    /**
     * Create PSM 2 ASM Trace model {@link Resource} wth isolated {@link ResourceSet}
     *
     * @param uri
     * @param uriHandler
     *
     * @return the trace {@link Resource} with the registered namespace.
     */
    public static Resource createAsm2OpenAPITraceResource(org.eclipse.emf.common.util.URI uri,
                                                      URIHandler uriHandler) {
        return createTraceModelResource(ASM_2_OPENAPI_URI_POSTFIX, uri, uriHandler);
    }

    /**
     * Resolves PSM 2 ASM Trace model {@link Resource} and resturns the trace {@link EObject } map
     *
     * @param traceResource
     * @param asmModel
     * @param openapiModel
     *
     * @return the trace {@link EObject} map between PSM source and ASM target.
     */
    public static Map<EObject, List<EObject>> resolveAsm2OpenAPITrace(Resource traceResource,
                                                                  AsmModel asmModel,
                                                                  OpenapiModel openapiModel) {
        return resolveAsm2OpenAPITrace(traceResource.getContents(), asmModel, openapiModel);
    }

    /**
     * Resolves PSM 2 ASM trace:Trace model entries returns the trace {@link EObject } map
     *
     * @param trace
     * @param asmModel
     * @param openapiModel
     *
     * @return the trace {@link EObject} map between PSM source and ASM target.
     */
    public static Map<EObject, List<EObject>> resolveAsm2OpenAPITrace(List<EObject> trace,
                                                                  AsmModel asmModel,
                                                                  OpenapiModel openapiModel) {
        return resolveTransformationTraceAsEObjectMap(trace,
                ImmutableList.of(asmModel.getResourceSet(), openapiModel.getResourceSet()));
    }

    /**
     * Convert race {@link EObject } map to trace:Trace model entrie.
     *
     * @param trace
     *
     * @return the trace trace:Trace entries
     */
    public static List<EObject> getAsm2OpenAPITrace(Map<EObject, List<EObject>> trace) {
        return getTransformationTraceFromEtlExecutionContext(ASM_2_OPENAPI_URI_POSTFIX, trace);
    }

    /**
     * Convert race {@link EObject } map to trace:Trace model {@link Resource} with isolated {@link ResourceSet}.
     *
     * @param trace
     * @param modelUri
     * @param uriHandler
     * @return the trace trace:Trace entries
     */
    public static Resource getAsm2OpenAPITraceResource(Map<EObject, List<EObject>> trace,
                                                   org.eclipse.emf.common.util.URI modelUri,
                                                   URIHandler uriHandler) {
        return createTraceModelResourceFromEObjectMap(trace, ASM_2_OPENAPI_URI_POSTFIX, modelUri, uriHandler);
    }

    /**
     * Convert race {@link EObject } map to trace:Trace model {@link Resource} with isolated {@link ResourceSet}.
     *
     * @param trace
     * @param modelUri
     *
     * @return the trace trace:Trace entries
     */
    public static Resource getAsm2OpenAPITraceResource(Map<EObject, List<EObject>> trace,
                                                   org.eclipse.emf.common.util.URI modelUri) {
        return createTraceModelResourceFromEObjectMap(trace, ASM_2_OPENAPI_URI_POSTFIX, modelUri, null);
    }

    /**
     * Create transformation trace from models and trace inputstream.
     * @param modelName
     * @param asmModel
     * @param openapiModel
     * @param traceModelFile
     * @return
     * @throws IOException
     */
    public static Asm2OpenAPITransformationTrace fromModelsAndTrace(String modelName,
                                                                    AsmModel asmModel,
                                                                    OpenapiModel openapiModel,
                                                                    File traceModelFile) throws IOException {
        return fromModelsAndTrace(modelName, asmModel, openapiModel, new FileInputStream(traceModelFile));
    }

    /**
     * Create transformation trace from models and trace inputstream.
     * @param modelName
     * @param asmModel
     * @param openapiModel
     * @param traceModelInputStream
     * @return
     * @throws IOException
     */
    public static Asm2OpenAPITransformationTrace fromModelsAndTrace(String modelName,
                                                                    AsmModel asmModel,
                                                                    OpenapiModel openapiModel,
                                                                    InputStream traceModelInputStream) throws IOException {

        checkArgument(asmModel.getName().equals(openapiModel.getName()), "Model name does not match");

        Resource traceResoureLoaded = createAsm2OpenAPITraceResource(
                URI.createURI(ASM_2_OPENAPI_TRACE_URI_PREFIX + modelName),
                null);

        traceResoureLoaded.load(traceModelInputStream, ImmutableMap.of());

        return Asm2OpenAPITransformationTrace.asm2OpenAPITransformationTraceBuilder()
                .openAPIModel(openapiModel)
                .asmModel(asmModel)
                .trace(resolveAsm2OpenAPITrace(traceResoureLoaded, asmModel, openapiModel)).build();

    }

    /**
     * Save trace to the given stream.
     *
     * @param outputStream
     * @return
     * @throws IOException
     */
    public Resource save(OutputStream outputStream) throws IOException {
        Resource  traceResoureSaved = getAsm2OpenAPITraceResource(
                trace,
                URI.createURI(ASM_2_OPENAPI_TRACE_URI_PREFIX + getModelName()));

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
