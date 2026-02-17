package gui

import tools.aqua.bgw.core.*
import service.*
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.*

/**
 * [MenuScene] that is displayed when the game is started.
 * Also, there are two buttons: one for starting a game and one for quitting the program.
 *
 * @property quitButton button to exit program
 * @property startButton button to start the game
 */
class MainMenuScene : MenuScene(1920, 1080,
    background = ImageVisual("background.jpg") ), Refreshable  {

    private val headlineLabel = Label(
        width = 800,
        height = 120,
        posX = 560,
        posY = 340,
        text = "Card Game",
        font = Font(size = 90, Color(0xffffff), "JetBrains Mono ExtraBold")
    )

    val quitButton = Button(
        width = 200, height = 100,
        posX = 600, posY = 600,
        text = "Quit",
        font = Font(26, Color(0xFFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x49585D)),
    )

    val startButton = Button(
        width = 200, height = 100,
        posX = 1100, posY = 600,
        text = "Start",
        font = Font(26, Color(0xFFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x49585D)),
    )

    init {
        opacity = .5
        addComponents(headlineLabel, startButton, quitButton)
    }
}