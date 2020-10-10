package com.github.serivesmejia.deltautils

import com.github.serivesmejia.deltautils.deltacommander.*
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

        DeltaScheduler.instance.update()

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
            DeltaScheduler.instance.update()
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

        DeltaScheduler.instance.update()

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
            DeltaScheduler.instance.update()
        }

        DeltaScheduler.instance.stopAll()

        assertEquals(1, x)
        assertEquals(50, y)
        assertEquals(1, z)

        DeltaScheduler.reset()

    }

    @Test
    fun testCommandSubsystem() {

        x = 0
        y = 0
        z = 0

        var subsystem = object: DeltaSubsystem() {

            override fun loop() {
                x += 1
            }

            fun test() {
                y += 1;
            }

        }

        DeltaScheduler.instance.addSubsystem(subsystem)

        DeltaScheduler.instance.schedule(object: DeltaCommand() {

            init {
                require(subsystem)
            }

            override fun init() { }

            override fun run() {
                subsystem.test()
                z += 1
            }

            override fun end(interrupted: Boolean) { }

        })

        DeltaScheduler.instance.update()

        DeltaScheduler.instance.stopAll()

        assertEquals(1, x)
        assertEquals(1, y)
        assertEquals(1, z)

        DeltaScheduler.reset()

    }


    @Test
    fun testRunGroupedCommandParallel() {

        x = 0
        y = 0
        z = 0

        val cmd1 = XYZPlusCommand(50)
        val cmd2 = XYZPlusCommand(50)

        DeltaScheduler.instance.schedule(DeltaGroupedCommand(DeltaGroupedCommand.ExecutionMode.LINEAR, cmd1, cmd2))

        repeat(200) {
            DeltaScheduler.instance.update()
        }

        DeltaScheduler.instance.stopAll()

        assertEquals(1, cmd1.x)
        assertEquals(50, cmd1.y)
        assertEquals(1, cmd1.z)

        assertEquals(1, cmd2.x)
        assertEquals(50, cmd2.y)
        assertEquals(1, cmd2.z)

        DeltaScheduler.reset()

    }

    @Test
    fun testRunGroupedCommandLinear() {

        x = 0
        y = 0
        z = 0

        val cmd1 = XYZPlusCommand(50)
        val cmd2 = XYZPlusCommand(80)

        DeltaScheduler.instance.schedule(DeltaGroupedCommand(DeltaGroupedCommand.ExecutionMode.PARALLEL, cmd1, cmd2))

        repeat(200) {
            DeltaScheduler.instance.update()
        }

        DeltaScheduler.instance.stopAll()

        assertEquals(1, cmd1.x)
        assertEquals(50, cmd1.y)
        assertEquals(1, cmd1.z)

        assertEquals(1, cmd2.x)
        assertEquals(80, cmd2.y)
        assertEquals(1, cmd2.z)

        DeltaScheduler.reset()

    }

    class XYZPlusCommand(val maxY: Int) : DeltaCommand() {

        var x = 0
        var y = 0
        var z = 0

        override fun init() {
            x += 1
        }

        override fun run() {
            y += 1
            if(y >= maxY) finish()
        }

        override fun end(interrupted: Boolean) {
            z += 1
        }

    }

}