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

package me.moros.tasker.executor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import me.moros.tasker.Task;
import org.checkerframework.checker.nullness.qual.PolyNull;

/**
 * Represents an async task executor.
 */
public interface AsyncExecutor extends TaskExecutor {
  @Override
  default <V> CompletableFuture<@PolyNull V> submit(Supplier<@PolyNull V> task, int ticks) {
    return submit(task, toMillis(ticks), TimeUnit.MILLISECONDS);
  }

  @Override
  default Task repeat(Runnable task, int ticks, int periodTicks) {
    return repeat(task, toMillis(ticks), toMillis(periodTicks), TimeUnit.MILLISECONDS);
  }
}
