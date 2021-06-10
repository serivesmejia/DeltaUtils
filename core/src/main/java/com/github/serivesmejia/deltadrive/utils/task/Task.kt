package com.github.serivesmejia.deltadrive.utils.task

import com.github.serivesmejia.deltacommander.DeltaSubsystem
import com.github.serivesmejia.deltacommander.command.DeltaTaskCommand
import com.github.serivesmejia.deltacommander.deltaScheduler
import com.github.serivesmejia.deltamath.geometry.Rot2d
import com.qualcomm.robotcore.util.ElapsedTime

/**
 * Class to represent a task of any sort, from a encoder drive run to position task,
 * to a IMU PID Drive rotate task.
 * @param runn Runnable to be assigned to this task.
 * @param T Type to be returned as a result from the task
 */
@Suppress("UNUSED")
open class Task<T>(
    private val commandRequirements: Array<DeltaSubsystem?> = arrayOf(null),
    private val runn: Task<T>.() -> T
) {

    var finished = false
        private set

    var result: T? = null
        private set

    val command get() = DeltaTaskCommand(this, *commandRequirements)

    // GENERAL MARKERS
    private val timeMarkers = mutableMapOf<Marker<T>, Double>()
    private val inchesMarkers = mutableMapOf<Marker<T>, Double>()
    private val rotationMarker = mutableMapOf<Marker<T>, Rot2d>()

    private var hasRan = false

    private val runtime = ElapsedTime()

    open fun run() {
        if(finished) return
        first {
            runtime.reset()
        }

        result = runn(this)
        runTimeMarkers(runtime.seconds())

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

    fun schedule() = deltaScheduler.schedule(command)

    fun end() {
        finished = true
    }

    fun timeMarker(timeSeconds: Double, marker: Marker<T>): Task<T> {
        timeMarkers[marker] = timeSeconds
        return this
    }

    fun runTimeMarkers(elapsedSeconds: Double) {
        // time markers
        for((marker, time) in timeMarkers.entries.toTypedArray()) {
            if(elapsedSeconds >= time) {
                marker.run(this)
                timeMarkers.remove(marker)
            }
        }
    }

    fun inchesMarker(inches: Double, marker: Marker<T>): Task<T> {
        inchesMarkers[marker] = inches
        return this
    }

    fun runInchesMarkers(currentInches: Double) {
        // pose markers
        for((marker, inches) in inchesMarkers.entries.toTypedArray()) {
            if(inches >= currentInches) {
                marker.run(this)
                timeMarkers.remove(marker)
            }
        }
    }

    fun rotationMarker(rotation: Rot2d, marker: Marker<T>): Task<T> {
        rotationMarker[marker] = rotation
        return this
    }

    fun runRotationMarkers(currentRot: Rot2d) {
        // rot2d markers
        for((marker, rot) in rotationMarker.entries.toTypedArray()) {
            if(rot.radians >= currentRot.radians) {
                marker.run(this)
                timeMarkers.remove(marker)
            }
        }
    }

}