package com.github.serivesmejia.DeltaUtils

import com.github.serivesmejia.deltautils.deltacommander.DeltaCommand
import com.github.serivesmejia.deltautils.deltacommander.DeltaScheduler
import com.github.serivesmejia.deltautils.deltacommander.DeltaSchedulerEvent
import org.junit.Assert.*
import org.junit.Test

class DeltaCommanderTests {

    var x = 0
    var y = 0
    var z = 0

    val emptyCommand = object: DeltaCommand() {
        override fun init() { }
        override fun run() { }
        override fun end(interrupted: Boolean) { }
    }

    @Test
    fun testInitRunStopCommand() {

        x = 0
        y = 0
        z = 0

        DeltaScheduler.instance.schedule(object: DeltaCommand() {

            override fun init() {
                x += 1
            }

            override fun run() {
                y += 1
            }

            override fun end(interrupted: Boolean) {
                z += 1
            }

        })

        DeltaScheduler.instance.run()

        DeltaScheduler.instance.stopAll()

        assertEquals(1, x)
        assertEquals(1, y)
        assertEquals(1, z)

        DeltaScheduler.reset()

    }

    @Test
    fun testRunCommandMultipleTimes() {

        x = 0
        y = 0
        z = 0

        DeltaScheduler.instance.schedule(object: DeltaCommand() {

            override fun init() {
                x += 1
            }

            override fun run() {
                y += 1
            }

            override fun end(interrupted: Boolean) {
                z += 1
            }

        })

        repeat(100) {
            DeltaScheduler.instance.run()
        }

        DeltaScheduler.instance.stopAll()

        assertEquals(1, x)
        assertEquals(100, y)
        assertEquals(1, z)

        DeltaScheduler.reset()

    }

    @Test
    fun testRunUserEvents() {

        x = 0
        y = 0
        z = 0

        DeltaScheduler.instance.onInitCommand(object: DeltaSchedulerEvent {
            override fun run(command: DeltaCommand) {
                x += 1
            }
        })

        DeltaScheduler.instance.onRunCommand(object: DeltaSchedulerEvent {
            override fun run(command: DeltaCommand) {
                y += 1
            }
        })

        DeltaScheduler.instance.onInterruptCommand(object: DeltaSchedulerEvent {
            override fun run(command: DeltaCommand) {
                z += 1
            }
        })

        DeltaScheduler.instance.schedule(emptyCommand)

        DeltaScheduler.instance.run()

        DeltaScheduler.instance.stopAll()

        assertEquals(1, x)
        assertEquals(1, y)
        assertEquals(1, z)

        DeltaScheduler.reset()

    }

    @Test
    fun testFinishCommand() {

        x = 0
        y = 0
        z = 0

        val command = object: DeltaCommand() {

            override fun init() {
                x += 1
            }

            override fun run() {
                y += 1
                if(y >= 50) finish()
            }

            override fun end(interrupted: Boolean) {
                z += 1
            }

        }

        DeltaScheduler.instance.schedule(command)

        repeat(100) {
            DeltaScheduler.instance.run()
        }

        DeltaScheduler.instance.stopAll()

        assertEquals(1, x)
        assertEquals(50, y)
        assertEquals(1, z)

        DeltaScheduler.reset()

    }

}