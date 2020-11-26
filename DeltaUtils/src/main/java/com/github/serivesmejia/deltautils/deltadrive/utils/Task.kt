package com.github.serivesmejia.deltautils.deltadrive.utils

/**
 * Class to represent a task of any sort, from a encoder drive run to position task,
 * to a IMU PID Drive rotate task.
 * @param runn Runnable to be asigned to this task.
 */
class Task(val runn: TaskRunnable) {

    var isFinished = false
        private set

    fun execute() {
        if(isFinished) return
        isFinished = runn.run();
    }

    fun executeBlocking() {
        while(!isFinished) {
            execute()
        }
    }

    open class TaskRunnable {
        /**
         * Runs the task, should be overrrided to actually do something.
         * @return true if the task is finished, false to continue running the task
         */
        open fun run(): Boolean {
            return true;
        }
    }

}