package com.foo

import annotation.AutoService
import com.boo.BooService

@AutoService(BooService::class)
class FooServiceImpl2 : BooService {
    override fun doSomething() = println("FooServiceImpl2 do something.")
}
