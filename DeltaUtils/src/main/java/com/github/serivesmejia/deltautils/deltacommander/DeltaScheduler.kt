package com.github.serivesmejia.deltautils.deltacommander

class DeltaScheduler {

    companion object {
        val instance = DeltaScheduler()
    }

    val subsystem = HashMap<DeltaSubsystem, DeltaCommand>()

    fun setDefaultCommand(subsystem: DeltaSubsystem, command: DeltaCommand) {



    }

}