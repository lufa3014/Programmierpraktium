package logic;

import org.junit.Test;

import java.util.Arrays;

import static logic.ActionCard.*;
import static org.junit.Assert.*;

public class GameLogicTest {
    FakeGUI gui = new FakeGUI();

    int
            firstCarrot = 2,
            secondCarrot = 5,
            thirdCarrot = 13,

            firstSalad = 7,

            firstHedgehog = 8,
            secondHedgehog = 11,
            thirdHedgehog = 15,

            numberThree = 4,

            firstHare = 1,
            penultimateHare = 61,
            lastHare = 63,

            start = 0,
            end = 64;

    @Test
    public void carrotTileShouldBeAvailable() {
        Game game = new Game(gui.defaultNames, gui);
        boolean available = Board.isAccessibleTile(firstCarrot, game, game.getPlayer(0));
        assertTrue(available);
    }

    @Test
    public void carrotTileShouldNotBeAvailableDueToCarrotShortage() {
        Game game = new Game(Arrays.copyOf(gui.defaultNames, 2), gui);
        boolean available = Board.isAccessibleTile(thirdCarrot, game, game.getPlayer(0));
        assertFalse(available);
    }

    @Test
    public void carrotTileShouldBeAvailableDueToEnoughPlayers() {
        Game game = new Game(gui.defaultNames, gui);
        boolean available = Board.isAccessibleTile(thirdCarrot, game, game.getPlayer(0));
        assertTrue(available);
    }

    @Test
    public void carrotTileShouldNotBeAvailableDueToAlreadyOnIt() {
        Game game = new Game(gui.defaultNames, gui);
        Player player = game.getPlayer(0);
        player.setField(firstCarrot);
        boolean available = Board.isAccessibleTile(firstCarrot, game, player);
        assertFalse(available);
    }

    @Test
    public void carrotTileShouldNotBeAvailableDueToOccupation() {
        Game game = new Game(gui.defaultNames, gui);
        game.getPlayer(0).setField(firstCarrot);
        boolean available = Board.isAccessibleTile(firstCarrot, game, game.getPlayer(1));
        assertFalse(available);
    }

    @Test
    public void saladTileShouldBeAvailable() {
        Game game = new Game(gui.defaultNames, gui);
        boolean available = Board.isAccessibleTile(firstSalad, game, game.getPlayer(0));
        assertTrue(available);
    }

    @Test
    public void saladTileShouldNotBeAvailableDueToSaladShortage() {
        Game game = new Game(gui.defaultNames, gui);
        Player player = game.getPlayer(0);
        player.consumeAllSalads();

        boolean available = Board.isAccessibleTile(firstSalad, game, player);
        assertFalse(available);
    }

    @Test
    public void hedgehogTileShouldBeAvailable() {
        Game game = new Game(gui.defaultNames, gui);
        Player player = game.getPlayer(0);
        player.setField(firstHedgehog + 1);
        boolean available = Board.isAccessibleTile(firstHedgehog, game, player);
        assertTrue(available);
    }

    @Test
    public void hedgehogTileShouldNotBeAvailableDueToPositioning() {
        Game game = new Game(gui.defaultNames, gui);
        Player player = game.getPlayer(0);
        player.setField(firstHedgehog - 1);
        boolean available = Board.isAccessibleTile(firstHedgehog, game, player);
        assertFalse(available);
    }

    @Test
    public void hedgehogTileShouldBeAvailableDueToOnHedgehogTile() {
        Game game = new Game(gui.defaultNames, gui);
        Player player = game.getPlayer(0);

        player.setField(thirdHedgehog);

        boolean available = Board.isAccessibleTile(secondHedgehog, game, player);
        assertTrue(available);
    }

    @Test
    public void hedgehogTileShouldNotBeAvailableDueToNotClosestHedgehogTile() {
        Game game = new Game(gui.defaultNames, gui);
        Player player = game.getPlayer(0);

        player.setField(thirdHedgehog);

        boolean available = Board.isAccessibleTile(firstHedgehog, game, player);
        assertFalse(available);
    }

