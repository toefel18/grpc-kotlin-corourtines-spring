package nl.toefel.grpckotlincorourtinesspring

import nl.toefel.RngRequest
import nl.toefel.RngServiceGrpcKt
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

import kotlinx.coroutines.*

data class Response(val generatedNumbers: List<Long>, val delay: Long)

@RestController
class CoroutineController(val client: RngServiceGrpcKt.RngServiceCoroutineStub) {

    @GetMapping("/single/{delay}")
    suspend fun getSingleNumber(
            @PathVariable delay: Long
    ): Response {
        val request = RngRequest.newBuilder()
                .setNumGenerated(1)
                .setDelayMillis(delay)
                .build()

        log.info("sending request to grpc backend server $request")

        // this is also a suspend function!
        val response = client.calculateRandoms(request)


        log.info("received $response")
        return Response(listOf(response.getRandoms(0)), delay)
    }

    @GetMapping("/concurrent/{numConcurrent}/{delay}")
    suspend fun getNumbersConcurrently(
            @PathVariable numConcurrent: Int,
            @PathVariable delay: Long
    ): Response {

        val outstandingRequests = (0..numConcurrent).map {
            GlobalScope.async {
                val request = RngRequest.newBuilder()
                        .setNumGenerated(1)
                        .setDelayMillis(delay)
                        .build()

                log.info("sending request to grpc backend server $request")

                // this is also a suspend function!
                val response = client.calculateRandoms(request)


                log.info("received $response")
                response
            }
        }

        return Response(outstandingRequests.map { it.await().getRandoms(0) }, delay)
    }


    companion object {
        val log = LoggerFactory.getLogger(CoroutineController::class.java)
    }
}