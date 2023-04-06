package processor

import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSType

class Providers : MutableMap<Parent, Provider> by HashMap() {
    fun setOrCreate(provider: Provider) {
        val old = this.getOrElse(provider.parent) {
            this[provider.parent] = provider
            return@setOrCreate
        }
        this[provider.parent] = Provider(
            parent = old.parent,
            children = old.children + provider.children,
            sources = old.sources + provider.sources,
        )
    }
}

data class Provider(
    val parent: Parent,
    val children: List<Child>,
    val sources: List<KSFile>,
) {
    companion object {
        fun create(symbol: KSAnnotated) = Provider(
            Parent(symbol.getParentClassInfo()),
            listOf(Child(symbol.getChildClassInfo())),
            symbol.containingFile?.let { listOf(it) } ?: emptyList(),
        )
    }
}

@JvmInline
value class Parent(val value: String)

@JvmInline
value class Child(val value: String)

/**
 * @AutoService(SampleService::class <- here)
 * class SampleServiceImpl1 : SampleService
 */
private fun KSAnnotated.getParentClassInfo(): String =
    (this.annotations.first().arguments.first().value as KSType)
        .declaration
        .qualifiedName!!
        .asString()

/**
 * @AutoService(SampleService::class)
 * class SampleServiceImpl1 <- here : SampleService
 */
private fun KSAnnotated.getChildClassInfo(): String =
    (this as KSClassDeclaration).qualifiedName!!.asString()
