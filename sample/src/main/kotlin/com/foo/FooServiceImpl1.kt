package com.foo

import annotation.AutoService

@AutoService(FooService::class)
class FooServiceImpl1 : FooService {
    override fun doSomething() = println("FooServiceImpl1 do something.")
}
