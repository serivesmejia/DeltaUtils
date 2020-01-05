package com.github.deltarobotics9351.deltasystem.subsystems;

import com.github.deltarobotics9351.deltasystem.utils.Hardware;
import com.github.deltarobotics9351.deltasystem.utils.HardwareType;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.HashMap;

public class MotionSubSystem extends SubSystem{

    public MotionSubSystem(String name){
        super(name);
    }

    private HashMap<String, DcMotor> motors;
    private HashMap<String, Servo> servos;
    private HashMap<String, CRServo> crservos;

    public final DcMotor getMotor(String name){
        return motors.get(name);
    }

    public final Servo getServo(String name){
        return servos.get(name);
    }

    public final CRServo getCRServo(String name){
        return crservos.get(name);
    }


    @Override
    public final void _init(){
        if(!isInitialized) {
            for (Hardware hdw : hardware) {
                if (hdw.type == HardwareType.DcMotor) {
                    motors.put(hdw.name, hdwMap.get(DcMotor.class, hdw.name));
                } else if (hdw.type == HardwareType.Servo) {
                    servos.put(hdw.name, hdwMap.get(Servo.class, hdw.name));
                } else if (hdw.type == HardwareType.CRServo) {
                    crservos.put(hdw.name, hdwMap.get(CRServo.class, hdw.name));
                } else {
                    return;
                }
            }
            __init();
            isInitialized = true;
        }
    }

    public void __init(){ }

    @Override
    public void step(){

    }

}
