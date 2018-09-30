//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is a part of the 'coroutines' project.
// Copyright 2018 Elmar Sonnenschein, esoco GmbH, Flensburg, Germany
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

import java.util.concurrent.CompletionException;


/********************************************************************
 * The base class of unchecked exceptions that may be thrown by {@link
 * Coroutine} executions.
 *
 * @author eso
 */
public class CoroutineException extends CompletionException
{
	//~ Static fields/initializers ---------------------------------------------

	private static final long serialVersionUID = 1L;

	//~ Constructors -----------------------------------------------------------

	/***************************************
	 * Creates a new instance.
	 *
	 * @param sMessage The error message
	 */
	public CoroutineException(String sMessage)
	{
		super(sMessage);
	}

	/***************************************
	 * Creates a new instance.
	 *
	 * @param eCause The causing exception
	 */
	public CoroutineException(Throwable eCause)
	{
		super(eCause);
	}

	/***************************************
	 * Creates a new instance.
	 *
	 * @param sMessage The error message
	 * @param eCause   The causing exception
	 */
	public CoroutineException(String sMessage, Throwable eCause)
	{
		super(sMessage, eCause);
	}
}
