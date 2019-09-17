package hu.blackbelt.judo.tatami.rdbms2liquibase;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

import static hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngineBuilder.aNewWorkFlowEngine;
import static hu.blackbelt.judo.tatami.core.workflow.flow.ParallelFlow.Builder.aNewParallelFlow;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngine;
import hu.blackbelt.judo.tatami.core.workflow.flow.WorkFlow;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.core.workflow.work.Work;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;

public class Rdbms2LiquibaseWorkTest {
	
	public static final String NORTHWIND = "northwind";
	public static final String NORTHWIND_RDBMS_MODEL = "northwind-rdbms.model";
	public static final List<String> DIALECT_LIST = new LinkedList<String>(Arrays.asList("hsqldb", "oracle"));
    
	List<Rdbms2LiquibaseWork> rdbms2LiquibaseWorks = Lists.newArrayList();
	TransformationContext transformationContext;

	@BeforeEach
	void setUp() throws IOException, RdbmsModel.RdbmsValidationException {
		RdbmsModel rdbmsModel = RdbmsModel.buildRdbmsModel().name(NORTHWIND).build();

		transformationContext = new TransformationContext(NORTHWIND);
		transformationContext.put("rdbms:hsqldb",rdbmsModel);
		transformationContext.put("rdbms:oracle",rdbmsModel);

		DIALECT_LIST.forEach(dialect -> new Rdbms2LiquibaseWork(transformationContext,
				new File("src/main/epsilon/transformations").toURI(),
				dialect));
	}

	@Test
	void testSimpleWorkflow() {
		WorkFlow workflow = aNewParallelFlow().execute(rdbms2LiquibaseWorks.toArray(new Work[rdbms2LiquibaseWorks.size()])).build();

		WorkFlowEngine workFlowEngine = aNewWorkFlowEngine().build();
		WorkReport workReport = workFlowEngine.run(workflow);

		assertThat(workReport.getStatus(), equalTo(WorkStatus.COMPLETED));
	}

}
