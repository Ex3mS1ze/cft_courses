package com.cft.model;

import com.cft.api.CellCode;
import com.cft.api.PojoDifficulty;
import com.cft.api.PojoGamerResult;
import com.cft.model.cell.GameCell;
import com.cft.model.difficulty.MinesweeperDifficulty;
import com.cft.model.score.MinesweeperGameResult;
import com.cft.model.score.MinesweeperGameResultManipulator;
import com.cft.util.DifficultyConverter;
import com.cft.util.GameResultConverter;
import com.cft.view.MinesweeperView;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
@Slf4j
public class MinesweeperGame {
    private static final int DEFAULT_QUANTITY_OF_FLAGGED_CELLS = 0;
    private static final int DEFAULT_QUANTITY_OF_REVEALED_EMPTY_CELLS = 0;

    private String playerName;

    @Getter(AccessLevel.PRIVATE)
    private final GameStopwatch gameStopwatch;
    @Getter(AccessLevel.PRIVATE)
    private final MinesweeperViewNotifier viewNotifier;
    private final MinesweeperGameResultManipulator minesweeperGameResultManipulator;

    @Getter(AccessLevel.PRIVATE)
    private GameCell[][] gameCells;
    private MinesweeperDifficulty difficulty;

    private int quantityOfEmptyCells;
    private int quantityOfFlaggedCells;
    private int quantityOfRevealedEmptyCells;

    private boolean isFirstTurn = true;
    private boolean isGameInitialized;
    private boolean isGameOver;

    public MinesweeperGame(MinesweeperDifficulty difficulty) {
        this.difficulty = difficulty;
        this.viewNotifier = new MinesweeperViewNotifier();
        this.quantityOfEmptyCells = calcEmptyCells(difficulty);
        this.gameCells = new GameCell[difficulty.getRowQuantity()][difficulty.getColumnQuantity()];
        this.gameStopwatch = new GameStopwatch();
        this.minesweeperGameResultManipulator = new MinesweeperGameResultManipulator();
    }

    private int calcEmptyCells(MinesweeperDifficulty difficulty) {
        return difficulty.getRowQuantity() * difficulty.getColumnQuantity() - difficulty.getMineQuantity();
    }

    private void initializeGameCells(int rowQuantity, int columnQuantity) {
        boolean[][] minedCells = new boolean[rowQuantity][columnQuantity];
        int plantedMinesQuantity = 0;
        Random random = new Random();

        while (plantedMinesQuantity != difficulty.getMineQuantity()) {
            int randomRow = random.nextInt(rowQuantity);
            int randomColumn = random.nextInt(columnQuantity);

            if (minedCells[randomRow][randomColumn]) {
                continue;
            }

            minedCells[randomRow][randomColumn] = true;
            plantedMinesQuantity++;
        }

        for (int rowIndex = 0; rowIndex < rowQuantity; rowIndex++) {
            for (int columnIndex = 0; columnIndex < columnQuantity; columnIndex++) {
                gameCells[rowIndex][columnIndex] = new GameCell(rowIndex, columnIndex,
                                                                minedCells[rowIndex][columnIndex]);
            }
        }

        for (int rowIndex = 0; rowIndex < rowQuantity; rowIndex++) {
            for (int columnIndex = 0; columnIndex < columnQuantity; columnIndex++) {
                gameCells[rowIndex][columnIndex].setSurroundingCells(getSurroundingCells(rowIndex, columnIndex));
            }
        }

        log.info("Поле было инициализировано");
    }

    public void attachView(MinesweeperView minesweeperView) {
        viewNotifier.attachView(minesweeperView);
    }

    public void startNewGame() {
        resetParameters();
        initializeGameCells(difficulty.getRowQuantity(), difficulty.getColumnQuantity());

        if (!isGameInitialized) {
            viewNotifier.notifyViewAboutGameInitialized(DifficultyConverter.toPojo(difficulty));
            viewNotifier.notifyViewAboutPlayerNameRequested();
            isGameInitialized = true;
        }

        viewNotifier.notifyViewsNewGame(difficulty.getMineQuantity());
    }

