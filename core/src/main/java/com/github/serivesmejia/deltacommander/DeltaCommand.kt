package com.github.serivesmejia.deltacommander

import com.github.serivesmejia.deltacommander.command.DeltaRunCommand
import com.github.serivesmejia.deltacommander.command.DeltaSequentialCommand
import com.github.serivesmejia.deltacommander.command.DeltaWaitCommand
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.reflect.KClass

abstract class DeltaCommand {

    val name = this.javaClass.simpleName
    val requirements: ArrayList<DeltaSubsystem> = ArrayList()

    var finished = false

    var blockParallelCommand = true

    open fun init() {}

    abstract fun run()

    open fun end(interrupted: Boolean) {}

    fun require(vararg reqs: DeltaSubsystem) {
        reqs.forEach {
            if (!requirements.contains(it))
                requirements.add(it)
        }
    }

    inline fun <reified S : DeltaSubsystem> require() = require(S::class)

    @Suppress("UNCHECKED_CAST")
    fun <S : DeltaSubsystem> require(clazz: KClass<S>): S {
        for(subsystem in deltaScheduler.subsystems) {
            if(subsystem::class == clazz) {
                require(subsystem)
                return subsystem as S
            }
        }

        throw IllegalArgumentException("Unable to find subsystem ${clazz::class.simpleName} in DeltaScheduler")
    }

    fun finish() {
        finished = true
    }

    fun schedule() = deltaScheduler.schedule(this)

    fun stopAfter(seconds: Double): DeltaCommand {
        DeltaSequentialCommand(
            DeltaWaitCommand(seconds),
            DeltaRunCommand { finish() }
        ).schedule()

        return this
    }

    operator fun unaryPlus() = schedule()

    data class State(val interruptible: Boolean)

}