/*
 * Copyright 2021-2023 Moros
 *
 * This file is part of Tasker.
 *
 * Tasker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Tasker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Tasker. If not, see <https://www.gnu.org/licenses/>.
 */

package me.moros.tasker.executor;

import java.util.concurrent.TimeUnit;

/**
 * Allows converting time to game ticks.
 */
public interface TickAdapter {
  /**
   * Convert the given time to game ticks.
   * @param time the amount of time
   * @param unit the time unit
   * @return the result in game ticks
   */
  default int toTicks(long time, TimeUnit unit) {
    return (int) unit.toMillis(time) / 50;
  }

  /**
   * Convert game ticks to {@link TimeUnit#MILLISECONDS}.
   * @param ticks the amount of game ticks
   * @return the result in milliseconds.
   */
  default long toMillis(int ticks) {
    return ticks * 50L;
  }

  /**
   * Convert ticks to a unit of time.
   * @param ticks the amount of game ticks
   * @param unit the time unit to convert to
   * @return the result in the provided unit
   */
  default long convert(int ticks, TimeUnit unit) {
    return unit.convert(toMillis(ticks), TimeUnit.MILLISECONDS);
  }
}
