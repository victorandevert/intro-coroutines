package tasks

import contributors.MockGithubService
import contributors.expectedConcurrentResults
import contributors.testRequestData
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Request5ConcurrentKtTest {
    @Test
    fun testConcurrent() = runTest {
        val startTime = currentTime
        val result = loadContributorsConcurrent(MockGithubService, testRequestData)
        assertEquals("Wrong result for 'loadContributorsConcurrent'", expectedConcurrentResults.users, result)
        val totalTime = currentTime - startTime

        assertEquals(
            "The calls run concurrently, so the total virtual time should be 2200 ms: " +
                    "1000 ms for repos request plus max(1000, 1200, 800) = 1200 ms for concurrent contributors requests)",
            expectedConcurrentResults.timeFromStart, totalTime
        )

        assertTrue(
            "The calls run concurrently, so the total virtual time should be 2200 ms: " +
                    "1000 ms for repos request plus max(1000, 1200, 800) = 1200 ms for concurrent contributors requests)",
            totalTime in expectedConcurrentResults.timeFromStart..(expectedConcurrentResults.timeFromStart + 500)
        )
    }
}