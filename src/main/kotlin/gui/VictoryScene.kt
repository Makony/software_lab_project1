package gui

import tools.aqua.bgw.core.*
import service.*
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.util.*
import tools.aqua.bgw.visual.*
import entity.*
import tools.aqua.bgw.components.container.LinearLayout
import tools.aqua.bgw.components.gamecomponentviews.CardView

/**
 * Scene to display results after the game ends. It shows the winner, their score and collected cards.
 * Also, it has two buttons: one to exit the game and one to restart the game.
 *
 * @param rootService The [RootService] instance to access the other service methods and entity layer
 * @property quitButton button to exit the program
 * @property restartButton button to play again
 */
class VictoryScene(private val rootService: RootService) : BoardGameScene(1920, 1080,
    background = ImageVisual("background.jpg")), Refreshable {

    private val headlineLabel = Label(
        width = 800,
        height = 120,
        posX = 560,
        posY = 90,
        text = "Game Over",
        font = Font(size = 70, Color(0xffffff), "JetBrains Mono ExtraBold")
    )

    private val winnerLabel = Label(
        width = 800,
        height = 120,
        posX = 560,
        posY = 200,
        text = "Winner",
        font = Font(size = 50, Color(0xffffff), "JetBrains Mono ExtraBold")
    )

    val quitButton = Button(
        width = 200, height = 90,
        posX = 600, posY = 850,
        text = "Quit",
        font = Font(26, Color(0xFFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x49585D)),
    )

    val restartButton = Button(
        width = 200, height = 90,
        posX = 1100, posY = 850,
        text = "Restart",
        font = Font(26, Color(0xFFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x49585D)),
    )

    private val player1Score = Label(
        width = 300, height = 70,
        posX = 340, posY = 420,
        font = Font(26, Color(0xFFFFFFF), "JetBrains Mono ExtraBold"),
    )

    private val player2Score = Label(
        width = 300, height = 70,
        posX = 340, posY = 620,
        font = Font(26, Color(0xFFFFFFF), "JetBrains Mono ExtraBold")
    )

    private val player1Hand = LinearLayout<CardView>(
        posX = 560,
        posY = 400,
        width = 800,
        height = 100,
        spacing = -10,
        alignment = Alignment.CENTER
    )

    private val player2Hand = LinearLayout<CardView>(
        posX = 560,
        posY = 600,
        width = 800,
        height = 100,
        spacing = -10,
        alignment = Alignment.CENTER
    )

    private val cardMap: BidirectionalMap<Card, CardView> = BidirectionalMap()

    init {
        opacity = .5
        addComponents(headlineLabel, player1Score, player2Score, restartButton,
            quitButton, winnerLabel, player1Hand, player2Hand)
    }

    // Display the winner, player names, score and collected cards
    override fun refreshAfterEndGame(winner: Player?) {
        val game = rootService.currentGame ?: return

        cardMap.clear()
        CardValue.entries.forEach { value ->
            CardSuit.entries.forEach { suit ->
                cardMap[Card(suit, value)] = CardView(
                    posX = 0,
                    posY = 0,
                    width = 70,
                    height = 100,
                    front = CardImageLoader().frontImageFor(suit, value),
                    back = CardImageLoader().backImage
                ).apply { showFront() }
            }
        }

        player1Score.text = game.players[0].name + "'s score: " + game.players[0].score.toString()
        player2Score.text = game.players[1].name + "'s score: " + game.players[1].score.toString()

        if(winner?.name != null){
            winnerLabel.text = winner.name + " has won the game"
        }else{
            winnerLabel.text = "It's a tie"
        }

        player1Hand.clear()
        game.players[0].cardsPlayed.forEach { card ->
            player1Hand.add(cardMap[card])
        }

        player2Hand.clear()
        game.players[1].cardsPlayed.forEach { card ->
            player2Hand.add(cardMap[card])
        }

        SopraApplication.showGameScene(SopraApplication.victoryScene)
    }
}