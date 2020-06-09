package com.cft.view.swing;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Slf4j
class SwingMinesweeperMenuBar extends JPanel {
    private static final String MENU_TITLE = "Game";
    private static final String NEW_GAME_ITEM_TITLE = "New Game (F2)";
    private static final String EXIT_ITEM_TITLE = "Exit";
    private static final String HIGH_SCORES_ITEM_TITLE = "High Scores";
    private static final String CHANGE_DIFFICULTY_ITEM_TITLE = "Change Difficulty";

    private static final int ZERO_GAP = 0;

    SwingMinesweeperMenuBar(SwingMinesweeperView view) {
        super(new FlowLayout(FlowLayout.CENTER, ZERO_GAP, ZERO_GAP));
        JMenu gameMenu = new JMenu(MENU_TITLE);
        JMenuItem newGameMenuItem = new JMenuItem(NEW_GAME_ITEM_TITLE);
        JMenuItem exitMenuItem = new JMenuItem(EXIT_ITEM_TITLE);
        JMenuItem highScoresMenuItem = new JMenuItem(HIGH_SCORES_ITEM_TITLE);
        JMenuItem changeDifficultyMenuItem = new JMenuItem(CHANGE_DIFFICULTY_ITEM_TITLE);
        gameMenu.add(newGameMenuItem);
        gameMenu.add(highScoresMenuItem);
        gameMenu.add(changeDifficultyMenuItem);
        gameMenu.addSeparator();
        gameMenu.add(exitMenuItem);

        newGameMenuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    log.info("Нажатие ЛКМ на 'New Game'");
                    view.getMinesweeperController().handleLeftMouseButtonClickOnNewGame();
                }
            }
        });

        highScoresMenuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    log.info("Нажатие ЛКМ на 'High Scores'");
                    view.getMinesweeperController().handleLeaderBoardClicked();
                }
            }
        });

        changeDifficultyMenuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    log.info("Нажатие ЛКМ на 'Change Difficulty'");
                    view.getMinesweeperController().handleDifficultySettingsClicked();
                }
            }
        });

        exitMenuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    log.info("Нажатие ЛКМ на 'Exit'");
                    System.exit(0);
                }
            }
        });

        JMenu aboutMenu = new JMenu("About");
        aboutMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    log.info("Нажатие ЛКМ на 'About'");
                    view.renderAbout();
                }
            }
        });

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(gameMenu);
        menuBar.add(aboutMenu);
        menuBar.setPreferredSize(new Dimension(view.getWidth(), SwingMinesweeperView.MENU_BAR_HEIGHT));
        menuBar.setVisible(true);
        add(menuBar);
    }
}
