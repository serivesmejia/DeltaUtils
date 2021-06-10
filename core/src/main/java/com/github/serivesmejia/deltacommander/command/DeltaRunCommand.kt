package com.github.serivesmejia.deltacommander.command

import com.github.serivesmejia.deltacommander.DeltaCommand

class DeltaRunCommand(private val callback: () -> Unit) : DeltaCommand() {

    override fun run() {
        callback()
        finish()
    }

}