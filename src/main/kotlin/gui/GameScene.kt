package gui

import service.*
import entity.*
import tools.aqua.bgw.animation.*
import tools.aqua.bgw.visual.*
import tools.aqua.bgw.components.container.*
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.components.layoutviews.Pane
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.core.*
import tools.aqua.bgw.util.*

/**
 * This is the main scene for the game. The scene shows the complete table at once.
 * Player "sits" is on the bottom half of the screen. Scene includes stairs of cards, draw and discard stacks, logs
 * and quit button. Player can drag and drop cards, also click on open cards in the staircase.
 *
 * @param rootService The [RootService] instance to access the other service methods and entity layer
 * @property quitButton button to exit the program
 */
class GameScene(private val rootService: RootService) : BoardGameScene(1920, 1080,
    background = ImageVisual("background.jpg")), Refreshable {

    val quitButton = Button(
        width = 120, height = 45,
        posX = 1800, posY = 0,
        text = "Quit",
        font = Font(20, Color(0xFFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(Color(0x0C2027))
    )
    private val drawStack = CardStack<CardView>(
        posX = 100,
        posY = 520,
        width = 112,
        height = 200
    )

    private val drawLabel = Label(
        posX = 50,
        posY = 470,
        width = 200,
        height = 50,
        text = "Draw Stack",
        font = Font(22, Color(0xFFFFFFF), "JetBrains Mono ExtraBold")
    )

    private val discardStack = CardStack<CardView>(
        posX = 100,
        posY = 200,
        width = 112,
        height = 200,
    )

    private val discardDropArea = Area<CardView>(
        posX = 100,
        posY = 200,
        width = 112,
        height = 200
    ).apply {

        dropAcceptor = { dragEvent ->
            when (dragEvent.draggedComponent) {
                is CardView -> {
                   true
                }
                else -> false
            }
        }
        onDragDropped = { event ->
            val handCardView = event.draggedComponent as CardView
            val card = cardMap.backward(handCardView)
            rootService.playerService.discardCard(card)
        }
    }

    private val discardLabel = Label(
        posX = 50,
        posY = 150,
        width = 200,
        height = 50,
        text = "Discard Stack",
        font = Font(22, Color(0xFFFFFFF), "JetBrains Mono ExtraBold")
    )

    private val playerHand = LinearLayout<CardView>(
        posX = 0,
        posY = 1080 - 275,
        width = 1920,
        height = 200,
        alignment = Alignment.CENTER,
        spacing = -10
    )

    private val playerName = Label(
        posX = 1920 - 250,
        posY = 1080 - 100,
        width = 200,
        height = 50,
        text = "Spieler",
        alignment = Alignment.CENTER,
        visual = ColorVisual(Color(0x0C2027)),
        font = Font(22, Color(0xFFFFFFF), "JetBrains Mono ExtraBold")
    )

    private val score = Label(
        posX = 250,
        posY = 1080 - 100,
        width = 200,
        height = 50,
        text = "Score",
        alignment = Alignment.CENTER,
        visual = ColorVisual(Color(0x0C2027)),
        font = Font(22, Color(0xFFFFFFF), "JetBrains Mono ExtraBold")
    )

    private val log = TextArea(
        posX = 1300,
        posY = 90,
        width = 700,
        height = 400,
        font = Font(30, Color(0xFFFFFFF), "JetBrains Mono ExtraBold"),
        visual = ColorVisual(0, 0, 0, 0)
    ).apply { isReadonly = true }

    private val stairs = Pane<Area<CardView>>(
        posX = 500,
        posY = 50,
        width = 0,
        height = 0
    )

    private val cardMap: BidirectionalMap<Card, CardView> = BidirectionalMap()

    init {
        addComponents( playerHand, playerName, drawStack, discardStack, quitButton,
            score, drawLabel, discardLabel, discardDropArea, log, stairs)
    }

    // refreshes the gui after game has started
    override fun refreshAfterStartNewGame() {
        val game = rootService.currentGame ?: return

        cardMap.clear()
        CardValue.entries.forEach { value ->
            CardSuit.entries.forEach { suit ->
                cardMap[Card(suit, value)] = CardView(
                    posX = 0,
                    posY = 0,
                    width = 112,
                    height = 200,
                    front = CardImageLoader().frontImageFor(suit, value),
                    back = CardImageLoader().backImage
                )
            }
        }
        buildStairsView()

        drawStack.clear()
        game.drawCardPile.reversed().forEach { card ->
            drawStack.push(cardMap[card])
        }

        log.text = ""
        discardStack.clear()
    }

    // refreshes the gui after turn has started
    override fun refreshAfterStartTurn() {
        val game = rootService.currentGame ?: return

        playerName.text = game.players[game.currentPlayer].name
        score.text = "Score: " + game.players[game.currentPlayer].score.toString()

        buildHandView()
    }

    // refreshes the gui after two cards were combined
    override fun refreshAfterPlayCard(target: Card, source: Card) {
        val game = rootService.currentGame ?: return

        val handCardView = cardMap[source].apply {
            posX = 0.0
            posY = 0.0
            removeHoverEffect(this)
        }
        val targetCardView = cardMap[target].apply {
            posX = 0.0
            posY = 0.0
            removeHoverEffect(this)
        }

        playerHand.remove(handCardView)

        val area = stairs.first { it.contains(targetCardView) }
        area.remove(targetCardView)
        stairs.remove(area)
        score.text = "Score: " + game.players[game.currentPlayer].score.toString()
    }

    // refreshes the gui after card was revealed
    override fun refreshAfterRevealCard(card: Card) {

         val cardView = cardMap[card].apply {
             applyHoverEffect(this)
             isDraggable = false
         }
        applyDropArea(cardView)
    }

    // refreshes the gui after card was drawn
    override fun refreshAfterDrawCard(card: Card) {

        val cardView = cardMap[card]

        playAnimation(
            MovementAnimation(
                componentView = cardView,
                fromX = 100,
                fromY = 520,
                toX = 1050,
                toY = 805,
                duration = 1500,
                interpolation = AnimationInterpolation.SMOOTH
            ).apply {
                onFinished = {
                    drawStack.remove(cardView)
                    playerHand.add(cardView)
                    applyHoverEffect(cardView)
                    SopraApplication.showMenuScene(SopraApplication.nextTurnScene)
                }
            }
        )
    }

    // refreshes the gui after card was discarded
    override fun refreshAfterDiscardCard(card: Card) {

        val cardView = (cardMap[card])
        playerHand.remove(cardView)
        removeHoverEffect(cardView)
        discardStack.push(cardView)
    }


    // refreshes the gui after card was destroyed
    override fun refreshAfterDestroyCard(target: Card) {
        val game = rootService.currentGame ?: return

        val cardView = cardMap[target]

        playAnimation(
            FadeAnimation(
                componentView = cardView,
                fromOpacity = cardView.opacity,
                toOpacity = 0,
                duration = 600,
                interpolation = AnimationInterpolation.SMOOTH
            ).apply {
                onFinished = {

                    cardView.apply {
                        posX = 0.0
                        posY = 0.0
                        removeHoverEffect(this)
                    }
                    discardStack.push(cardView)

                    val area = stairs.first { it.contains(cardView) }
                    area.remove(cardView)
                    stairs.remove(area)
                }
            }
        )
        score.text = "Score: " + game.players[game.currentPlayer].score.toString()
    }

    // shows logs on the screen
    override fun refreshAfterLog(message: String) {
        log.text = message
    }

    // refreshes after the draw stack is empty
    override fun refreshAfterShuffleStack() {
        val game = rootService.currentGame ?: return
        drawStack.clear()
        game.drawCardPile.reversed().forEach { card ->
            drawStack.push(cardMap[card])
        }
        discardStack.clear()
    }

    /**
     * Method to build the view for cards in stairs
     */
    private fun buildStairsView(){
        val game = rootService.currentGame ?: return

        stairs.forEach { area -> area.clear() }
        stairs.clear()

        game.stairs.forEachIndexed { stackIndex, stack ->

            stack.forEachIndexed { cardIndex, card ->
                val cardView = cardMap[card]

                val area = Area<CardView>(
                    posX = stackIndex * 180.toDouble(),
                    posY = (4 - cardIndex) * 120.toDouble(),
                    width = cardView.width,
                    height = cardView.height
                )

                area.add(cardView)
                stairs.add(area)

            }.apply {
                val cardView = cardMap[stack.peek()].apply { applyHoverEffect(this) }
                cardView.isDraggable = false
                applyDropArea(cardView)
            }
        }
    }

    /**
     * Method to add drop functionalities to the drop areas of the cards
     */
    private fun applyDropArea(cardView: CardView){

        val area = stairs.first { it.contains(cardView) }

        area.apply {

            dropAcceptor = { dragEvent ->
                when (dragEvent.draggedComponent) {

                    is CardView -> {
                        val card = cardMap.backward(dragEvent.draggedComponent as CardView)
                        val target = cardMap.backward(cardView)
                        card.checkSuitOrValue(target)
                    }
                    else -> false
                }
            }
            onDragDropped = { event ->

                val handCardView = event.draggedComponent as CardView
                val card = cardMap.backward(handCardView)
                val target = cardMap.backward(cardView)
                rootService.playerService.playCard(card, target)
            }
            onMouseClicked = {

                val target = cardMap.backward(cardView)
                rootService.playerService.destroyCard(target)
            }
        }
    }

    /**
     * Method to build the view for cards in player's hand
     */
    private fun buildHandView(){
        val game = rootService.currentGame ?: return
        playerHand.clear()

        // Add all cards of the current player's hand to the playerHand
        game.players[game.currentPlayer].hand.forEach { card ->
            playerHand.add(
                (cardMap[card]).apply {
                    applyHoverEffect(this)
                }
            )
        }
    }

    /**
     *   The applyHoverEffect method is used to apply a hover effect to a CardView.
     *   It moves the CardView up by 25 pixels when the mouse enters and moves it back down when the mouse exits.
     *
     *   @param cardView The CardView to apply the hover effect and properties to
     */
    private fun applyHoverEffect(cardView: CardView) {
        val posYBefore = cardView.posY

        cardView.onMouseEntered = {
            cardView.posY = -15.0 + posYBefore
        }
        cardView.onMouseExited = {
            cardView.posY = posYBefore
        }
        cardView.width = 112.0
        cardView.height = 200.0
        cardView.showFront()
        cardView.isDraggable = true
    }

    /**
     *   The removeHoverEffect method is used to remove the hover effect from a CardView.
     *   It resets the CardView to its original position and size and removes all event handlers.
     *
     *   @param cardView The CardView to remove the hover effect and properties from
     */
    private fun removeHoverEffect(cardView: CardView) {
        cardView.width = 112.0
        cardView.height = 200.0
        cardView.showBack()
        cardView.isDraggable = false
    }
}