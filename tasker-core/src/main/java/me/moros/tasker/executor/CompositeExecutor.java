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

import java.util.Objects;

/**
 * Represents a composite executor composed of a sync and an async task executor.
 */
public interface CompositeExecutor {
  /**
   * Get the sync task executor.
   * @return the sync task executor
   */
  SyncExecutor sync();

  /**
   * Get the async task executor.
   * @return the async task executor
   */
  AsyncExecutor async();

  /**
   * Shutdown both executors.
   */
  default void shutdown() {
    sync().shutdown();
    async().shutdown();
  }

  /**
   * Create a composite executor from the provided sync executor and a {@link SimpleAsyncExecutor}.
   * @param sync the sync executor
   * @return the created composite executor
   */
  static CompositeExecutor of(SyncExecutor sync) {
    return of(sync, new SimpleAsyncExecutor());
  }

  /**
   * Create a composite executor from the provided task executors.
   * @param sync the sync executor
   * @param async the async executor
   * @return the created composite executor
   */
  static CompositeExecutor of(SyncExecutor sync, AsyncExecutor async) {
    Objects.requireNonNull(sync);
    Objects.requireNonNull(async);
    return new CompositeHolder(sync, async);
  }
}
