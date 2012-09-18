/*
 * Copyright 2012 CoreMedia AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.joala.condition;

import com.google.common.base.Objects;
import net.joala.base.DeceleratingWait;
import net.joala.base.Timeout;
import net.joala.base.Wait;
import net.joala.base.WaitFailStrategy;
import net.joala.base.WaitTimeoutFailStrategy;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsAnything;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * <p>
 * An abstract implementation for conditions. Using this you only have to override the method {@link #get()} to retrieve
 * the value/state to check directly. Everything else, waiting, asserting etc. is done by this condition implementation.
 * </p>
 * <p>
 * To override this class and provide the same syntax for configuration you should override any methods returning
 * a self reference, call the super method and just return {@code this}.
 * </p>
 *
 * @param <T> result type of {@link #get()}
 * @since 2/24/12
 */
public class DefaultCondition<T> implements Condition<T>, FailSafeCondition<T> {
  private static final WaitFailStrategy TIMEOUT_FAIL_STRATEGY = new WaitTimeoutFailStrategy();
  private static final WaitFailStrategy ASSUMPTION_FAIL_STRATEGY = new WaitAssumptionFailStrategy();
  private static final WaitFailStrategy ASSERTION_FAIL_STRATEGY = new WaitAssertionFailStrategy();
  /**
   * Message to print on failure. {@code null} for none.
   */
  @Nullable
  private String message;

  /**
   * Runnable to execute after condition has been performed.
   */
  @Nullable
  private Runnable runFinallyRunnable;

  /**
   * Runnable to execute before the condition is being performed.
   */
  @Nullable
  private Runnable runBeforeRunnable;

  @Nonnull
  private final Timeout timeout;

  /**
   * The expression to retrieve the value/state while waiting.
   */
  @Nonnull
  private final Expression<T> expression;
  @Nonnegative
  private double factor = 1.0;

  public DefaultCondition(@Nonnull final Expression<T> expression, @Nonnull final Timeout timeout) {
    checkNotNull(expression, "Expression must not be null.");
    checkNotNull(timeout, "Timeout must not be null.");
    this.expression = expression;
    this.timeout = timeout;
  }

  @Override
  @Nullable
  public final T get() {
    return expression.get();
  }

  @Override
  @Nullable
  public final T await() {
    return await(IsAnything.anything());
  }

  @Override
  @Nullable
  public T await(@Nonnull final Matcher<? super T> matcher) {
    return until(matcher, TIMEOUT_FAIL_STRATEGY);
  }

  private T until(final Matcher<? super T> matcher, final WaitFailStrategy failStrategy) {
    return until(new DeceleratingWait(timeout, factor, failStrategy), matcher);
  }

  private T until(@Nonnull final Wait wait, @Nullable final Matcher<? super T> matcher) {
    if (runBeforeRunnable != null) {
      runBeforeRunnable.run();
    }
    try {
      return wait.until(message, expression, new ExpressionFunction<T>(), matcher);
    } finally {
      if (runFinallyRunnable != null) {
        runFinallyRunnable.run();
      }
    }
  }

  @Override
  public void assumeThat(@Nonnull final Matcher<? super T> matcher) {
    until(matcher, ASSUMPTION_FAIL_STRATEGY);
  }

  @Override
  public final void assumeEquals(@Nullable final T expected) {
    assumeThat(equalTo(expected));
  }

  @Override
  public void assertThat(@Nonnull final Matcher<? super T> matcher) {
    until(matcher, ASSERTION_FAIL_STRATEGY);
  }

  @Override
  public final void assertEquals(@Nullable final T expected) {
    assertThat(equalTo(expected));
  }

  @Override
  @Nonnull
  public DefaultCondition<T> runFinally(@Nullable final Runnable runnable) {
    runFinallyRunnable = runnable;
    return this;
  }

  @Override
  @Nonnull
  public DefaultCondition<T> runBefore(@Nullable final Runnable runnable) {
    runBeforeRunnable = runnable;
    return this;
  }

  @Override
  @Nonnull
  public DefaultCondition<T> withTimeoutFactor(@Nonnegative final double newFactor) {
    this.factor = newFactor;
    return this;
  }

  @Override
  @Nonnull
  public DefaultCondition<T> withMessage(@Nullable final String newMessage) {
    this.message = newMessage;
    return this;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
            .add("message", message)
            .add("expression", expression)
            .add("runBefore", runBeforeRunnable)
            .add("runFinally", runFinallyRunnable)
            .add("timeout", timeout)
            .add("factor", factor)
            .toString();
  }


}
