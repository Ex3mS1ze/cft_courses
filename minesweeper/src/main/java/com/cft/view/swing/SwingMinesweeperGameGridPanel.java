package com.cft.view.swing;

import com.cft.api.CellCode;
import com.cft.api.PojoDifficulty;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;

@Slf4j
class SwingMinesweeperGameGridPanel extends JPanel {
    private static final String NO_ICON_ERROR_MESSAGE = "No image found for code: ";
    private static final String ERROR_TITLE = "Error";

    private boolean isLeftMouseButtonPressed;
    private boolean isRightMouseButtonPressed;
    private boolean isMiddleMousePressed;

    private SwingMinesweeperCell lastEntered;
    private SwingMinesweeperCell[][] cells;
    private LastMouseEvent lastMouseEvent;

    private final IconRegistry iconRegistry = new IconRegistry();
    private final SwingMinesweeperView view;

    SwingMinesweeperGameGridPanel(SwingMinesweeperView view, PojoDifficulty difficulty) {
        updateCellArraySizes(difficulty.getRowQuantity(), difficulty.getColumnQuantity());
        this.view = view;

        setLayout(new GridLayout(difficulty.getRowQuantity(), difficulty.getColumnQuantity(), SwingMinesweeperView.CELL_WIDTH_GAP,
                                 SwingMinesweeperView.CELL_HEIGHT_GAP));
        setPreferredSize(new Dimension(difficulty.getColumnQuantity() * SwingMinesweeperView.CELL_WIDTH,
                                       difficulty.getRowQuantity() * SwingMinesweeperView.CELL_HEIGHT));
        setFocusable(true);
        requestFocusInWindow();
    }

    private boolean isLikeMiddleMouseButtonPressed() {
        return isMiddleMousePressed || (isLeftMouseButtonPressed && isRightMouseButtonPressed);
    }

    private boolean isOnlyLeftMouseButtonPressed() {
        return isLeftMouseButtonPressed && !isMiddleMousePressed && !isRightMouseButtonPressed;
    }

    private void resetAllIsPressed() {
        isLeftMouseButtonPressed = false;
        isRightMouseButtonPressed = false;
        isMiddleMousePressed = false;
    }

    /**
     * Возращает {@code true} если положение события {@code event} находится в пределах игрового поля {@link #SwingMinesweeperGameGridPanel}.
     * Для проверки используются координаты относительно экрана.
     *
     * @param event событие, положение на экране которого проверяется
     * @return {@code true} если положение события находится в пределах игрового поля
     */
    private boolean isOutsideGameGridPanel(MouseEvent event) {
        Point gameGridLocation = SwingMinesweeperGameGridPanel.this.getLocationOnScreen();

        if (event.getXOnScreen() < gameGridLocation.getX() ||
            event.getYOnScreen() < gameGridLocation.getY() ||
            event.getXOnScreen() > gameGridLocation.getX() + SwingMinesweeperGameGridPanel.this.getWidth() ||
            event.getYOnScreen() > gameGridLocation.getY() + SwingMinesweeperGameGridPanel.this.getHeight()) {
            log.warn("Мышь вне игрового поля");
            return true;
        }

        return false;
    }

