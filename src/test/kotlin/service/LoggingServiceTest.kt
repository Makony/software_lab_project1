package service

import entity.*
import kotlin.test.*

/**
 * This class tests the functionality of the [LoggingService] class.
 */
class LoggingServiceTest {

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
        rootService.gameService.startNewGame("Bob", "Alice")
    }

    /**
     * Test to ensure that all the functionalities of [LoggingService.logAction] work correctly
     */
    @Test
    fun testLogAction(){
        val testRefreshable = TestRefreshable()
        rootService.addRefreshable(testRefreshable)

        var action = PlayerAction.PLAY_CARD
        val card = Card(CardSuit.DIAMONDS, CardValue.KING)
        val target = Card(CardSuit.SPADES, CardValue.NINE)
        val revealedCard = Card(CardSuit.HEARTS, CardValue.EIGHT)

        val message1 = "- Bob combined " + '\u2666' + "K with " + '\u2660' + "9 from " +
                "the stairs.\nThen drew a new card.\n" +
                "The card " + '\u2665' + "8 from below was revealed.\nNow it's Alice's turn."

        val message2 = "- Bob combined " + '\u2666' + "K with " + '\u2660' + "9 from " +
                "the stairs.\nThen drew a new card.\nNow it's Alice's turn."

        val message3 = "- Bob discarded " + '\u2666' + "K from hand.\n" +
                "Then drew a new card.\nNow it's Alice's turn."

        val message4 = "- Bob destroyed " + '\u2660' + "9 from the stairs.\n" +
                "The card " + '\u2665' + "8 from below was revealed."

        val message5 = "- Bob destroyed " + '\u2660' + "9 from the stairs."

        assertFalse(testRefreshable.refreshAfterLogCalled)

        rootService.loggingService.logAction(action, card, target, revealedCard)
        assertTrue(testRefreshable.refreshAfterLogCalled)
        assertEquals("\n" + message1,testRefreshable.message)
        rootService.loggingService.logAction(action, card, target, null)
        assertEquals(message1 + "\n" + message2,testRefreshable.message)

        action = PlayerAction.DISCARD_CARD
        rootService.loggingService.logAction(action, card, target, revealedCard)
        assertEquals(message2 + "\n" + message3,testRefreshable.message)

        action = PlayerAction.DESTROY_CARD
        rootService.loggingService.logAction(action, card, target, revealedCard)
        assertEquals(message3 + "\n" + message4,testRefreshable.message)
        rootService.loggingService.logAction(action, card, target, null)
        assertEquals(message4 + "\n" + message5,testRefreshable.message)
    }

    /**
     * Check for the case if parameters are null
     */
    @Test
    fun testLogPlayCardFails(){

        assertFails { rootService.loggingService.logAction(PlayerAction.PLAY_CARD) }
    }

    /**
     * Check for the case if parameters are null
     */
    @Test
    fun testLogDiscardCardFails(){

        assertFails { rootService.loggingService.logAction(PlayerAction.DISCARD_CARD) }
    }

    /**
     * Check for the case if parameters are null
     */
    @Test
    fun testLogDestroyCardFails(){

        assertFails { rootService.loggingService.logAction(PlayerAction.DESTROY_CARD) }
    }
}