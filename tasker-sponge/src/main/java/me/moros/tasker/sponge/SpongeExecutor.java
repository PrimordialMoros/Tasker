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

package me.moros.tasker.sponge;

import java.util.Objects;

import me.moros.tasker.executor.AbstractSyncExecutor;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.plugin.PluginContainer;

/**
 * Synchronous executor for Sponge.
 */
public class SpongeExecutor extends AbstractSyncExecutor {
  private final ScheduledTask task;

  public SpongeExecutor(PluginContainer plugin) {
    Objects.requireNonNull(plugin);
    Task t = Task.builder().plugin(plugin).delay(Ticks.single())
      .interval(Ticks.single()).execute(wheel::advance).build();
    this.task = Sponge.server().scheduler().submit(t);
  }

  @Override
  public boolean isValid() {
    return Sponge.isServerAvailable();
  }

  @Override
  public void shutdown() {
    super.shutdown();
    task.cancel();
  }
}
