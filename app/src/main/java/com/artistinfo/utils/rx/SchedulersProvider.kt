package com.artistinfo.utils.rx

import io.reactivex.Scheduler

interface SchedulersProvider {

    fun main(): Scheduler
    fun io(): Scheduler
    fun computation(): Scheduler
}
