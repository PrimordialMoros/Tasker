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

import java.util.function.Consumer;
import java.util.function.Function;

import me.moros.tasker.TaskList.PendingTaskList;

final class HierarchicalTimerWheel extends AbstractTimerWheel {
  private final int[] BUCKETS = {60, 40, 30, 8, 3}; // 3s, 2m, 1h, 8h, 1d
  private final int[] SPANS = {60, 2400, 72_000, 576_000, 1_728_000, 1_728_000};

  private final TaskList pending;
  private final TaskList[][] wheel;
  private final int[] index;
  private final int length;

  HierarchicalTimerWheel() {
    length = BUCKETS.length;
    index = new int[length];
    pending = new PendingTaskList();
    wheel = new TaskList[length][];
    for (int i = 0; i < length; i++) {
      int innerLength = BUCKETS[i];
      wheel[i] = new TaskList[innerLength];
      for (int j = 0; j < innerLength; j++) {
        wheel[i][j] = new TaskList();
      }
    }
  }

  @Override
  protected void advanceSync() {
    incrementTick();
    for (int i = 0; i < length; i++) {
      boolean cascade = increment(i);
      expire(wheel[i][index[i]], cascade && i > 0);
      if (!cascade) {
        break;
      }
    }
    expire(pending);
  }

  @Override
  protected void shutdownSync(boolean run) {
    Consumer<? super Expiring> action = run ? Expiring::run : Function.identity()::apply;
    pending.clear(action);
    for (int i = 0; i < length; i++) {
      for (TaskList tasks : wheel[i]) {
        tasks.clear(action);
      }
    }
  }

  private void expire(TaskList tasks, boolean cascadeReschedule) {
    Expiring node = tasks.first();
    Expiring next;
    while (node != null) {
      next = node.next();
      if (node.expiringTick <= currentTick()) {
        node.unlink();
        node.run();
        int repeat = node.repeat();
        if (repeat > 0) {
          reschedule(node, repeat);
        }
      } else if (cascadeReschedule) {
        reschedule(node, node.expiringTick - currentTick());
      } else {
        return;
      }
      node = next;
    }
  }

  @Override
  protected TaskList findBucket(int ticks) {
    if (ticks <= 0) {
      return pending;
    }
    for (int i = 0; i < length; i++) {
      int tickCapacity = SPANS[i + 1];
      if (ticks <= tickCapacity) {
        return offset(i, ticks);
      }
    }
    return offset(length - 1, Math.min(ticks, SPANS[length]));
  }

  private TaskList offset(int idx, int ticks) {
    int innerIndex = (index[idx] + ticks % SPANS[idx]) % BUCKETS[idx];
    return wheel[idx][innerIndex];
  }

  /**
   * Attempt to increment the index for the given bucket.
   * @param idx the bucket index
   * @return true if the wheel performed a full rotation, false otherwise
   */
  private boolean increment(int idx) {
    if (++index[idx] >= BUCKETS[idx]) {
      index[idx] = 0;
      return true;
    }
    return false;
  }
}