    @Test
    public void hedgehogTileShouldNotBeAvailableDueToOccupation() {
        Game game = new Game(gui.defaultNames, gui);
        Player player = game.getPlayer(0);

        player.setField(firstHedgehog + 1);
        game.getPlayer(1).setField(firstHedgehog);

        boolean available = Board.isAccessibleTile(firstHedgehog, game, player);
        assertFalse(available);
    }

    @Test
    public void hedgehogTileShouldBeAvailableEvenIfCarrotShortage() {
        Game game = new Game(gui.defaultNames, gui);
        Player player = game.getPlayer(0);

        player.setField(firstHedgehog + 1);
        player.removeCarrots(player.getCarrots());

        boolean available = Board.isAccessibleTile(firstHedgehog, game, player);
        assertTrue(available);
    }

    @Test
    public void startTileShouldBeAvailableDueToCarrotShortage() {
        Game game = new Game(gui.defaultNames, gui);
        Player player = game.getPlayer(0);

        // needs to be a position with no hedgehog in reach
        player.setField(firstSalad);
        player.removeCarrots(player.getCarrots());

        // should move the player to the start tile
        Board.FIELDS[player.getField()].getAction().execute(gui, game, player, Game.NO_OP);

        boolean available = player.getField() == Board.START_FIELD;
        assertTrue(available);
    }

    @Test
    public void startTileShouldNotBeAvailableDueToCarrotExcess() {
        Game game = new Game(gui.defaultNames, gui);
        Player player = game.getPlayer(0);

        player.setField(firstSalad);

        boolean available = Board.isAccessibleTile(start, game, player);
        assertFalse(available);
    }

    @Test
    public void startTileShouldNotBeAvailableDueToHedgehogTileInReach() {
        Game game = new Game(gui.defaultNames, gui);
        Player player = game.getPlayer(0);

        player.setField(firstHedgehog + 1);
        player.removeCarrots(player.getCarrots());

        boolean available = Board.isAccessibleTile(start, game, player);
        assertFalse(available);
    }

    @Test
    public void startTileShouldNotBeAvailableDueToOnCarrotTile() {
        Game game = new Game(gui.defaultNames, gui);
        Player player = game.getPlayer(0);

        player.setField(firstCarrot);
        player.removeCarrots(player.getCarrots());

        boolean available = Board.isAccessibleTile(start, game, player);
        assertFalse(available);
    }

    @Test
    public void endTileShouldBeAvailable() {
        Game game = new Game(gui.defaultNames, gui);
        Player player = game.getPlayer(0);

        player.setField(end - 1);
        player.consumeAllSalads();
        player.removeCarrots(player.getCarrots() - game.getMaxAmountOfCarrotsToEnd());

        boolean available = Board.isAccessibleTile(end, game, player);
        assertTrue(available);
    }

    @Test
    public void endTileShouldNotBeAvailableDueToCarrotExcess() {
        Game game = new Game(gui.defaultNames, gui);
        Player player = game.getPlayer(0);

        player.setField(end - 1);
        player.consumeAllSalads();
        player.removeCarrots(player.getCarrots() - game.getMaxAmountOfCarrotsToEnd() - 2);

        boolean available = Board.isAccessibleTile(end, game, player);
        assertFalse(available);
    }

    @Test
    public void endTileShouldBeAvailableDueToSecond() {
        Game game = new Game(gui.defaultNames, new int[] { 1 }, gui);
        Player player = game.getPlayer(0);

        player.setField(end - 1);
        player.consumeAllSalads();
        player.removeCarrots(player.getCarrots() - game.getMaxAmountOfCarrotsToEnd());

        boolean available = Board.isAccessibleTile(end, game, player);
        assertTrue(available);
    }

    @Test
    public void endTileShouldNotBeAvailableDueToSalad() {
        Game game = new Game(gui.defaultNames, gui);
        Player player = game.getPlayer(0);

        player.setField(end - 1);

        int carrotsToRemove = player.getCarrots() - game.getMaxAmountOfCarrotsToEnd();
        player.removeCarrots(carrotsToRemove);

        boolean available = Board.isAccessibleTile(end, game, player);
        assertFalse(available);
    }

