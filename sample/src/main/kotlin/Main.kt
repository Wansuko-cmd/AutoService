import com.sample.SampleService
import java.util.ServiceLoader

fun main() {
    (ServiceLoader.load(SampleService::class.java)).forEach(SampleService::doSomething)
}
