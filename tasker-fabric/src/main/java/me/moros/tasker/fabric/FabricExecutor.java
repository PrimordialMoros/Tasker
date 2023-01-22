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

package me.moros.tasker.fabric;

import me.moros.tasker.executor.AbstractSyncExecutor;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

/**
 * Synchronous executor for Fabric.
 */
public class FabricExecutor extends AbstractSyncExecutor {
  private boolean stopped;

  public FabricExecutor() {
    ServerTickEvents.START_SERVER_TICK.register(this::tick);
  }

  private void tick(MinecraftServer server) {
    if (isValid()) {
      wheel.advance();
    }
  }

  @Override
  public boolean isValid() {
    return !stopped;
  }

  @Override
  public void shutdown() {
    super.shutdown();
    stopped = true;
  }
}
