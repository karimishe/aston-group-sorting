package app;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import app.Main;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {
    @TempDir
    Path directory;

    @Test
    void exitsSuccessfullyWhenUserSelectsZero() throws Exception {
        runMain("0\n", 0, "Работа программы завершена.", "");
    }

    @Test
    void reportsUnexpectedEndOfInputAndExitsWithError() throws Exception {
        runMain("", 1, "Меню:", "Ошибка ввода: Ввод завершён до выбора выхода.");
    }

    private void runMain(String input, int expectedExitCode, String expectedOutput,
            String expectedError) throws IOException, URISyntaxException, InterruptedException {
        String executable = System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java";
        Path java = Path.of(System.getProperty("java.home"), "bin", executable);
        Path classes = Path.of(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        Path output = directory.resolve("stdout.txt");
        Path error = directory.resolve("stderr.txt");
        // Кодировка стандартного вывода может отличаться от file.encoding.
        Process process = new ProcessBuilder(java.toString(), "-Dfile.encoding=UTF-8",
                "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8",
                "-cp", classes.toString(), Main.class.getName())
                .redirectOutput(output.toFile())
                .redirectError(error.toFile())
                .start();
        try {
            try (OutputStream stdin = process.getOutputStream()) {
                stdin.write(input.getBytes(StandardCharsets.UTF_8));
            }
            assertTrue(process.waitFor(10, TimeUnit.SECONDS), "Приложение должно завершиться");
            assertEquals(expectedExitCode, process.exitValue());
            assertTrue(Files.readString(output, StandardCharsets.UTF_8).contains(expectedOutput));
            assertEquals(expectedError, Files.readString(error, StandardCharsets.UTF_8).strip());
        } finally {
            if (process.isAlive()) {
                process.destroyForcibly();
                process.waitFor(5, TimeUnit.SECONDS);
            }
        }
    }
}
