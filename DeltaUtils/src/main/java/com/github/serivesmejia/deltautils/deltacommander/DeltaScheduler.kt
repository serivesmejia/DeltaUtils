package com.github.serivesmejia.deltautils.deltacommander

open class DeltaScheduler {

    //Singleton initializer
    companion object {

        //actual variable containing the singleton
        private var inst: DeltaScheduler? = null

        //variable which will be accessed statically
        val instance: DeltaScheduler get() {

            if(inst == null) inst = DeltaScheduler()

            return inst!!

        }

        /**
         * Destroys the current scheduler instance
         * A new one will be created the next time the variable is accessed
         */
        fun reset() {
            inst = null
        }

    }

    var enabled = true

    //hashmap containing the subsystems and their default commands
    val subsystems: HashMap<DeltaSubsystem, DeltaCommand> = HashMap()

    //hashmap containing the currently scheduled commands and their state
    val scheduledCommands: HashMap<DeltaCommand, DeltaCommand.State> = HashMap()
    
     //hashmap containing the required subsystems by specific commands
    val requirements: HashMap<DeltaSubsystem, DeltaCommand> = HashMap()

    //user events
    private val initEvents: ArrayList<DeltaSchedulerEvent> = ArrayList()
    private val runEvents: ArrayList<DeltaSchedulerEvent> = ArrayList()
    private val interruptEvents: ArrayList<DeltaSchedulerEvent> = ArrayList()
    private val endEvents: ArrayList<DeltaSchedulerEvent> = ArrayList()

    fun commandInit(cmd: DeltaCommand, isInterruptible: Boolean, reqs: List<DeltaSubsystem>) {

        cmd.init()
        val state = DeltaCommand.State(isInterruptible)

        for(evt in initEvents) { evt.run(cmd) } //run the init user events

        scheduledCommands[cmd] = state

    }

    fun schedule(cmd: DeltaCommand, isInterruptible: Boolean) {

        if(!enabled) return

        if(cmd == null) return

        val cmdReqs = cmd.requirements
        var reqsCurrentlyInUse = false

        //check if a requirement from the scheduled command is currently in use
        for(req in cmdReqs) {
            reqsCurrentlyInUse = reqsCurrentlyInUse && requirements.contains(req)
        }

        if(!reqsCurrentlyInUse) {

            commandInit(cmd, isInterruptible, cmdReqs) //directly run it, if none of its requirements are in use

        } else {

            //check if the commands requiring a specific subsystem are interruptible
            for(req in cmdReqs) {

                if(requirements.containsKey(req) && !scheduledCommands[requirements[req]]!!.interruptible) {
                    return;
                }

            }

            //cancel all the commands that require a subsystem
            for(req in cmdReqs) {
                if(requirements.containsKey(req)) {
                    requirements[req]?.let { stop(it) }
                }
            }

            commandInit(cmd, isInterruptible, cmdReqs) //schedule the command once all the other requiring commands were cancelled

        }

    }

    fun schedule(vararg cmds: DeltaCommand, isInterruptible: Boolean = true) {

        for(cmd in cmds) {
            schedule(cmd, isInterruptible)
        }

    }

    /**
     * Run all the scheduled commands & events
     */
    fun run() {

        if(!enabled) return //if the schedulers is disabled then abort

        for(subsystem in subsystems.keys) { subsystem.loop() } //run the loop method of all the subsystems

        val toDeleteCmds: ArrayList<DeltaCommand> = ArrayList() //list of commands to be deleted from the scheduled arraylist at the end

        for((cmd, status) in scheduledCommands) { //iterate through the scheduled commands

            cmd.run() //actually run the command

            for(evt in runEvents) { evt.run(cmd) } //execute the user events

            if(cmd.finished) { //end and remove the command if it's finished

                cmd.end(false)

                for(evt in endEvents) { evt.run(cmd) }

                toDeleteCmds.add(cmd)

            }

        }

        //delete finished commands
        for(cmd in toDeleteCmds) {
            scheduledCommands.remove(cmd)
            requirements.keys.removeAll(cmd.requirements)
        }

        //register default command if no command is requiring the subsystem
        for((subsystem, defCmd) in subsystems) {

            if(!requirements.containsKey(subsystem)) {
                schedule(defCmd) //schedule the default command if no other command is scheduled for this subsystem
            }

        }

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

    fun stop(vararg cmds: DeltaCommand) {

        for(cmd in cmds) {

            if(!scheduledCommands.containsKey(cmd)) continue

            cmd.end(true)

            for(evt in interruptEvents) { evt.run(cmd) }

            scheduledCommands.remove(cmd)
            requirements.keys.removeAll(cmd.requirements)

        }

    }

    fun stopAll() {

        for((cmd, state) in scheduledCommands) {
            stop(cmd)
        }

    }

    fun isScheduled(command: DeltaCommand) = scheduledCommands.containsKey(command)

    fun requires(subsystem: DeltaSubsystem) = requirements[subsystem]

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

}