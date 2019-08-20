package hu.blackbelt.judo.tatami.core.workflow.flow;

import hu.blackbelt.judo.tatami.core.workflow.work.DefaultWorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.Work;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReport;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkStatus;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class ParallelFlowExecutorTest {

    @Test
    public void call() throws Exception {

        // given
        HelloWorldWork work1 = new HelloWorldWork("work1", WorkStatus.COMPLETED);
        HelloWorldWork work2 = new HelloWorldWork("work2", WorkStatus.FAILED);
        ParallelFlowExecutor parallelFlowExecutor = new ParallelFlowExecutor();

        // when
        List<WorkReport> workReports = parallelFlowExecutor.executeInParallel(Arrays.asList(work1, work2));

        // then

        assertThat(workReports, hasSize(2));
        assertThat(work1.isExecuted(), equalTo(true));
        assertThat(work2.isExecuted(), equalTo(true));
    }

    class HelloWorldWork implements Work {

        private String name;
        private WorkStatus status;
        private boolean executed;

        HelloWorldWork(String name, WorkStatus status) {
            this.name = name;
            this.status = status;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public WorkReport call() {
            executed = true;
            return new DefaultWorkReport(status);
        }

        public boolean isExecuted() {
            return executed;
        }
    }

}