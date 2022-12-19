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

import java.util.Objects;

abstract class AbstractTimerWheel implements TimerWheel {
  private int currentTick;

  protected AbstractTimerWheel() {
  }

  protected void incrementTick() {
    ++currentTick;
  }

  protected int currentTick() {
    return currentTick;
  }

  @Override
  public <T extends Expiring> T schedule(T task, int delay) {
    Objects.requireNonNull(task);
    if (task.repeat() < 0 || delay < 0) {
      throw new IllegalArgumentException();
    }
    reschedule(task, delay);
    return task;
  }

  protected void reschedule(Expiring node, int ticks) {
    TaskList tasks = findBucket(ticks);
    node.unlink();
    node.parent = tasks;
    node.expiringTick = currentTick + ticks;
    tasks.add(node);
  }

  protected void expire(TaskList tasks) {
    Expiring node = tasks.unlinkFirst();
    while (node != null) {
      node.run();
      int repeat = node.repeat();
      if (repeat > 0) {
        reschedule(node, repeat);
      }
      node = tasks.unlinkFirst();
    }
  }

  protected abstract TaskList findBucket(int ticks);
}
