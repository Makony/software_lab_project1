package service
import entity.Card
import entity.PlayerAction

/**
 * Service layer class that provides the logic for logging player's actions
 *
 * @param rootService The [RootService] instance to access the other service methods and entity layer
 *
 */
class LoggingService (private val rootService: RootService): AbstractRefreshingService() {

    /**
     * Logs player's actions providing details of player's every move
     * @param action player's action to log
     * @param card card from player's hand
     * @param target card from the stairs
     * @param revealedCard card that was revealed in the stairs
     * @throws IllegalArgumentException if required information for logging weren't given
     */
    fun logAction(
        action: PlayerAction?,
        card: Card? = null,
        target: Card? = null,
        revealedCard: Card? = null
    ) {
        val game = rootService.currentGame ?: return

        val currentPlayer = game.players[game.currentPlayer]
        val otherPlayer = game.players[(game.currentPlayer + 1) % 2]

        if (action == PlayerAction.PLAY_CARD) {

            require(card != null && target != null) {}

            if (revealedCard != null) {
                game.gameLog.add("- ${currentPlayer.name} combined $card with $target from " +
                            "the stairs.\nThen drew a new card.\n" +
                            "The card $revealedCard from below was revealed.\nNow it's ${otherPlayer.name}'s turn.")
            } else {
                game.gameLog.add(
                    "- ${currentPlayer.name} combined $card with $target from " +
                            "the stairs.\nThen drew a new card.\nNow it's ${otherPlayer.name}'s turn.")
            }
        } else if (action == PlayerAction.DISCARD_CARD) {

            checkNotNull(card) {}

            game.gameLog.add("- ${currentPlayer.name} discarded $card from hand.\nThen drew a new card." +
                        "\nNow it's ${otherPlayer.name}'s turn.")

        }else if (action == PlayerAction.DESTROY_CARD) {

            requireNotNull(target){}

            if (revealedCard != null) {
                game.gameLog.add( "- ${currentPlayer.name} destroyed $target from the stairs.\n" +
                        "The card $revealedCard from below was revealed.")
            } else {
                game.gameLog.add("- ${currentPlayer.name} destroyed $target from the stairs.")
            }
        }

        var previous = ""
        if (game.gameLog.size > 1) {
            previous = game.gameLog[game.gameLog.size - 2]
        }
        val last = game.gameLog.last()
        onAllRefreshables { refreshAfterLog(previous+ "\n" + last) }
    }
}