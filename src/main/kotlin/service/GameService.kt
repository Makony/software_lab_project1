package service
import entity.*
import java.util.Stack

/**
 * Service layer class that provides the logic for actions not directly
 * related to a single player.
 *
 * @param rootService The [RootService] instance to access the other service methods and entity layer
 */
class GameService(private val rootService: RootService): AbstractRefreshingService(){

    /**
     * Creates the game with two players, sets up with staircase of cards and a draw stack, and deals cards to players
     * @param player1 first player
     * @param player2 second player
     * @throws IllegalArgumentException if player's name are empty
     */
    fun startNewGame(player1: String, player2: String){

        require(player1.isNotEmpty() && player2.isNotEmpty()){"Set both player's name"}

        // Create all 52 shuffled cards
        val allCards = defaultRandomCardList()

        // Initialize players
        val players: List<Player> = listOf(Player(player1), Player(player2))

        // Create draw pile stack
        val drawCardPile = allCards.subList(15, 42).toMutableList()

        // Deal cards to the player's hand
        players[0].hand.addAll(allCards.subList(42, 47))
        players[1].hand.addAll(allCards.subList(47, 52))

        // Initialize the game
        val game = Game(buildStairs(allCards), drawCardPile, players,0)

        rootService.currentGame = game

        onAllRefreshables { refreshAfterStartNewGame() }
    }

    /**
     * Ends the game and defines the winner
     * @throws IllegalStateException if no game has started
     * @throws IllegalArgumentException if a game shouldn't be ended
     */
    fun endGame(){

        val game = rootService.currentGame
        checkNotNull(game) { "No game currently running."}
        require(isGameEnded()){"Game shouldn't be ended."}

        val player1Score = game.players[0].score
        val player2Score = game.players[1].score
        var winner: Player? = null

        if (player1Score > player2Score){
            winner = game.players[0]
        }else if(player1Score < player2Score){
            winner = game.players[1]
        }

        onAllRefreshables { refreshAfterEndGame(winner) }
        rootService.currentGame = null
    }

    /**
     * Creates a new shuffled draw stack, clears the discard stack and resets [Game.stairsModified] back to false
     * @throws IllegalStateException if no game has started
     * @throws IllegalArgumentException if discard stack is empty
     * @return a new shuffled draw stack
     */
    fun shuffleStack(): MutableList<Card>{

        val game = rootService.currentGame
        checkNotNull(game) { "No game started yet."}
        require(game.discardCardPile.isNotEmpty()) { "Nothing in discard card stack"}

        if (isGameEnded()){
            endGame()
        }

        game.discardCardPile.shuffle()
        game.drawCardPile.addAll(game.discardCardPile)
        game.discardCardPile.clear()

        if (game.stairsModified){
            game.stairsModified = false
        }
        onAllRefreshables { refreshAfterShuffleStack() }

        return game.drawCardPile
    }

    /**
     * Builds stairs of 15 cards
     * @param allCards list of randomly shuffled cards to build stairs with
     * @return list of stacks of cards from size 5 to 1
     */
    private fun buildStairs(allCards: List<Card>): MutableList<Stack<Card>>{

        var index = 0
        val stairs = (5 downTo 1).map { size ->
            Stack<Card>().apply {
                addAll(allCards.subList(index, index + size))
                index += size
            }
        }.toMutableList()
        return stairs
    }

    /**
     * Checks whether all the requirements for the game to end have been met.
     * @return true if stairs is empty or when the last card of the draw pile has been drawn and no card has been
     * removed from stairs since the last shuffle, otherwise false
     */
    private fun isGameEnded() : Boolean {

        val game = rootService.currentGame
        checkNotNull(game) { "No game currently running."}

        return game.stairs.isEmpty() || (game.drawCardPile.isEmpty() && !game.stairsModified)
    }

    /**
     * Creates a shuffled 52 cards list of all four suits and cards from 2 to Ace
     */
    private fun defaultRandomCardList() = List(52) { index ->
        Card(
            CardSuit.values()[index / 13],
            CardValue.values()[(index % 13)]
        )
    }.shuffled()
}