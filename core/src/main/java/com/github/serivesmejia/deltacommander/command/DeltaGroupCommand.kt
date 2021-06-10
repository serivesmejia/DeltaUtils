@file:Suppress("UNUSED")

package com.github.serivesmejia.deltacommander.command

import com.github.serivesmejia.deltacommander.DeltaCommand

open class DeltaGroupCommand(private val executionMode: ExecutionMode = ExecutionMode.PARALLEL, vararg commands: DeltaCommand) : DeltaCommand() {

    var commands: ArrayList<DeltaCommand> = ArrayList()

    var currentCommandIndex = 0

    init {
        if(commands.isEmpty()) {
            throw IllegalArgumentException("You should provide one or more commands to the GroupedCommand")
        }

        //Require all the subcommands requirements
        //and add all the commands from the vararg to the arraylist
        for(cmd in commands) {
            for(req in cmd.requirements) {
                require(req)
            }
            this.commands.add(cmd)
        }

    }

    override fun init() {
        for(cmd in commands) { cmd.init() }
    }

    override fun run() {
        when(executionMode) {
            //execute commands in linear mode, which will run one command at a time sequentially until
            //all the commands are finished, which the grouped command (this) will also be finished
            ExecutionMode.LINEAR -> {
                val command = commands[currentCommandIndex]
                command.run()

                if(command.finished) {
                    command.end(false)
                    currentCommandIndex++

                    if(currentCommandIndex >= commands.size) {
                        finish()
                    }
                }
            }

            //execute commands in parallel mode, which will run all the commands at once until they're all finished
            //if all the subcommands are finished, the grouped command (this) will also finish
            ExecutionMode.PARALLEL -> {
                var finishedCount = 0
                var nonBlockingCmdsCount = 0

                for(cmd in commands) {
                    if(!cmd.finished) {
                        cmd.run()
                        if(cmd.finished) cmd.end(false)
                    } else {
                        finishedCount++
                    }

                    if(!cmd.blockParallelCommand) nonBlockingCmdsCount++
                }

                if(finishedCount >= commands.size - nonBlockingCmdsCount) {
                    finish()
                }
            }
        }
    }

    override fun end(interrupted: Boolean) {
        for(cmd in commands) {
            if(!cmd.finished) cmd.end(interrupted)
        }
    }

}

enum class ExecutionMode { LINEAR, PARALLEL }

class DeltaSequentialCommand(
        vararg commands: DeltaCommand
) : DeltaGroupCommand(ExecutionMode.LINEAR, *commands)

class DeltaParallelCommand(
        vararg commands: DeltaCommand
) : DeltaGroupCommand(ExecutionMode.PARALLEL, *commands)