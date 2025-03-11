package playground.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ConfigurationPropertiesScan("playground")
@ComponentScan("playground")
class PlaygroundApplication

@SuppressWarnings("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<PlaygroundApplication>(*args)
}
