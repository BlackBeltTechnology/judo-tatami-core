package hu.blackbelt.judo.tatami.asm2rdbms;

import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.osgi.BundleURIHandler;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModelLoader;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import java.io.File;

import static hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModelLoader.createRdbmsResourceSet;
import static hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModelLoader.registerRdbmsMetamodel;
import static hu.blackbelt.judo.tatami.asm2rdbms.Asm2Rdbms.*;


@ObjectClassDefinition(name = "ASM2RDMS Transformation", description = "Judo Tatami ASM2RDMS Transformation")
@interface Asm2RdbmsSerivceServiceConfiguration {
    @AttributeDefinition(name = "dialects", description = "SQL Dialecst to transforms")
    String[] dialects() default {DIALECT_HSQLDB, DIALECT_POSTGGRESSQL, DIALECT_ORACLE} ;
}
@Component(immediate = true, service = Asm2RdbmsSerivce.class)
@Designate(ocd = Asm2RdbmsSerivceServiceConfiguration.class)
@Slf4j
public class Asm2RdbmsSerivce {

    public static final String RDBMS_META_VERSION_RANGE = "Rdbms-Meta-Version-Range";

    @Reference
    Asm2RdbmsScriptResource asm2RdbmsScriptResource;

    @Reference
    Asm2RdbmsModelResource asm2RdbmsModelResource;

    Asm2RdbmsSerivceServiceConfiguration configuration;

    @Activate
    public void activate(Asm2RdbmsSerivceServiceConfiguration configuration) {
        this.configuration = configuration;
    }

    public RdbmsModel install(AsmModel asmModel, BundleContext bundleContext) throws Exception {
        BundleURIHandler bundleURIHandler = new BundleURIHandler("urn", "",
                bundleContext.getBundle());



        ResourceSet resourceSet = createRdbmsResourceSet(bundleURIHandler);
        RdbmsModel rdbmsModel = RdbmsModelLoader.loadRdbmsModel(resourceSet, URI.createURI("urn:" + asmModel.getName()),
                asmModel.getName(), asmModel.getVersion(), asmModel.getChecksum(),
                bundleContext.getBundle().getHeaders().get(RDBMS_META_VERSION_RANGE));

        registerRdbmsMetamodel(resourceSet);

        // TODO: make configurable dialect
        Asm2Rdbms.executeAsm2RdbmsTransformation(resourceSet, asmModel, rdbmsModel, new Slf4jLog(log),
                new File(asm2RdbmsScriptResource.getSctiptRoot().getAbsolutePath(), "asm2rdbms/transformations"),
                asm2RdbmsModelResource.getModelRoot(),
                "hsqldb");

        return rdbmsModel;
    }
}