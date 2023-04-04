import annotation.AutoService
import java.util.ServiceLoader

fun main(args: Array<String>) {
    (ServiceLoader.load(MainService::class.java)).forEach(MainService::doSomething)
}

interface MainService {
    fun doSomething()
}

@AutoService<MainService>(MainService::class)
class MainServiceImpl1 : MainService {
    override fun doSomething() = println("Impl1 do something.")
}