    @Test
    public void numberTileShouldBeAvailable() {
        Game game = new Game(gui.defaultNames, gui);
        Player player = game.getPlayer(0);

        boolean available = Board.isAccessibleTile(numberThree, game, player);
        assertTrue(available);
    }

    @Test
    public void numberTileShouldAddCarrots() {
        Game game = new Game(gui.defaultNames, gui);
        game.getPlayer(1).setField(numberThree + 1);
        game.getPlayer(2).setField(numberThree + 2);

        Player player = game.getPlayer(0);
        int carrotsBefore = player.getCarrots();

        player.setField(numberThree);
        Board.FIELDS[player.getField()].getAction().execute(gui, game, player, Game.NO_OP);

        assertEquals(carrotsBefore + Game.CARROT_EXCHANGE_AMOUNT * 3, player.getCarrots());
    }

    @Test
    public void numberTileShouldNotAddCarrots() {
        Game game = new Game(gui.defaultNames, gui);

        Player player = game.getPlayer(0);
        int carrotsBefore = player.getCarrots();

        player.setField(numberThree);
        Board.FIELDS[player.getField()].getAction().execute(gui, game, player, Game.NO_OP);

        assertEquals(carrotsBefore, player.getCarrots());
    }

    @Test
    public void hareTileShouldDrawCard() {
        Game game = new Game(gui.defaultNames, gui);
        Player player = game.getPlayer(0);
        ActionCardStack cardsBefore = game.getCards().copy();

        player.setField(firstHare);
        Board.FIELDS[player.getField()].getEntryAction(0).execute(gui, game, player, Game.NO_OP);

        assertNotEquals(cardsBefore, game.getCards());
    }

    @Test
    public void hareTileShouldDrawMultipleCards() {
        // We set up the game so that the player will move from one hare tile
        // to another hare tile by drawing a card.
        ActionCard[] cards = new ActionCard[] {
                FreeLastMove,
                MoveToLastCarrotField,
                MoveToNextCarrotField,
                MoveUpRank, // this card will be drawn
        };
        Game game = new Game(gui.defaultNames, cards, gui);
        game.getPlayer(1).setField(firstHare + 1);

        Player player = game.getPlayer(0);
        player.setField(firstHare);

        ActionCardStack cardsBefore = game.getCards().copy();
        Board.FIELDS[player.getField()].getEntryAction(0).execute(gui, game, player, Game.NO_OP);
        ActionCardStack cardsAfter = game.getCards().copy();


        // expected two cards to be drawn
        assertNotEquals(cardsBefore, cardsAfter);
        cardsBefore.draw();
        cardsBefore.draw();
        assertEquals(cardsBefore, cardsAfter);
    }

    @Test
    public void MoveUpRankShouldNotMovePlayerToEndDueToCarrotExcess() {
        Game game = new Game(gui.defaultNames, gui);
        game.getPlayer(1).setField(end - 1);
        Player player = game.getPlayer(0);
        player.consumeAllSalads();
        player.setField(penultimateHare);
        int oldPosition = player.getField();

        MoveUpRank.execute(gui, game, player);

        // expected player to not move
        assertEquals(oldPosition, player.getField());
    }

    @Test
    public void MoveUpRankShouldNotMovePlayerToEndDueToSaladExcess() {
        Game game = new Game(gui.defaultNames, gui);
        game.getPlayer(1).setField(end - 1);
        Player player = game.getPlayer(0);
        player.removeCarrots(player.getCarrots() - game.getMaxAmountOfCarrotsToEnd());
        player.setField(penultimateHare);
        int oldPosition = player.getField();

        MoveUpRank.execute(gui, game, player);

        // expected player to not move
        assertEquals(oldPosition, player.getField());
    }

    @Test
    public void MoveUpRankShouldMovePlayerToEnd() {
        Game game = new Game(gui.defaultNames, gui);
        game.getPlayer(1).setField(end - 1);
        Player player = game.getPlayer(0);
        player.removeCarrots(player.getCarrots() - game.getMaxAmountOfCarrotsToEnd());
        player.consumeAllSalads();
        player.setField(penultimateHare);

        MoveUpRank.execute(gui, game, player);

        // expected player to move to the end tile
        assertEquals(end, player.getField());
    }

