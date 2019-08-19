package hu.blackbelt.judo.tatami.core.workflow.work;

import java.util.concurrent.Callable;

/**
 * Implementations of this interface must:
 * <ul>
 *     <li>catch exceptions and return {@link WorkStatus#FAILED}</li>
 *     <li>make sure the work in finished in a finite amount of time</li>
 * </ul>
 *
 * Work name must be unique within a workflow.
 */
public interface Work extends Callable<WorkReport> {

    String getName();

    WorkReport call();
}
