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

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import me.moros.tasker.Task;
import org.checkerframework.checker.nullness.qual.PolyNull;

/**
 * A simple async executor implementation that delegates to a {@link ScheduledExecutorService} for repeated tasks.
 */
public class SimpleAsyncExecutor implements AsyncExecutor {
  private final ScheduledExecutorService delegate;

  public SimpleAsyncExecutor(ScheduledExecutorService delegate) {
    this.delegate = Objects.requireNonNull(delegate);
    if (this.delegate instanceof ScheduledThreadPoolExecutor ex) {
      ex.setRemoveOnCancelPolicy(true);
    }
  }

  public SimpleAsyncExecutor() {
    this(Executors.newScheduledThreadPool(0));
  }

  @Override
  public <V> CompletableFuture<@PolyNull V> submit(Supplier<@PolyNull V> task, long delay, TimeUnit unit) {
    checkValid();
    Executor ex = delay <= 0 ? delegate : CompletableFuture.delayedExecutor(delay, unit, delegate);
    return CompletableFuture.supplyAsync(task, ex);
  }

  @Override
  public Task repeat(Runnable task, long delay, long period, TimeUnit unit) {
    checkValid();
    int periodTicks = toTicks(period, unit);
    var future = (RunnableFuture<?>) delegate.scheduleAtFixedRate(task, delay, period, unit);
    return new FutureWrapper(future, periodTicks);
  }

  @Override
  public boolean isValid() {
    return !delegate.isShutdown();
  }

  @Override
  public void shutdown() {
    delegate.shutdown();
    try {
      if (!delegate.awaitTermination(60, TimeUnit.SECONDS)) {
        delegate.shutdownNow();
      }
    } catch (InterruptedException e) {
      delegate.shutdownNow();
    }
  }

  protected void checkValid() {
    if (!isValid()) {
      throw new RejectedExecutionException("Cannot execute now!");
    }
  }
}
