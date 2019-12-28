package com.deltarobotics9351.deltasystem;

import com.deltarobotics9351.deltasystem.subsystems.MotionSubSystem;
import com.deltarobotics9351.deltasystem.subsystems.SubSystem;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.HashMap;
import java.util.Map;

public class System {

    private HardwareMap hdwMap;

    private HashMap<String, SubSystem> subsystems = new HashMap<String, SubSystem>();

    private boolean alreadyInitalizedSubSystems = false;

    public System(HardwareMap hdwMap){ this.hdwMap = hdwMap; }

    public final void addSubSystem(SubSystem subsys){
        subsystems.put(subsys.name, subsys);
    }

    public final void initSubSystems(){
        if(!alreadyInitalizedSubSystems) {
            for (Map.Entry<String, SubSystem> entry : subsystems.entrySet()) {
                entry.getValue().init();
            }
        }
        alreadyInitalizedSubSystems = true;
    }

    public final void requestStopSubSystems(){
        if(alreadyInitalizedSubSystems) {
            for (Map.Entry<String, SubSystem> entry : subsystems.entrySet()) {
                entry.getValue().requestStop();
            }
        }
    }

    public final SubSystem getSubSystem(String name){
        return subsystems.get(name);
    }

    public final void stepSubSystems(){
        if(alreadyInitalizedSubSystems) {
            for (Map.Entry<String, SubSystem> entry : subsystems.entrySet()) {
                entry.getValue().step();
            }
        }
    }

}
