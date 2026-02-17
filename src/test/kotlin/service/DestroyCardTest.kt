package service

import entity.*
import kotlin.test.*
import java.util.Stack

/**
 * This class tests the functionality of the [PlayerService.destroyCard] function.
 */
class DestroyCardTest {
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

        return Game(stairs, drawCardPile, players, currentPlayer = 0)
    }

    /**
     * Test to ensure that all the functionalities of [PlayerService.destroyCard] work correctly
     */
    @Test
    fun testDestroyCard() {
        val testRefreshable = TestRefreshable()
        rootService.addRefreshable(testRefreshable)

        val game = rootService.currentGame
        assertNotNull(game)

        val target = game.stairs[0].peek()
        game.players[0].score = 5

        assertFalse(testRefreshable.refreshAfterDestryCardCalled)
        assertFalse(testRefreshable.refreshAfterRevealCardCalled)
        assertFalse(testRefreshable.refreshAfterDrawCardCalled)
        assertFalse(testRefreshable.refreshAfterLogCalled)

        val scoreBefore = game.players[0].score

        rootService.playerService.destroyCard(target)

        assertTrue(testRefreshable.refreshAfterDestryCardCalled)
        assertTrue(testRefreshable.refreshAfterRevealCardCalled)
        assertFalse(testRefreshable.refreshAfterDrawCardCalled)
        assertTrue(testRefreshable.refreshAfterLogCalled)

        assertFalse { game.stairs.any { stack -> stack.contains(target) } }
        assertTrue { game.discardCardPile.contains(target) }
        assertEquals(5, scoreBefore - game.players[0].score)

        val revealedCard = game.stairs[0].peek()
        val queenOfHearts = Card(CardSuit.HEARTS, CardValue.QUEEN)
        assertEquals(queenOfHearts, revealedCard)

        assertEquals(PlayerAction.DESTROY_CARD, game.players[0].lastAction)
        assertTrue { game.stairsModified }
        assertEquals(5, game.players[0].hand.size)
    }

    /**
     * Check that a player cannot destroy a card twice
     */
    @Test
    fun testDestroyCardTwice(){

        val target = Card(CardSuit.HEARTS, CardValue.ACE)
        val target2 = Card(CardSuit.HEARTS, CardValue.QUEEN)
        val game = rootService.currentGame
        assertNotNull(game)
        game.players[0].score = 5

        rootService.playerService.destroyCard(target)

        assertFailsWith<IllegalArgumentException>{
            rootService.playerService.destroyCard(target2)
        }
    }

    /**
     * Check for case if a player's score is less than 5
     */
    @Test
    fun testDestroyCardLessScore(){

        val target = Card(CardSuit.HEARTS, CardValue.ACE)
        val game = rootService.currentGame
        assertNotNull(game)
        game.players[0].score = 4

        assertFailsWith<IllegalArgumentException>{
            rootService.playerService.destroyCard(target)
        }
    }

    /**
     * Check function if no game is running
     */
    @Test
    fun testDestroyCardNoGame(){

        val target = Card(CardSuit.HEARTS, CardValue.ACE)
        rootService.currentGame = null

        assertFailsWith<IllegalStateException>{
            rootService.playerService.destroyCard(target)
        }
    }
}