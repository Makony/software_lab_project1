package service

import entity.*
import kotlin.test.*
import java.util.Stack

/**
 * This class tests the functionality of the [PlayerService.discardCard] function.
 */
class DiscardCardTest {
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
     * Test to ensure that all the functionalities of [PlayerService.discardCard] work correctly
     */
    @Test
    fun testDiscardCard() {
        val testRefreshable = TestRefreshable()
        rootService.addRefreshable(testRefreshable)

        val game = rootService.currentGame
        assertNotNull(game)

        val currentPlayer = game.players[0]
        val card = currentPlayer.hand[2]
        val beforeDrawing = game.drawCardPile.size

        assertFalse(testRefreshable.refreshAfterDiscardCardCalled)
        assertFalse(testRefreshable.refreshAfterDrawCardCalled)
        assertFalse(testRefreshable.refreshAfterLogCalled)
        assertFalse(testRefreshable.refreshAfterEndTurnCalled)

        rootService.playerService.discardCard(card)

        assertTrue(testRefreshable.refreshAfterDiscardCardCalled)
        assertTrue(testRefreshable.refreshAfterDrawCardCalled)
        assertTrue(testRefreshable.refreshAfterLogCalled)
        assertTrue(testRefreshable.refreshAfterEndTurnCalled)

        assertFalse { currentPlayer.hand.contains(card) }
        assertTrue { game.discardCardPile.contains(card) }
        assertEquals(PlayerAction.DISCARD_CARD, currentPlayer.lastAction)
        assertEquals(5, currentPlayer.hand.size)
        assertTrue { game.drawCardPile.size < beforeDrawing }
    }

    /**
     * Check that a player cannot discard a card twice
     */
    @Test
    fun testDiscardCardTwice(){

        val card = Card(CardSuit.HEARTS, CardValue.FOUR)
        val card2 = Card(CardSuit.HEARTS, CardValue.THREE)
        val game = rootService.currentGame
        assertNotNull(game)

        rootService.playerService.discardCard(card)

        assertFailsWith<IllegalArgumentException>{
            rootService.playerService.destroyCard(card2)
        }
    }

    /**
     * Check function if no game is running
     */
    @Test
    fun testDiscardCardNoGame(){

        val card = Card(CardSuit.HEARTS, CardValue.FOUR)
        rootService.currentGame = null

        assertFailsWith<IllegalStateException>{
            rootService.playerService.discardCard(card)
        }
    }

    /**
     * Check if method shuffles when draw stack is empty
     */
    @Test
    fun testIfDiscardCardShuffles(){

        val game = rootService.currentGame
        assertNotNull(game)
        val card = Card(CardSuit.HEARTS, CardValue.FOUR)

        game.drawCardPile.clear()
        game.discardCardPile.add(card)

        rootService.playerService.discardCard(card)

        assertTrue { game.discardCardPile.isEmpty() }
        assertTrue { game.drawCardPile.isNotEmpty() }
    }
}