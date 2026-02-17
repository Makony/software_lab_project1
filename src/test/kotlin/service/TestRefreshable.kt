package service
import entity.*

/**
 * [Refreshable] implementation that refreshes nothing, but remembers
 * if a refresh method has been called (since last [reset])
 */
class TestRefreshable: Refreshable {

    var refreshAfterStartNewGameCalled: Boolean = false
        private set

    var refreshAfterShuffleStackCalled: Boolean = false
        private set

    var refreshAfterEndGameCalled: Boolean = false
        private set

    var refreshAfterLogCalled: Boolean = false
        private set

    var refreshAfterPlaycardCalled: Boolean = false
        private set

    var refreshAfterRevealCardCalled: Boolean = false
        private set

    var refreshAfterDestryCardCalled: Boolean = false
        private set

    var refreshAfterDiscardCardCalled: Boolean = false
        private set

    var refreshAfterDrawCardCalled: Boolean = false
        private set

    var refreshAfterStartTurnCalled: Boolean = false
        private set

    var refreshAfterEndTurnCalled: Boolean = false
        private set

    var winner: Player? = null
    var message: String? = null

    /**
     * resets all *Called properties to false
     */
    fun reset() {
        refreshAfterStartNewGameCalled = false
        refreshAfterShuffleStackCalled = false
        refreshAfterEndGameCalled = false
        refreshAfterLogCalled = false
        refreshAfterPlaycardCalled = false
        refreshAfterRevealCardCalled = false
        refreshAfterDestryCardCalled = false
        refreshAfterDiscardCardCalled = false
        refreshAfterDrawCardCalled = false
        refreshAfterStartTurnCalled = false
        refreshAfterEndTurnCalled = false
    }

    /**
     * Checks if a refresh method has been called
     */
    override fun refreshAfterStartNewGame() {
        refreshAfterStartNewGameCalled = true
    }

    /**
     * Checks if a refresh method has been called
     */
    override fun refreshAfterShuffleStack() {
        refreshAfterShuffleStackCalled = true
    }

    /**
     * Checks if a refresh method has been called
     */
    override fun refreshAfterEndGame(winner: Player?) {
        refreshAfterEndGameCalled = true
        this.winner = winner
    }

    /**
     * Checks if a refresh method has been called
     */
    override fun refreshAfterLog(message: String) {
        refreshAfterLogCalled = true
        this.message = message
    }

    /**
     * Checks if a refresh method has been called
     */
    override fun refreshAfterPlayCard(target: Card, source: Card) {
        refreshAfterPlaycardCalled = true
    }

    /**
     * Checks if a refresh method has been called
     */
    override fun refreshAfterRevealCard(card: Card) {
        refreshAfterRevealCardCalled = true
    }

    /**
     * Checks if a refresh method has been called
     */
    override fun refreshAfterDestroyCard(target: Card) {
        refreshAfterDestryCardCalled = true
    }

    /**
     * Checks if a refresh method has been called
     */
    override fun refreshAfterDiscardCard(card: Card) {
        refreshAfterDiscardCardCalled = true
    }

    /**
     * Checks if a refresh method has been called
     */
    override fun refreshAfterDrawCard(card: Card) {
        refreshAfterDrawCardCalled = true
    }

    /**
     * Checks if a refresh method has been called
     */
    override fun refreshAfterStartTurn() {
        refreshAfterStartTurnCalled = true
    }

    /**
     * Checks if a refresh method has been called
     */
    override fun refreshAfterEndTurn() {
        refreshAfterEndTurnCalled = true
    }
}