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

package me.moros.tasker;

/**
 * Represents an expiring task that can be cancelled.
 */
public interface Task extends Runnable {
  /**
   * Cancel this task without executing.
   */
  void cancel();

  /**
   * Get the amount of game ticks between each repetition of this task. A non-positive value indicates
   * no repetition scheduling for this task.
   * @return the amount of game ticks
   */
  default int repeat() {
    return 0;
  }
}
