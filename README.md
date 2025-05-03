# CR2 to JPEG Converter

This Java program automates the conversion of CR2 (Canon Raw version 2) image files to the more widely used JPEG format. It provides a user-friendly graphical interface to select either individual CR2 files or entire directories containing them.

### **Key Features and Functionality**

* **Batch Conversion**: The program can efficiently process multiple CR2 files at once, including all CR2 files within a selected directory.

* **ImageMagick Integration**: It leverages the ImageMagick command-line tool to perform the actual conversion. ImageMagick is a powerful and versatile image processing library that supports a wide range of formats, including CR2. The program locates the ImageMagick installation path.

* **Concurrent Processing**: For enhanced performance, especially when converting large numbers of files, the program uses multi-threading to process conversions concurrently.

* **User-Friendly Interface**: The program provides a simple and intuitive graphical interface using Swing. A file chooser dialog allows users to easily select the CR2 files or directories they want to convert. Clear messages are displayed to indicate the progress and outcome of the conversion process.

* **Error Handling**: The program includes robust error handling to manage potential issues such as file access problems or errors during the conversion process. Informative error messages are displayed to the user.

* **Cross-Platform Compatibility**: The core Java code is designed to be cross-platform, and the program attempts to adapt to the user's operating system.

* **Output Location**: Converted JPEG files are saved in the same directory as the original CR2 files, preserving a logical organization of files.

* **Dependencies**:

    * **Java Standard Library**: The program relies on various classes from the Java Standard Library, including `java.io.File`, `java.io.IOException`, `java.nio.file.Files`, `java.nio.file.Path`, `java.util.List`, `java.util.concurrent.ExecutorService`, `javax.swing.JFileChooser`, `javax.swing.JOptionPane`, and others. These are included with any standard Java Development Kit (JDK).
    * **External Dependency**:
        * **ImageMagick**: This is an external command-line tool, not a Java library. The program uses Java's `Runtime.getRuntime().exec()` method to execute ImageMagick commands. ImageMagick must be installed separately on the system where the Java code is run.

### **How It Works**

1.  **User Selection**: The program presents a file chooser dialog, allowing the user to select either a single CR2 file or a directory containing CR2 files.

2.  **File Processing**:

    * If a single CR2 file is selected, the program proceeds to convert it to JPEG.
    * If a directory is selected, the program scans the directory for all CR2 files and processes them in parallel using multiple threads.

3.  **CR2 to JPEG Conversion**: The program constructs and executes an ImageMagick command to convert each CR2 file to a JPEG file.

4.  **Output Generation**: The converted JPEG files are created in the same directory as the original CR2 files, with the same filename but a ".jpg" extension.

5.  **Status and Error Reporting**: The program displays messages to the user, indicating the progress of the conversion process, any errors that occur, and when the conversion is complete.

### **Key Differences**

* The default thread pool size has been increased to 30.
* The program now uses "windows" instead of "win" to check the operating system.
