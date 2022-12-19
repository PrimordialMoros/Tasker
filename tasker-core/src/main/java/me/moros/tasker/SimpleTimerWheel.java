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

import java.util.function.Consumer;
import java.util.function.Function;

import me.moros.tasker.TaskList.PendingTaskList;

final class SimpleTimerWheel extends AbstractTimerWheel {
  private final TaskList[] wheel;
  private int index;

  SimpleTimerWheel(int length) {
    wheel = new TaskList[length];
    for (int i = 0; i < length; i++) {
      wheel[i] = new PendingTaskList();
    }
  }

  @Override
  public synchronized void advance() {
    incrementTick();
    expire(wheel[index]);
    index = (index + 1) % wheel.length;
  }

  @Override
  public void shutdown(boolean run) {
    synchronized (this) {
      Consumer<? super Expiring> action = run ? Expiring::run : Function.identity()::apply;
      for (TaskList tasks : wheel) {
        tasks.clear(action);
      }
    }
  }

  @Override
  protected TaskList findBucket(int ticks) {
    if (ticks > wheel.length) {
      throw new IllegalArgumentException("Provided ticks (" + ticks + ") greater than capacity (" + wheel.length + ")!");
    }
    int innerIndex = (index + ticks) % wheel.length;
    return wheel[innerIndex];
  }
}
