package nl.toefel.grpckotlincorourtinesspring

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import nl.toefel.Constants
import nl.toefel.RngServiceGrpcKt
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class GrpcKotlinCorourtinesSpringApplication {

	@Bean
	fun channel() : ManagedChannel {
		return ManagedChannelBuilder.forAddress("localhost", Constants.port)
				.usePlaintext()
				.executor(Dispatchers.Default.asExecutor())
				.build()
	}

	@Bean
	fun client(channel: ManagedChannel): RngServiceGrpcKt.RngServiceCoroutineStub {
		return RngServiceGrpcKt.RngServiceCoroutineStub(channel)
	}

}

fun main(args: Array<String>) {
	runApplication<GrpcKotlinCorourtinesSpringApplication>(*args)
}
