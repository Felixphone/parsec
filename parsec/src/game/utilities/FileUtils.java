package game.utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicReference;

public class FileUtils {

    public static boolean createDirectory(String path) {
        return new File(path).mkdirs();
    }

    public static boolean createFile(String path) throws IOException {
        return new File(path).createNewFile();
    }

    public static void writeToFile(String path, String content) throws IOException {
        FileWriter fileWriter = new FileWriter(path);
        fileWriter.write(content);
        fileWriter.close();
    }

    public static void deleteFile(String path) throws IOException {
        Files.delete(Paths.get(path));
    }

    public static void deleteDirectory(String path) throws IOException {
        AtomicReference<IOException> e = new AtomicReference<>();
        Path dir = Paths.get(path); //path to the directory
        Files
                .walk(dir) // Traverse the file tree in depth-first order
                .sorted(Comparator.reverseOrder())
                .forEach(subPath -> {
                    try {
                        Files.delete(subPath);  //delete each file or directory
                    } catch (IOException e2) {
                        e.set(e2);
                    }
                });

        if (e.get() != null) {
            IOException exception = e.get();
            throw exception;
        }
    }
}
