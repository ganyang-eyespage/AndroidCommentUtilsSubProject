package com.eyespage.utils;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import com.eyespage.lib.thread.Worker;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskUtil {

  private static ExecutorService mRapidWorkExecutor =
      Executors.newFixedThreadPool(5, new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override public Thread newThread(Runnable r) {
          return new Thread(r, "RapidWorkTask #" + mCount.getAndIncrement());
        }
      });

  private static ExecutorService mHeavyWorkExecutor =
      Executors.newCachedThreadPool(new ThreadFactory() {
            private final AtomicInteger mCount = new AtomicInteger(1);

            @Override public Thread newThread(Runnable r) {
              return new Thread(r, "HeavyWorkTask #" + mCount.getAndIncrement());
            }
          });

  public static Handler mMainHandler = new Handler(Looper.getMainLooper());

  public static <Params, Progress, Result> void executeAsyncTask(
      AsyncTask<Params, Progress, Result> task, Params... params) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    } else {
      task.execute(params);
    }
  }

  public static void doHeavyWork(Worker background) {
    mHeavyWorkExecutor.submit(background);
  }

  /**
   * run a rapid task on background.
   *
   * @param background the background Work
   */
  public static void doRapidWork(Worker background) {
    mRapidWorkExecutor.submit(background);
  }

  /**
   * run a heavy task on the background and then post the "post" work on the ui thread
   *
   * @param background the background Work
   * @param post the post work
   */
  public static void doHeavyWorkAndPost(final Worker background, final Worker post) {
    doRapidWorkAndPost(background, post, 0);
  }

  /**
   * run a rapid task on the background and then post the "post" work on the ui thread
   *
   * @param background the background Work
   * @param post the post work
   */
  public static void doRapidWorkAndPost(final Worker background, final Worker post) {
    doRapidWorkAndPost(background, post, 0);
  }

  public static void doHeavyWork(AsyncWorker<?> asyncWorker) {
    asyncWorker.start(mHeavyWorkExecutor, mMainHandler);
  }

  public static void doRapidWork(AsyncWorker<?> asyncWorker) {
    asyncWorker.start(mRapidWorkExecutor, mMainHandler);
  }

  /**
   * run a heavy task on the background and then post the "post" work on the ui thread after
   * the delay time.
   *
   * @param background the background Work
   * @param post the post work
   * @param delay time delayed befor post
   */
  public static void doHeavyWorkAndPost(final Worker background, final Worker post,
      final int delay) {
    mHeavyWorkExecutor.submit(new Runnable() {
      @Override public void run() {
        background.work();
        mMainHandler.postDelayed(post, delay);
      }
    });
  }

  /**
   * run a rapid task on the background and then post the "post" work on the ui thread after
   * the delay time.
   *
   * @param background the background Work
   * @param post the post work
   * @param delay time delayed befor post
   */
  public static void doRapidWorkAndPost(final Worker background, final Worker post,
      final long delay) {
    mRapidWorkExecutor.submit(new Runnable() {
      @Override public void run() {
        background.work();
        mMainHandler.postDelayed(post, delay);
      }
    });
  }

  /**
   * post the "post" work on the ui thread after
   * the delay time.
   *
   * @param post the post work
   * @param delay time delayed befor post
   */
  public static void postOnMain(Worker post, final long delay) {
    mMainHandler.postDelayed(post, delay);
  }

  public static void postOnMain(Worker post) {
    mMainHandler.post(post);
  }

  public static void removePostedWork(Worker post) {
    mMainHandler.removeCallbacks(post);
  }

  public static abstract class AsyncWorker<Result> {
    private void start(ExecutorService service, Handler mainHandler) {
      service.submit(new Worker() {
        @Override public void work() {
          final Result result = AsyncWorker.this.work();
          mMainHandler.post(new Worker() {
            @Override public void work() {
              AsyncWorker.this.post(result);
            }
          });
        }
      });
    }

    public abstract Result work();

    public abstract void post(Result result);
  }
}
