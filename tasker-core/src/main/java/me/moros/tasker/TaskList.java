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

import org.checkerframework.checker.nullness.qual.Nullable;

class TaskList {
  private Expiring first;
  private Expiring last;

  TaskList() {
  }

  @Nullable Expiring unlinkFirst() {
    final Expiring toRemove = first;
    if (toRemove != null) {
      final Expiring next = toRemove.next();
      toRemove.next(null); // help GC
      first = next;
      if (next == null) {
        last = null;
      } else {
        next.previous(null);
      }
      toRemove.parent = null;
    }
    return toRemove;
  }

  void unlink(Expiring node) {
    final Expiring next = node.next();
    final Expiring prev = node.previous();
    if (prev == null) {
      first = next;
    } else {
      prev.next(next);
      node.previous(null);
    }
    if (next == null) {
      last = prev;
    } else {
      next.previous(prev);
      node.next(null);
    }
    node.parent = null;
  }

  @Nullable Expiring first() {
    return first;
  }

  // Sorted add to reduce bookkeeping costs
  void add(Expiring node) {
    Expiring check = first;
    while (check != null) {
      if (node.compareTo(check) <= 0) {
        linkBefore(node, check);
        return;
      }
      check = check.next();
    }
    linkLast(node);
  }

  void linkBefore(Expiring node, Expiring next) {
    final Expiring prev = next.previous();
    node.previous(prev);
    node.next(next);
    next.previous(node);
    if (prev == null) {
      first = node;
    } else {
      prev.next(node);
    }
  }

  void linkLast(Expiring node) {
    final Expiring oldLast = last;
    node.previous(oldLast);
    node.next(null);
    last = node;
    if (oldLast == null) {
      first = node;
    } else {
      oldLast.next(node);
    }
  }

  void clear(Consumer<? super Expiring> action) {
    // Clearing all the links between nodes is "unnecessary", but:
    // - helps a generational GC if the discarded nodes inhabit more than one generation
    // - is sure to free memory even if there is a reachable Iterator
    for (Expiring node = first; node != null; ) {
      Expiring next = node.next();
      node.next(null);
      node.previous(null);
      node.parent = null;
      action.accept(node); // Call action after node has been unlinked to avoid issues with self cancelling
      node = next;
    }
    first = last = null;
  }

  static final class PendingTaskList extends TaskList {
    PendingTaskList() {
    }

    // Optimized add at the end of the list without sorting
    @Override
    void add(Expiring node) {
      linkLast(node);
    }
  }
}

