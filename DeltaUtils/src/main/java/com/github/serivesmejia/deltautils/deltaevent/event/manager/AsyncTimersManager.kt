/*
 * Copyright (c) 2020 FTC Delta Robotics #9351 - Sebastian Erives
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.serivesmejia.deltautils.deltaevent.event.manager

import com.github.serivesmejia.deltautils.deltaevent.timer.SuperAsyncTimer
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class AsyncTimersManager {

    companion object {
        var asyncTimersManager: Thread? = null

        var asyncTimers = ArrayList<SuperAsyncTimer>()

        var alreadyInitialized = false

        @OpModeRegistrar //annotation for the FTC SDK to execute this method every time the robot initializes
        fun initialize(manager: OpModeManager?) {
            removeAsyncTimers()
            if (!asyncTimersManager!!.isAlive) asyncTimersManager!!.start()
        }

        fun removeAsyncTimers() {
            for (timer in asyncTimers) {
                GlobalScope.launch { timer.destroy() }
                asyncTimers.remove(timer)
            }
        }

        fun addAsyncTimer(timer: SuperAsyncTimer) {
            if (!asyncTimers.contains(timer)) {
                asyncTimers.add(timer)
            }
        }

        fun removeAsyncTimer(timer: SuperAsyncTimer) {
            if (asyncTimers.contains(timer)) {
                GlobalScope.launch { timer.destroy() }
                asyncTimers.remove(timer)
            }
        }

        private class AsyncTimersManagerRunnable : Runnable {
            override fun run() {
                while (!Thread.interrupted()) {
                    val safeAsyncTimers = asyncTimers.toTypedArray()
                    for (asyncTimer in safeAsyncTimers) {
                        GlobalScope.launch { asyncTimer.update() }
                    }
                }
            }
        }
    }
}