package entity
import kotlin.test.*

/**
 * Test cases for [Card]
 */
class CardTest {

    // Create cards to perform the tests
    private val twoOfClubs = Card(CardSuit.CLUBS, CardValue.TWO)
    private val twoOfSpades = Card(CardSuit.SPADES, CardValue.TWO)
    private val fourOfSpades = Card(CardSuit.SPADES, CardValue.FOUR)
    private val sixOfHearts = Card(CardSuit.HEARTS, CardValue.SIX)
    private val queenOfClubs = Card(CardSuit.CLUBS, CardValue.QUEEN)
    private val aceOfDiamonds = Card(CardSuit.DIAMONDS, CardValue.ACE)

    // Unicode characters for the suits, as those should be used by [Card.toString]
    private val heartsChar = '\u2665' // ♥
    private val diamondsChar = '\u2666' // ♦
    private val spadesChar = '\u2660' // ♠
    private val clubsChar = '\u2663' // ♣

    /**
     * Function to test if the [toString] from [Card] correctly shows cards
     */
    @Test
    fun testToString(){
        assertEquals(clubsChar + "2", twoOfClubs.toString())
        assertEquals(spadesChar + "4", fourOfSpades.toString())
        assertEquals(heartsChar + "6", sixOfHearts.toString())
        assertEquals(clubsChar + "Q", queenOfClubs.toString())
        assertEquals(diamondsChar + "A", aceOfDiamonds.toString())
    }

    /**
     * Test to check [Card.checkSuitOrValue]
     */
    @Test
    fun testEqualSuitOrValue(){
        assertTrue { twoOfClubs.checkSuitOrValue(queenOfClubs) }
        assertTrue { twoOfClubs.checkSuitOrValue(twoOfSpades) }
        assertFalse { twoOfSpades.checkSuitOrValue(sixOfHearts) }
    }
}