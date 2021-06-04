package com.github.serivesmejia.deltadrive.utils

import com.github.serivesmejia.deltacommander.DeltaScheduler
import com.github.serivesmejia.deltacommander.command.DeltaTaskCommand
import com.github.serivesmejia.deltacommander.deltaScheduler

/**
 * Class to represent a task of any sort, from a encoder drive run to position task,
 * to a IMU PID Drive rotate task.
 * @param runn Runnable to be assigned to this task.
 * @param T Type to be returned as a result from the task
 */
class Task<T>(private val runn: Task<T>.() -> T) {

    var finished = false
        private set

    var result: T? = null
        private set

    private var hasRan =false

    fun run() {
        if(finished) return
        result = runn(this)

        hasRan = true
    }

    fun first(callback: () -> Unit) {
        if(!hasRan) callback()
    }

    fun runBlocking() {
        while(!finished) {
            run()
        }
    }

    fun schedule() = deltaScheduler.schedule(DeltaTaskCommand(this))

    fun end() {
        finished = true
    }

}