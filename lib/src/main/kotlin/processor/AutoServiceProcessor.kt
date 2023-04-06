package processor

import annotation.AutoService
import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import kotlin.reflect.KClass

class AutoServiceProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    private val providers = mutableMapOf<String, MutableSet<String>>()
    private val sources = mutableSetOf<KSFile>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(AutoService::class.qualifiedName!!)
        val (valid, invalid) = symbols.partition(KSAnnotated::validate)

        valid.forEach { symbol ->
            providers.getOrPut(symbol.getInterface(), ::mutableSetOf).add(symbol.getImplementer())
            symbol.containingFile?.let { sources.add(it) }
        }

        return invalid
    }

    override fun finish() {
        super.finish()
        providers.forEach { (inter, impl) ->
            codeGenerator.createNewFile(
                Dependencies(true, *sources.toList().toTypedArray()),
                "META-INF.services",
                inter,
                "",
            ).use { it.write(impl.joinToString("\n").toByteArray()) }
        }
    }

    /**
     * @AutoService(SampleService::class <- here)
     * class SampleServiceImpl1 : SampleService
     */
    private fun KSAnnotated.getInterface(): String =
        (this.annotations.first().arguments.first().value as KSType)
            .declaration
            .qualifiedName!!
            .asString()

    /**
     * @AutoService(SampleService::class)
     * class SampleServiceImpl1 <- here : SampleService
     */
    private fun KSAnnotated.getImplementer(): String =
        (this as KSClassDeclaration).qualifiedName!!.asString()
}
