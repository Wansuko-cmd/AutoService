package com.boo

import annotation.AutoService

@AutoService(BooService::class)
class BooServiceImpl1 : BooService {
    override fun doSomething() = println("BooServiceImpl1 do something.")
}
