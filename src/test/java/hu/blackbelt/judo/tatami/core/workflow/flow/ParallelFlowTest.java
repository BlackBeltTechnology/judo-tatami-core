package hu.blackbelt.judo.tatami.core.workflow.flow;

/*-
 * #%L
 * Judo :: Tatami :: Core
 * %%
 * Copyright (C) 2018 - 2022 BlackBelt Technology
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 * #L%
 */

import hu.blackbelt.judo.tatami.core.workflow.work.Work;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
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
