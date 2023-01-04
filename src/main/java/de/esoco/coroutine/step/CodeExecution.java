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
package de.esoco.coroutine.step;

import de.esoco.coroutine.Continuation;
import de.esoco.coroutine.CoroutineScope;
import de.esoco.coroutine.CoroutineStep;
import de.esoco.lib.expression.Functions;
import org.obrel.core.RelationType;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An coroutine step that executes a code function.
 *
 * @author eso
 */
public class CodeExecution<I, O> extends CoroutineStep<I, O> {

	private final BiFunction<I, Continuation<?>, O> code;

	/**
	 * Creates a new instance from a binary function that accepts the
	 * continuation of the execution and the input value.
	 *
	 * @param code A binary function containing the code to be executed
	 */
	public CodeExecution(BiFunction<I, Continuation<?>, O> code) {
		Objects.requireNonNull(code);
		this.code = code;
	}

	/**
	 * Creates a new instance from a simple function that processes the input
	 * into the output value.
	 *
	 * @param code A function containing the code to be executed
	 */
	public CodeExecution(Function<I, O> code) {
		Objects.requireNonNull(code);
		this.code = (i, c) -> code.apply(i);
	}

	/**
	 * Applies a {@link Function} to the step input and return the processed
	 * output.
	 *
	 * @param code The function to be executed
	 * @return A new instance of this class
	 */
	public static <I, O> CodeExecution<I, O> apply(Function<I, O> code) {
		return new CodeExecution<>(code);
	}

	/**
	 * Applies a {@link BiFunction} to the step input and the continuation of
	 * the current execution and return the processed output.
	 *
	 * @param code The binary function to be executed
	 * @return A new instance of this class
	 */
	public static <I, O> CodeExecution<I, O> apply(
		BiFunction<I, Continuation<?>, O> code) {
		return new CodeExecution<>(code);
	}

	/**
	 * Consumes the input value with a {@link Consumer} and returns the input
	 * value.
	 *
	 * @param code The consumer to be executed
	 * @return A new instance of this class
	 */
	public static <T> CodeExecution<T, T> consume(Consumer<T> code) {
		return new CodeExecution<>(Functions.asFunction(code));
	}

	/**
	 * Consumes the input value with a {@link Consumer} and returns the input
	 * value.
	 *
	 * @param code The consumer to be executed
	 * @return A new instance of this class
	 */
	public static <T> CodeExecution<T, T> consume(
		BiConsumer<T, Continuation<?>> code) {
		return new CodeExecution<>(Functions.asFunction(code));
	}

	/**
	 * Queries a parameter relation from the {@link Continuation} and returns it
	 * as the result of the execution.
	 *
	 * @param rSource The relation type of the parameter
	 * @return A new instance of this class
	 */
	public static <I, O> CodeExecution<I, O> getParameter(
		RelationType<O> rSource) {
		return supply(c -> c.get(rSource));
	}

	/**
	 * Queries a parameter relation from the {@link CoroutineScope} and returns
	 * it as the result of the execution.
	 *
	 * @param rSource The relation type of the parameter
	 * @return A new instance of this class
	 */
	public static <I, O> CodeExecution<I, O> getScopeParameter(
		RelationType<O> rSource) {
		return supply(c -> c.scope().get(rSource));
	}

	/**
	 * A semantic alternative for {@link #apply(Function)}.
	 *
	 * @param mapper The mapping function
	 * @return A new instance of this class
	 */
	public static <I, O> CodeExecution<I, O> map(Function<I, O> mapper) {
		return new CodeExecution<>(mapper);
	}

	/**
	 * Executes a {@link Runnable}, ignoring any input value and returning no
	 * result.
	 *
	 * @param code The runnable to be executed
	 * @return A new instance of this class
	 */
	public static <T> CodeExecution<T, Void> run(Runnable code) {
		return new CodeExecution<>(Functions.asFunction(code));
	}

	/**
	 * Executes a {@link Runnable} and then returns the input value.
	 *
	 * @param code The runnable to be executed
	 * @return A new instance of this class
	 */
	public static <T> CodeExecution<T, T> run(Consumer<Continuation<?>> code) {
		return new CodeExecution<>((v, c) -> {
			code.accept(c);

			return v;
		});
	}

	/**
	 * Sets the input value into a parameter of the {@link Continuation} and
	 * then returns it.
	 *
	 * @param rTarget The type of the relation to set the parameter in
	 * @return A new instance of this class
	 */
	public static <T> CodeExecution<T, T> setParameter(
		RelationType<T> rTarget) {
		return apply((v, c) -> {
			c.set(rTarget, v);

			return v;
		});
	}

	/**
	 * Sets the input value into a parameter of the {@link CoroutineScope} and
	 * then returns it.
	 *
	 * @param rTarget The type of the relation to set the parameter in
	 * @return A new instance of this class
	 */
	public static <T> CodeExecution<T, T> setScopeParameter(
		RelationType<T> rTarget) {
		return apply((v, c) -> {
			c.scope().set(rTarget, v);

			return v;
		});
	}

	/**
	 * Provides a value from a {@link Supplier} as the result, ignoring any
	 * input value.
	 *
	 * @param code The supplier to be executed
	 * @return A new instance of this class
	 */
	public static <I, O> CodeExecution<I, O> supply(Supplier<O> code) {
		return new CodeExecution<>(Functions.asFunction(code));
	}

	/**
	 * Provides a value from a {@link Supplier} as the result, ignoring any
	 * input value.
	 *
	 * @param code The supplier to be executed
	 * @return A new instance of this class
	 */
	public static <I, O> CodeExecution<I, O> supply(
		Function<Continuation<?>, O> code) {
		return new CodeExecution<>((v, c) -> code.apply(c));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected O execute(I input, Continuation<?> continuation) {
		return code.apply(input, continuation);
	}
}
