package gui

import service.*
import tools.aqua.bgw.core.BoardGameApplication

/**
 * Represents the main application for the SoPra board game.
 * The application initializes the [RootService] and displays the scenes.
 *
 * @property nextTurnScene scene to display everytime when turns change
 * @property victoryScene scene to display after the game has ended
 * @property rootService [RootService] instance to access the service methods and entity layer
 */
object SopraApplication : BoardGameApplication("Staircase Card Game"), Refreshable {

    /**
     * The root service instance. This is used to call service methods and access the entity layer.
     */
    val rootService: RootService = RootService()

    /**
     * The scenes displayed in the application.
     */
    private val mainMenuScene = MainMenuScene().apply {
        startButton.onMouseClicked = {
            playerNameScene.player1Input.text = ""
            playerNameScene.player2Input.text = ""
            showMenuScene(playerNameScene)
        }

        quitButton.onMouseClicked = {
            exit()
        }
    }
    private val gameScene = GameScene(rootService).apply {
        quitButton.onMouseClicked = {
            exit()
        }
    }
    val nextTurnScene = NextTurnScene(rootService).apply {

        readyButton.onMouseClicked = {
            rootService.playerService.startTurn()
            showGameScene(gameScene)
            hideMenuScene()
        }
    }
    private val playerNameScene = PlayerNameScene().apply {
        saveButton.onMouseClicked = {

            rootService.gameService.startNewGame(player1Input.text.trim(), player2Input.text.trim())
            showMenuScene(nextTurnScene)
        }
    }

    val victoryScene = VictoryScene(rootService).apply {
        quitButton.onMouseClicked = {
            exit()
        }

        restartButton.onMouseClicked = {
            this@SopraApplication.showMenuScene(mainMenuScene)
        }
    }

    init {
        rootService.addRefreshables(
            this,
            mainMenuScene,
            playerNameScene,
            nextTurnScene,
            gameScene,
            victoryScene
        )

        this.showMenuScene(mainMenuScene)
    }
}

