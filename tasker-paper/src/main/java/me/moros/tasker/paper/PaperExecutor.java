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

package me.moros.tasker.paper;

import java.util.Objects;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.moros.tasker.executor.AbstractSyncExecutor;
import org.bukkit.plugin.Plugin;

/**
 * Synchronous executor for Paper/Folia.
 */
public class PaperExecutor extends AbstractSyncExecutor {
  private final Plugin plugin;
  private final ScheduledTask task;

  public PaperExecutor(Plugin plugin) {
    this.plugin = Objects.requireNonNull(plugin);
    task = this.plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(this.plugin, t -> wheel.advance(), 1, 1);
  }

  @Override
  public boolean isValid() {
    return plugin.isEnabled();
  }

  @Override
  public void shutdown() {
    super.shutdown();
    task.cancel();
  }
}