# DeltaUtils
[![](https://jitpack.io/v/DeltaRobotics-9351/DeltaDrive.svg)](https://jitpack.io/#DeltaRobotics-9351/DeltaDrive)

Easy to use library containing many functions to control your FTC robot

## Docs
You can see the generated docs [here](https://deltarobotics-9351.github.io/DeltaUtils/ "DeltaUtils Docs").

## Install (Credit to EasyOpenCV & DogeCV)
### This library requires a SDK version of 5.3
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
3. Add the line `implementation 'com.github.DeltaRobotics-9351:DeltaUtils:v1.7-beta'` to TeamCode's `build.release.gradle`, inside the dependencies block
7. Press the `Sync Now` button that should appear in the top right

## Features
 
**Mecanum wheels:**
   - IMU sensor turns w/automatic corrections
   - Time based drive
   - Encoder based drive
   - PID with IMU turns
   
**Math:**
   - Vector2d 
   - Rotation2d (Degrees, Radians, From X & Y)
   - Pos2d
   
## TO-DO
   - Wiki and sample classes
   - More chassis types:
      - 2-motor 4-wheel omni
      - 4-wheel omni
      - H-Drive 
      - X-Drive 
   - OpenCV pipelines
   - Systems and Subsystems
   - State Machines
