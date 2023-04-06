import annotation.AutoService
import com.sample.SampleService
import com.sample.SampleServiceImpl1
import com.sample.SampleServiceImpl2
import java.util.ServiceLoader

fun main(args: Array<String>) {
    (ServiceLoader.load(SampleService::class.java)).forEach(SampleService::doSomething)
}
