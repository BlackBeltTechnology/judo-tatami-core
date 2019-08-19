package hu.blackbelt.judo.tatami.core.workflow.flow;

import hu.blackbelt.judo.tatami.core.workflow.work.Work;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ParallelFlowTest {

    @Test
    public void call() throws Exception {
        // given
        Work work1 = mock(Work.class);
        Work work2 = mock(Work.class);
        ParallelFlowExecutor parallelFlowExecutor = mock(ParallelFlowExecutor.class);
        List<Work> works = Arrays.asList(work1, work2);
        ParallelFlow parallelFlow = new ParallelFlow("pf", works, parallelFlowExecutor);

        // when
        ParallelFlowReport parallelFlowReport = parallelFlow.call();

        // then
        assertThat(parallelFlowReport, notNullValue());
        verify(parallelFlowExecutor).executeInParallel(works);
    }

}