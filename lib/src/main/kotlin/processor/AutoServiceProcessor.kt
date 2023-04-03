package processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated

class AutoServiceProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    private var invoked = false
    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) return emptyList()
        invoked = true
        return processImpl()
    }

    private fun processImpl(): List<KSAnnotated> {
        codeGenerator.createNewFile(
            Dependencies(false),
            "com.example",
            "Sample",
        ).use {
            it.write(
                """
                val sample = "Sample"
                """.trimIndent().toByteArray(),
            )
        }

        return emptyList()
    }
}
