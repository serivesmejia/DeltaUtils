package com.github.serivesmejia.deltacommander.command

import com.github.serivesmejia.deltacommander.DeltaCommand
import com.github.serivesmejia.deltadrive.utils.Task

class DeltaTaskCommand<T>(val task: Task<T>) : DeltaCommand() {
    override fun init() {}

    override fun run() {
        task.execute()
        if(task.finished) finish()
    }

    override fun end(interrupted: Boolean) {
        task.end()
    }

}