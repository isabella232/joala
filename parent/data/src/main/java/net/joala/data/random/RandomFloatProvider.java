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

package net.joala.data.random;

import javax.annotation.Nonnull;

/**
 * <p>
 * Random number provider for float values.
 * </p>
 *
 * @since 9/17/12
 */
public class RandomFloatProvider extends AbstractRandomNumberProvider<Float> {
  /**
   * <p>
   * Creates a random number provider for float values.
   * </p>
   */
  public RandomFloatProvider() {
    super(new FloatRandomNumberType());
  }

  /**
   * <p>
   * Describe the float number type as needed to provide random data.
   * </p>
   */
  private static final class FloatRandomNumberType extends AbstractRandomNumberType<Float> {

    private FloatRandomNumberType() {
      super(Float.class);
    }

    @Override
    @Nonnull
    public Float min() {
      return Float.MIN_VALUE;
    }

    @Override
    @Nonnull
    public Float max() {
      return Float.MAX_VALUE;
    }

    @Override
    @Nonnull
    public Float sum(final Float value1, final Float value2) {
      return value1 + value2;
    }

    @Override
    @Nonnull
    public Float percentOf(final double percent, final Float value) {
      checkPercentageArgument(percent);
      return (float) percent * value;
    }
  }
}