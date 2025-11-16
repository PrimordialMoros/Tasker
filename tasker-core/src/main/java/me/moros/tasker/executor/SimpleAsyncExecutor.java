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
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import me.moros.tasker.Task;
import org.jspecify.annotations.Nullable;

/**
 * A simple async executor implementation that delegates to a {@link ScheduledExecutorService} for repeated tasks.
 */
public class SimpleAsyncExecutor implements AsyncExecutor {
  private final ScheduledThreadPoolExecutor scheduler;
  private final ExecutorService executor;

  public SimpleAsyncExecutor(ExecutorService executor) {
    var threadFactory = new DaemonThreadFactory("TaskerScheduler");
    this.scheduler = new ScheduledThreadPoolExecutor(1, threadFactory);
    this.scheduler.setRemoveOnCancelPolicy(true);
    this.executor = Objects.requireNonNull(executor);
  }

  public SimpleAsyncExecutor() {
    this(Executors.newFixedThreadPool(Math.max(1, Runtime.getRuntime().availableProcessors() / 2)));
  }

  private Executor delayedExecutor(long delay, TimeUnit unit) {
    return delay <= 0 ? executor : new Delayer(scheduler, executor, delay, unit);
  }

  @Override
  public <V> CompletableFuture<@Nullable V> submit(Supplier<@Nullable V> task, long delay, TimeUnit unit) {
    Objects.requireNonNull(task);
    checkValid();
    return CompletableFuture.supplyAsync(task, delayedExecutor(delay, unit));
  }

  @Override
  public Task repeat(Runnable task, long delay, long period, TimeUnit unit) {
    Objects.requireNonNull(task);
    checkValid();
    int periodTicks = toTicks(period, unit);
    var future = (RunnableFuture<?>) scheduler.scheduleAtFixedRate(() -> executor.execute(task), delay, period, unit);
    return new FutureWrapper(future, periodTicks);
  }

  @Override
  public boolean isValid() {
    return !executor.isShutdown();
  }

  @Override
  public void shutdown() {
    executor.shutdown();
    scheduler.shutdown();
    try {
      if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
        executor.shutdownNow();
      }
      if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
        scheduler.shutdownNow();
      }
    } catch (InterruptedException e) {
      executor.shutdownNow();
      scheduler.shutdownNow();
    }
  }

  protected void checkValid() {
    if (!isValid()) {
      throw new RejectedExecutionException("Cannot execute now!");
    }
  }

  private record DaemonThreadFactory(String name) implements ThreadFactory {
    @Override
    public Thread newThread(Runnable r) {
      Thread t = new Thread(r, name);
      t.setDaemon(true);
      t.setName(name);
      return t;
    }
  }

  private record Delayer(ScheduledExecutorService del, Executor ex, long delay, TimeUnit unit) implements Executor {
    @Override
    public void execute(Runnable command) {
      del.schedule(() -> ex.execute(command), delay, unit);
    }
  }
}
