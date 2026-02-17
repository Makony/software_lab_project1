package gui

import tools.aqua.bgw.core.MenuScene
import service.*
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.core.Color
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.*

/**
 * [MenuScene] that is displayed after MainMenuScene. It takes players inputs as their names.
 * Also, there is a button to save the names and start the game in the background.
 *
 * @property player1Input input field to save player's name
 * @property player2Input input field to save player's name
 * @property saveButton button to save players names and start the game
 */
class PlayerNameScene : MenuScene(1920, 1080,
    background = ImageVisual("background.jpg")), Refreshable {

    val player1Input: TextField = TextField(
        width = 500, height = 70,
        posX = 560, posY = 300,
        prompt = "Player 1"
    ).apply {
        onKeyPressed = {
            saveButton.isDisabled = player2Input.text.isBlank() || this.text.isBlank()
        }
    }

    val player2Input: TextField = TextField(
        width = 500, height = 70,
        posX = 560, posY = 460,
        prompt = "Player 2"
    ).apply {
        onKeyPressed = {
            saveButton.isDisabled = player1Input.text.isBlank() || this.text.isBlank()
        }
    }

    val saveButton = Button(
        width = 300, height = 100,
        posX = 560, posY = 600,
        text = "Save",
        font = Font(26, Color(0xFFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x49585D)),
    )

    init {
        opacity = .5
        addComponents(player1Input, player2Input, saveButton)
    }
}