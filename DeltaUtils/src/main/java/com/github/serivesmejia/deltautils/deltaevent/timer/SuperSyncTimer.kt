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

package com.github.serivesmejia.deltautils.deltaevent.timer

import com.github.serivesmejia.deltautils.DeltaAppUtil
import com.github.serivesmejia.deltautils.deltaevent.Super
import com.github.serivesmejia.deltautils.deltaevent.event.Event
import com.github.serivesmejia.deltautils.deltaevent.event.manager.AsyncTimersManager.Companion.removeAsyncTimer
import com.github.serivesmejia.deltautils.deltaevent.event.timer.TimerEvent
import com.qualcomm.robotcore.hardware.HardwareMap
import java.util.*
import kotlin.collections.ArrayList
import kotlinx.coroutines.*

open class SuperSyncTimer(private val hardwareMap: HardwareMap) : Super {

    private val eventsTime = HashMap<TimerEvent, TimerDataPacket>()

    var destroying = false

    override var events: ArrayList<Event> = ArrayList()

    private var finishedDestroying = false

    val msStuckInDestroy: Long = 1000

    override fun registerEvent(event: Event): SuperSyncTimer {
        require(event is TimerEvent) { "Event is not TimerEvent" }
        events.add(event)
        eventsTime[event] = TimerDataPacket(0, 0, false)
        return this
    }

    override fun registerEvent(event: Event, repeat: Boolean): SuperSyncTimer {
        require(event is TimerEvent) { "Event is not TimerEvent" }
        events.add(event)
        eventsTime[event] = TimerDataPacket(0, 0, repeat)
        return this
    }

    override fun registerEvent(event: Event, timeSeconds: Int): SuperSyncTimer {
        require(event is TimerEvent) { "Event is not TimerEvent" }
        events.add(event)
        eventsTime[event] = TimerDataPacket((timeSeconds * 1000).toLong(), 0, false)
        return this
    }

    override fun registerEvent(event: Event, timeSeconds: Double): SuperSyncTimer {
        require(event is TimerEvent) { "Event is not TimerEvent" }
        events.add(event)
        eventsTime[event] = TimerDataPacket(timeSeconds.toLong() * 1000, 0, false)
        return this
    }

    override fun registerEvent(event: Event, timeSeconds: Int, repeat: Boolean): SuperSyncTimer {
        require(event is TimerEvent) { "Event is not TimerEvent" }
        events.add(event)
        eventsTime[event] = TimerDataPacket(timeSeconds.toLong() * 1000, 0, repeat)
        return this
    }

    override fun registerEvent(event: Event, timeSeconds: Double, repeat: Boolean): SuperSyncTimer {
        require(event is TimerEvent) { "Event is not TimerEvent" }
        events.add(event)
        eventsTime[event] = TimerDataPacket(timeSeconds.toLong() * 1000, 0, repeat)
        return this
    }

    override fun unregisterEvents() {
        eventsTime.clear()
    }

    /**
     * Update method, which is not needed to call manually because it is called in another thread.
     */
    override fun update() {
        if (destroying) return

        val evtToRemove = ArrayList<TimerEvent>()

        for ((evt, evtTimeDataPacket) in eventsTime) {

            if (evtTimeDataPacket.msLastSystemTime == 0L) {
                evt.startEvent()
                evtTimeDataPacket.msStartSystemTime = System.currentTimeMillis()
                evtTimeDataPacket.msLastSystemTime = System.currentTimeMillis()
            }

            if (evt.isCancelled()) {
                evtToRemove.add(evt)
                continue
            }

            evt.loopEvent(TimerDataPacket(evtTimeDataPacket))

            if (evtTimeDataPacket.msEventTime < 1) {

                evt.timeoutEvent()

                if (!evtTimeDataPacket.repeat) {
                    evtToRemove.add(evt)
                } else {
                    evtTimeDataPacket.msStartSystemTime = 0
                    evtTimeDataPacket.msElapsedTime = 0
                    evtTimeDataPacket.msLastSystemTime = 0
                }

            } else {
                val msElapsed = System.currentTimeMillis() - evtTimeDataPacket.msStartSystemTime

                evtTimeDataPacket.msElapsedTime = msElapsed
                evtTimeDataPacket.msLastSystemTime = System.currentTimeMillis()

                if (evtTimeDataPacket.msEventTime >= evtTimeDataPacket.msElapsedTime) {
                    evt.timeoutEvent()
                    if (!evtTimeDataPacket.repeat) {
                        evtToRemove.add(evt)
                    } else {
                        evtTimeDataPacket.msStartSystemTime = 0
                        evtTimeDataPacket.msElapsedTime = 0
                        evtTimeDataPacket.msLastSystemTime = 0
                    }
                }
            }
            eventsTime.remove(evt)
            eventsTime[evt] = evtTimeDataPacket
        }

        for (evt in evtToRemove) {
            eventsTime.remove(evt)
        }
    }

    /**
     * Destroy this SuperSyncTimer synchronously.
     * IT NEEDS TO BE CALLED AT THE END OF YOUR OPMODE
     */
    fun destroy() {

        destroying = true

        GlobalScope.launch {

            val msStartDestroying = System.currentTimeMillis()
            val msMaxTimeDestroying = msStartDestroying + msStuckInDestroy

            delay(msMaxTimeDestroying)

            if (!finishedDestroying) {
                DeltaAppUtil.restartAppCausedByError(hardwareMap!!, "User SuperTimer stuck in destroy(). Restarting robot controller app.", "SuperTimer stuck in destroy(). Restarting robot controller app.")
            }

        }

        if (this is SuperAsyncTimer) removeAsyncTimer(this)
        cancelAllEvents()
    }


    fun cancelAllEvents() {
        val safeEvents = events.toTypedArray()

        for (evt in safeEvents) {
            val e = evt as TimerEvent
            e.cancel()
        }

        if (destroying) finishedDestroying = true
    }

    fun hasFinishedDestroying(): Boolean {
        return finishedDestroying
    }


}