    private void resetParameters() {
        isGameOver = false;
        quantityOfFlaggedCells = DEFAULT_QUANTITY_OF_FLAGGED_CELLS;
        quantityOfRevealedEmptyCells = DEFAULT_QUANTITY_OF_REVEALED_EMPTY_CELLS;
        isFirstTurn = true;
        gameStopwatch.stop();
    }

    /**
     * Открывает переданную клетку.
     *
     * <p>Если ход первый происходит расстановка мин.
     * Если первый ход выпадает на заминированную клетку просходит вызов {@link #moveMineFromCell(int, int) moveMineFromCell}
     * для перемещения мины в другую клетку.
     *
     * <p>Если ход не первый, а переданная клетка заминирована и не помечена вызывается {@link #playerLose(int, int) playerLose}.
     * Иначе вызывается {@link #revealCell(int row, int) revealCell} для ракскрытия переданной клетки и её соседей.
     *
     * @param row    строка клетки
     * @param column колонка клетки
     */
    public void openCell(int row, int column) {
        if (isGameOver) {
            return;
        }

        if (isFirstTurn) {
            if (gameCells[row][column].isMined()) {
                log.info("Первый ход выпал на заминированную клетку");
                moveMineFromCell(row, column);
            }

            isFirstTurn = false;
            gameStopwatch.start();
        }

        if (gameCells[row][column].isMined() && !gameCells[row][column].isFlagged()) {
            playerLose(row, column);
        } else {
            revealCell(row, column);
        }
    }

    private void moveMineFromCell(int rowOfMinedCell, int columnOfMinedCell) {
        gameCells[rowOfMinedCell][columnOfMinedCell].setMined(false);

        Random random = new Random();
        boolean isMoveDone = false;

        while (!isMoveDone) {
            int randomRow = random.nextInt(difficulty.getRowQuantity());
            int randomColumn = random.nextInt(difficulty.getColumnQuantity());

            if (gameCells[randomRow][randomColumn].isMined() ||
                randomRow == rowOfMinedCell && randomColumn == columnOfMinedCell) {
                continue;
            }

            gameCells[randomRow][randomColumn].setMined(true);
            isMoveDone = true;
            log.info("Мина в клетке '{} {}' была перемещена в клетку '{} {}'", rowOfMinedCell, columnOfMinedCell, randomRow, randomColumn);
        }
    }

    private void playerLose(int row, int column) {
        isGameOver = true;
        gameStopwatch.stop();
        for (GameCell[] gameRow : gameCells) {
            for (GameCell gameCell : gameRow) {
                if (gameCell.isMined() && gameCell.isFlagged()) {
                    viewNotifier.notifyViewAboutCellStatusChanged(gameCell.getRow(), gameCell.getColumn(), CellCode.GREEN_MINE);
                    continue;
                }
                if (gameCell.isMined() && !gameCell.isFlagged()) {
                    viewNotifier.notifyViewAboutCellStatusChanged(gameCell.getRow(), gameCell.getColumn(), CellCode.MINE);
                }
            }
        }
        viewNotifier.notifyViewAboutCellStatusChanged(row, column, CellCode.RED_MINE);
        viewNotifier.notifyViewAboutGameLose();
    }

    /**
     * Раскрывает клетку и все окружающие её клетки.
     * Вызывается рекурсивно для всех окрудающих клеток.
     *
     * <p>Заминированные или помеченные или раскрытые клетки пропускаются.
     *
     * @param row    строка клетки
     * @param column колонка клетки
     */
    private void revealCell(int row, int column) {
        if (isOutOfBounds(row, column) || isCellNotOpenable(gameCells[row][column]) ||
            gameCells[row][column].isMined()) {
            return;
        }

        int quantityOfSurroundingMines = countSurroundingCellsSatisfyingFunction(row, column, GameCell::isMined);
        gameCells[row][column].setRevealed(true);
        quantityOfRevealedEmptyCells++;
        viewNotifier.notifyViewAboutCellStatusChanged(row, column, CellCode.values()[quantityOfSurroundingMines]);

        if (quantityOfEmptyCells == quantityOfRevealedEmptyCells) {
            playerWon();
            return;
        }

        if (quantityOfSurroundingMines != 0) {
            return;
        }

        for (GameCell cell : gameCells[row][column].getSurroundingCells()) {
            if (isGameOver) {
                break;
            }

            revealCell(cell.getRow(), cell.getColumn());
        }
    }

