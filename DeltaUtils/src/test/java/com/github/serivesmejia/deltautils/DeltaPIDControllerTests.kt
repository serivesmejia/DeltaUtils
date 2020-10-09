package com.github.serivesmejia.deltautils

import com.github.serivesmejia.deltautils.deltapid.PIDCoefficients
import com.github.serivesmejia.deltautils.deltapid.PIDController
import org.junit.Assert.*
import org.junit.Test

class DeltaPIDControllerTests {

    @Test
    fun testPIDControllerOutput() {

        val pidController = PIDController(PIDCoefficients(0.0168, 0.0, 0.0))

        pidController.setSetpoint(90.0)
                     .setDeadzone(0.1)
                     .setInitialPower(1.0)
                     .setErrorTolerance(1.0)

        var currDeg = 0.0

        while(!pidController.onSetpoint()) {

            val powerF = pidController.calculate(currDeg);

            currDeg += powerF * 0.8

            println("Power: $powerF, Sim. degrees: $currDeg")

            Thread.sleep(20)

        }

        println("Final simulated degrees: $currDeg")

        assertTrue(pidController.onSetpoint())

    }


    @Test
    fun testPIDControllerOutputInverted() {

        val pidController = PIDController(PIDCoefficients(0.0168, 0.0, 0.0))

        pidController.setSetpoint(-90.0)
                     .setDeadzone(0.1)
                     .setInitialPower(1.0)
                     .setErrorTolerance(1.0)
                     .setErrorInverted()

        var currDeg = 0.0

        while(!pidController.onSetpoint()) {

            val powerF = pidController.calculate(currDeg);

            currDeg -= powerF * 0.8

            println("Power: $powerF, Sim. degrees: $currDeg")

            Thread.sleep(20)

        }

        println("Final simulated degrees: $currDeg")

        assertTrue(pidController.onSetpoint())

    }

}