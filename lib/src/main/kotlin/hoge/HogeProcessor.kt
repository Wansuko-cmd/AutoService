package hoge

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated

class HogeProcessor(private val codeGenerator: CodeGenerator) : SymbolProcessor {
    private var invoked = false
    override fun process(resolver: Resolver): List<KSAnnotated> {
        if(invoked) return emptyList()

        codeGenerator.createNewFile(
            Dependencies(true, *resolver.getAllFiles().toList().toTypedArray()),
            "com.hoge",
            "HogeService",
        ).use { it.write("""
            package com.hoge

            import annotation.AutoService
            import com.sample.SampleService
            
            @AutoService(SampleService::class)
            class HogeService : SampleService {
                override fun doSomething() = println("hoge do something.")
            }
        """.trimIndent().toByteArray()) }

        invoked = true
        return emptyList()
    }
}