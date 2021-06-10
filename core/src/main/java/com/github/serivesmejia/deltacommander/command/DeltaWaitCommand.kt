package com.github.serivesmejia.deltacommander.command

import com.github.serivesmejia.deltacommander.DeltaCommand
import com.qualcomm.robotcore.util.ElapsedTime

class DeltaWaitCommand(val seconds: Double) : DeltaCommand() {

    private val timer = ElapsedTime()

    override fun init() = timer.reset()

    override fun run() {
        if(timer.seconds() >= seconds) finish()
    }

}