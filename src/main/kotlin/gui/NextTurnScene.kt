package gui

import tools.aqua.bgw.core.*
import service.*
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.*

import tools.aqua.bgw.visual.ImageVisual
/**
 * [MenuScene] that is displayed when the game has started. It shows who's turn it is and has a Ready button.
 *
 * @param rootService [RootService] instance to access the service methods and entity layer
 * @property readyButton button to access the game scene
 */
class NextTurnScene(private val rootService: RootService) : MenuScene(1920, 1080,
    background = ImageVisual("background.jpg")), Refreshable {

    private val nextPlayerLabel = Label(
        width = 800,
        height = 120,
        posX = 560,
        posY = 340,
        text = "Next Player",
        font = Font(size = 90, Color(0xffffff), "JetBrains Mono ExtraBold")
    )

    val readyButton = Button(
        width = 300, height = 100,
        posX = 790, posY = 520,
        text = "Ready",
        font = Font(26, Color(0xFFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x49585D)),
    )

    private val rules = TextArea(
        text = "TO COMBINE: Drag a card from hand and drop it to a card in the stairs\n\n" +
                "TO DISCARD: Drag a card from hand to the discard stack\n\n" +
                "TO DESTROY: Click on a card in the stairs",
        posX = 600,
        posY = 800,
        width = 1000,
        height = 400,
        font = Font(25, Color(255, 255, 255, 100),
            "JetBrains Mono ExtraBold"),
        visual = ColorVisual(0, 0, 0, 0)
    ).apply { isReadonly = true }

    init {
        opacity = .5
        addComponents(nextPlayerLabel, readyButton, rules)
    }

    override fun refreshAfterStartNewGame() {
        val game = rootService.currentGame
        checkNotNull(game)
        nextPlayerLabel.text = game.players[game.currentPlayer + 1].name + "'s turn"
    }

    override fun refreshAfterEndTurn() {
        val game = rootService.currentGame
        checkNotNull(game)
        nextPlayerLabel.text = game.players[(game.currentPlayer + 1) % 2].name + "'s turn"
    }
}