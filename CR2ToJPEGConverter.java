import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CR2ToJPEGConverter {

    private static final int THREAD_POOL_SIZE = 30; // Adjust as needed
    private static final String OS = System.getProperty("os.name").toLowerCase();

    public static void main(String[] args) {

        // Set cross-platform look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace(); // Handle the exception as appropriate for your application
        }

        SwingUtilities.invokeLater(() -> {
            File selectedFile = chooseFileOrDirectory();
            if (selectedFile != null) {
                processFiles(selectedFile);
            }
        });
    }

    /**
     * Opens a file chooser dialog to select a file or directory.
     *
     * @return The selected File object, or null if no file/directory was
     * chosen.
     */
    private static File chooseFileOrDirectory() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setDialogTitle("Select CR2 File(s) or Directory");

        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        } else {
            JOptionPane.showMessageDialog(null, "No file or directory selected.", "Cancelled", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    /**
     * Processes the selected file or directory. If it's a directory, it
     * recursively processes all files within it. CR2 files are converted to
     * JPEG, and other files are ignored.
     *
     * @param file The File object to process.
     */
    private static void processFiles(File file) {
        if (file.isDirectory()) {
            handleDirectory(file);
        } else {
            handleFile(file);
        }
    }

    private static void handleDirectory(File directory) {
        try {
            // Use try-with-resources to automatically close the stream
            List<Path> files = listFiles(directory.toPath());
            if (files.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No files found in the selected directory.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

            for (Path file : files) {
                executorService.submit(() -> handleFile(file.toFile())); // Pass File
            }

            executorService.shutdown();
            try {
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS); // Wait for all tasks to complete
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore the interrupted status
                JOptionPane.showMessageDialog(null, "Conversion process interrupted.", "Error", JOptionPane.ERROR_MESSAGE);
                return; // Exit the method
            }

            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "All CR2 files processed.", "Done", JOptionPane.INFORMATION_MESSAGE);
            });

        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "Error reading directory: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            e.printStackTrace();
        }
    }

    private static List<Path> listFiles(Path directory) throws IOException {
        List<Path> fileList = new ArrayList<>();
        Files.walk(directory)
                .filter(Files::isRegularFile)
                .forEach(fileList::add);
        return fileList;
    }

    private static void handleFile(File file) {
        String fileName = file.getName();
        if (fileName.toLowerCase().endsWith(".cr2")) {
            convertCR2ToJPEG(file);
        } else {
            System.out.println("Skipping non-CR2 file: " + fileName);
        }
    }

    /**
     * Converts a CR2 file to JPEG using the ImageMagick `convert` command.
     *
     * @param cr2File The CR2 File object to convert.
     */
    private static void convertCR2ToJPEG(File cr2File) {
        String cr2FilePath = cr2File.getAbsolutePath();
        String jpegFilePath = cr2FilePath.substring(0, cr2FilePath.lastIndexOf('.')) + ".jpg"; // change extension to .jpg

        // Construct the ImageMagick command.  Use absolute paths.
        String command;
        if (OS.contains("windows")) {
            command = "magick \"" + cr2FilePath + "\" \"" + jpegFilePath + "\""; // For Windows
        } else {
            command = "convert \"" + cr2FilePath + "\" \"" + jpegFilePath + "\""; // For Linux and macOS
        }

        try {
            Process process = Runtime.getRuntime().exec(command); // Execute the command
            int exitCode = process.waitFor(); // Wait for the process to complete.

            if (exitCode == 0) {
                System.out.println("Converted: " + cr2FilePath + " to " + jpegFilePath);
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, "Converted CR2 to JPEG", "Success", JOptionPane.INFORMATION_MESSAGE);
                });
            } else {
                System.err.println("Error converting " + cr2FilePath + ". Exit code: " + exitCode);
                //Improved error message
                String errorMessage = "Failed to convert " + cr2FilePath + " to JPEG.  Exit Code: " + exitCode
                        + ".  Please ensure ImageMagick is correctly installed and the 'convert' command is available in your system's PATH.";

                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, errorMessage, "Conversion Error", JOptionPane.ERROR_MESSAGE);
                });
            }
            process.destroy();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace(); /* Log the exception */
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "Error during conversion: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            });
        }
    }
}

