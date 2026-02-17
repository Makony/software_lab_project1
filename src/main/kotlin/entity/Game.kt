package entity
import java.util.Stack

/**
 * Class that represents a game state of "Staircase Card Game"
 * @property stairs represents the staircase of cards
 * @property drawCardPile pile of cards to draw a new card
 * @property currentPlayer current player on turn
 * @property gameLog holds actions of the players
 * @property stairsModified boolean to check if the staircase of cards has been changed
 * @property players stores two player objects who are playing the game
 * @property discardCardPile pile for discarded cards
 */

class Game(
    val stairs: MutableList<Stack<Card>>,
    var drawCardPile: MutableList<Card>,
    var players: List<Player>,
    var currentPlayer: Int
) {
    var gameLog: MutableList<String> = mutableListOf()
    var stairsModified: Boolean = true
    val discardCardPile: MutableList<Card> = mutableListOf()
}