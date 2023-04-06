package processor

import annotation.AutoService
import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import kotlin.reflect.KClass

class AutoServiceProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    private val providers = mutableMapOf<String, MutableSet<String>>()
    private var generated = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(AutoService::class.qualifiedName!!)
        val (valid, invalid) = symbols.partition { it.validate() }
        valid.forEach {
            providers.getOrPut(it.getInterface(), ::mutableSetOf).add(it.getImplementer())
        }
        if (valid.isEmpty() && !generated) {
            generated = true
            providers.forEach { (inter, impl) ->
                log(impl)
                codeGenerator.createNewFile(
                    Dependencies(true, *resolver.getAllFiles().toList().toTypedArray()),
                    "META-INF.services",
                    inter,
                    "",
                ).use { it.write(impl.joinToString("\n").toByteArray()) }
            }
        }
        return invalid
    }

//    override fun finish() {
//        super.finish()
//        providers.forEach { (inter, impl) ->
//            log(impl)
//            codeGenerator.createNewFile(
//                Dependencies(true, ),
//                "META-INF.services",
//                inter,
//                "",
//            ).use { it.write(impl.joinToString("\n").toByteArray()) }
//        }
//    }

    private fun KSAnnotated.getInterface(): String =
        (this.annotations.first().arguments.first().value as KSType)
            .declaration
            .qualifiedName!!
            .asString()

    private fun KSAnnotated.getImplementer(): String =
        (this as KSClassDeclaration).qualifiedName!!.asString()


    private var count = 1
    private fun log(message: Any?) {
        codeGenerator.createNewFile(
            Dependencies(false),
            "",
            "log_${count++}",
            "",
        ).use { it.write(message.toString().toByteArray()) }
    }
}
