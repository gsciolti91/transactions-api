package com.gsciolti.transactionsapi

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant.now
import java.util.concurrent.Executors.newFixedThreadPool
import java.util.concurrent.TimeUnit.SECONDS
import kotlin.random.Random

@RunWith(SpringRunner::class)
@AutoConfigureMockMvc
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = [TransactionsApi::class]
)
class CreateTransactionThreadSafetyIT {

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun `create transaction should be thread safe`() {

        newFixedThreadPool(5).apply {
            repeat(5) { execute(create1000RecentTransactionsOf1Eur) }
            shutdown()
            awaitTermination(10, SECONDS)
        }

        mvc.perform(get("/statistics"))
            .andExpect(status().isOk())
            .andExpect(
                content()
                    .json(
                        """
                            {   
                                "sum": "5000.00",
                                "avg": "1.00",
                                "max": "1.00",
                                "min": "1.00",
                                "count": 5000
                            }
                        """.trimIndent()
                    )
            )
    }

    private val create1000RecentTransactionsOf1Eur: () -> Unit = {

        val thread = Thread.currentThread().name

        repeat(1000) {
            val timestamp = now().minusSeconds(Random.nextLong(1, 50))

            println("$thread - Creating transaction at: $timestamp")

            mvc.perform(
                post("/transactions")
                    .contentType(APPLICATION_JSON)
                    .content("{ \"amount\": \"1.00\", \"timestamp\": \"$timestamp\" }")
            )
                .andExpect(status().isCreated())
        }
    }
}