package com.github.serivesmejia.deltacommander

abstract class DeltaSubsystem {

    val name = this.javaClass.simpleName

    protected var maxAllowedRequirements = 1

    /**
     * Method to be executed repeatedly, independently of any command
     * Called on each DeltaScheduler.run() call
     */
    abstract fun loop();

    /**
     * Sets the default command for this subsystem, which will be scheduled when no other
     * command is running for this subsystem.
     * @param defCmd default DeltaCommand to be set
     */
    fun setDefaultCommand(defCmd: DeltaCommand) {
        DeltaScheduler.instance.setDefaultCommand(this, defCmd)
    }

    /**
     * Get the default command of this subsystem
     */
    fun getDefaultCommand() = DeltaScheduler.instance.getDefaultCommand(this)

}