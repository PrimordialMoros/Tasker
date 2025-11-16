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

package me.moros.tasker;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Represents a Timer Wheel data structure designed for use in games with a consistent tick duration.
 * <br>
 * The wheel holds various {@link Task Tasks} that are scheduled to execute at
 * a specific point in the future, in sync with the game's main loop.
 * Internally the wheel utilizes one or more circular arrays that rotate every time {@link #advance()}
 * is called.
 * <br>
 * There are 2 provided implementations, {@link #simple(int) Simple} and
 * {@link #hierarchical() Hierarchical}.
 * @see <a href="https://dl.acm.org/doi/10.1109/90.650142">Hashed and hierarchical timing wheels: efficient data structures for implementing a timer facility</a>
 * @see <a href="https://lwn.net/Articles/646950/">Linux timer wheel</a>
 * @see <a href="https://www.confluent.io/blog/apache-kafka-purgatory-hierarchical-timing-wheels/">Apache Kafka timer wheel</a>
 */
public interface TimerWheel {
  /**
   * Get the current tick tracked by the wheel.
   * @return the current tick
   */
  int currentTick();

  /**
   * Advance this wheel by a single tick, performing all necessary bookkeeping
   * and removing or rescheduling any expired tasks.
   */
  void advance();

  /**
   * Create and schedule a task on this timer wheel.
   * @param consumer the task to schedule
   * @param delay the delay in game ticks
   * @param period the repeat period in game ticks
   * @return the scheduled task
   * @see #schedule(Expiring, int)
   */
  default Task schedule(Consumer<? super Task> consumer, int delay, int period) {
    Objects.requireNonNull(consumer);
    LinkedTask task = new LinkedTask(consumer, period);
    return schedule(task, delay);
  }

  /**
   * Schedule a task on this timer wheel.
   * @param task the task to schedule
   * @param delay the delay in game ticks
   * @param <T> the type of task
   * @return the scheduled task
   * @see #schedule(Consumer, int, int)
   */
  <T extends Expiring> T schedule(T task, int delay);

  /**
   * Shutdown this timer wheel by clearing all scheduled tasks.
   * @param run whether to run each expiring task when cleared
   */
  void shutdown(boolean run);

  /**
   * Create a hierarchical timer wheel suited for variable expiration.
   * @return the timer wheel instance
   */
  static TimerWheel hierarchical() {
    return new HierarchicalTimerWheel();
  }

  /**
   * Create a simple timer wheel for tasks that expire shortly. This offers better performance
   * than a {@link #hierarchical() Hierarchical} wheel by eliminating bookkeeping and wheel cascading
   * at the cost of memory. For that reason, the wheel's capacity will be limited to the given size.
   * @param capacity the wheel's max capacity in ticks
   * @return the timer wheel instance
   */
  static TimerWheel simple(int capacity) {
    return new SimpleTimerWheel(Math.max(1, capacity));
  }
}
