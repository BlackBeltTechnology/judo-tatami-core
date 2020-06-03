package hu.blackbelt.judo.tatami.asm2rdbms;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngine;
import hu.blackbelt.judo.tatami.core.workflow.flow.WorkFlow;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.core.workflow.work.Work;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;

import static hu.blackbelt.judo.tatami.asm2rdbms.Asm2Rdbms.calculateAsm2RdbmsModelURI;
import static hu.blackbelt.judo.tatami.asm2rdbms.Asm2Rdbms.calculateAsm2RdbmsTransformationScriptURI;
import static hu.blackbelt.judo.tatami.core.workflow.engine.WorkFlowEngineBuilder.aNewWorkFlowEngine;
import static hu.blackbelt.judo.tatami.core.workflow.flow.SequentialFlow.Builder.aNewSequentialFlow;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
public class Asm2RdbmsWorkTest {

	public static final String NORTHWIND = "northwind";
	public static final List<String> DIALECT_LIST = new LinkedList<String>(Arrays.asList("hsqldb", "oracle"));

	List<Asm2RdbmsWork> asm2RdbmsWorks = Lists.newArrayList();
	TransformationContext transformationContext;

	@BeforeEach
	void setUp() throws Exception {
		NorthwindModelLoader northwindModelLoader = NorthwindModelLoader.createNorthwindModelLoader(NORTHWIND);
		AsmModel asmModel = northwindModelLoader.getAsmModel();

		transformationContext = new TransformationContext(NORTHWIND);
		transformationContext.put(asmModel);
		
		DIALECT_LIST.forEach(dialect -> {
			try {
				asm2RdbmsWorks.add(new Asm2RdbmsWork(transformationContext,
						calculateAsm2RdbmsTransformationScriptURI(), calculateAsm2RdbmsModelURI(),
						dialect));
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
		});
	}

	@Test
	void testSimpleWorkflow() {
		WorkFlow workflow = aNewSequentialFlow().execute(asm2RdbmsWorks.toArray(new Work[asm2RdbmsWorks.size()])).build();
		//WorkFlow workflow = aNewParallelFlow().execute(asm2RdbmsWorks.toArray(new Work[asm2RdbmsWorks.size()])).build();

		WorkFlowEngine workFlowEngine = aNewWorkFlowEngine().build();
		WorkReport workReport = workFlowEngine.run(workflow);

		if (workReport.getError() != null) {
			log.error("Error found, ", workReport.getError());
		}

		assertThat(workReport.getStatus(), equalTo(WorkStatus.COMPLETED));
	}
}
