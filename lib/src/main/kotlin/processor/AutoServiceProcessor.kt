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

    private val providers = Providers()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(AutoService::class.qualifiedName!!)
        val (valid, invalid) = symbols.partition(KSAnnotated::validate)

        valid.forEach { symbol -> providers.setOrCreate(Provider.create(symbol)) }

        return invalid
    }

    override fun finish() {
        super.finish()
        providers.forEach { (_, provider) ->
            codeGenerator.createNewFile(
                Dependencies(false, *provider.sources.toTypedArray()),
                META_INF_URL,
                provider.parent.value,
                "",
            ).use { outputStream ->
                outputStream.write(
                    provider
                        .children
                        .joinToString("\n") { it.value }
                        .toByteArray(),
                )
            }
        }
    }

    companion object {
        private const val META_INF_URL = "META-INF.services"
    }
}
