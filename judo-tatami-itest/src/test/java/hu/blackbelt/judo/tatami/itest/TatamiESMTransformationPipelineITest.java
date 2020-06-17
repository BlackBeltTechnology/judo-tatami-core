package hu.blackbelt.judo.tatami.itest;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import org.osgi.framework.InvalidSyntaxException;

import javax.inject.Inject;
import java.io.*;
import java.util.Optional;

import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.SaveArguments.esmSaveArgumentsBuilder;

public abstract class TatamiESMTransformationPipelineITest extends TatamiPSMTransformationPipelineITest {

    @Inject
    protected EsmModel esmModel;

}