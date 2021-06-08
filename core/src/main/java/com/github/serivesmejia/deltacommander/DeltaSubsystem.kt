package com.github.serivesmejia.deltacommander

@Suppress("LeakingThis", "UNUSED")
abstract class DeltaSubsystem(addToScheduler: Boolean = true) {

    val name = this.javaClass.simpleName

    init {
        if(addToScheduler) {
            deltaScheduler.addSubsystem(this)
        }
    }

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