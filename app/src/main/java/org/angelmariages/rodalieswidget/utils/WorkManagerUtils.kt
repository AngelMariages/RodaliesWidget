/*
 * MIT License
 *
 * Copyright (c) 2022 Àngel Mariages
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package org.angelmariages.rodalieswidget.utils

import android.content.Context
import androidx.annotation.*
import androidx.work.*
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

// FROM: https://github.com/AndroidDeveloperLB/CommonUtils/blob/f1a23fbb29a91bcc2589eae59b2fc54371aa3ee2/library/src/main/java/com/lb/common_utils/WorkerManagerUtils.kt
/**reason to use this: https://issuetracker.google.com/issues/115575872 https://commonsware.com/blog/2018/11/24/workmanager-app-widgets-side-effects.html*/
object WorkerManagerUtils {
    fun interface OnGotWorkerManager {
        @WorkerThread
        fun onGotWorkerManager(workerManager: WorkManager)
    }

    @WorkerThread
    fun getWorkerManager(context: Context, onGotWorkerManager: OnGotWorkerManager) {
        val workManager = WorkManager.getInstance(context)
        val dummyWorkers = workManager.getWorkInfosByTag(DummyWorker.DUMMY_WORKER_TAG).get()
        val hasPendingDummyWorker =
            (dummyWorkers?.indexOfFirst { it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING } ?: -1) >= 0
        if (!hasPendingDummyWorker) {
            DummyWorker.schedule(context)
        }
        onGotWorkerManager.onGotWorkerManager(workManager)
    }

    class DummyWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
        override fun doWork(): Result {
            schedule(applicationContext)
            return Result.success()
        }

        companion object {
            const val DUMMY_WORKER_TAG = "DummyWorker"

            @AnyThread
            fun schedule(context: Context) {
                WorkManager.getInstance(context).enqueue(OneTimeWorkRequest.Builder(
                    DummyWorker::class.java).addTag(DUMMY_WORKER_TAG).setInitialDelay(10L * 365L, TimeUnit.DAYS)
                    .build())
            }
        }
    }
}