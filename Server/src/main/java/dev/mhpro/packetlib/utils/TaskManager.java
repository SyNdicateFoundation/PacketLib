/**
 * Codded by MH_ProDev
 * No one can use this without add my username at contributors
 * Hehe boy 😆
 */

package dev.mhpro.packetlib.utils;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Queue;
import java.util.concurrent.*;

@SuppressWarnings({"unused", "UnusedReturnValue"})
@UtilityClass
public class TaskManager {
    private final Queue<Runnable> RUNNABLE_QUEUE = new ConcurrentLinkedQueue<>();
    private final ScheduledExecutorService SCHEDULED_THREAD_POOL = Executors.newScheduledThreadPool(16);
    private final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(16);
    private final ThreadFactory THREAD_FACTORY = Executors.defaultThreadFactory();

    public static @NotNull Task async(Runnable runnable) {
        Task task = new Task();
        task.setFuture(EXECUTOR_SERVICE.submit(runnable));
        return task;
    }

    public static @NotNull Task asyncRepeat(Runnable runnable, long delay, long period, TimeUnit unit) {
        Task task = new Task();
        task.setFuture(SCHEDULED_THREAD_POOL.scheduleAtFixedRate(runnable, delay, period, unit));
        return task;
    }

    public static @NotNull Task asyncLater(Runnable runnable, long delay, TimeUnit unit) {
        Task task = new Task();
        task.setFuture(SCHEDULED_THREAD_POOL.schedule(runnable, delay, unit));
        return task;
    }

    public static @NotNull Task asyncRepeat(Runnable runnable, long period) {
        return TaskManager.asyncRepeat(runnable, 0, period, TimeUnit.MILLISECONDS);

    }

    public static @NotNull Task asyncRepeat(Runnable runnable, long period, TimeUnit unit) {
        return TaskManager.asyncRepeat(runnable, 0, period, unit);

    }

    public static @NotNull Task asyncLater(Runnable runnable, long delay) {
        return TaskManager.asyncLater(runnable, delay, TimeUnit.MILLISECONDS);
    }


//    public static @NotNull Task sync(Runnable runnable) {
//        Task task = new Task();
//        task.setFuture(EXECUTOR_SERVICE.submit(runnable));
//        return task;
//    }
//
//    public static @NotNull Task syncRepeat(Runnable runnable, long delay, long period, TimeUnit unit) {
//        Task task = new Task();
//        task.setFuture(SCHEDULED_THREAD_POOL.scheduleAtFixedRate(runnable, delay, period, unit));
//        return task;
//    }
//
//    public static @NotNull Task syncLater(Runnable runnable, long delay, TimeUnit unit) {
//        Task task = new Task();
//        task.setFuture(SCHEDULED_THREAD_POOL.schedule(runnable, delay, unit));
//        return task;
//    }
//
//    public static @NotNull Task syncRepeat(Runnable runnable, long period) {
//        return TaskManager.asyncRepeat(runnable, 0, period, TimeUnit.MILLISECONDS);
//
//    }
//
//    public static @NotNull Task syncRepeat(Runnable runnable, long period, TimeUnit unit) {
//        return TaskManager.asyncRepeat(runnable, 0, period, unit);
//
//    }
//
//    public static @NotNull Task syncLater(Runnable runnable, long delay) {
//        return TaskManager.syncLater(runnable, delay, TimeUnit.MILLISECONDS);
//    }


    public static @NotNull Task thread(Runnable runnable) {
        Task task = new Task();
        task.setThread(THREAD_FACTORY.newThread(runnable));
        return task;
    }

    public static @NotNull Task threadRepeat(Runnable runnable, long delay, long period, TimeUnit unit) {
        Task task = new Task();

        task.setThread(THREAD_FACTORY.newThread(() -> {
            try {
                unit.sleep(delay);

                while (!task.canceled) {
                    runnable.run();
                    unit.sleep(period);
                }

            } catch (InterruptedException ignored) {
            }

        }));

        return task;
    }


    public static @NotNull Task threadLater(Runnable runnable, long delay, TimeUnit unit) {
        Task task = new Task();

        task.setThread(THREAD_FACTORY.newThread(() -> {
            try {
                unit.sleep(delay);
                if (task.canceled) return;
                runnable.run();
            } catch (InterruptedException ignored) {
            }

        }));

        return task;
    }

    public static @NotNull Task threadLater(Runnable runnable, long delay) {
        return TaskManager.threadLater(runnable, delay, TimeUnit.MILLISECONDS);
    }

    public static @NotNull Task threadRepeat(Runnable runnable, long period) {
        return TaskManager.threadRepeat(runnable, 0, period, TimeUnit.MILLISECONDS);
    }

    public static @NotNull Task threadRepeat(Runnable runnable, long period, TimeUnit unit) {
        return TaskManager.threadRepeat(runnable, 0, period, unit);
    }

    public static @NotNull Task asyncRepeat(Runnable runnable) {
        return TaskManager.threadRepeat(runnable, 1);
    }


    @SuppressWarnings("unused")
    public static class Task {
        @Getter
        private long id;
        private Thread thread;
        private Future<?> future;
        @Getter
        private boolean canceled = false;


        private void setThread(Thread thread) {
            this.thread = thread;
            this.id = thread.getId();
            thread.start();
        }

        @SneakyThrows
        public void join() {
            if (thread != null) {
                thread.join();
                return;
            }

            if (future != null) {
                future.get();
                return;
            }

            throw new IllegalStateException("Invalid Task");

        }

        @SneakyThrows
        public void join(long timeout) {
            this.join(timeout, TimeUnit.MILLISECONDS);
        }

        @SneakyThrows
        public void join(long timeout, TimeUnit unit) {
            if (thread != null) {
                thread.join(unit.toMillis(timeout));
                return;
            }

            if (future != null) {
                future.get(timeout, unit);
                return;
            }

            throw new IllegalStateException("Invalid Task");
        }

        private void setFuture(Future<?> future) {
            this.future = future;
            this.id = (long) (Math.random() * 0x8888);
        }

        @SuppressWarnings("unchecked")
        @SneakyThrows
        public <T> T get() {
            return (T) future.get();
        }

        public void cancel() {
            if (thread != null) {
                thread.interrupt();
                canceled = true;
                return;
            }

            if (future != null) {
                future.cancel(true);
                return;
            }

            throw new IllegalStateException("Invalid Task");
        }
    }
}
