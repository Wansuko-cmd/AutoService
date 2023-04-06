import com.boo.BooService
import com.foo.FooService
import java.util.ServiceLoader

fun main() {
    (ServiceLoader.load(BooService::class.java)).forEach(BooService::doSomething)
    (ServiceLoader.load(FooService::class.java)).forEach(FooService::doSomething)
}
