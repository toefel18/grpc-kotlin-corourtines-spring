package nl.toefel.grpcbackend

import io.grpc.Server
import io.grpc.ServerBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import nl.toefel.Constants
import nl.toefel.RngRequest
import nl.toefel.RngResponse
import nl.toefel.RngServiceGrpcKt
import kotlin.random.Random

class RngService : RngServiceGrpcKt.RngServiceCoroutineImplBase() {
    override suspend fun calculateRandoms(request: RngRequest): RngResponse {
        println("received request for ${request.numGenerated} random nrs with a delay of ${request.delayMillis}")
        delay(request.delayMillis)
        return RngResponse.newBuilder()
                .addAllRandoms((0..request.numGenerated).map { Random.nextLong() })
                .build()
    }
}

fun main(args: Array<String>) = runBlocking {
    val server: Server = ServerBuilder
            .forPort(Constants.port)
            .addService(RngService())
            .build()
    Runtime.getRuntime().addShutdownHook(
            Thread {
                println("*** shutting down gRPC server since JVM is shutting down")
                server.shutdown()
            }
    )
    println("Server started, listening on ${Constants.port}")
    server.start()
    server.awaitTermination()
}