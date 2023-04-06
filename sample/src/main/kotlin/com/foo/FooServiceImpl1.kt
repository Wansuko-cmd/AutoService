package com.foo

import annotation.AutoService
import com.boo.BooService

@AutoService(BooService::class)
class FooServiceImpl1 : BooService {
    override fun doSomething() = println("FooServiceImpl1 do something.")
}
