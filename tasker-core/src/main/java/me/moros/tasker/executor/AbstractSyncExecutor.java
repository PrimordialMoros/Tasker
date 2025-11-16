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

package me.moros.tasker.executor;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import me.moros.tasker.Task;
import me.moros.tasker.TimerWheel;
import org.jspecify.annotations.Nullable;

/**
 * An abstract sync executor utilizing that delegates to a {@link TimerWheel}.
 */
public abstract class AbstractSyncExecutor implements SyncExecutor {
  protected final TimerWheel wheel;

  protected AbstractSyncExecutor(TimerWheel wheel) {
    this.wheel = Objects.requireNonNull(wheel);
  }

  protected AbstractSyncExecutor() {
    this.wheel = TimerWheel.hierarchical();
  }

  @Override
  public <V> CompletableFuture<@Nullable V> submit(Supplier<@Nullable V> task, int ticks) {
    Objects.requireNonNull(task);
    checkValid();
    CompletableFuture<V> future = new CompletableFuture<>();
    wheel.schedule(bind(task, future), ticks, 0);
    return future;
  }

  @Override
  public Task repeat(Consumer<? super Task> consumer, int ticks, int periodTicks) {
    Objects.requireNonNull(consumer);
    checkValid();
    return wheel.schedule(consumer, ticks, periodTicks);
  }

  @Override
  public final void clear() {
    wheel.shutdown(false);
  }

  @Override
  public void shutdown() {
    clear();
  }

  protected void checkValid() {
    if (!isValid()) {
      throw new RejectedExecutionException("Cannot execute now!");
    }
  }

  private static <V> Consumer<? super Task> bind(Supplier<@Nullable V> task, CompletableFuture<@Nullable V> future) {
    return t -> {
      try {
        future.complete(task.get());
      } catch (Exception ex) {
        future.completeExceptionally(ex);
      }
    };
  }
}
