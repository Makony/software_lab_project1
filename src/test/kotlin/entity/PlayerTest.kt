package entity
import kotlin.test.*

/**
 * Test cases for [Player]
 */
class PlayerTest {

    // Create a player
    val player = Player("player")

    /**
     * Test if a player has a name
     */
    @Test
    fun testPlayersName(){
        assertTrue(player.name.isNotBlank())
    }

    /**
     * Test whether the player's score is within the valid range.
     */
    @Test
    fun testPlayersScore(){
        assertTrue(player.score >= 0)
    }
}