package com.github.serivesmejia.deltautils.deltacommander

import java.util.*

abstract class DeltaCommand {

    val name = this.javaClass.simpleName
    val requirements: ArrayList<DeltaSubsystem> = ArrayList()

    var finished = false

    abstract fun init()

    abstract fun run()

    abstract fun end(interrupted: Boolean)

    abstract fun idle()

    fun require(vararg reqs: DeltaSubsystem) {
        reqs.forEach { requirements.add(it) }
    }

    fun finish() {
        finished = true
    }

    class State(val interruptible: Boolean)

}