//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'coroutines' project.
// Copyright 2019 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//	  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package de.esoco.coroutine;

import de.esoco.coroutine.step.CodeExecution;

import java.util.concurrent.CompletableFuture;

import org.obrel.core.FluentRelatable;
import org.obrel.core.RelatedObject;

import static org.obrel.type.StandardTypes.NAME;

/********************************************************************
 * This is the base class for all steps of coroutines. For simple steps it is
 * sufficient to implement the single abstract method
 * {@link #execute(Object, Continuation)} which must perform the actual
 * (blocking) code execution. The default implementations of
 * {@link #runBlocking(Object, Continuation)} and
 * {@link #runAsync(CompletableFuture, CoroutineStep, Continuation)} then invoke
 * this method as needed.
 *
 * <p>
 * In most cases it is not necessary to extend this class because the 'step'
 * sub-package already contains implementations of several common steps. For
 * example, a simple code execution can be achieved by wrapping a closure in an
 * instance of the {@link CodeExecution} step.
 * </p>
 *
 * <p>
 * Creating a new step subclass is only needed to implement advanced coroutine
 * suspensions that are not already provided by existing steps. In such a case
 * it is typically also necessary to override the method
 * {@link #runAsync(CompletableFuture, CoroutineStep, Continuation)} to check
 * for the suspension condition. If a suspension is necessary a
 * {@link Suspension} object can be created by invoking
 * {@link Continuation#suspend(CoroutineStep, CoroutineStep)} for the current
 * step. The suspension object can then be used by code that waits for some
 * external condition to resume the coroutine when appropriate.
 * </p>
 *
 * <p>
 * It is recommended that a step implementation provides one or more static
 * factory methods alongside the constructor(s). These factory methods can then
 * be used as static imports for the fluent builder API of coroutines.
 * </p>
 *
 * @author eso
 */
public abstract class CoroutineStep<I, O> extends RelatedObject
    implements FluentRelatable<CoroutineStep<I, O>> {
    //~ Constructors -----------------------------------------------------------

    /***************************************
     * Creates a new instance.
     */
    protected CoroutineStep() {
        set(NAME, getClass().getSimpleName());
    }

    //~ Methods ----------------------------------------------------------------

    /***************************************
     * Runs this execution step asynchronously as a continuation of a previous
     * code execution in a {@link CompletableFuture} and proceeds to the next
     * step afterwards.
     *
     * <p>
     * Subclasses that need to suspend the invocation of the next step until
     * some condition is met (e.g. sending or receiving data has finished) need
     * to override this method and create a {@link Suspension} by invoking
     * {@link Continuation#suspend(CoroutineStep, CoroutineStep)} on the next
     * step. If the condition that caused the suspension resolves the coroutine
     * execution can be resumed by calling {@link Suspension#resume(Object)}.
     * </p>
     *
     * <p>
     * Subclasses that override this method also need to handle errors by
     * terminating any further execution (i.e. not resuming a suspension if such
     * exists) and forwarding the causing exception to
     * {@link Continuation#fail(Throwable)}.
     * </p>
     *
     * @param fPreviousExecution The future of the previous code execution
     * @param rNextStep          The next step to execute or NULL for none
     * @param rContinuation      The continuation of the execution
     */
    public void runAsync(CompletableFuture<I> fPreviousExecution,
        CoroutineStep<O, ?> rNextStep, Continuation<?> rContinuation) {
        rContinuation.continueApply(fPreviousExecution,
            i -> execute(i, rContinuation), rNextStep);
    }

    /***************************************
     * Runs this execution immediately, blocking the current thread until the
     * execution finishes.
     *
     * @param rInput        The input value
     * @param rContinuation The continuation of the execution
     *
     * @return The execution result
     */
    public O runBlocking(I rInput, Continuation<?> rContinuation) {
        return execute(rInput, rContinuation);
    }

    /***************************************
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return get(NAME);
    }

    /***************************************
     * This method must be implemented by subclasses to provide the actual
     * functionality of this step.
     *
     * @param rInput        The input value
     * @param rContinuation The continuation of the execution
     *
     * @return The result of the execution
     */
    protected abstract O execute(I rInput, Continuation<?> rContinuation);

    /***************************************
     * Allow subclasses to terminate the coroutine they currently run in.
     *
     * @param rContinuation The continuation of the current execution
     */
    protected void terminateCoroutine(Continuation<?> rContinuation) {
        rContinuation.getCurrentCoroutine().terminate(rContinuation);
    }
}
