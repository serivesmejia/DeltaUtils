package com.github.serivesmejia.deltautils.deltapid

import com.github.serivesmejia.deltautils.deltamath.DeltaMathUtil

class PIDController(var pid: PIDCoefficients) {

    //temp values params

    private var errorTolerance = 0.0
    private var deadZone = 0.0

    private var setpoint = 0.0

    private var prevErrorDelta = 0.0
    private var prevMillis = 0.0
    private var prevIntegral = 0.0
    private var prevInput = -1.0

    private var velocityDelta = 1.0
    private var errorDelta = errorTolerance + 1

    private var pidMultiplier = 1.0
    private var invertError = false

    private var initialPower = 1.0

    private var firstLoop = true

    fun setPID(pid: PIDCoefficients) {
        this.pid = pid
    }

    fun setSetpoint(setpoint: Double) : PIDController {
        this.setpoint = setpoint;
        return this
    }

    fun setErrorTolerance(errorTolerance: Double) : PIDController {
        this.errorTolerance = errorTolerance
        return this
    }

    fun setDeadzone(deadzone: Double) : PIDController {
        this.deadZone = deadzone
        return this
    }

    fun setPIDMultiplier(multiplier: Double) : PIDController {
        pidMultiplier = multiplier
        return this
    }

    fun setErrorInverted() : PIDController {
        invertError = !invertError
        return this
    }

    fun setErrorInverted(inverted: Boolean) : PIDController {
        invertError = inverted
        return this
    }

    fun setInitialPower(initialPower: Double) : PIDController {
        this.initialPower = initialPower
        return this
    }

    fun getPID() = pid

    fun getCurrentError() = errorDelta

    fun onSetpoint() = (errorDelta == errorTolerance)

    /**
     * Calculate the output with a given input
     * @return the output
     */
    fun calculate(input: Double) : Double {

        if(firstLoop) errorDelta = errorTolerance + 1

        firstLoop = false

        errorDelta = if(invertError)
            -(-input + setpoint)
        else
            setpoint - input

        velocityDelta = errorDelta - prevErrorDelta

        prevIntegral += errorDelta

        val proportional = errorDelta * (pid.kP * pidMultiplier)
        val integral = prevIntegral * (pid.kI * pidMultiplier)
        val derivative = velocityDelta * (pid.kD * pidMultiplier)

        val turbo: Double = DeltaMathUtil.clamp(proportional + integral + derivative, -1.0, 1.0)
        var powerF = initialPower * turbo

        if (powerF > 0) {
            powerF = DeltaMathUtil.clamp(powerF, deadZone, 1.0)
        } else if (powerF < 0) {
            powerF = DeltaMathUtil.clamp(powerF, -1.0, -deadZone)
        }

        prevErrorDelta = errorDelta
        prevInput = input

        return powerF

    }

    /**
     * Resets all values to default in order to start a different PID Loop
     */
    fun reset() : PIDController {

        errorTolerance = 0.0
        deadZone = 0.0

        setpoint = 0.0

        prevErrorDelta = 0.0
        prevMillis = 0.0
        prevIntegral = 0.0
        prevInput = -1.0

        velocityDelta = 1.0
        errorDelta = errorTolerance + 1

        pidMultiplier = 1.0
        invertError = false

        firstLoop = true

        return this

    }

}