    private boolean isOutOfBounds(int row, int column) {
        return row < 0 || row >= difficulty.getRowQuantity() || column < 0 || column >= difficulty.getColumnQuantity();
    }

    /**
     * Возрашает {@code true} если клетка помечена или заминирована
     *
     * @param gameCell проверяемая клетка
     * @return {@code true} если клетка помечена или заминирована
     */
    private boolean isCellNotOpenable(GameCell gameCell) {
        return gameCell.isFlagged() || gameCell.isRevealed();
    }

    private void playerWon() {
        isGameOver = true;
        gameStopwatch.stop();
        MinesweeperGameResult minesweeperGameResult = new MinesweeperGameResult(difficulty.getName(),
                                                                                gameStopwatch.gameTime.get(),
                                                                                LocalDateTime.now(),
                                                                                playerName);
        minesweeperGameResultManipulator.updateFileWithResults(minesweeperGameResult);
        viewNotifier.notifyViewAboutGameWon();
    }

    /**
     * Возращает количество клеток, удовлетворяющих переданной функции
     *
     * @param row      строка клетки
     * @param column   колонка клетки
     * @param function метод, содержащий условие для проверки
     * @return количество клеток, удовлетворяющих переданной функции
     */
    private int countSurroundingCellsSatisfyingFunction(int row, int column, Predicate<GameCell> function) {
        int surroundingCellsQuantity = 0;
        List<GameCell> surroundingCells = gameCells[row][column].getSurroundingCells();

        for (GameCell cell : surroundingCells) {
            if (function.test(cell)) {
                surroundingCellsQuantity++;
            }
        }

        return surroundingCellsQuantity;
    }

    private List<GameCell> getSurroundingCells(int row, int column) {
        List<GameCell> surroundingCells = new ArrayList<>();

        //Нижние клетки
        if (row + 1 < difficulty.getRowQuantity()) {
            //Под
            surroundingCells.add(gameCells[row + 1][column]);

            //Справа
            if (column + 1 < difficulty.getColumnQuantity()) {
                surroundingCells.add(gameCells[row + 1][column + 1]);
            }

            //Слева
            if (column - 1 >= 0) {
                surroundingCells.add(gameCells[row + 1][column - 1]);
            }
        }

        //Верхние клетки
        if (row - 1 >= 0) {
            //Над
            surroundingCells.add(gameCells[row - 1][column]);

            //Справа
            if (column + 1 < difficulty.getColumnQuantity()) {
                surroundingCells.add(gameCells[row - 1][column + 1]);
            }

            //Слева
            if (column - 1 >= 0) {
                surroundingCells.add(gameCells[row - 1][column - 1]);
            }
        }

        //Клетка справа
        if (column + 1 < difficulty.getColumnQuantity()) {
            surroundingCells.add(gameCells[row][column + 1]);
        }

        //Клетка слева
        if (column - 1 >= 0) {
            surroundingCells.add(gameCells[row][column - 1]);
        }

        return surroundingCells;
    }

    public void switchCellFlag(int row, int column) {
        if (isFirstTurn || isGameOver || gameCells[row][column].isRevealed()) {
            return;
        }

        if (gameCells[row][column].isFlagged()) {
            quantityOfFlaggedCells--;
            viewNotifier.notifyViewAboutCellStatusChanged(row, column, CellCode.CLOSED);
        } else {
            quantityOfFlaggedCells++;
            viewNotifier.notifyViewAboutCellStatusChanged(row, column, CellCode.MARKED_AS_MINE);
        }

        gameCells[row][column].setFlagged(!gameCells[row][column].isFlagged());
        viewNotifier.notifyViewAboutQuantityOfRemainingMinesChanged(
                difficulty.getMineQuantity() - quantityOfFlaggedCells);
    }

