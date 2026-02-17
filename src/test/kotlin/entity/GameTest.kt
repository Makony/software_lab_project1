package entity
import kotlin.test.*

/**
 * Test cases for [Game]
 */
class GameTest {

    private lateinit var game: Game
    // Create a game with initial parameters
    @BeforeTest
    fun init(){
        game = Game(mutableListOf(), mutableListOf(), mutableListOf(),0)
    }

    /**
     * Test if stairs have been created after the initialization
     */
    @Test
    fun testEmptyStairs(){
        assertTrue {game.stairs.isEmpty()}
    }

    /**
     * Test if players have been created after the initialization
     */
    @Test
    fun testEmptyPlayer(){
        assertTrue {game.players.isEmpty()}
    }

    /**
     * Test if stairs has been modified after the initialization
     */
    @Test
    fun testStairsModified(){
        assertTrue {game.stairsModified}
    }
}