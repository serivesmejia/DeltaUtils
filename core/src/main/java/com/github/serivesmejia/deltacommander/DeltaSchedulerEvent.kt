package com.github.serivesmejia.deltacommander

interface DeltaSchedulerEvent {

    fun run(command: DeltaCommand)

}