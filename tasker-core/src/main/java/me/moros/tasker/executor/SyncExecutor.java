/*
 * Copyright 2021-2025 Moros
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

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import me.moros.tasker.Task;
import org.jspecify.annotations.Nullable;

/**
 * Represents a sync task executor.
 */
public interface SyncExecutor extends TaskExecutor {
  @Override
  default <V> CompletableFuture<@Nullable V> submit(Supplier<@Nullable V> task, long delay, TimeUnit unit) {
    return submit(task, toTicks(delay, unit));
  }

  @Override
  default Task repeat(Runnable task, int periodTicks) {
    return repeat(toConsumer(task), 0, periodTicks);
  }

  @Override
  default Task repeat(Runnable task, int ticks, int periodTicks) {
    return repeat(toConsumer(task), ticks, periodTicks);
  }

  @Override
  default Task repeat(Runnable task, long delay, long period, TimeUnit unit) {
    return repeat(toConsumer(task), delay, period, unit);
  }

  /**
   * Schedule a repeating task.
   * @param task the task to schedule
   * @param periodTicks how often to repeat the task in game ticks
   * @return the scheduled task
   */
  default Task repeat(Consumer<? super Task> task, int periodTicks) {
    return repeat(task, 0, periodTicks);
  }

  /**
   * Schedule a repeating task.
   * @param task the task to schedule
   * @param period how often to repeat the task
   * @param unit the unit of time for period
   * @return the scheduled task
   */
  default Task repeat(Consumer<? super Task> task, long period, TimeUnit unit) {
    return repeat(task, 0, period, unit);
  }

  /**
   * Schedule a repeating task.
   * @param task the task to schedule
   * @param delay the delay before the first task execution
   * @param period how often to repeat the task
   * @param unit the unit of time for delay and period
   * @return the scheduled task
   */
  default Task repeat(Consumer<? super Task> task, long delay, long period, TimeUnit unit) {
    return repeat(task, toTicks(delay, unit), toTicks(period, unit));
  }

  /**
   * Schedule a repeating task.
   * @param task the task to schedule
   * @param ticks the delay before the first task execution in game ticks
   * @param periodTicks how often to repeat the task in game ticks
   * @return the scheduled task
   */
  Task repeat(Consumer<? super Task> task, int ticks, int periodTicks);

  /**
   * Clear all scheduled tasks in this executor without shutting down.
   */
  void clear();

  private Consumer<? super Task> toConsumer(Runnable task) {
    Objects.requireNonNull(task);
    return t -> task.run();
  }
}
