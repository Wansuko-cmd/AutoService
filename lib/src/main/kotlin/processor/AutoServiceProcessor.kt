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
import java.lang.RuntimeException
import kotlin.math.log
import kotlin.reflect.KClass

class AutoServiceProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    private val providers = Providers()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        try {
            val symbols = resolver.getSymbolsWithAnnotation(AutoService::class.qualifiedName!!)
            val (valid, invalid) = symbols.partition(KSAnnotated::validate)

            valid.forEach { symbol ->
                val provider = Provider.create(symbol)
                providers.setOrCreate(provider)
            }

            return invalid
        } catch (e: RuntimeException) {
            logger.exception(e)
            throw e
        }
    }

    override fun finish() {
        super.finish()
        try {
            providers.forEach { (_, provider) ->

                logger.info("provider interface: ${provider.parent}")
                logger.info("provider implementer: ${provider.children}")

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
        } catch (e: RuntimeException) {
            logger.exception(e)
            throw e
        }
    }

    companion object {
        private const val META_INF_URL = "META-INF.services"
    }
}
