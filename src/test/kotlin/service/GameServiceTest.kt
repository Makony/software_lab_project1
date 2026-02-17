package service
import entity.*
import kotlin.test.*

/**
 * This class tests the functionality of the [GameService] class.
 */
class GameServiceTest {

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
     * Tests the default case of starting a game: instantiate a [RootService] and then run
     * startNewGame on its [RootService.gameService].
     */
    @Test
    fun testStartNewGame() {
        val testRefreshable = TestRefreshable()
        rootService.addRefreshable(testRefreshable)

        assertFalse(testRefreshable.refreshAfterStartNewGameCalled)
        assertNull(rootService.currentGame)
        rootService.gameService.startNewGame("Bob", "Alice")
        assertTrue(testRefreshable.refreshAfterStartNewGameCalled)

        val currentGame = rootService.currentGame
        assertNotNull(currentGame)

        assertEquals(5, currentGame.players[0].hand.size)
        assertEquals(0, currentGame.players[0].cardsPlayed.size)

        assertEquals(5, currentGame.players[1].hand.size)
        assertEquals(0, currentGame.players[1].cardsPlayed.size)

        assertEquals(27, currentGame.drawCardPile.size)
        assertEquals(0, currentGame.discardCardPile.size)
        assertEquals(15, currentGame.stairs.sumOf { it.size })
    }

    /**
     * Check for case if player's name is empty
     */
    @Test
    fun testStartNewGameNameFail() {

        assertNull(rootService.currentGame)
        assertFailsWith<IllegalArgumentException> {
            rootService.gameService.startNewGame("", "")
        }
    }

    /**
     * Test to ensure that all the functionalities of [GameService.endGame] work correctly
     */
    @Test
    fun testEndGame(){

        val testRefreshable = TestRefreshable()
        rootService.addRefreshable(testRefreshable)

        // Start a new game
        rootService.gameService.startNewGame("Bob", "Alice")
        val game = rootService.currentGame
        assertNotNull(game)

        // Simulate scores
        game.players[0].score = 20
        game.players[1].score = 10

        game.stairs.clear()

        rootService.gameService.endGame()

        assertTrue(testRefreshable.refreshAfterEndGameCalled)
        assertEquals("Bob", testRefreshable.winner?.name)
        assertNull(rootService.currentGame)
    }

    /**
     * Check for case when conditions for End Game weren't met
     */
    @Test
    fun testEndGameFail(){

        rootService.gameService.startNewGame("Bob", "Alice")
        val game = rootService.currentGame
        assertNotNull(game)

        game.players[0].score = 30
        game.players[1].score = 10

        assertFailsWith<IllegalArgumentException> {
            rootService.gameService.endGame()
        }

    }

    /**
     * Test for case when there is no winner
     */
    @Test
    fun testEndGameTie(){
        val testRefreshable = TestRefreshable()
        rootService.addRefreshable(testRefreshable)

        rootService.gameService.startNewGame("Bob", "Alice")
        val game = rootService.currentGame
        assertNotNull(game)

        game.players[0].score = 10
        game.players[1].score = 10

        game.stairs.clear()

        rootService.gameService.endGame()

        assertNull(testRefreshable.winner)
        assertNull(rootService.currentGame)
    }

    /**
     * Test to ensure that all the functionalities of [GameService.shuffleStack] work correctly
     */
    @Test
    fun testShuffleStack(){
        val testRefreshable = TestRefreshable()
        rootService.addRefreshable(testRefreshable)

        rootService.gameService.startNewGame("Bob", "Alice")
        val game = rootService.currentGame
        assertNotNull(game)

        game.drawCardPile.clear()
        game.discardCardPile.add(Card(CardSuit.CLUBS, CardValue.TWO))

        assertFalse { testRefreshable.refreshAfterShuffleStackCalled }
        rootService.gameService.shuffleStack()

        assertTrue(testRefreshable.refreshAfterShuffleStackCalled)
        assertTrue { game.drawCardPile.isNotEmpty() }
        assertTrue { game.discardCardPile.isEmpty() }
        assertFalse { game.stairsModified }

        game.stairs.clear()
        game.drawCardPile.clear()
        game.discardCardPile.add(Card(CardSuit.CLUBS, CardValue.TWO))
        rootService.gameService.shuffleStack()
        assertNull(rootService.currentGame)
    }

    /**
     * Check if shuffle fails for empty discard stack
     */
    @Test
    fun testShuffleStackFail(){

        rootService.gameService.startNewGame("Bob", "Alice")
        val game = rootService.currentGame
        assertNotNull(game)

        assertFailsWith<IllegalArgumentException> {
            rootService.gameService.shuffleStack()
        }
    }
}