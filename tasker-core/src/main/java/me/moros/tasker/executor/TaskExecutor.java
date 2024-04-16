/*
 * Copyright 2021-2024 Moros
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
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import me.moros.tasker.Task;
import org.checkerframework.checker.nullness.qual.PolyNull;

/**
 * Represents a task executor.
 */
public interface TaskExecutor extends TickAdapter, Executor {
  @Override
  default void execute(Runnable command) {
    submit(command, 0);
  }

  /**
   * Submit a task.
   * @param task the task to submit
   * @return the scheduled task
   */
  default CompletableFuture<?> submit(Runnable task) {
    return submit(toSupplier(task), 0);
  }

  /**
   * Submit a task.
   * @param task the task to submit
   * @param <V> the type of result the task returns
   * @return the scheduled task as a future
   */
  default <V> CompletableFuture<@PolyNull V> submit(Supplier<@PolyNull V> task) {
    return submit(task, 0);
  }

  /**
   * Submit a task.
   * @param task the task to submit
   * @param ticks the delay before the task is executed in game ticks
   * @return the scheduled task as a future
   */
  default CompletableFuture<?> submit(Runnable task, int ticks) {
    return submit(toSupplier(task), ticks);
  }

  /**
   * Submit a task.
   * @param task the task to submit
   * @param ticks the delay before the task is executed in game ticks
   * @param <V> the type of result the task returns
   * @return the scheduled task as a future
   */
  <V> CompletableFuture<@PolyNull V> submit(Supplier<@PolyNull V> task, int ticks);

  /**
   * Submit a task.
   * @param task the task to submit
   * @param delay the delay before the task is executed
   * @param unit the unit of time for delay
   * @return the scheduled task as a future
   */
  default CompletableFuture<?> submit(Runnable task, long delay, TimeUnit unit) {
    return submit(toSupplier(task), delay, unit);
  }

  /**
   * Submit a task.
   * @param task the task to submit
   * @param delay the delay before the task is executed
   * @param unit the unit of time for delay
   * @param <V> the type of result the task returns
   * @return the scheduled task as a future
   */
  <V> CompletableFuture<@PolyNull V> submit(Supplier<@PolyNull V> task, long delay, TimeUnit unit);

  /**
   * Schedule a repeating task.
   * @param task the task to schedule
   * @param periodTicks how often to repeat the task in game ticks
   * @return the scheduled task
   */
  default Task repeat(Runnable task, int periodTicks) {
    return repeat(task, 0, periodTicks);
  }

  /**
   * Schedule a repeating task.
   * @param task the task to schedule
   * @param period how often to repeat the task
   * @param unit the unit of time for period
   * @return the scheduled task
   */
  default Task repeat(Runnable task, long period, TimeUnit unit) {
    return repeat(task, 0, period, unit);
  }

  /**
   * Schedule a repeating task.
   * @param task the task to schedule
   * @param ticks the delay before the first task execution in game ticks
   * @param periodTicks how often to repeat the task in game ticks
   * @return the scheduled task
   */
  Task repeat(Runnable task, int ticks, int periodTicks);

  /**
   * Schedule a repeating task.
   * @param task the task to schedule
   * @param delay the delay before the first task execution
   * @param period how often to repeat the task
   * @param unit the unit of time for delay and period
   * @return the scheduled task
   */
  Task repeat(Runnable task, long delay, long period, TimeUnit unit);

  private Supplier<?> toSupplier(Runnable task) {
    Objects.requireNonNull(task);
    return () -> {
      task.run();
      return null;
    };
  }

  /**
   * Check whether this executor can currently schedule and execute tasks.
   * This should return false if it hasn't been initialized or has been terminated.
   * @return whether tasks can be scheduled and executed
   */
  boolean isValid();

  /**
   * Safely shutdown this executor.
   */
  void shutdown();
}