    void resetCellIcons() {
        resetAllIsPressed();
        Optional<ImageIcon> closedCell = iconRegistry.getImageForCell(CellCode.CLOSED);
        if (!closedCell.isPresent()) {
            JOptionPane.showMessageDialog(this, NO_ICON_ERROR_MESSAGE + CellCode.CLOSED, ERROR_TITLE,
                                          JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (SwingMinesweeperCell[] row : cells) {
            for (SwingMinesweeperCell cell : row) {
                cell.setIcon(closedCell.get());
            }
        }
    }

    void setIconToCell(int row, int column, CellCode code) {
        Optional<ImageIcon> iconForCell = iconRegistry.getImageForCell(code);

        if (!iconForCell.isPresent()) {
            JOptionPane.showMessageDialog(this, NO_ICON_ERROR_MESSAGE + CellCode.CLOSED, ERROR_TITLE,
                                          JOptionPane.ERROR_MESSAGE);
            return;
        }

        cells[row][column].setIcon(iconForCell.get());
    }

    void updateCellArraySizes(int row, int column) {
        cells = new SwingMinesweeperCell[row][column];
        resetAllIsPressed();

        for (int rowIndex = 0; rowIndex < row; rowIndex++) {
            for (int columnIndex = 0; columnIndex < column; columnIndex++) {
                SwingMinesweeperCell cell = new SwingMinesweeperCell(rowIndex, columnIndex);
                add(cell);
                cell.setOpaque(true);

                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        lastEntered = cell;

                        if (isOnlyLeftMouseButtonPressed()) {
                            view.getMinesweeperController()
                                .handleCellEnteredWithLeftMousePressed(lastEntered.getRow(), lastEntered.getColumn());
                        }

                        if (isLikeMiddleMouseButtonPressed()) {
                            view.getMinesweeperController()
                                .handleCellEnteredWithMiddleMousePressed(lastEntered.getRow(), lastEntered.getColumn());
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (isOnlyLeftMouseButtonPressed()) {
                            view.getMinesweeperController()
                                .handleCellExitedWithLeftMousePressed(cell.getRow(), cell.getColumn());
                        }

                        if (isLikeMiddleMouseButtonPressed()) {
                            view.getMinesweeperController()
                                .handleCellExitedWithMiddleMousePressed(cell.getRow(), cell.getColumn());
                        }
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (isOutsideGameGridPanel(e)) {
                            resetAllIsPressed();
                            return;
                        }

                        if (e.getButton() == MouseEvent.BUTTON1) {

                            if (isRightMouseButtonPressed) {
                                log.info("LMB+RMB released on cell: '{} {}'", lastEntered.getRow(), lastEntered.getColumn());
                                view.getMinesweeperController()
                                    .handleCellExitedWithMiddleMousePressed(lastEntered.getRow(), lastEntered.getColumn());
                                view.getMinesweeperController()
                                    .handleLeftAndRightButtonsClickOnCell(lastEntered.getRow(), lastEntered.getColumn());
                            } else {
                                log.info("LMB released on cell: '{} {}'", lastEntered.getRow(), lastEntered.getColumn());
                                view.getMinesweeperController()
                                    .handleLeftMouseButtonClickOnCell(lastEntered.getRow(), lastEntered.getColumn());
                            }

                            isLeftMouseButtonPressed = false;
                            lastMouseEvent = new LastMouseEvent(e, EventType.RELEASED);

                            return;
                        }

                        if (e.getButton() == MouseEvent.BUTTON2) {
                            log.info("MMB released on cell: '{} {}'", lastEntered.getRow(), lastEntered.getColumn());

                            view.getMinesweeperController()
                                .handleCellExitedWithMiddleMousePressed(lastEntered.getRow(), lastEntered.getColumn());
                            view.getMinesweeperController()
                                .handleMiddleMouseButtonClickOnCell(lastEntered.getRow(), lastEntered.getColumn());

                            isMiddleMousePressed = false;

                            return;
                        }

                        if (e.getButton() == MouseEvent.BUTTON3) {

                            if (isLeftMouseButtonPressed) {
                                log.info("LMB+RMB released on cell: '{} {}'", lastEntered.getRow(), lastEntered.getColumn());
                                view.getMinesweeperController()
                                    .handleCellExitedWithMiddleMousePressed(lastEntered.getRow(), lastEntered.getColumn());
                                view.getMinesweeperController()
                                    .handleLeftAndRightButtonsClickOnCell(lastEntered.getRow(),lastEntered.getColumn());
                            }

                            if (lastMouseEvent.isRightButton() && !lastMouseEvent.isReleased()){
                                log.info("RMB released on cell: '{} {}'", lastEntered.getRow(), lastEntered.getColumn());
                                view.getMinesweeperController()
                                    .handleRightMouseButtonClickOnCell(lastEntered.getRow(), lastEntered.getColumn());
                            }

                            isRightMouseButtonPressed = false;
                            lastMouseEvent = new LastMouseEvent(e, EventType.RELEASED);
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            isLeftMouseButtonPressed = true;
                            lastMouseEvent = new LastMouseEvent(e, EventType.PRESSED);
                            log.info("LMB pressed on cell: '{} {}'", lastEntered.getRow(), lastEntered.getColumn());
                            view.getMinesweeperController()
                                .handleCellEnteredWithLeftMousePressed(lastEntered.getRow(), lastEntered.getColumn());

                            if (isRightMouseButtonPressed) {
                                view.getMinesweeperController()
                                    .handleCellEnteredWithMiddleMousePressed(lastEntered.getRow(), lastEntered.getColumn());
                            }

                            return;
                        }

                        if (e.getButton() == MouseEvent.BUTTON2) {
                            isMiddleMousePressed = true;
                            log.info("MMB pressed on cell: '{} {}'", lastEntered.getRow(), lastEntered.getColumn());
                            view.getMinesweeperController().handleCellEnteredWithMiddleMousePressed(lastEntered.getRow(), lastEntered.getColumn());
                        }

                        if (e.getButton() == MouseEvent.BUTTON3) {
                            log.info("RMB pressed on cell: '{} {}'", lastEntered.getRow(), lastEntered.getColumn());
                            isRightMouseButtonPressed = true;
                            lastMouseEvent = new LastMouseEvent(e, EventType.PRESSED);

                            if (isLeftMouseButtonPressed) {
                                view.getMinesweeperController()
                                    .handleCellEnteredWithMiddleMousePressed(lastEntered.getRow(), lastEntered.getColumn());
                            }
                        }
                    }
                });

                cells[rowIndex][columnIndex] = cell;
            }
        }
    }

    void focusLost() {
        resetAllIsPressed();
        if (lastEntered == null) {
            return;
        }

        view.getMinesweeperController().handleCellExitedWithMiddleMousePressed(lastEntered.getRow(), lastEntered.getColumn());
    }

    @AllArgsConstructor
    private static class LastMouseEvent{
        private final MouseEvent event;
        private final EventType type;

        private boolean isReleased() {
            return type.equals(EventType.RELEASED);
        }

        private boolean isLeftButton() {
            return MouseEvent.BUTTON1 == event.getButton();
        }

        private boolean isRightButton() {
            return MouseEvent.BUTTON3 == event.getButton();
        }
    }

    private enum EventType {
        RELEASED,
        PRESSED
    }
}