    public void openSurroundingCells(int row, int column) {
        if (isFirstTurn || isGameOver) {
            return;
        }

        if (gameCells[row][column].isRevealed() &&
            countSurroundingCellsSatisfyingFunction(row, column, GameCell::isMined) ==
            countSurroundingCellsSatisfyingFunction(row, column, GameCell::isFlagged)) {
            log.info("Количество отмеченных мин вокруг клетки совпадает с реальным их количеством, начинается открытие окружающих клеток");

            List<GameCell> surroundingCells = gameCells[row][column].getSurroundingCells();
            Optional<GameCell> notFlaggedButMinedCell = surroundingCells.stream()
                                                                        .filter(cell -> cell.isMined() && !cell.isFlagged())
                                                                        .findFirst();
            if (notFlaggedButMinedCell.isPresent()) {
                playerLose(notFlaggedButMinedCell.get().getRow(), notFlaggedButMinedCell.get().getColumn());
                return;
            }

            for (GameCell cell : surroundingCells) {
                openCell(cell.getRow(), cell.getColumn());
            }
        } else {
            log.info("Количество отмеченных мин вокруг клетки не совпадает с реальным их количеством или клетка не раскрыта");
        }
    }

    public void changeDifficulty(PojoDifficulty pojoDifficulty) {
        resetParameters();

        this.difficulty = DifficultyConverter.toModel(pojoDifficulty);
        this.gameCells = new GameCell[difficulty.getRowQuantity()][difficulty.getColumnQuantity()];
        this.quantityOfEmptyCells = calcEmptyCells(difficulty);
        log.info("Сложность изменена '{}'", difficulty.getName());
        initializeGameCells(difficulty.getRowQuantity(), difficulty.getColumnQuantity());
        viewNotifier.notifyViewAboutDifficultyChanged(DifficultyConverter.toPojo(difficulty));
    }

    public void hoverCell(int row, int column) {
        if (isFirstTurn || !isGameOver && !isCellNotOpenable(gameCells[row][column])) {
            viewNotifier.notifyViewAboutCellStatusChanged(row, column, CellCode.ZERO_MINES);
        }
    }

    public void unhoverCell(int row, int column) {
        if (isFirstTurn || !isGameOver && !isCellNotOpenable(gameCells[row][column])) {
            viewNotifier.notifyViewAboutCellStatusChanged(row, column, CellCode.CLOSED);
        }
    }

    public void hoverCellWithNeighbors(int row, int column) {
        applyEffectToCells(gameCells[row][column], this::hoverCell);
    }

    public void unhoverCellWithNeighbors(int row, int column) {
        applyEffectToCells(gameCells[row][column], this::unhoverCell);
    }

    private void applyEffectToCells(GameCell mainCell, BiConsumer<Integer, Integer> function) {
        if (!isGameOver) {
            List<GameCell> cellsToApply = mainCell.getSurroundingCells().stream()
                                                  .filter(cell -> !cell.isFlagged() && !cell.isRevealed())
                                                  .collect(Collectors.toList());

            if (!mainCell.isRevealed()) {
                function.accept(mainCell.getRow(), mainCell.getColumn());
            }

            cellsToApply.forEach(cell -> function.accept(cell.getRow(), cell.getColumn()));
        }
    }

    public void openDifficultySettings() {
        viewNotifier.notifyViewAboutCurrentDifficulty(DifficultyConverter.toPojo(difficulty));
    }

    public void openLeaderBoard() {
        List<PojoGamerResult> pojoGameResults = minesweeperGameResultManipulator.getMinesweeperGameResults()
                                                                                .stream()
                                                                                .map(GameResultConverter::toPojo)
                                                                                .collect(Collectors.toList());
        viewNotifier.notifyViewAboutHighScores(pojoGameResults);
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
        log.info("Установлено имя игрока '{}'", playerName);
    }

    private class GameStopwatch {
        private static final long PERIOD = 1000L;
        private static final int DELAY = 0;

        private Timer timer;
        private AtomicLong gameTime;

        private void start() {
            gameTime = new AtomicLong();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    viewNotifier.notifyViewAboutTimeChange(gameTime.incrementAndGet());
                }
            }, DELAY, PERIOD);
        }

        private void stop() {
            if (timer != null) {
                timer.cancel();
            }
        }
    }
}
