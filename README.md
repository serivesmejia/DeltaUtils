# DeltaUtils
[![](https://jitpack.io/v/serivesmejia/DeltaUtils.svg)](https://jitpack.io/#serivesmejia/DeltaUtils)
[![Build Status](https://travis-ci.com/serivesmejia/DeltaUtils.svg?branch=master)](https://travis-ci.com/serivesmejia/DeltaUtils)

Easy to use library containing many functions to control your FTC robot

## Docs
You can see the generated docs [here](https://serivesmejia.github.io/DeltaUtils/ "DeltaUtils Docs").

## Install (Credit to DogeCV)
### This library requires an SDK version of at least 5.4git
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
3. Add the line `implementation 'com.github.serivesmejia:DeltaUtils:1.14-beta'` to TeamCode's `build.release.gradle`, inside the dependencies block
7. Press the `Sync Now` button that will appear in the top right

## Features
 
**Holonomic wheels:**
   - Normal & field centric joystick drive 
   - IMU sensor turns with automatic correction
   - PID with IMU turns
   - Time based drive
   - Encoder based drive
   - Extendable OpModes already containing the code for various drives, for a quick setup
   
**Math:**
   - Vec2d 
   - Rot2d (Degrees, Radians, From X & Y)
   - Pos2d
   - Twist2d
   
**Coding paradigms:**
   - Event based gamepads (SuperGamepad) which simplify the code in your TeleOp
   - Simple commander-subsystems implementation, inspired by WPILib & FTCLib

## TO-DO
   - Wiki and sample classes
   - More chassis types:
      - 2-motor 4-wheel omni
      - 4-motor 4-wheel omni
      - H-Drive 
   - OpenCV pipelines
   - Odometry
   - DeltaPanel
