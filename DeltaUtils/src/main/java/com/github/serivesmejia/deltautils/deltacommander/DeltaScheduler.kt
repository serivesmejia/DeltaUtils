package com.github.serivesmejia.deltautils.deltacommander

class DeltaScheduler {

    companion object { val instance = DeltaScheduler() }

    //hashmap containing the subsystems and their default commands
    val subsystems: HashMap<DeltaSubsystem, DeltaCommand> = HashMap()

    //hashmap containing the currently scheduled commands and their state
    val scheduledCommands: HashMap<DeltaCommand, DeltaCommand.State> = HashMap()

    //hashmap containing the required subsystems by specific commands
    val requirements: HashMap<DeltaSubsystem, DeltaCommand> = HashMap()

    fun run() {

    }

    /**
     * Sets the default command for the specified subsystem, which will run
     * when no other command is running requiring the subsystem.
     * @param subsystem the subsystem to set the default command
     * @param command the default command
     */
    fun setDefaultCommand(subsystem: DeltaSubsystem, command: DeltaCommand) {

        if(!command.requirements.contains(subsystem)) {
            throw IllegalArgumentException("Default command \"${command.name}\" does not require subsystem \"${subsystem.name}\"")
        }

        if(command.finished) {
            throw IllegalArgumentException("Default command \"${command.name}\" is finished")
        }

        subsystems[subsystem] = command

    }

    /**
     * Get the default command of a specified subsystem
     * @param subsystem the subsystem to get the default command from
     */
    fun getDefaultCommand(subsystem: DeltaSubsystem) = subsystems[subsystem]

    fun stop(vararg cmds: DeltaCommand) {

        for(cmd in cmds) {

            if(!scheduledCommands.containsKey(cmd)) continue

            cmd.end()

            scheduledCommands.remove(cmd)
            requirements.keys.removeAll(cmd.requirements)

        }

    }

    fun stopAll() {git add
        for((cmd, state) in scheduledCommands) {
            stop(cmd)
        }
    }

    fun isScheduled()

}