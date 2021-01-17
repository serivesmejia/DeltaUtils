package com.github.serivesmejia.deltacommander

import java.util.*

abstract class DeltaCommand {

    val name = this.javaClass.simpleName
    val requirements: ArrayList<DeltaSubsystem> = ArrayList()

    var finished = false

    fun init() {}

    abstract fun run()

    fun end(interrupted: Boolean) {}

    fun require(vararg reqs: DeltaSubsystem) {
        reqs.forEach {
            if(!requirements.contains(it)) requirements.add(it)
        }
    }

    fun finish() {
        finished = true
    }

    class State(val interruptible: Boolean)

}