package logic;

import logic.Logging.LogMessage;
import logic.Tile.TileRegistry;

/**
 * The `ActionBuilder` class provides static methods to create {@link Action}s.
 * Here an action is a function that takes a GUI, a game context, a player and a callback function as parameters.<br>
 * An Action's purpose is to change the state of the given player, based on the given game context (and player input)
 * and instruct the GUI to update its visuals accordingly.<br>
 * <br>
 * This acts as the core of implementing and joining together all the different mechanics/rules of the game.
 * Therefore, a bunch of logging is done here.<br>
 * <br>
 * Details on how the game and it's rules work can be found in the Manual.
 * @see Action
 *
 * @author Lukas Frahm
 */
public class ActionBuilder {

    /**
     * Creates an action that draws a card and executes the action of the drawn card.
     * @param from The field the player has moved from.
     * @return The created action.
     */
    public static Action createDrawActionCard(int from) {
        return (gui, context, player, onComplete) -> {
            ActionCard card = context.drawCard();
            LogMessage.drawsCard(player.getName(), card);

            Action action = convertActionCard(card, from);
            action.execute(gui, context, player, onComplete);
        };
    }

    /**
     * Creates an action that adds or removes carrots from the player.
     * @param amount The amount of carrots to add or remove.
     * @param isAddition Whether the carrots are added or removed.
     * @return The created action.
     */
    public static Action createCarrotExchange(int amount, boolean isAddition) {
        return (gui, context, player, onComplete) ->
                gui.showCarrotExchange(amount, isAddition, () -> {
                    if (isAddition) {
                        player.addCarrots(amount);
                    } else {
                        player.removeCarrots(amount);
                    }
                    onComplete.run();
                });
    }

    /**
     * Creates an action that lets the player choose to add or remove carrots or move.
     * @return The created action.
     */
    public static Action createCarrotExchangeDecision() {
        return (gui, context, player, onComplete) -> {
            int amount = context.getCarrotExchangeAmount();
            gui.showCarrotExchangeDecision(
                    amount,
                    () -> {
                        LogMessage.choosesToAddCarrots(player.getName(), amount);
                        player.addCarrots(amount);
                        onComplete.run();
                    },
                    () -> {
                        LogMessage.choosesToRemoveCarrots(player.getName(), amount);
                        player.removeCarrots(amount);
                        onComplete.run();
                    },
                    () -> {
                        LogMessage.choosesToMove(player.getName());
                        createSelectFieldAndMove().execute(gui, context, player, onComplete);
                    },
                    player.getCarrots() - amount >= 0
            );
        };
    }

    /**
     * Creates an action that lets the player consume a salad.
     * If the player has no salads, the action will do nothing.
     * In both cases will the state of the player, that he is eating a salad, be set to {@code false}.
     * @return The created action.
     */
    public static Action createConsumeSalad() {
        return (gui, context, player, onComplete) -> {
            if (player.getSalads() > 0) {
                gui.showEatingSalad(player.getName(), () -> {
                    player.consumeSalad();
                    LogMessage.consumesSalad(player.getName());

                    player.setEatsSalad(false);
                    LogMessage.isEatingSalad(player.getName(), false);

                    int amount = context.getCarrotExchangeAmount() * context.getRank(player);
                    createCarrotExchange(amount, true).execute(gui, context, player, onComplete);
                });
            } else {
                gui.showNoSaladsToConsume(player.getName(), () -> {
                    LogMessage.hasNoSaladsToConsume(player.getName());

                    player.setEatsSalad(false);
                    LogMessage.isEatingSalad(player.getName(), false);

                    onComplete.run();
                });
            }
        };
    }

