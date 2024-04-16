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

package me.moros.tasker.minestom;

import me.moros.tasker.executor.AbstractSyncExecutor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

/**
 * Synchronous executor for Minestom.
 */
public class MinestomExecutor extends AbstractSyncExecutor {
  private final Task task;

  public MinestomExecutor() {
    task = MinecraftServer.getSchedulerManager().buildTask(this::tick)
      .delay(TaskSchedule.nextTick())
      .repeat(TaskSchedule.nextTick())
      .schedule();
  }

  private void tick() {
    wheel.advance();
  }

  @Override
  public boolean isValid() {
    return MinecraftServer.isStarted();
  }

  @Override
  public void shutdown() {
    super.shutdown();
    task.cancel();
  }
}
