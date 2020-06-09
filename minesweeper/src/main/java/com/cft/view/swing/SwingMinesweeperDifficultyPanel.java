package com.cft.view.swing;

import com.cft.api.DifficultyCode;
import com.cft.api.PojoDifficulty;
import com.cft.model.difficulty.DifficultyValuesAndConstraints;
import com.cft.util.Formatters;
import com.google.common.primitives.Ints;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
class SwingMinesweeperDifficultyPanel extends JPanel {
    private static final int TEXT_FIELD_WIDTH = 30;
    private static final int TEXT_FIELD_HEIGHT = 20;

    private static final String ROW_LABEL_TEXT = "Rows (" + DifficultyValuesAndConstraints.MINIMUM_ROW_QUANTITY + "-" +
                                                 DifficultyValuesAndConstraints.MAXIMUM_ROW_QUANTITY + ")";
    private static final String COLUMN_LABEL_TEXT =
            "Columns (" + DifficultyValuesAndConstraints.MINIMUM_COLUMN_QUANTITY + "-" +
            DifficultyValuesAndConstraints.MAXIMUM_COLUMN_QUANTITY + ")";
    private static final String MINES_LABEL_TEXT =
            "Mines (" + DifficultyValuesAndConstraints.MINIMUM_MINE_QUANTITY + "-" +
            DifficultyValuesAndConstraints.MAXIMUM_MINE_QUANTITY + ")";

    private static final double SPACE_BETWEEN_COLUMN = 0.5;
    private static final int FIRST_COLUMN = 0;
    private static final int SECOND_COLUMN = 1;
    private static final int THIRD_COLUMN = 2;
    private static final int FIRST_ROW = 0;
    private static final int SECOND_ROW = 1;
    private static final int THIRD_ROW = 2;
    private static final int FOURTH_ROW = 3;

    private static final int MIN_NUMBER = 1;

    private final JTextField rowTextField;
    private final JTextField columnTextField;
    private final JTextField minesTextField;

    private final List<SwingMinesweeperDifficultyButton> buttons = new ArrayList<>();

    SwingMinesweeperDifficultyPanel(PojoDifficulty difficulty) {
        super(new GridBagLayout());

        this.rowTextField = new JFormattedTextField(Formatters.createIntegerFormatter(MIN_NUMBER, DifficultyValuesAndConstraints.MAXIMUM_ROW_QUANTITY));
        this.columnTextField = new JFormattedTextField(Formatters.createIntegerFormatter(MIN_NUMBER, DifficultyValuesAndConstraints.MAXIMUM_COLUMN_QUANTITY));
        this.minesTextField = new JFormattedTextField(Formatters.createIntegerFormatter(MIN_NUMBER, DifficultyValuesAndConstraints.MAXIMUM_MINE_QUANTITY));

        SwingMinesweeperDifficultyButton easyDifficultyRadioButton = new SwingMinesweeperDifficultyButton(
                DifficultyValuesAndConstraints.EASY_DIFFICULTY_NAME, DifficultyCode.EASY);
        SwingMinesweeperDifficultyButton mediumDifficultyRadioButton = new SwingMinesweeperDifficultyButton(
                DifficultyValuesAndConstraints.MEDIUM_DIFFICULTY_NAME, DifficultyCode.MEDIUM);
        SwingMinesweeperDifficultyButton hardDifficultyRadioButton = new SwingMinesweeperDifficultyButton(
                DifficultyValuesAndConstraints.HARD_DIFFICULTY_NAME, DifficultyCode.HARD);
        SwingMinesweeperDifficultyButton customDifficultyRadioButton = new SwingMinesweeperDifficultyButton(
                DifficultyValuesAndConstraints.CUSTOM_DIFFICULTY_NAME, DifficultyCode.CUSTOM);

        customDifficultyRadioButton.addActionListener(e -> {
            if (customDifficultyRadioButton.isSelected()) {
                setTextFieldsEditable(true);
            } else {
                setTextFieldsEditable(false);
            }
        });

        groupButtons(easyDifficultyRadioButton, mediumDifficultyRadioButton, hardDifficultyRadioButton,
                     customDifficultyRadioButton);

        buttons.addAll(Arrays.asList(easyDifficultyRadioButton, mediumDifficultyRadioButton, hardDifficultyRadioButton,
                                     customDifficultyRadioButton));

        setSizesForTextFields();
        setTextFieldsEditable(false);

        switch (difficulty.getDifficultyCode()) {
            case EASY: {
                easyDifficultyRadioButton.setSelected(true);
                break;
            }
            case MEDIUM: {
                mediumDifficultyRadioButton.setSelected(true);
                break;
            }
            case HARD:{
                hardDifficultyRadioButton.setSelected(true);
                break;
            }
            case CUSTOM:{
                setTextFieldsEditable(true);
                setTextForTextFields(difficulty);
                customDifficultyRadioButton.setSelected(true);
            }
        }

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = SPACE_BETWEEN_COLUMN;

        setFirstColumn(easyDifficultyRadioButton, mediumDifficultyRadioButton, hardDifficultyRadioButton, constraints);
        setSecondColumn(customDifficultyRadioButton, constraints);
        setThirdColumn(constraints);
    }