    @Test
    public void MoveUpRankShouldMovePlayerOverMultiplePlayer() {
        Game game = new Game(gui.defaultNames, gui);
        game.getPlayer(1).setField(firstHare + 1);
        game.getPlayer(2).setField(firstHare + 2);
        Player player = game.getPlayer(0);
        player.setField(firstHare);

        MoveUpRank.execute(gui, game, player);

        assertEquals(firstHare + 3, player.getField());
    }

    @Test
    public void MoveUpRankShouldMovePlayerOverSaladAndHedgehogTile() {
        Game game = new Game(gui.defaultNames, gui);
        game.getPlayer(1).setField(firstSalad - 1);
        Player player = game.getPlayer(0);
        player.consumeAllSalads();
        player.setField(firstHare);


        MoveUpRank.execute(gui, game, player);

        // Assuming after the salad tile is a hedgehog tile
        assertEquals(firstSalad + 2, player.getField());
    }

    @Test
    public void FallBackRankShouldMovePlayerToStart() {
        Game game = new Game(gui.defaultNames, gui);
        game.getPlayer(1).setField(start + 1);
        Player player = game.getPlayer(0);
        player.setField(penultimateHare);

        FallBackRank.execute(gui, game, player);

        assertEquals(start, player.getField());
    }

    @Test
    public void FallBackRankShouldMovePlayerOverMultiplePlayer() {
        Game game = new Game(gui.defaultNames, gui);
        game.getPlayer(1).setField(start + 1);
        game.getPlayer(2).setField(start + 2);
        Player player = game.getPlayer(0);
        player.setField(penultimateHare);

        FallBackRank.execute(gui, game, player);

        assertEquals(start, player.getField());
    }

    @Test
    public void FallBackRankShouldMovePlayerOverSaladTileAndSecondDrawToStart() {
        ActionCard[] cards = new ActionCard[]{
                FallBackRank
        };
        Game game = new Game(gui.defaultNames, cards, gui);
        game.getPlayer(1).setField(firstHedgehog);
        Player player = game.getPlayer(0);
        player.consumeAllSalads();
        player.setField(penultimateHare);

        ActionBuilder.createDrawActionCard(player.getField()).execute(gui, game, player, Game.NO_OP);

        // Assuming after the hedgehog tile is a salad tile
        // We expect the player to move to the salad tile and draw another fallBackRank card
        // and move to the start tile, because the other players are on the start tile
        assertEquals(start, player.getField());
    }

    @Test
    public void FallBackRankShouldMovePlayerOnHedgehogNotExchangingCarrots() {
        Game game = new Game(gui.defaultNames, gui);
        game.getPlayer(1).setField(firstHedgehog + 1);
        Player player = game.getPlayer(0);
        int oldCarrots = player.getCarrots();
        player.consumeAllSalads();
        player.setField(penultimateHare);

        FallBackRank.execute(gui, game, player);

        assertEquals(firstHedgehog, player.getField());
        assertEquals(oldCarrots, player.getCarrots());
    }

    @Test
    public void GetSuspendedShouldSuspendPlayer() {
        Game game = new Game(gui.defaultNames, gui);
        Player player = game.getPlayer(0);
        player.setField(firstHare);

        GetSuspended.execute(gui, game, player);

        assertTrue(player.isSuspended());
    }

    @Test
    public void ConsumeSaladShouldSetIsEatingSalad() {
        Game game = new Game(gui.defaultNames, gui);
        Player player = game.getPlayer(0);
        player.setField(firstHare);

        ConsumeSalad.execute(gui, game, player);

        assertTrue(player.isEatingSalad());
    }

    @Test
    public void MoveToNextCarrotFieldShouldMoveToNextCarrotField() {
        Game game = new Game(gui.defaultNames, gui);
        Player player = game.getPlayer(0);
        player.setField(firstHare);

        MoveToNextCarrotField.execute(gui, game, player);

        assertEquals(firstCarrot, player.getField());
    }

