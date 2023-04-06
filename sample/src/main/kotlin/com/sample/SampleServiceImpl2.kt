package com.sample

import annotation.AutoService

@AutoService(SampleService::class)
class SampleServiceImpl2 : SampleService {
    override fun doSomething() = println("Impl2 do something.")
}