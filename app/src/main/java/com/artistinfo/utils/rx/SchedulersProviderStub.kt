package com.artistinfo.utils.rx

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class SchedulersProviderStub : SchedulersProvider {

    override fun main(): Scheduler {
        return Schedulers.trampoline()
    }

    override fun io(): Scheduler {
        return Schedulers.trampoline()
    }

    override fun computation(): Scheduler {
        return Schedulers.trampoline()
    }
}
