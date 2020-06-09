package com.cft.model.score;

import com.cft.util.CryptoUtils;
import com.google.common.base.Charsets;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class MinesweeperGameResultManipulator {
    private static final String RESULTS_FILE_PATH = "results.txt";
    private static final Charset RESULTS_FILE_CHARSET = Charsets.UTF_8;
    private static final String SECRET_KEY = "EW6zQ87WrwGL1ekm";

    private final MinesweeperGameResultParser minesweeperGameResultParser = new MinesweeperGameResultParser();

    @Getter
    private final List<MinesweeperGameResult> minesweeperGameResults;

    public MinesweeperGameResultManipulator() {
        this.minesweeperGameResults = fetchExistedResultsFromFile();
    }

    public MinesweeperGameResultManipulator(List<MinesweeperGameResult> minesweeperGameResults) {
        this.minesweeperGameResults = minesweeperGameResults;
    }

    /**
     * Возращает список игровых результатов.
     * Если файл с результатами существует, читает его.
     * Инача создает новый.
     *
     * @return список игровых результатов
     */
    private List<MinesweeperGameResult> fetchExistedResultsFromFile() {
        Path resultFilePath = Paths.get(RESULTS_FILE_PATH);
        List<MinesweeperGameResult> minesweeperGameResults = new ArrayList<>();

        if (Files.exists(resultFilePath)) {
            minesweeperGameResults.addAll(readResultsFromFile(resultFilePath));
        } else {
            createResultFile(resultFilePath);
        }

        return minesweeperGameResults;
    }

    private List<MinesweeperGameResult> readResultsFromFile(Path resultFilePath) {
        List<MinesweeperGameResult> minesweeperGameResults = new ArrayList<>();

        try {
            byte[] encryptedBytes = Files.readAllBytes(resultFilePath);
            Optional<byte[]> decryptedBytes = CryptoUtils.decrypt(encryptedBytes, SECRET_KEY);
            log.info("Зашифрованный файл с результатами был успешно прочитан");

            if (decryptedBytes.isPresent()) {
                String resultFileInOneString = new String(decryptedBytes.get(), RESULTS_FILE_CHARSET);
                List<String> linesFromResultFile = Arrays.asList(resultFileInOneString.split(System.lineSeparator()));
                minesweeperGameResults.addAll(minesweeperGameResultParser.parseStringsToGameResults(linesFromResultFile));
                log.info("Файл с результатами был успешно расширован, содержит '{}' строк", linesFromResultFile.size());
            }
        } catch (IOException e) {
            log.error("Не удалось прочитать файл с результатами '{}'", RESULTS_FILE_PATH);
        }

        return minesweeperGameResults;
    }

    private void createResultFile(Path resultFilePath) {
        try {
            Files.createFile(resultFilePath);
            log.info("Файл для хранения результатов был создан");
        } catch (IOException e) {
            log.error("Не удалось создать файл для хранения результатов");
        }
    }

    /**
     * Обновляет файл с результатами, но только в том случае, если {@code newMinesweeperGameResult}
     * должен заменить существующий рекорд или является результатом для уровня сложности, у которого еще нет ни одного рекорда
     *
     * @param newMinesweeperGameResult новый результат игры
     */
    public void updateFileWithResults(MinesweeperGameResult newMinesweeperGameResult) {
        Optional<MinesweeperGameResult> candidateForReplacement = searchCandidateForReplacement(
                newMinesweeperGameResult);

        if (!candidateForReplacement.isPresent()) {
            minesweeperGameResults.add(newMinesweeperGameResult);
            log.info("Добавлен новый рекорд для уровня сложности '{}'",
                     newMinesweeperGameResult.getGameDifficultyName());
        } else if (candidateForReplacement.get().getGameTime() > newMinesweeperGameResult.getGameTime()) {
            int indexOfElementToReplace = minesweeperGameResults.indexOf(candidateForReplacement.get());
            minesweeperGameResults.set(indexOfElementToReplace, newMinesweeperGameResult);
            log.info("Рекорд для сложности '{}' обновлен", newMinesweeperGameResult.getGameDifficultyName());
        } else {
            log.info("Результат игры: '{}' не является рекордом", newMinesweeperGameResult.toString());
            return;
        }

        List<String> gameResultStrings = minesweeperGameResults.stream()
                                                               .map(MinesweeperGameResult::getCsvView)
                                                               .collect(Collectors.toList());
        writeToResultsFile(gameResultStrings);
    }

    /**
     * Возращает кандидата для замены или {@code null} если подходящий кандидат не найден.
     * Подходящий кандидат - кандидат с тем же уровнем сложности, что и новый результат, но с худшим временем.
     *
     * @param newMinesweeperGameResult новый результат игры
     * @return кандидата для замены или {@code null} если подходящий кандидат не найден
     */
    private Optional<MinesweeperGameResult> searchCandidateForReplacement(
            MinesweeperGameResult newMinesweeperGameResult) {
        Optional<MinesweeperGameResult> candidateForReplacement = Optional.empty();

        for (MinesweeperGameResult minesweeperGameResult : minesweeperGameResults) {
            if (minesweeperGameResult.getGameDifficultyName()
                                     .equalsIgnoreCase(newMinesweeperGameResult.getGameDifficultyName())) {
                candidateForReplacement = Optional.of(minesweeperGameResult);
                break;
            }
        }

        return candidateForReplacement;
    }

    private void writeToResultsFile(List<String> gameResultStrings) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String string : gameResultStrings) {
            stringBuilder.append(string).append(System.lineSeparator());
        }

        Optional<byte[]> encryptedBytes = CryptoUtils.encrypt(stringBuilder.toString().getBytes(), SECRET_KEY);
        if (!encryptedBytes.isPresent()) {
            log.warn("Не удалось зашифровать результаты игры");
            return;
        }

        log.info("Результаты игры успешно зашифрованы");
        try {
            Files.write(Paths.get(RESULTS_FILE_PATH), encryptedBytes.get());
            log.info("Файл с результатами обновлен");
        } catch (IOException e) {
            log.error("Не удалось записать результаты в файл {}", RESULTS_FILE_PATH, e);
        }
    }
}
