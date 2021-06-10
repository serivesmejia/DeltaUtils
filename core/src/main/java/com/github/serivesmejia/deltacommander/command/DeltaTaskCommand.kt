package com.github.serivesmejia.deltacommander.command

import com.github.serivesmejia.deltacommander.DeltaCommand
import com.github.serivesmejia.deltacommander.DeltaSubsystem
import com.github.serivesmejia.deltadrive.utils.task.Task

class DeltaTaskCommand<T>(
    val task: Task<T>, vararg requirements: DeltaSubsystem? = arrayOf(null)
) : DeltaCommand() {

    init {
        for(req in requirements) {
            if(req != null) require(req)
        }
    }

    override fun init() {}

    override fun run() {
        task.run()
        if(task.finished) finish()
    }

    override fun end(interrupted: Boolean) {
        task.end()
    }

}