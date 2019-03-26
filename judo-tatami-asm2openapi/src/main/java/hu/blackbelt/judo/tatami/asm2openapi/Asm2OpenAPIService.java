package hu.blackbelt.judo.tatami.asm2openapi;

import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.osgi.BundleURIHandler;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.openapi.runtime.OpenAPIModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.File;

import static hu.blackbelt.judo.meta.openapi.runtime.OpenAPIModelLoader.createOpenAPIResourceSet;
import static hu.blackbelt.judo.meta.openapi.runtime.OpenAPIModelLoader.registerOpenAPIMetamodel;
import static hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPI.*;

@Component(immediate = true, service = Asm2OpenAPIService.class)
@Slf4j
public class Asm2OpenAPIService {

    public static final String OPENAPI_META_VERSION_RANGE = "OpenAPI-Meta-Version-Range";

    @Reference
    Asm2OpenAPIScriptResource asm2OpenAPIScriptResource;

    public OpenAPIModel install(AsmModel asmModel, BundleContext bundleContext) throws Exception {
        BundleURIHandler bundleURIHandler = new BundleURIHandler("urn", "",
                bundleContext.getBundle());

        ResourceSet resourceSet = createOpenAPIResourceSet(bundleURIHandler);
        registerOpenAPIMetamodel(resourceSet);

        URI openAPIUri = URI.createURI("urn:" + asmModel.getName() + ".openapi");
        Resource openAPIResource = resourceSet.createResource(openAPIUri);

        OpenAPIModel openAPIModel = OpenAPIModel.buildOpenAPIModel()
                .name(asmModel.getName())
                .version(asmModel.getVersion())
                .uri(openAPIUri)
                .checksum(asmModel.getChecksum())
                .resource(openAPIResource)
                .checksum(asmModel.getChecksum())
                .metaVersionRange(bundleContext.getBundle().getHeaders().get(OPENAPI_META_VERSION_RANGE))
                .build();

        executeAsm2OpenAPITransformation(resourceSet, asmModel, openAPIModel, new Slf4jLog(log),
                new File(asm2OpenAPIScriptResource.getSctiptRoot().getAbsolutePath(), "asm2openapi/transformations"));

        return openAPIModel;
    }
}
