package service

import kotlin.test.*

/**
 * Class that provides tests for [Refreshable] inside [GameService], [PlayerService] and [LoggingService]
 * [TestRefreshable] is used to validate correct refreshing behavior even though no GUI
 * is present.
 */
class RootServiceTest {
    /**
     * The [RootService] is initialized in the [setUpGame] function
     * hence it is a late-initialized property.
     */
    private lateinit var rootService: RootService

    /**
     * This [setUpGame] function is executed before every test
     * due to the [BeforeTest] annotation.
     */
    @BeforeTest
    fun setUpGame() {
        rootService = RootService()
    }

    /**
     * Tests if [RootService.addRefreshable] properly forwards the added [Refreshable] to
     * its service classes.
     */
    @Test
    fun testRootServiceSingleRefreshable() {
        val testRefreshable = TestRefreshable()
        rootService.addRefreshable(testRefreshable)

        // Test if testRefreshable was successfully added to GameService
        assertFalse(testRefreshable.refreshAfterStartNewGameCalled)
        rootService.gameService.onAllRefreshables { refreshAfterStartNewGame() }
        assertTrue(testRefreshable.refreshAfterStartNewGameCalled)
        testRefreshable.reset()

        // Test if testRefreshable was successfully added to PlayerActionService
        assertFalse(testRefreshable.refreshAfterStartNewGameCalled)
        rootService.playerService.onAllRefreshables { refreshAfterStartNewGame() }
        assertTrue(testRefreshable.refreshAfterStartNewGameCalled)
        testRefreshable.reset()

    }

    /**
     * Tests if [RootService.addRefreshable] properly forwards the added [Refreshable] to
     * its service classes.
     */
    @Test
    fun testRootServiceMultiRefreshable() {
        val testRefreshable1 = TestRefreshable()
        val testRefreshable2 = TestRefreshable()
        rootService.addRefreshables(testRefreshable1, testRefreshable2)

        // Test if testRefreshables were successfully added to GameService
        assertFalse(testRefreshable1.refreshAfterStartNewGameCalled)
        assertFalse(testRefreshable2.refreshAfterStartNewGameCalled)
        rootService.gameService.onAllRefreshables { refreshAfterStartNewGame() }
        assertTrue(testRefreshable1.refreshAfterStartNewGameCalled)
        assertTrue(testRefreshable2.refreshAfterStartNewGameCalled)
        testRefreshable1.reset()
        testRefreshable2.reset()

        // Test if testRefreshable was successfully added to PlayerActionService
        assertFalse(testRefreshable1.refreshAfterStartNewGameCalled)
        assertFalse(testRefreshable2.refreshAfterStartNewGameCalled)
        rootService.playerService.onAllRefreshables { refreshAfterStartNewGame() }
        assertTrue(testRefreshable1.refreshAfterStartNewGameCalled)
        assertTrue(testRefreshable2.refreshAfterStartNewGameCalled)
        testRefreshable1.reset()
        testRefreshable2.reset()

    }
}