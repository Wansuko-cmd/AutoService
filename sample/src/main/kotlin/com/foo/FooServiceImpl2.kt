package com.foo

import annotation.AutoService

@AutoService(FooService::class)
class FooServiceImpl2 : FooService {
    override fun doSomething() = println("FooServiceImpl2 do something.")
}
