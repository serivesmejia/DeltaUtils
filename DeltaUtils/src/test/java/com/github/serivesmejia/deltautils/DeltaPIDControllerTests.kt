package com.github.serivesmejia.deltautils

import com.github.serivesmejia.deltapid.PIDFCoefficients
import com.github.serivesmejia.deltapid.MotorPIDFController
import org.junit.Assert.*
import org.junit.Test

class DeltaPIDControllerTests {

    val coeffs = PIDFCoefficients(0.0113, 0.003, 0.05)

    @Test
    fun testPIDControllerOutput() {
        var currDeg = 0.0
        var onSetpoint = false

        TestUtil.spawnTimeoutThread({

            val pidController = MotorPIDFController(coeffs)

            pidController.setSetpoint(90.0)
                    .setDeadzone(0.1)
                    .setInitialPower(1.0)
                    .setErrorTolerance(1.0)

            while(!pidController.onSetpoint() && !Thread.currentThread().isInterrupted) {
                val powerF = pidController.calculate(currDeg);
                currDeg += powerF * 0.764
                onSetpoint = pidController.onSetpoint()

                println("Power: $powerF, Sim. degrees: $currDeg")

                Thread.sleep(20)
            }

            if(Thread.currentThread().isInterrupted) {
                println("Timeout!")
            }

        }, 10.0)

        println("Final simulated degrees: $currDeg")

        assertTrue(onSetpoint)
    }


    @Test
    fun testPIDControllerOutputInverted() {
        var currDeg = 0.0
        var onSetpoint = false

        TestUtil.spawnTimeoutThread({

            val pidController = MotorPIDFController(coeffs)

            pidController.setSetpoint(90.0)
                    .setDeadzone(0.1)
                    .setInitialPower(1.0)
                    .setErrorTolerance(1.0)
                    .setErrorInverted()

            while(!pidController.onSetpoint() && !Thread.currentThread().isInterrupted) {
                val powerF = pidController.calculate(currDeg);
                currDeg += powerF * 0.34
                onSetpoint = pidController.onSetpoint()

                println("Power: $powerF, Sim. degrees: $currDeg")

                Thread.sleep(20)
            }

            if(Thread.currentThread().isInterrupted) {
                println("Timeout!")
            }

        }, 20.0)

        println("Final simulated degrees: $currDeg")

        assertTrue(onSetpoint)
    }

}