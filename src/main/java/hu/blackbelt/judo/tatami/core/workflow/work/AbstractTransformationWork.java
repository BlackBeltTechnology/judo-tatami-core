package hu.blackbelt.judo.tatami.core.workflow.work;

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


import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractTransformationWork implements Work {

    TransformationContext transformationContext;
    protected MetricsCollector metricsCollector;

    public AbstractTransformationWork(TransformationContext transformationContext) {
        this.transformationContext = transformationContext;
    }

    public abstract void execute() throws Exception;

    public String getName() {
        return this.getClass().getName() + " - " + transformationContext.getModelName();
    }

    public TransformationContext getTransformationContext() {
        return transformationContext;
    }

    public AbstractTransformationWork withMetricsCollector(MetricsCollector metricsCollector) {
        this.metricsCollector = metricsCollector;
        return this;
    }

    public WorkReport call() {
        if (metricsCollector != null) {
            metricsCollector.invokedTransformation(this.getClass().getSimpleName());
        }
        final Long startTs = System.nanoTime();
        boolean failed = false;
        try {
            execute();
            return new DefaultWorkReport(WorkStatus.COMPLETED);
        } catch (Exception e) {
            failed = true;
            return new DefaultWorkReport(WorkStatus.FAILED, e);
        } finally {
            if (metricsCollector != null) {
                metricsCollector.stoppedTransformation(getClass().getSimpleName(), System.nanoTime() - startTs, failed);
            }
        }
    }
}