    private void setTextForTextFields(PojoDifficulty difficulty) {
        rowTextField.setText(String.valueOf(difficulty.getRowQuantity()));
        columnTextField.setText(String.valueOf(difficulty.getColumnQuantity()));
        minesTextField.setText(String.valueOf(difficulty.getMineQuantity()));
    }

    private void setSizesForTextFields() {
        rowTextField.setPreferredSize(new Dimension(TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT));
        columnTextField.setPreferredSize(new Dimension(TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT));
        minesTextField.setPreferredSize(new Dimension(TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT));
    }

    private void groupButtons(SwingMinesweeperDifficultyButton... radioButtons) {
        ButtonGroup buttonGroup = new ButtonGroup();
        for (SwingMinesweeperDifficultyButton radioButton : radioButtons) {
            buttonGroup.add(radioButton);
        }
    }

    private void setTextFieldsEditable(boolean isEditable) {
        rowTextField.setEditable(isEditable);
        columnTextField.setEditable(isEditable);
        minesTextField.setEditable(isEditable);
    }

    private void setFirstColumn(SwingMinesweeperDifficultyButton easyDifficultyRadioButton,
                                SwingMinesweeperDifficultyButton mediumDifficultyRadioButton,
                                SwingMinesweeperDifficultyButton hardDifficultyRadioButton, GridBagConstraints constraints) {
        constraints.gridx = FIRST_COLUMN;
        constraints.gridy = FIRST_ROW;
        add(easyDifficultyRadioButton, constraints);

        constraints.gridy = SECOND_ROW;
        add(mediumDifficultyRadioButton, constraints);

        constraints.gridy = THIRD_ROW;
        add(hardDifficultyRadioButton, constraints);
    }

    private void setSecondColumn(SwingMinesweeperDifficultyButton customDifficultyRadioButton,
                                 GridBagConstraints constraints) {
        constraints.gridx = SECOND_COLUMN;
        constraints.gridy = FIRST_ROW;
        add(customDifficultyRadioButton, constraints);

        constraints.gridy = SECOND_ROW;
        add(new JLabel(ROW_LABEL_TEXT), constraints);

        constraints.gridy = THIRD_ROW;
        add(new JLabel(COLUMN_LABEL_TEXT), constraints);

        constraints.gridy = FOURTH_ROW;
        add(new JLabel(MINES_LABEL_TEXT), constraints);

        add(minesTextField, constraints);
    }

    private void setThirdColumn(GridBagConstraints constraints) {
        constraints.gridx = THIRD_COLUMN;
        constraints.gridy = SECOND_ROW;
        add(rowTextField, constraints);

        constraints.gridy = THIRD_ROW;
        add(columnTextField, constraints);

        constraints.gridy = FOURTH_ROW;
        add(minesTextField, constraints);
    }

    @SuppressWarnings("UnstableApiUsage")
    PojoDifficulty getSelectedDifficulty() {
        PojoDifficulty selectedPojoDifficulty = new PojoDifficulty();
        for (SwingMinesweeperDifficultyButton button : buttons) {
            if (!button.isSelected()) {
                continue;
            }

            DifficultyCode selectedDifficultyCode = button.getDifficultyCode();
            selectedPojoDifficulty.setDifficultyCode(selectedDifficultyCode);

            if (!selectedDifficultyCode.equals(DifficultyCode.CUSTOM)) {
                break;
            }

            Integer rowQuantity = Ints.tryParse(rowTextField.getText());
            Integer columnQuantity = Ints.tryParse(columnTextField.getText());
            Integer mineQuantity = Ints.tryParse(minesTextField.getText());

            if (rowQuantity == null || columnQuantity == null || mineQuantity == null) {
                log.info("Не удалось преобразовать параметры сложности 'Custom' к числам");
                break;
            }

            selectedPojoDifficulty.setRowQuantity(rowQuantity);
            selectedPojoDifficulty.setColumnQuantity(columnQuantity);
            selectedPojoDifficulty.setMineQuantity(mineQuantity);
        }

        return selectedPojoDifficulty;
    }
}
