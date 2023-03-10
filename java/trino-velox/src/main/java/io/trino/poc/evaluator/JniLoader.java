package io.trino.poc.evaluator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

import static java.util.UUID.randomUUID;

class JniLoader {
    private static final String LIBRARY_NAME = "velox_jni";

    private static volatile JniLoader INSTANCE;

    private final JniWrapper wrapper;

    private JniLoader() {
        this.wrapper = new JniWrapper();
    }

    static JniLoader getInstance() {
        if (INSTANCE == null) {
            synchronized (JniLoader.class) {
                if (INSTANCE == null) {
                    INSTANCE = setupInstance();
                }
            }
        }
        return INSTANCE;
    }

    private static JniLoader setupInstance() {
        try {
            String tempDir = System.getProperty("java.io.tmpdir");
            loadJniLibraryFromJar(tempDir);
            return new JniLoader();
        } catch (IOException ioException) {
            throw new RuntimeException("unable to create native instance", ioException);
        }
    }

    private static void loadJniLibraryFromJar(final String tmpDir)
            throws IOException {
        final String libraryToLoad =
                getNormalizedArch() + File.separator + System.mapLibraryName(LIBRARY_NAME);
        final File libraryFile = moveFileFromJarToTemp(tmpDir, libraryToLoad, LIBRARY_NAME);
        System.load(libraryFile.getAbsolutePath());
    }

    private static String getNormalizedArch() {
        String arch = System.getProperty("os.arch").toLowerCase(Locale.US);
        switch (arch) {
            case "amd64":
                arch = "x86_64";
                break;
            case "aarch64":
                arch = "aarch_64";
                break;
            default:
                break;
        }
        return arch;
    }

    private static File moveFileFromJarToTemp(final String tmpDir, String libraryToLoad, String libraryName)
            throws IOException {
        final File temp = setupFile(tmpDir, libraryName);
        try (final InputStream is = JniLoader.class.getClassLoader()
                .getResourceAsStream(libraryToLoad)) {
            if (is == null) {
                throw new RuntimeException(libraryToLoad + " was not found inside JAR.");
            } else {
                Files.copy(is, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        return temp;
    }

    private static File setupFile(String tmpDir, String libraryToLoad)
            throws IOException {

        final String randomizeFileName = libraryToLoad + randomUUID();
        final File temp = new File(tmpDir, randomizeFileName);
        if (temp.exists() && !temp.delete()) {
            throw new RuntimeException("File: " + temp.getAbsolutePath() +
                    " already exists and cannot be removed.");
        }
        if (!temp.createNewFile()) {
            throw new RuntimeException("File: " + temp.getAbsolutePath() +
                    " could not be created.");
        }
        temp.deleteOnExit();
        return temp;
    }

    /**
     * Returns the jni wrapper.
     */
    JniWrapper getWrapper() {
        return wrapper;
    }


}
