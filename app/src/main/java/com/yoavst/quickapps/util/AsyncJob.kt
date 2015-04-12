package com.yoavst.mashov

import android.os.Handler
import android.os.Looper

import java.util.concurrent.ExecutorService
import java.util.concurrent.FutureTask
import com.yoavst.util.r

/**
 * Android library to easily queue background and UI tasks
 * Created by Arasthel on 08/07/14.
 * Modified by Yoav Sternberg
 */
public class AsyncJob<JobResult> {
    // Action to do in background
    public var actionInBackground: AsyncAction<JobResult>? = null
    private var exceptionAction: AsyncResultAction<Exception>? = null

    // Action to do when the background action ends
    public var actionOnResult: AsyncResultAction<JobResult>? = null

    // An optional ExecutorService to enqueue the actions
    public var executorService: ExecutorService? = null

    // The thread created for the action
    private var asyncThread: Thread? = null
    // The FutureTask created for the action
    private var asyncFutureTask: FutureTask<Any>? = null

    // The result of the background action
    private var result: JobResult = null

    /**
     * Begins the background execution providing a result, similar to an AsyncTask.
     * It will execute it on a new Thread or using the provided ExecutorService
     */
    public fun start() {
        if (actionInBackground != null) {
            val jobToRun = r {
                var hasThrown = false
                try {
                    result = actionInBackground!!.doAsync()
                } catch (e: Exception) {
                    hasThrown = true
                    if (exceptionAction != null)
                        uiHandler.post { exceptionAction!!.onResult(e) }
                }
                if (!hasThrown) onResult()
            }
            if (executorService != null) {
                asyncFutureTask = executorService!!.submit(jobToRun) as FutureTask<Any>
            } else {
                asyncThread = Thread(jobToRun)
                asyncThread!!.start()
            }
        }
    }

    /**
     * Cancels the AsyncJob interrupting the inner thread.
     */
    public fun cancel() {
        if (actionInBackground != null) {
            if (executorService != null) {
                asyncFutureTask!!.cancel(true)
            } else {
                asyncThread!!.interrupt()
            }
        }
    }

    private fun onResult() {
        if (actionOnResult != null) {
            uiHandler.post(object : Runnable {
                override fun run() {
                    actionOnResult!!.onResult(result)
                }
            })
        }
    }

    /**
     * Specifies which action to run when the background throws exception
     *
     * @param actionOnException the action
     */
    public fun setActionOnException(actionOnException: AsyncResultAction<Exception>?) {
        this.exceptionAction = actionOnException
    }

    public trait AsyncAction<ActionResult> {
        throws(javaClass<Exception>())
        public fun doAsync(): ActionResult
    }

    public trait AsyncResultAction<ActionResult> {
        public fun onResult(result: ActionResult)
    }

    public trait OnMainThreadJob {
        public fun doInUIThread()
    }

    public trait OnBackgroundJob {
        public fun doOnBackground()
    }

    /**
     * Builder class to instantiate an AsyncJob in a clean way
     *
     * @param <JobResult> the type of the expected result
     */
    public class AsyncJobBuilder<JobResult> {

        private var asyncAction: AsyncAction<JobResult>? = null
        private var asyncException: AsyncResultAction<Exception>? = null
        private var asyncResultAction: AsyncResultAction<JobResult>? = null
        private var executor: ExecutorService? = null

        /**
         * Specifies which action to run on background
         *
         * @param action the AsyncAction to run
         * @return the builder object
         */
        public fun doInBackground(action: AsyncAction<JobResult>): AsyncJobBuilder<JobResult> {
            asyncAction = action
            return this
        }

        /**
         * Specifies which action to run on background
         *
         * @param action the AsyncAction to run
         * @return the builder object
         */
        public fun doInBackground(action: () -> JobResult): AsyncJobBuilder<JobResult> {
            return doInBackground(object : AsyncAction<JobResult> {
                override fun doAsync(): JobResult {
                    return action()
                }
            })
        }

        /**
         * Specifies which action to run on background
         *
         * @param action the AsyncAction to run
         * @return the builder object
         */
        public fun onException(action: AsyncResultAction<Exception>): AsyncJobBuilder<JobResult> {
            asyncException = action
            return this
        }

        /**
         * Specifies which action to run on background
         *
         * @param action the AsyncAction to run
         * @return the builder object
         */
        public fun onException(action: (exception: Exception) -> Unit): AsyncJobBuilder<JobResult> {
            return onException(object : AsyncResultAction<Exception> {
                override fun onResult(result: Exception) {
                    action(result)
                }
            })
        }

        /**
         * Specifies which action to run when the background action ends
         *
         * @param action the AsyncAction to run
         * @return the builder object
         */
        public fun doWhenFinished(action: AsyncResultAction<JobResult>): AsyncJobBuilder<JobResult> {
            asyncResultAction = action
            return this
        }

        /**
         * Specifies which action to run when the background action ends
         *
         * @param action the AsyncAction to run
         * @return the builder object
         */
        public fun doWhenFinished(action: (result: JobResult) -> Unit): AsyncJobBuilder<JobResult> {
           return doWhenFinished(object: AsyncResultAction<JobResult> {
               override fun onResult(result: JobResult) {
                   action(result)
               }

           })
        }

        /**
         * Used to provide an ExecutorService to launch the AsyncActions
         *
         * @param executor the ExecutorService which will queue the actions
         * @return the builder object
         */
        public fun withExecutor(executor: ExecutorService): AsyncJobBuilder<JobResult> {
            this.executor = executor
            return this
        }

        /**
         * Instantiates a new AsyncJob of the given type
         *
         * @return a configured AsyncJob instance
         */
        public fun create(): AsyncJob<JobResult> {
            val asyncJob = AsyncJob<JobResult>()
            asyncJob.actionInBackground = asyncAction
            asyncJob.actionOnResult = asyncResultAction
            asyncJob.executorService = executor
            asyncJob.setActionOnException(asyncException)
            return asyncJob
        }

    }

    class object {

        private val uiHandler = Handler(Looper.getMainLooper())

        /**
         * Executes the provided code immediately on the UI Thread
         *
         * @param onMainThreadJob Interface that wraps the code to execute
         */
        public fun doOnMainThread(onMainThreadJob: OnMainThreadJob) {
            uiHandler.post(r { onMainThreadJob.doInUIThread() })
        }

        /**
         * Executes the provided code immediately on the UI Thread
         *
         */
        public fun doOnMainThread(runnable: () -> Unit) {
            doOnMainThread(object : OnMainThreadJob {
                override fun doInUIThread() {
                    runnable()
                }
            })
        }

        /**
         * Executes the provided code immediately on a background thread
         *
         * @param onBackgroundJob Interface that wraps the code to execute
         */
        public fun doInBackground(onBackgroundJob: OnBackgroundJob) {
            Thread(r { onBackgroundJob.doOnBackground() }).start()
        }

        /**
         * Executes the provided code immediately on a background thread
         *
         */
        public fun doInBackground(runnable: () -> Unit) {
            doInBackground(object : OnBackgroundJob {
                override fun doOnBackground() {
                    runnable()
                }
            })
        }

        /**
         * Executes the provided code immediately on a background thread that will be submitted to the
         * provided ExecutorService
         *
         * @param onBackgroundJob Interface that wraps the code to execute
         * @param executor        Will queue the provided code
         */
        public fun doInBackground(onBackgroundJob: OnBackgroundJob, executor: ExecutorService): FutureTask<Any> {
            val task = executor.submit(r { onBackgroundJob.doOnBackground() }) as FutureTask<Any>
            return task
        }
    }
}
