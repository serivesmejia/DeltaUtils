package com.github.serivesmejia.deltautils.deltacommander

interface DeltaSchedulerEvent {

    fun run(command: DeltaCommand)

}