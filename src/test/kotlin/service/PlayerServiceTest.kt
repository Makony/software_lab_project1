package service
import entity.*
import kotlin.test.*
import java.util.Stack

/**
 * This class tests the functionality of the [PlayerService] class.
 * Functions [PlayerService.playCard], [PlayerService.destroyCard], [PlayerService.discardCard]
 * were tested in separated test classes
 *
 * @see PlayCardTest
 * @see DestroyCardTest
 * @see DiscardCardTest
 */
class PlayerServiceTest {

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
        val game = createTestGame()
        rootService.currentGame = game
    }

    /**
     * Creates determined, controlled game for testing
     */
    private fun createTestGame(): Game {
        val players = listOf(Player("Bob"), Player("Alice"))

        players[0].hand.addAll(listOf(
            Card(CardSuit.CLUBS, CardValue.TWO),
            Card(CardSuit.HEARTS, CardValue.THREE),
            Card(CardSuit.HEARTS, CardValue.FOUR),
            Card(CardSuit.DIAMONDS, CardValue.FIVE),
            Card(CardSuit.CLUBS, CardValue.SIX)
        ))

        val stairs = mutableListOf(
            Stack<Card>().apply {
                push(Card(CardSuit.CLUBS, CardValue.KING))
                push(Card(CardSuit.HEARTS, CardValue.QUEEN))
                push(Card(CardSuit.HEARTS, CardValue.ACE))
            },
            Stack<Card>().apply {
                push(Card(CardSuit.SPADES, CardValue.TWO))
                push(Card(CardSuit.SPADES, CardValue.THREE))
            }
        )

        val drawCardPile = mutableListOf(
            Card(CardSuit.CLUBS, CardValue.KING),
            Card(CardSuit.SPADES, CardValue.TEN),
            Card(CardSuit.HEARTS, CardValue.JACK)
        )

        return Game(stairs, drawCardPile, players, 0)
    }

    /**
     * Test to ensure that all the functionalities of [PlayerService.startTurn] work correctly
     */
    @Test
    fun testStartTurn(){
        val testRefreshable = TestRefreshable()
        rootService.addRefreshable(testRefreshable)

        val game = rootService.currentGame
        assertNotNull(game)

        var currentPlayer = game.players[game.currentPlayer]

        assertFalse { testRefreshable.refreshAfterStartTurnCalled}
        assertEquals("Bob", currentPlayer.name)

        rootService.playerService.startTurn()

        currentPlayer = game.players[game.currentPlayer]

        assertTrue { testRefreshable.refreshAfterStartTurnCalled }
        assertEquals("Alice", currentPlayer.name)
        assertNull(currentPlayer.lastAction)
    }

    /**
     * Check function if no game is running
     */
    @Test
    fun testStartTurnNoGame(){

        rootService.currentGame = null

        assertFailsWith<IllegalStateException> {
            rootService.playerService.startTurn()
        }
    }
}