/*
 * Copyright (c) 2020 FTC Delta Robotics #9351 - Sebastian Erives
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.serivesmejia.deltautils

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.RobotCoreLynxUsbDevice
import com.qualcomm.robotcore.util.RobotLog
import org.firstinspires.ftc.robotcore.internal.system.AppUtil
import org.firstinspires.ftc.robotcore.internal.ui.UILocation

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

object DeltaAppUtil {

    fun restartAppCausedByError(hardwareMap: HardwareMap, globalErrorMessage: String, toast: String) {

        val countDownFailsafe = CountDownLatch(1);

        GlobalScope.launch {
            for (device in hardwareMap.getAll(RobotCoreLynxUsbDevice::class.java)) {
                device.lockNetworkLockAcquisitions();
                device.failSafe();
            }
            countDownFailsafe.countDown();
        }

        try {
            if (countDownFailsafe.await(250, TimeUnit.MILLISECONDS)) { //wait for failSafe command to be sent, with a timeout of 250 ms
                RobotLog.e("DeltaUtils - Successfully sent failsafe commands to all Lynx USB devices before the app restarts")
            } else {
                RobotLog.e("DeltaUtils - Timed out to send failsafe commands to all Lynx USB devices before the app restarts")
            }
        } catch (e: InterruptedException) { } //ignore the exception

        RobotLog.setGlobalErrorMsg(globalErrorMessage) //show error messages

        RobotLog.e(globalErrorMessage)
        AppUtil.getInstance().showToast(UILocation.BOTH, toast)

        threadStackTracesDump() //show all stacktraces, for debugging purposes.

        try {
            Thread.sleep(3000) //wait a bit for the messages to be seen
        } catch (e: InterruptedException) { } //ignore the exception again, anyways the app will restart soon.

        AppUtil.getInstance().restartApp(-1) //use the FTC SDK's app util class to restart the app

    }

    fun threadStackTracesDump() {

        RobotLog.e("DeltaUtils - Thread dump start")

        for ((key, value) in Thread.getAllStackTraces()) {
            RobotLog.logStackTrace(key, value)
        }

        RobotLog.e("DeltaUtils - Thread dump end")

    }

}