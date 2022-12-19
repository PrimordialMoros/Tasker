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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
class SyncExecutorTest {
  private ManualTimer syncExecutor;

  @BeforeEach
  void setup() {
    syncExecutor = new ManualTimer();
  }

  @Test
  void testSubmit() {
    AtomicBoolean b = new AtomicBoolean(false);
    syncExecutor.submit(() -> {
      int tick = syncExecutor.currentTick();
      b.compareAndSet(false, true);
      assertEquals(tick, 1);
    });
    assertFalse(b.get());
    int TICKS = 10;
    for (int i = 0; i < TICKS; i++) {
      syncExecutor.tick();
    }
    assertTrue(b.get());
  }

  @Test
  void testSubmitMultiple() {
    AtomicInteger counter = new AtomicInteger(0);
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 5; j++) {
        syncExecutor.submit(counter::incrementAndGet, i + 1);
      }
    }
    int TICKS = 10;
    for (int i = 0; i < TICKS; i++) {
      syncExecutor.tick();
    }
    assertEquals(15, counter.get());
  }

  @Test
  void testSubmitExact() {
    int initial = syncExecutor.currentTick();
    AtomicInteger counter = new AtomicInteger(0);
    syncExecutor.submit(() -> counter.set(syncExecutor.currentTick()), 666);
    int TICKS = 1200;
    for (int i = 0; i < TICKS; i++) {
      syncExecutor.tick();
    }
    assertEquals(initial + 666, counter.get());
  }

  @Test
  void testRepeat() {
    int initial = syncExecutor.currentTick();
    AtomicInteger counter = new AtomicInteger(initial);
    syncExecutor.repeat(() -> counter.addAndGet(2), 2);
    assertEquals(syncExecutor.currentTick(), counter.get());
    int TICKS = 10;
    for (int i = 0; i < TICKS; i++) {
      syncExecutor.tick();
    }
    assertEquals(initial + TICKS, counter.get());
  }

  @AfterEach
  void cleanup() {
    syncExecutor.shutdown();
  }
}
