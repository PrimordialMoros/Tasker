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

package me.moros.tasker.executor;

import java.util.concurrent.RunnableFuture;

import me.moros.tasker.Task;

final class FutureWrapper implements Task {
  private final RunnableFuture<?> future;
  private final int repeat;

  FutureWrapper(RunnableFuture<?> future, int repeat) {
    this.future = future;
    this.repeat = repeat;
  }

  @Override
  public void run() {
    future.run();
  }

  @Override
  public int repeat() {
    return repeat;
  }

  @Override
  public void cancel() {
    future.cancel(false);
  }

  @Override
  public String toString() {
    return "Repeating every: " + repeat;
  }
}
