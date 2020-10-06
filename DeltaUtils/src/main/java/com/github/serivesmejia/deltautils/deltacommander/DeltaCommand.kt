package com.github.serivesmejia.deltautils.deltacommander

open abstract class DeltaCommand {

    open abstract fun init()

    open abstract fun execute()

    open abstract fun end()

    open abstract fun idle()

    fun require() {

    }

}