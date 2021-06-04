package com.github.serivesmejia.deltacommander

@Suppress("UNUSED")
class DeltaScheduler internal constructor() {

    var enabled = true

    //hashmap containing the subsystems and their default commands
    val subsystems: HashMap<DeltaSubsystem, DeltaCommand?> = HashMap()

    //hashmap containing the currently scheduled commands and their state
    val scheduledCommands: HashMap<DeltaCommand, DeltaCommand.State> = HashMap()
    
     //hashmap containing the required subsystems by specific commands
    val requirements: HashMap<DeltaSubsystem, DeltaCommand> = HashMap()

    //user events
    private val initEvents: ArrayList<DeltaSchedulerEvent> = ArrayList()
    private val runEvents: ArrayList<DeltaSchedulerEvent> = ArrayList()
    private val interruptEvents: ArrayList<DeltaSchedulerEvent> = ArrayList()
    private val endEvents: ArrayList<DeltaSchedulerEvent> = ArrayList()
    private val runSchedulerEvents: ArrayList<Runnable> = ArrayList()

    private fun commandInit(cmd: DeltaCommand, isInterruptible: Boolean) {
        cmd.init()
        val state = DeltaCommand.State(isInterruptible)

        for(evt in initEvents) { evt.run(cmd) } //run the init user events

        scheduledCommands[cmd] = state
    }

    /**
     * Schedule a command to be runned
     * @param cmd the command to be scheduled
     * @param isInterruptible whether the command can be interrupted
     */
    fun schedule(cmd: DeltaCommand, isInterruptible: Boolean = true) {
        if(!enabled) return

        val cmdReqs = cmd.requirements
        var reqsCurrentlyInUse = false

        //check if a requirement from the scheduled command is currently in use
        for(req in cmdReqs) {
            reqsCurrentlyInUse = reqsCurrentlyInUse && requirements.contains(req)
        }

        if(!reqsCurrentlyInUse) {
            commandInit(cmd, isInterruptible) //directly run it, if none of its requirements are in use
        } else {
            //check if the commands requiring a specific subsystem are interruptible
            for(req in cmdReqs) {
                if(requirements.containsKey(req) && !scheduledCommands[requirements[req]]!!.interruptible) {
                    return
                }
            }

            //cancel all the commands that require a subsystem
            for(req in cmdReqs) {
                if(requirements.containsKey(req)) {
                    requirements[req]?.let { stop(it) }
                }
            }

            commandInit(cmd, isInterruptible) //schedule the command once all the other requiring commands were cancelled
        }
    }

    /**
     * Schedule multiple commands
     * @param cmds multiple commands to be scheduled
     * @param isInterruptible whether the commands can be interrupted
     */
    fun schedule(isInterruptible: Boolean = true, vararg cmds: DeltaCommand) {
        for(cmd in cmds) {
            schedule(cmd, isInterruptible)
        }
    }

    /**
     * Add one or multiple subsystems
     * @param subsystems multiple subsystems to be scheduled
     */
    fun addSubsystem(vararg subsystems: DeltaSubsystem) {
        for(subsystem in subsystems) {
            this.subsystems[subsystem] = null
        }
    }

    /**
     * Remove one or multiple subsystems
     * @param subsystems multiple subsystems to be removed
     */
    fun removeSubsystem(vararg subsystems: DeltaSubsystem) {
        for(subsystem in subsystems) {
            this.subsystems.remove(subsystem)
        }
    }

    /**
     * Run all the scheduled commands & events
     */
    fun update() {

        if(!enabled) return //if the schedulers is disabled then abort

        for(subsystem in subsystems.keys) { subsystem.loop() } //run the loop method of all the subsystems

        for((cmd, _) in scheduledCommands.entries.toTypedArray()) { //iterate through the scheduled commands
            cmd.run() //actually run the command

            for(evt in runEvents) { evt.run(cmd) } //execute the user events

            if(cmd.finished) { //end and remove the command if it's finished
                cmd.end(false)

                for(evt in endEvents) { evt.run(cmd) }

                scheduledCommands.remove(cmd)
                requirements.keys.removeAll(cmd.requirements)
            }
        }

        //register default command if no command is requiring the subsystem
        for((subsystem, defCmd) in subsystems) {
            if(!requirements.containsKey(subsystem) && defCmd != null) {
                schedule(false, defCmd) //schedule the default command if no other command is scheduled for this subsystem
            }
        }

        for(evt in runSchedulerEvents) { evt.run() }
    }

    /**
     * Sets the default command for the specified subsystem, which will run
     * when no other command is running requiring the subsystem.
     * @param subsystem the subsystem to set the default command
     * @param command the default command
     */
    fun setDefaultCommand(subsystem: DeltaSubsystem, command: DeltaCommand) {
        if(!command.requirements.contains(subsystem)) {
            throw IllegalArgumentException("Default command \"${command.name}\" does not require its subsystem \"${subsystem.name}\"")
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

    /**
     * Check if a command is scheduled
     */
    fun isScheduled(command: DeltaCommand) = scheduledCommands.containsKey(command)

    /**
     * Stop one or more commands
     * @param cmds commands to be stopped
     */
    fun stop(vararg cmds: DeltaCommand) {
        for(cmd in cmds) {
            if(!scheduledCommands.containsKey(cmd)) continue

            cmd.end(true)

            for(evt in interruptEvents) { evt.run(cmd) }

            scheduledCommands.remove(cmd)
            requirements.keys.removeAll(cmd.requirements)
        }
    }

    /**
     * Stop all the currently requested commands
     */
    fun stopAll() {
        for((cmd, _) in scheduledCommands) {
            stop(cmd)
        }
    }

    /**
     * Get the command requiring a subsystem.
     * Will return null if not currently required.
     */
    fun requirements(subsystem: DeltaSubsystem) = requirements[subsystem]

    fun enable() {
        enabled = true
    }

    fun disable() {
        enabled = false
    }

    //Event register methods

    fun onInitCommand(event: DeltaSchedulerEvent) = initEvents.add(event)

    fun onRunCommand(event: DeltaSchedulerEvent) = runEvents.add(event)

    fun onInterruptCommand(event: DeltaSchedulerEvent) = interruptEvents.add(event)

    fun onEndCommand(event: DeltaSchedulerEvent) = endEvents.add(event)

    fun onRunScheduler(event: Runnable) = runSchedulerEvents.add(event)

}

//actual variable containing the singleton
private var inst: DeltaScheduler? = null

//variable which will be accessed statically
val deltaScheduler: DeltaScheduler
    get() {
        if(inst == null) inst = DeltaScheduler()
        return inst!!
    }

/**
 * Destroys the current scheduler instance
 * A new one will be created the next time the variable is accessed
 */
fun DeltaScheduler.reset() {
    inst = null
}