package com.cft.view.swing;

import com.cft.api.CellCode;
import com.cft.api.PojoDifficulty;
import com.cft.api.PojoGamerResult;
import com.cft.controller.MinesweeperController;
import com.cft.view.MinesweeperView;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

@Slf4j
public class SwingMinesweeperView extends JFrame implements MinesweeperView {
    //number constants
    //margin, gap
    private static final int MARGIN_HORIZONTAL = 25;
    private static final int MARGIN_VERTICAL = 25;

    private static final int ZERO_GAP = 0;
    static final int DEFAULT_GAP = 10;

    static final int CELL_WIDTH_GAP = 0;
    static final int CELL_HEIGHT_GAP = 0;

    //width, height
    private static final int ACTIVE_CELL_WIDTH = 20;
    private static final int ACTIVE_CELL_HEIGHT = 20;

    static final int MENU_BAR_HEIGHT = 20;

    private static final int GAME_INFO_PANEL_HEIGHT = 50;

    static final int CELL_WIDTH = ACTIVE_CELL_WIDTH + CELL_WIDTH_GAP;
    static final int CELL_HEIGHT = ACTIVE_CELL_HEIGHT + CELL_HEIGHT_GAP;

    //other
    private static final int BUFFERS_QUANTITY = 4;

    //string constants
    //title
    private static final String GAME_TITLE = "Minesweeper";
    private static final String WIN_TITLE = "Win";
    private static final String LOSE_TITLE = "Lose";
    private static final String ABOUT_TITLE = "About";
    private static final String HIGH_SCORES_TITLE = "High scores";
    private static final String DIFFICULTY_TITLE = "Difficulty settings";
    private static final String NAME_TITLE = "Имя";

    //message
    private static final String WIN_MESSAGE = "You win!";
    private static final String LOSE_MESSAGE = "You lose!";

    //text
    private static final String ABOUT_TEXT = "ЛКМ - открыть клетку;" + System.lineSeparator() + "ПКМ - поставить флаг;" +
                                             System.lineSeparator() + "СКМ/(ЛКМ + ПКМ) - открыть соседние клетки;" +
                                             System.lineSeparator() + "F2 - начать новую игру;" + System.lineSeparator() +
                                             "Application author: Arkady Belkov 2020";

    //other
    private static final String STOPWATCH_INITIAL_VALUE = "0";
    private static final String DEFAULT_NAME = "Аноним";

    @Getter(AccessLevel.PACKAGE)
    private final JLabel minesLeftLabel;
    @Getter(AccessLevel.PACKAGE)
    private final JLabel stopwatchLabel;
    @Getter(AccessLevel.PACKAGE)
    private final MinesweeperController minesweeperController;
    private SwingMinesweeperGameGridPanel gameGridPanel;
    private SwingMinesweeperMenuBar menuBarPanel;
    private SwingMinesweeperInfoPanel infoPanel;

    public SwingMinesweeperView(MinesweeperController minesweeperController) {
        super(GAME_TITLE);
        this.minesweeperController = minesweeperController;
        this.minesLeftLabel = new JLabel();
        this.stopwatchLabel = new JLabel(STOPWATCH_INITIAL_VALUE);

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new FlowLayout(FlowLayout.CENTER, ZERO_GAP, ZERO_GAP));
    }

    @Override
    public void initialize(PojoDifficulty difficulty) {
        setSize(calcWidth(difficulty), calcHeight(difficulty));

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F2) {
                    minesweeperController.handlePressedF2();
                }
            }
        });

        createAndSetPanels(difficulty);

        setFocusable(true);
        setVisible(true);
        createBufferStrategy(BUFFERS_QUANTITY);

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                gameGridPanel.focusLost();
                log.info("Focus gained");
            }

            @Override
            public void focusLost(FocusEvent e) {
                log.info("Focus lost");
            }
        });

        requestFocusInWindow();
    }

    @Override
    public void renderNameInput() {
        String playerName = "";

        while (StringUtils.isBlank(playerName)) {
            playerName = JOptionPane.showInputDialog(this, NAME_TITLE, DEFAULT_NAME);

            if (playerName == null) {
                playerName = DEFAULT_NAME;
            }
        }

        minesweeperController.handlePlayerSubmitName(playerName);
    }

    private void createAndSetPanels(PojoDifficulty difficulty) {
        this.menuBarPanel = new SwingMinesweeperMenuBar(this);
        add(menuBarPanel);
        this.infoPanel = new SwingMinesweeperInfoPanel(this);
        add(infoPanel);
        this.gameGridPanel = new SwingMinesweeperGameGridPanel(this, difficulty);
        add(gameGridPanel);
    }

    private int calcWidth(PojoDifficulty difficulty) {
        return difficulty.getColumnQuantity() * CELL_WIDTH + MARGIN_HORIZONTAL;
    }

    private int calcHeight(PojoDifficulty difficulty) {
        return difficulty.getRowQuantity() * CELL_HEIGHT + MARGIN_VERTICAL + GAME_INFO_PANEL_HEIGHT + MENU_BAR_HEIGHT;
    }

    @Override
    public void renderNewGame(int mineQuantity) {
        gameGridPanel.resetCellIcons();

        stopwatchLabel.setText(STOPWATCH_INITIAL_VALUE);
        minesLeftLabel.setText(String.valueOf(mineQuantity));
    }

    @Override
    public void renderGameWon() {
        JOptionPane.showMessageDialog(this, WIN_MESSAGE, WIN_TITLE, JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void renderGameLose() {
        JOptionPane.showMessageDialog(this, LOSE_MESSAGE, LOSE_TITLE, JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void updateCell(int row, int column, CellCode code) {
        gameGridPanel.setIconToCell(row, column, code);
    }

    @Override
    public void updateStopwatch(long time) {
        stopwatchLabel.setText(String.valueOf(time));
    }

    @Override
    public void updateQuantityOfRemainingMines(int quantityOfRemainingMines) {
        minesLeftLabel.setText(String.valueOf(quantityOfRemainingMines));
    }

    void renderAbout() {
        JOptionPane.showMessageDialog(this, ABOUT_TEXT, ABOUT_TITLE, JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void renderLeaderBoard(List<PojoGamerResult> highScores) {
        SwingMinesweeperLeaderBoardPanel highScorePanel = new SwingMinesweeperLeaderBoardPanel(highScores);

        JOptionPane.showMessageDialog(this, highScorePanel, HIGH_SCORES_TITLE, JOptionPane.PLAIN_MESSAGE);
    }

    @Override
    public void renderDifficultySettings(PojoDifficulty currentDifficulty) {
        SwingMinesweeperDifficultyPanel swingMinesweeperDifficultyPanel = new SwingMinesweeperDifficultyPanel(currentDifficulty);

        int answer = JOptionPane.showConfirmDialog(this, swingMinesweeperDifficultyPanel, DIFFICULTY_TITLE,
                                                   JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (answer == JOptionPane.YES_OPTION) {
            PojoDifficulty selectedPojoDifficulty = swingMinesweeperDifficultyPanel.getSelectedDifficulty();
            minesweeperController.handleChangeDifficulty(selectedPojoDifficulty);
        }
    }

    @Override
    public void updateDifficulty(PojoDifficulty difficulty) {
        gameGridPanel.updateCellArraySizes(difficulty.getRowQuantity(), difficulty.getColumnQuantity());

        setSize(calcWidth(difficulty), calcHeight(difficulty));

        remove(menuBarPanel);
        remove(infoPanel);
        remove(gameGridPanel);

        createAndSetPanels(difficulty);

        revalidate();
        repaint();
        renderNewGame(difficulty.getMineQuantity());
    }
}
