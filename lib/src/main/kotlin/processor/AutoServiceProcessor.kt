package processor

import annotation.AutoService
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate

class AutoServiceProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(AutoService::class.qualifiedName!!)
        val (valid, invalid) = symbols.partition { it.validate() }
        if (valid.isNotEmpty()) {
            codeGenerator.createNewFile(
                Dependencies(false),
                "META-INF.services",
                "MainService",
                "",
            ).use { it.write("MainServiceImpl1".toByteArray()) }
        }
        return invalid
    }
}
