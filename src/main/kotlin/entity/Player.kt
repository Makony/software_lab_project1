package entity

/**
 * Data class to represent a single Player in the game
 * @property name name of the player
 * @property score player's score
 * @property lastAction player's last action in game
 * @property hand cards in player's hand
 * @property cardsPlayed player's collected cards
 */
data class Player(var name: String ){

    var score: Int = 0
    var lastAction: PlayerAction? = null
    val hand: MutableList<Card> = mutableListOf()
    val cardsPlayed: MutableList<Card> = mutableListOf()
}
