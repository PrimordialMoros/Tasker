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

package me.moros.tasker;

import java.util.function.Consumer;

final class LinkedTask extends Expiring {
  private final Consumer<? super Task> task;
  private final int repeat;

  LinkedTask(Consumer<? super Task> task, int repeat) {
    this.task = task;
    this.repeat = repeat;
  }

  @Override
  public void run() {
    task.accept(this);
  }

  @Override
  public int repeat() {
    return repeat;
  }

  @Override
  public String toString() {
    return "Repeating every: " + repeat;
  }
}
