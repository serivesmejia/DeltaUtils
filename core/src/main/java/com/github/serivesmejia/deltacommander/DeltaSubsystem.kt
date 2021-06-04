package com.github.serivesmejia.deltacommander

abstract class DeltaSubsystem {

    val name = this.javaClass.simpleName

    /**
     * Method to be executed repeatedly, independently of any command
     * Called on each DeltaScheduler.run() call
     */
    abstract fun loop()

    /**
     * The default command for this subsystem, which will be scheduled
     * when no other command is running for this subsystem
     */
    var defaultCommand: DeltaCommand?
        get() = deltaScheduler.getDefaultCommand(this)
        set(value) { deltaScheduler.setDefaultCommand(this, value!!) }

}