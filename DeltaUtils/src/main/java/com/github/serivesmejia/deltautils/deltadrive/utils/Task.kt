package com.github.serivesmejia.deltautils.deltadrive.utils

import com.github.serivesmejia.deltautils.deltacommander.DeltaScheduler
import com.github.serivesmejia.deltautils.deltacommander.command.DeltaTaskCommand

/**
 * Class to represent a task of any sort, from a encoder drive run to position task,
 * to a IMU PID Drive rotate task.
 * @param runn Runnable to be asigned to this task.
 * @param T Type to be returned as a result from the task
 */
class Task<T>(private val runn: (Task<T>) -> T) {

    var finished = false
        private set

    var result: T? = null
        private set

    fun execute() {
        if(finished) return
        result = runn(this)
    }

    fun scheduleToCommander() = DeltaScheduler.instance.schedule(DeltaTaskCommand(this))

    fun executeBlocking() {
        while(!finished) {
            execute()
        }
    }

    fun end() {
        finished = true
    }

}