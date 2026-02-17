package service

import entity.*
import kotlin.test.*
import java.util.Stack

/**
 * This class tests the functionality of the [PlayerService.playCard] function.
 */
class PlayCardTest {
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
                push(Card(CardSuit.HEARTS, CardValue.QUEEN))
                push(Card(CardSuit.HEARTS, CardValue.ACE))
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
     * Test to ensure that all the functionalities of [PlayerService.playCard] work correctly
     */
    @Test
    fun testPlayCard() {
        val testRefreshable = TestRefreshable()
        rootService.addRefreshable(testRefreshable)

        val game = rootService.currentGame
        assertNotNull(game)

        val currentPlayer = game.players[0]

        val card = currentPlayer.hand[1]
        val target = game.stairs[0].peek()

        assertFalse(testRefreshable.refreshAfterPlaycardCalled)
        assertFalse(testRefreshable.refreshAfterRevealCardCalled)
        assertFalse(testRefreshable.refreshAfterDrawCardCalled)
        assertFalse(testRefreshable.refreshAfterLogCalled)
        assertFalse(testRefreshable.refreshAfterEndTurnCalled)

        val scoreBefore = currentPlayer.score
        val beforeDrawing = game.drawCardPile.size

        rootService.playerService.playCard(card, target)

        assertTrue(testRefreshable.refreshAfterPlaycardCalled)
        assertTrue(testRefreshable.refreshAfterRevealCardCalled)
        assertTrue(testRefreshable.refreshAfterDrawCardCalled)
        assertTrue(testRefreshable.refreshAfterLogCalled)
        assertTrue(testRefreshable.refreshAfterEndTurnCalled)

        assertFalse { currentPlayer.hand.contains(card) }
        assertFalse { game.stairs.any { stack -> stack.contains(target) } }
        assertTrue { currentPlayer.cardsPlayed.containsAll(listOf(card, target)) }

        val sum = card.value.toInt() + target.value.toInt()
        assertEquals(sum, currentPlayer.score - scoreBefore)

        val revealedCard = game.stairs[0].peek()
        val queenOfHearts = Card(CardSuit.HEARTS, CardValue.QUEEN)
        assertEquals(queenOfHearts, revealedCard)

        assertEquals(PlayerAction.PLAY_CARD, currentPlayer.lastAction)
        assertTrue { game.stairsModified }
        assertEquals(5, currentPlayer.hand.size)
        assertTrue { game.drawCardPile.size < beforeDrawing }
    }

    /**
     * Check if cards to combine are not compatible
     */
    @Test
    fun testPlayCardNotValidCards(){

        val card = Card(CardSuit.CLUBS, CardValue.TWO)
        val target = Card(CardSuit.SPADES, CardValue.FOUR)

        assertFailsWith<IllegalArgumentException>{
            rootService.playerService.playCard(card, target)
        }
    }

    /**
     * Check that a player cannot combine a card twice
     */
    @Test
    fun testPlayCardTwice(){

        val card = Card(CardSuit.HEARTS, CardValue.FOUR)
        val card2 = Card(CardSuit.HEARTS, CardValue.THREE)
        val target = Card(CardSuit.HEARTS, CardValue.ACE)
        val target2 = Card(CardSuit.HEARTS, CardValue.QUEEN)

        rootService.playerService.playCard(card, target)

        assertFailsWith<IllegalArgumentException>{
            rootService.playerService.playCard(card2, target2)
        }
    }

    /**
     * Check function if no game is running
     */
    @Test
    fun testPlayCardNoGame(){

        rootService.currentGame = null

        val card = Card(CardSuit.HEARTS, CardValue.FOUR)
        val target = Card(CardSuit.HEARTS, CardValue.ACE)

        assertFailsWith<IllegalStateException>{
            rootService.playerService.playCard(card, target)
        }
    }

    /**
     * Check whether the game ends when the stairs are empty
     */
    @Test
    fun testIfPlayCardEndsGame(){

        val game = rootService.currentGame
        assertNotNull(game)
        val currentPlayer = game.players[game.currentPlayer]
        currentPlayer.score = 5

        var target = game.stairs[0].peek()
        rootService.playerService.destroyCard(target)

        val card = currentPlayer.hand[1]
        target = game.stairs[0].peek()
        rootService.playerService.playCard(card, target)

        assertNull(rootService.currentGame)
    }

    /**
     * Check if the method shuffles when draw stack is empty
     */
    @Test
    fun testIfPlayCardShuffles(){

        val game = rootService.currentGame
        assertNotNull(game)
        val currentPlayer = game.players[game.currentPlayer]
        currentPlayer.score = 5

        val card = currentPlayer.hand[1]
        val target = game.stairs[0].peek()

        game.drawCardPile.clear()
        game.discardCardPile.addAll(listOf(card, target))

        rootService.playerService.playCard(card, target)

        assertTrue { game.discardCardPile.isEmpty() }
        assertTrue { game.drawCardPile.isNotEmpty() }
    }
}