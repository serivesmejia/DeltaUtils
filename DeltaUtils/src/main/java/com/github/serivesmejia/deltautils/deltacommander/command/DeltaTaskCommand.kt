package com.github.serivesmejia.deltautils.deltacommander.command

import com.github.serivesmejia.deltautils.deltacommander.DeltaCommand
import com.github.serivesmejia.deltautils.deltadrive.utils.Task

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