# DeltaUtils
[![](https://jitpack.io/v/DeltaRobotics-9351/DeltaDrive.svg)](https://jitpack.io/#DeltaRobotics-9351/DeltaDrive)<br/>
[![](https://travis-ci.com/DeltaRobotics-9351/DeltaUtils.svg?branch=master)](https://travis-ci.com/DeltaRobotics-9351/DeltaUtils)

Easy to use library containing many functions to control your FTC robot

## Docs
You can see the generated docs [here](https://deltarobotics-9351.github.io/DeltaUtils/ "DeltaUtils Docs").

## Install (Credit to DogeCV)
### This library requires an SDK version of at least 5.4
1. Pull up Android Studio, with the FTC application SDK open
2. Go to the root `build.gradle`
3. To the repositories section, add the lines 
```groovy
allprojects {
  repositories {
    maven { url 'https://jitpack.io' } // this line!
  }
}
```
3. Add the line `implementation 'com.github.DeltaRobotics-9351:DeltaUtils:1.14-beta'` to TeamCode's `build.release.gradle`, inside the dependencies block
7. Press the `Sync Now` button that should appear in the top right

## Features
 
**Mecanum wheels:**
   - Normal & field centric joystick drive 
   - IMU sensor turns with automatic correction
   - PID with IMU turns
   - Time based drive
   - Encoder based drive
   
**Math:**
   - Vec2d 
   - Rot2d (Degrees, Radians, From X & Y)
   - Pos2d
   - Twist2d
   
## TO-DO
   - Wiki and sample classes
   - More chassis types:
      - 2-motor 4-wheel omni
      - 4-motor 4-wheel omni
      - H-Drive 
      - X-Drive 
   - OpenCV pipelines
   - Systems and Subsystems
   - State Machines
   - Odometry
   - DeltaPanel
