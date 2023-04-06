package com.boo

import annotation.AutoService

@AutoService(BooService::class)
class BooServiceImpl2 : BooService {
    override fun doSomething() = println("BooServiceImpl2 do something.")
}
