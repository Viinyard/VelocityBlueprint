package pro.vinyard.vb.engine.core.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class DirectoryUtils {


    public static String getWorkingDirectory() {
        return System.getProperty("user.home");
    }

    /**
     * Create a folder in the specified directory
     *
     * @param directory  The directory
     * @param folderName The folder name
     * @return The result message
     * @throws IllegalArgumentException if directory is null
     * @throws IllegalArgumentException if folderName is null
     */
    public static File createFolder(File directory, String folderName) {
        directory = Optional.ofNullable(directory).filter(File::isDirectory).orElseThrow(() -> new IllegalArgumentException("Directory is null or not a directory"));
        folderName = Optional.ofNullable(folderName).map(StringUtils::trimToNull).orElseThrow(() -> new IllegalArgumentException("Folder name cannot be null"));

        File folder = new File(directory, folderName);

        return createFolder(folder);
    }

    /**
     * Create a folder
     *
     * @param folder The folder to create
     * @throws IllegalArgumentException if folder is null
     * @throws RuntimeException         if folder cannot be created
     * @throws RuntimeException         if folder already exists
     */
    public static File createFolder(File folder) {
        folder = Optional.ofNullable(folder).orElseThrow(() -> new IllegalArgumentException("Folder cannot be null"));

        if (folder.exists())
            return folder;

        boolean result = folder.mkdirs();

        if (!result)
            throw new RuntimeException("Cannot create folder " + folder.getAbsolutePath());

        return folder;
    }

    public static void deleteFolder(File folder) {
        try {
            FileUtils.deleteDirectory(folder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<File> listFolders(File folder) {
        try (Stream<Path> paths = Files.list(folder.toPath())) {
            return paths.filter(Files::isDirectory).map(Path::toFile).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
