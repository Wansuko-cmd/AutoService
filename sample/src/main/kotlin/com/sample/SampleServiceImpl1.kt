package com.sample

import annotation.AutoService

//@AutoService(SampleService::class)
class SampleServiceImpl1 : SampleService {
    override fun doSomething() = println("Impl1 do something.")
}