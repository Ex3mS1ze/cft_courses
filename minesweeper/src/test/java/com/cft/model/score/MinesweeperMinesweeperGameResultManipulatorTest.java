package com.cft.model.score;

import com.cft.model.difficulty.CustomMinesweeperDifficulty;
import com.cft.model.difficulty.DifficultyValuesAndConstraints;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MinesweeperGameResultManipulator.class})
public class MinesweeperMinesweeperGameResultManipulatorTest {
    private static final long EASY_GAME_TIME = 100L;
    private static final long MEDIUM_GAME_TIME = 200L;
    private static final long HARD_GAME_TIME = 400L;
    private static final long CUSTOM_GAME_TIME = 800L;
    private static final String PRIVATE_METHOD_NAME_INSIDE_updateFileWithResults = "writeToResultsFile";
    private static final String NAME = "name";

    private final ArrayList<MinesweeperGameResult> minesweeperGameResults = new ArrayList<>();
    private final String customGameName;
    private MinesweeperGameResultManipulator manipulator;
    private MinesweeperGameResultManipulator spyManipulator;

    public MinesweeperMinesweeperGameResultManipulatorTest() {
        CustomMinesweeperDifficulty customDifficulty = new CustomMinesweeperDifficulty(20, 20, 20);
        this.customGameName = customDifficulty.getName();
    }

    @Before
    public void setUp() throws Exception {
        minesweeperGameResults.clear();
        minesweeperGameResults.add(new MinesweeperGameResult(DifficultyValuesAndConstraints.EASY_DIFFICULTY_NAME, EASY_GAME_TIME,
                                                             LocalDateTime.now(), NAME));
        minesweeperGameResults.add(new MinesweeperGameResult(DifficultyValuesAndConstraints.MEDIUM_DIFFICULTY_NAME, MEDIUM_GAME_TIME,
                                                             LocalDateTime.now(), NAME));
        minesweeperGameResults.add(new MinesweeperGameResult(DifficultyValuesAndConstraints.HARD_DIFFICULTY_NAME, HARD_GAME_TIME,
                                                             LocalDateTime.now(), NAME));
        minesweeperGameResults.add(new MinesweeperGameResult(customGameName, CUSTOM_GAME_TIME, LocalDateTime.now(), NAME));

        manipulator = new MinesweeperGameResultManipulator(minesweeperGameResults);
        spyManipulator = spy(manipulator);
        doNothing().when(spyManipulator,
                         method(MinesweeperGameResultManipulator.class, PRIVATE_METHOD_NAME_INSIDE_updateFileWithResults))
                   .withArguments(ArgumentMatchers.anyList());
    }

    @Test
    public void updateFileWithResults_updateFileResultsWithPassedGameResult_whenPassedGameResultWithEasyDifficultyAndBetterGameTime() throws Exception {
        MinesweeperGameResult newEasyMinesweeperGameResult = new MinesweeperGameResult(DifficultyValuesAndConstraints.EASY_DIFFICULTY_NAME,
                                                      EASY_GAME_TIME - 1, LocalDateTime.now(), NAME);

        spyManipulator.updateFileWithResults(newEasyMinesweeperGameResult);

        verifyPrivate(spyManipulator, Mockito.times(1)).invoke(PRIVATE_METHOD_NAME_INSIDE_updateFileWithResults,
                                                               ArgumentMatchers.anyList());
        assertTrue(manipulator.getMinesweeperGameResults().contains(newEasyMinesweeperGameResult));
    }

    @Test
    public void updateFileWithResults_updateFileResultsWithPassedGameResult_whenPassedGameResultWithMediumDifficultyAndBetterGameTime() throws Exception {
        MinesweeperGameResult newMediumMinesweeperGameResult = new MinesweeperGameResult(DifficultyValuesAndConstraints.MEDIUM_DIFFICULTY_NAME,
                                                        MEDIUM_GAME_TIME - 1, LocalDateTime.now(), NAME);

        spyManipulator.updateFileWithResults(newMediumMinesweeperGameResult);

        verifyPrivate(spyManipulator, Mockito.times(1)).invoke(PRIVATE_METHOD_NAME_INSIDE_updateFileWithResults,
                                                               ArgumentMatchers.anyList());
        assertTrue(manipulator.getMinesweeperGameResults().contains(newMediumMinesweeperGameResult));
    }

    @Test
    public void updateFileWithResults_updateFileResultsWithPassedGameResult_whenPassedGameResultWithHardDifficultyAndBetterGameTime() throws Exception {
        MinesweeperGameResult newHardMinesweeperGameResult = new MinesweeperGameResult(DifficultyValuesAndConstraints.HARD_DIFFICULTY_NAME,
                                                      HARD_GAME_TIME - 1, LocalDateTime.now(), NAME);

        spyManipulator.updateFileWithResults(newHardMinesweeperGameResult);

        verifyPrivate(spyManipulator, Mockito.times(1)).invoke(PRIVATE_METHOD_NAME_INSIDE_updateFileWithResults,
                                                               ArgumentMatchers.anyList());
        assertTrue(manipulator.getMinesweeperGameResults().contains(newHardMinesweeperGameResult));
    }

    @Test
    public void updateFileWithResults_updateFileResultsWithPassedGameResult_whenPassedGameResultWithCustomDifficultyAndBetterGameTime() throws Exception {
        MinesweeperGameResult customMinesweeperGameResult = new MinesweeperGameResult(customGameName, CUSTOM_GAME_TIME - 1, LocalDateTime.now(), NAME);

        spyManipulator.updateFileWithResults(customMinesweeperGameResult);

        verifyPrivate(spyManipulator, Mockito.times(1)).invoke(PRIVATE_METHOD_NAME_INSIDE_updateFileWithResults,
                                                               ArgumentMatchers.anyList());
        assertTrue(manipulator.getMinesweeperGameResults().contains(customMinesweeperGameResult));
    }
}