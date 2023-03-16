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
import org.mockito.InOrder;
import org.mockito.Mockito;

public class SequentialFlowTest {

    @Test
    public void call() throws Exception {
        // given
        Work work1 = Mockito.mock(Work.class);
        Work work2 = Mockito.mock(Work.class);
        Work work3 = Mockito.mock(Work.class);
        SequentialFlow sequentialFlow = SequentialFlow.Builder.aNewSequentialFlow()
                .execute(work1)
                .then(work2)
                .then(work3)
                .build();

        // when
        sequentialFlow.call();

        // then
        InOrder inOrder = Mockito.inOrder(work1, work2, work3);
        inOrder.verify(work1, Mockito.times(1)).call();
        inOrder.verify(work2, Mockito.times(1)).call();
        inOrder.verify(work3, Mockito.times(1)).call();
    }

}
