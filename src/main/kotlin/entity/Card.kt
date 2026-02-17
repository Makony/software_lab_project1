package entity
/**
 * Data class to represent a single Card entity
 * It is characterized by a [CardSuit] and a [CardValue]
 *
 * @property suit the suit of the card
 * @property value the value of the card
 */
data class Card(val suit: CardSuit, val value: CardValue){

    override fun toString() = "$suit$value"

    /**
     * Function to check if suit or value of the cards are same
     * @param otherCard another card to compare its suit or value
     * @return true if suit or value is same, otherwise false
     */
    fun checkSuitOrValue(otherCard: Card): Boolean {
        return suit == otherCard.suit || value.ordinal == otherCard.value.ordinal
    }
}