    @Test
    public void MoveToNextCarrotFieldShouldMoveOverCarrotFieldDueToOccupation() {
        Game game = new Game(gui.defaultNames, gui);
        game.getPlayer(1).setField(firstCarrot);
        Player player = game.getPlayer(0);
        player.setField(firstHare);

        MoveToNextCarrotField.execute(gui, game, player);

        assertEquals(secondCarrot, player.getField());
    }

    @Test
    public void MoveToNextCarrotFieldShouldNotMoveDueToNoValidField() {
        Game game = new Game(gui.defaultNames, gui);
        Player player = game.getPlayer(0);
        player.setField(lastHare);

        MoveToNextCarrotField.execute(gui, game, player);

        assertEquals(lastHare, player.getField());
    }

    @Test
    public void MoveToLastCarrotFieldShouldNotMoveDueToNoValidField() {
        Game game = new Game(gui.defaultNames, gui);
        Player player = game.getPlayer(0);
        player.setField(firstHare);

        MoveToLastCarrotField.execute(gui, game, player);

        assertEquals(firstHare, player.getField());
    }

    @Test
    public void MoveToLastCarrotFieldShouldMoveOverCarrotFieldDueToOccupation() {
        Game game = new Game(gui.defaultNames, gui);
        game.getPlayer(1).setField(secondCarrot);
        Player player = game.getPlayer(0);
        player.setField(6);

        MoveToLastCarrotField.execute(gui, game, player);

        assertEquals(firstCarrot, player.getField());
    }

    @Test
    public void MoveToLastCarrotFieldShouldMoveToCarrotField() {
        Game game = new Game(gui.defaultNames, gui);
        Player player = game.getPlayer(0);
        player.setField(3);

        MoveToLastCarrotField.execute(gui, game, player);

        assertEquals(firstCarrot, player.getField());
    }

    @Test
    public void FreeLastMoveShouldGivePlayerCarrotsFromLastMoveBack() {
        Game game = new Game(gui.defaultNames, gui);
        Player player = game.getPlayer(0);
        int oldField = player.getField();
        int oldCarrots = player.getCarrots();
        player.removeCarrots(game.calcMovementCost(player.getField(), 3));
        player.setField(3);

        FreeLastMove.execute(gui, game, player, oldField);

        assertEquals(oldCarrots, player.getCarrots());
    }

    @Test
    public void MoveActionShouldTriggerEntryAction() {
        ActionCard[] cards = new ActionCard[]{
                GetSuspended
        };
        Game game = new Game(gui.defaultNames, cards, gui);
        Player player = game.getPlayer(0);
        player.setSuspended(false); // just to be sure

        ActionBuilder.createMove(firstHare, true).execute(gui, game, player, Game.NO_OP);

        assertTrue(player.isSuspended());
    }

    @Test
    public void CalculateMovementCostShouldReturnZero() {
        Game game = new Game(gui.defaultNames, gui);
        int cost = game.calcMovementCost(firstHare, 0);
        assertEquals(0, cost);
    }

    @Test
    public void CalculateMovementCostForLongDistance() {
        Game game = new Game(gui.defaultNames, gui);
        int cost = game.calcMovementCost(0, end - 1);

        int expectedCost = 2016;
        assertEquals(expectedCost, cost);
    }

    @Test
    public void CalculateMaxDistanceShouldReturnZero() {
        Game game = new Game(gui.defaultNames, gui);
        int distance = game.calcMaxReachablePosition(0);
        assertEquals(0, distance);
    }

    @Test
    public void CalculateMaxDistanceForLongDistance() {
        Game game = new Game(gui.defaultNames, gui);
        int distance = game.calcMaxReachablePosition(2016);

        int expectedDistance = 63;
        assertEquals(expectedDistance, distance);
    }

    @Test
    public void GetRankShouldReturnTwo() {
        Game game = new Game(gui.defaultNames, gui);
        game.getPlayer(1).setField(end - 1);
        int rank = game.getRank(game.getPlayer(0));

        assertEquals(2, rank);
    }

    @Test
    public void GetRankShouldReturnOne() {
        Game game = new Game(gui.defaultNames, gui);
        game.getPlayer(0).setField(end - 1);
        int rank = game.getRank(game.getPlayer(0));

        assertEquals(1, rank);
    }

}



