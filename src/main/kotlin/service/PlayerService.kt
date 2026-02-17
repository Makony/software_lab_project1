package service
import entity.*
import kotlin.collections.isNotEmpty

/**
 * Service layer class that provides the logic for player's actions
 *
 * @param rootService The [RootService] instance to access the other service methods and entity layer
 */
class PlayerService(private val rootService: RootService): AbstractRefreshingService() {

    /**
     * Combines values of two cards, removes cards from player's hand and from the stairs, updates player's score
     * then reveals one card from the stairs
     * @param card card from the player's hand
     * @param target card from stairs to combine
     * @throws IllegalStateException if no game is running
     * @throws IllegalArgumentException if the player has already combined cards in a turn or
     * if cards can't be combined
     */
    fun playCard(card: Card, target: Card){

        val game = rootService.currentGame
        checkNotNull(game) { "No game currently running."}

        val currentPlayer = game.players[game.currentPlayer]
        val log = rootService.loggingService

        require(currentPlayer.lastAction != PlayerAction.PLAY_CARD){"You can't combine cards twice in a turn"}
        require(card.checkSuitOrValue(target)) {"Card's suit or value does not match"}

        // Remove card from hand
        currentPlayer.hand.remove(card)

        // Remove target card from the stairs and reveal from below
        val revealedCard = removeCardFromStairs(game, target)

        // Add combined cards to the collection stack
        currentPlayer.cardsPlayed.add(card)
        currentPlayer.cardsPlayed.add(target)

        // Update score
        val sum = card.value.toInt() + target.value.toInt()
        currentPlayer.score += sum

        currentPlayer.lastAction = PlayerAction.PLAY_CARD
        log.logAction(currentPlayer.lastAction, card, target, revealedCard)
        drawCard()
        onAllRefreshables { refreshAfterPlayCard(target, card) }
    }

    /**
     * Removes one card from the stairs, distracts 5 points from player's score, then reveals one card from the stairs
     * @param card card that player chose to remove
     * @throws IllegalStateException if no game is running
     * @throws IllegalArgumentException if the player has already destroyed a card in a turn or
     * if player's score is less than 5
     */
    fun destroyCard(card: Card){

        val game = rootService.currentGame
        checkNotNull(game) { "No game currently running."}

        val currentPlayer = game.players[game.currentPlayer]
        val log = rootService.loggingService

        require(currentPlayer.lastAction != PlayerAction.DESTROY_CARD){"You can't destroy twice in a turn"}
        require(currentPlayer.score >= 5){"You need at least 5 points"}

        // Remove target card from the stairs and reveal from below
        val revealedCard = removeCardFromStairs(game, card)

        // Add it to discard stack
        game.discardCardPile.add(card)

        // Distract 5 points from the score
        currentPlayer.score -= 5

        currentPlayer.lastAction = PlayerAction.DESTROY_CARD
        log.logAction(currentPlayer.lastAction, null, card, revealedCard)
        onAllRefreshables { refreshAfterDestroyCard(card) }
    }

    /**
     * Removes one card from the player's hand and adds it to the discard stack
     * @param card card to remove from the player's hand
     * @throws IllegalStateException if no game is running
     * @throws IllegalArgumentException if the player has already discarded a card in a turn
     */
    fun discardCard(card: Card){

        val game = rootService.currentGame
        checkNotNull(game) { "No game currently running."}

        val currentPlayer = game.players[game.currentPlayer]
        val log = rootService.loggingService

        require(currentPlayer.lastAction != PlayerAction.DISCARD_CARD){"You can't discard card twice in a turn"}

        // Remove card from hand
        currentPlayer.hand.remove(card)
        // Add removed card to the discard stack
        game.discardCardPile.add(card)

        currentPlayer.lastAction = PlayerAction.DISCARD_CARD

        log.logAction(currentPlayer.lastAction, card, null, null)
        drawCard()
        onAllRefreshables { refreshAfterDiscardCard(card) }
    }

    /**
     * Starts the next turn and changes the current player
     * @throws IllegalStateException if no game is running
     */
    fun startTurn(){

        val game = rootService.currentGame
        checkNotNull(game) { "No game currently running."}

        // Change the current player
        game.currentPlayer = (game.currentPlayer + 1) % 2

        // Reset last action of the player in a new turn
        val currentPlayer = game.players[game.currentPlayer]
        currentPlayer.lastAction = null

        onAllRefreshables { refreshAfterStartTurn() }
    }

    /**
     * Ends the turn after the player combined or discarded a card
     * @throws IllegalArgumentException if the player hasn't combined or discarded a card yet
     */
    private fun endTurn(){

        val game = rootService.currentGame ?: return

        val currentPlayer = game.players[game.currentPlayer]

        require(currentPlayer.lastAction == PlayerAction.PLAY_CARD ||
                currentPlayer.lastAction == PlayerAction.DISCARD_CARD){"Combine or discard a card"}

        onAllRefreshables { refreshAfterEndTurn() }
    }

    /**
     * Finds the card in the stairs and removes it
     * @param game current running game
     * @param target card to find and remove from the stairs
     * @throws IllegalStateException if no stack with the target card exists
     * @return index of the stack from where the card was removed
     */
    private fun removeCardFromStairs(game: Game, target: Card): Card? {

        val stairs = game.stairs
        val targetColumn = stairs.find { it.isNotEmpty() && it.peek() == target }

        checkNotNull(targetColumn){"No column found for $target"}

        // Get index of the column before it might get removed
        val index = stairs.indexOf(targetColumn)
        var revealedCard: Card? = null

        targetColumn.pop()
        game.stairsModified = true
        if (targetColumn.isNotEmpty()) {
                revealedCard = game.stairs[index].peek()
                onAllRefreshables { refreshAfterRevealCard(revealedCard) }
        }else{
            stairs.remove(targetColumn)
            if (stairs.isEmpty()){
                rootService.gameService.endGame()
            }
        }
        return revealedCard
    }

    /**
     * Draws a card to the player's hand, creates a new draw stack if it's empty and
     * ends the turn after the player has 5 cards in a hand
     * @throws IllegalStateException if player has at least 5 cards in hand
     */
    private fun drawCard(){
        val game = rootService.currentGame ?: return

        val currentPlayer = game.players[game.currentPlayer]

        check(currentPlayer.hand.size < 5){"You can't have more than 5 cards"}

        // Shuffle discard stack if draw pile is empty
        if (game.drawCardPile.isEmpty()){
            game.drawCardPile = rootService.gameService.shuffleStack()
        }

        val drawnCard = game.drawCardPile.first()
        currentPlayer.hand.add(drawnCard)
        game.drawCardPile.removeFirst()
        onAllRefreshables { refreshAfterDrawCard(drawnCard) }

        // Only end the turn if the player has five cards in their hand
        if (currentPlayer.hand.size == 5){
            endTurn()
        }
    }
}