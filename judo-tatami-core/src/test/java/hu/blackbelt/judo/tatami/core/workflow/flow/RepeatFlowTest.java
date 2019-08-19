package hu.blackbelt.judo.tatami.core.workflow.flow;

import hu.blackbelt.judo.tatami.core.workflow.work.Work;
import hu.blackbelt.judo.tatami.core.workflow.work.WorkReportPredicate;
import org.junit.Test;
import org.mockito.Mockito;

public class RepeatFlowTest {

    @Test
    public void testRepeatUntil() throws Exception {
        // given
        Work work = Mockito.mock(Work.class);
        WorkReportPredicate predicate = WorkReportPredicate.ALWAYS_FALSE;
        RepeatFlow repeatFlow = RepeatFlow.Builder.aNewRepeatFlow()
                .repeat(work)
                .until(predicate)
                .build();

        // when
        repeatFlow.call();

        // then
        Mockito.verify(work, Mockito.times(1)).call();
    }

    @Test
    public void testRepeatTimes() throws Exception {
        // given
        Work work = Mockito.mock(Work.class);
        RepeatFlow repeatFlow = RepeatFlow.Builder.aNewRepeatFlow()
                .repeat(work)
                .times(3)
                .build();

        // when
        repeatFlow.call();

        // then
        Mockito.verify(work, Mockito.times(3)).call();
    }

}