package service
import entity.Player
import entity.Card

/**
 * This interface provides a mechanism for the service layer classes to communicate
 * (usually to the GUI classes) that certain changes have been made to the entity
 * layer, so that the user interface can be updated accordingly.
 *
 * Default (empty) implementations are provided for all methods, so that implementing
 * GUI classes only need to react to events relevant to them.
 *
 * @see AbstractRefreshingService
 */
interface Refreshable{
    /**
     * perform refreshes that are necessary after a new game started
     */
    fun refreshAfterStartNewGame(){}

    /**
     * perform refreshes that are necessary after the last round was played
     * @param winner player that has the highest score
     */
    fun refreshAfterEndGame(winner: Player?){}

    /**
     * perform refreshes that are necessary after shuffling the stack
     */
    fun refreshAfterShuffleStack(){}

    /**
     * perform refreshes that are necessary after some logs were shown
     * @param message log to show on the screen
     */
    fun refreshAfterLog(message: String){}

    /**
     * perform refreshes that are necessary after a player has combined cards
     * @param target card from the staircase to combine
     * @param source card from the hand to combine
     */
    fun refreshAfterPlayCard(target: Card, source: Card){}

    /**
     * perform refreshes that are necessary after revealing a card
     * @param card card that has been revealed
     */
    fun refreshAfterRevealCard(card: Card){}

    /**
     * perform refreshes that are necessary after a player has destroyed a card
     * @param target card that has been destroyed
     */
    fun refreshAfterDestroyCard(target: Card){}

    /**
     * perform refreshes that are necessary after a player has discarded a card
     * @param card card that has been discarded
     */
    fun refreshAfterDiscardCard(card: Card){}

    /**
     * perform refreshes that are necessary after a player has drawn a card
     * @param card card that has been drawn from the draw pile
     */
    fun refreshAfterDrawCard(card: Card){}

    /**
     * perform refreshes that are necessary after the start of a player's turn
     */
    fun refreshAfterStartTurn(){}

    /**
     * perform refreshes that are necessary after the end of a player's turn
     */
    fun refreshAfterEndTurn(){}
}