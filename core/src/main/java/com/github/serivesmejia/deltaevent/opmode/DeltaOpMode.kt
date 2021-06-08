package com.github.serivesmejia.deltaevent.opmode

import com.github.serivesmejia.deltacommander.deltaScheduler
import com.github.serivesmejia.deltacommander.reset
import com.github.serivesmejia.deltadrive.hardware.DeltaHardware
import com.github.serivesmejia.deltaevent.gamepad.SuperGamepad
import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode

abstract class DeltaOpMode : LinearOpMode() {

    lateinit var deltaHardware: DeltaHardware

    lateinit var superGamepad1: SuperGamepad
    lateinit var superGamepad2: SuperGamepad

    private var isDefaultRun = false

    override fun runOpMode() {
        deltaScheduler.reset()

        superGamepad1 = SuperGamepad(gamepad1)
        superGamepad2 = SuperGamepad(gamepad2)

        initialize()

        superGamepad1.attachToScheduler()
        superGamepad2.attachToScheduler()
        deltaHardware.bulkCachingMode = LynxModule.BulkCachingMode.MANUAL

        waitForStart()

        if(isStopRequested) return

        while(opModeIsActive()) {
            deltaHardware.clearBulkCache()

            run()

            deltaScheduler.update()

            if(isDefaultRun && deltaScheduler.commandsAmount <= 0) {
                break
            }
        }
    }

    abstract fun initialize()

    open fun run() {
        isDefaultRun = true
    }

}