    /**
     * Creates an action that lets the player select a field to move to and moves him there.
     * If the player has no available fields to move to but still carrots, the action will do nothing.
     * If the player has no carrots and available fields, the action will move him back to the start.
     * @return The created action.
     */
    public static Action createSelectFieldAndMove() {
        return (gui, context, player, onComplete) -> {
            int[] availableFields = context.getAvailableFields(player);
            if (availableFields.length == 0) {
                if (player.getCarrots() <= 0) {
                    gui.showNoCarrotsBackToStart(
                            player.getName(),
                            () -> createMove(0, false).execute(gui, context, player, onComplete)
                    );

                    LogMessage.hasNoFieldToMoveTo(player.getName());
                    LogMessage.movesBackToStart(player.getName());
                } else {
                    gui.showNoValidField(player.getName(), () -> {
                        // If the player has no fields to move to, but is on the start field,
                        // we can't simply skip his turn, because on the start field he needs
                        // to be become the most left player to don't disturb the natural order
                        // of the start field.
                        if (player.getField() == Board.START_FIELD) {
                            gui.skipPlayerVisualOnStartField(onComplete);
                        } else {
                            onComplete.run();
                        }
                    });

                    LogMessage.hasNoFieldToMoveTo(player.getName());

                    // Why do we log skipping here, but not in other cases where we log
                    // that the player has no field to move to: the difference is the other
                    // occurrences of logging that the player has no field to move to are in
                    // the context of a card the player has already done a move on his turn,
                    // so logging skipping there would be counterintuitive
                    LogMessage.getsSkipped(player.getName());
                }
            } else {
                // only for preview cost in GUI (not used in logic)
                int[] carrotCosts = new int[availableFields.length];
                for (int i = 0; i < carrotCosts.length; i++) {
                    if (player.getField() <= availableFields[i]) {
                        carrotCosts[i] = context.calcMovementCost(player.getField(), availableFields[i]);
                    } else {
                        // backwards movement to a hedgehog field gives carrots
                        carrotCosts[i] = -(player.getField() - availableFields[i]) * context.getCarrotExchangeAmount();
                    }
                }

                gui.enableMoveSelection(
                        availableFields,
                        player.getCarrots(),
                        carrotCosts,
                        selectedPosition -> createMove(selectedPosition, true).execute(gui, context, player, onComplete)
                );
            }
        };
    }

