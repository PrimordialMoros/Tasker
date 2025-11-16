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

package me.moros.tasker;

import java.util.Comparator;

import org.jspecify.annotations.Nullable;

/**
 * Abstract base for {@link Task}.
 */
public abstract class Expiring implements Task, Comparable<Expiring> {
  private static final Comparator<Expiring> COMPARATOR = Comparator.comparingInt(Expiring::expiringTick);

  private @Nullable Expiring prev;
  private @Nullable Expiring next;

  @Nullable TaskList parent;
  int expiringTick;

  protected Expiring() {
    prev = next = null;
  }

  final @Nullable Expiring previous() {
    return prev;
  }

  final void previous(@Nullable Expiring prev) {
    this.prev = prev;
  }

  final @Nullable Expiring next() {
    return next;
  }

  final void next(@Nullable Expiring next) {
    this.next = next;
  }

  private int expiringTick() {
    return expiringTick;
  }

  final void unlink() {
    if (parent != null) {
      parent.unlink(this);
    }
  }

  @Override
  public final void cancel() {
    synchronized (this) {
      unlink();
    }
  }

  @Override
  public final int compareTo(Expiring o) {
    return COMPARATOR.compare(this, o);
  }
}