    /**
     * Creates an action from an {@link ActionCard}.
     * @param card The card to create an action from.
     * @param from The field the player has moved from.
     * @return The created action.
     */
    static Action convertActionCard(ActionCard card, int from) {
        // looks complicated, but it's just a switch statement with lambdas... ¯\_(ツ)_/¯
        return switch (card) {

            case ExchangeCarrots -> (gui, context, player, onComplete) -> {
                int amount = context.getCarrotExchangeAmount();
                gui.showCarrotExchangeCard(
                        amount,
                        () -> {
                            LogMessage.choosesToAddCarrots(player.getName(), amount);
                            player.addCarrots(amount);
                            onComplete.run();
                        },
                        () -> {
                            LogMessage.choosesToRemoveCarrots(player.getName(), amount);
                            player.removeCarrots(amount);
                            onComplete.run();
                        },
                        () -> {
                            LogMessage.choosesToDoNothing(player.getName());
                            onComplete.run();
                        },
                        player.getCarrots() - amount >= 0
                );
            };

            case ConsumeSalad -> (gui, context, player, onComplete) ->
                    gui.showConsumeSaladCard(() -> {
                        player.setEatsSalad(true);
                        LogMessage.isEatingSalad(player.getName(), true);

                        onComplete.run();
                    });

            case GetSuspended -> (gui, context, player, onComplete) ->
                    gui.showGetSuspendedCard(() -> {
                        player.setSuspended(true);
                        LogMessage.getsSuspended(player.getName());

                        onComplete.run();
                    });
            case TakeTurnAgain -> (gui, context, player, onComplete) ->
                    gui.showTakeTurnAgainCard(() -> {
                        Action selectFieldAndMove = createSelectFieldAndMove();
                        selectFieldAndMove.execute(gui, context, player, onComplete);
                    });

            case FreeLastMove -> (gui, context, player, onComplete) ->
                    gui.showFreeLastMoveCard(() -> {
                        player.addCarrots(context.calcMovementCost(from, player.getField()));
                        onComplete.run();
                    });

            case MoveUpRank -> (gui, context, player, onComplete) ->
                    gui.showMoveUpRankCard(() -> {
                        int to = context.getMoveUpRankPosition(player);
                        if (to == player.getField()) {
                            gui.showAlreadyFirstRank(onComplete);
                            LogMessage.isAlreadyFirstRank(player.getName());
                        } else if (to == Board.size() - 1 && !context.canFinish(player.getCarrots(), player.getSalads())) {
                            gui.showCantMoveUpRankToEnd(onComplete);
                            LogMessage.hasNoFieldToMoveTo(player.getName());
                        } else {
                            Action move = createMove(to, false);
                            move.execute(gui, context, player, onComplete);
                        }
                    });

            case FallBackRank -> (gui, context, player, onComplete) ->
                    gui.showFallBackRankCard(() -> {
                        int to = context.getFallBackRankPosition(player);
                        if (to == player.getField()) {
                            gui.showAlreadyLastRank(onComplete);
                            LogMessage.isAlreadyLastRank(player.getName());
                        } else if (Board.FIELDS[to] == TileRegistry.HEDGEHOG) {
                            int updatedFrom = player.getField();
                            player.setField(to);
                            LogMessage.moves(player.getName(), updatedFrom, Board.FIELDS[updatedFrom], to, Board.FIELDS[to]);
                            gui.movePlayerVisual(onComplete, updatedFrom, to);
                        } else {
                            Action move = createMove(to, false);
                            move.execute(gui, context, player, onComplete);
                        }
                    });

            case MoveToNextCarrotField -> (gui, context, player, onComplete) ->
                    gui.showToNextCarrotFieldCard(() -> {
                        int to = context.getNextCarrotField(player);
                        if (to == player.getField()) {
                            gui.showNoCarrotFieldToMoveTo(player.getName(), onComplete);
                            LogMessage.hasNoFieldToMoveTo(player.getName());
                        } else {
                            Action move = createMove(to, false);
                            move.execute(gui, context, player, onComplete);
                        }
                    });

            case MoveToLastCarrotField -> (gui, context, player, onComplete) ->
                    gui.showToLastCarrotFieldCard(() -> {
                        int to = context.getLastCarrotField(player);
                        if (to == player.getField()) {
                            gui.showNoCarrotFieldToMoveTo(player.getName(), onComplete);
                            LogMessage.hasNoFieldToMoveTo(player.getName());
                        } else {
                            Action move = createMove(to, false);
                            move.execute(gui, context, player, onComplete);
                        }
                    });

            default -> (gui, context, player, onComplete) -> {
                LogMessage.error(
                        "Unknown ActionCard: " + card,
                        new IllegalArgumentException("Unknown ActionCard: " + card)
                );

                onComplete.run();
            };
        };
    }

    /**
     * Creates an action that moves the player to the given field.
     * @param to The field to move to.
     * @param hasMovementCost Whether the player has to pay a movement cost or not.
     * @return The created action.
     */
    static Action createMove(int to, boolean hasMovementCost) { // could be private but is used in tests
        return (gui, context, player, onComplete) -> {
            int from = player.getField();
            if (hasMovementCost) {
                int movementCost = context.calcMovementCost(from, to);
                player.removeCarrots(movementCost);
            }

            player.setField(to);
            LogMessage.moves(player.getName(), from, Board.FIELDS[from], to, Board.FIELDS[to]);

            Action onEntry = context.getTile(to).getEntryAction(from);
            gui.movePlayerVisual(() -> onEntry.execute(gui, context, player, onComplete), from, to);
        };
    }
}
