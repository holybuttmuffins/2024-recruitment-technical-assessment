package src;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Collections;

public class Task {
    public record File(
        int id,
        String name,
        List<String> categories,
        int parent,
        int size
    ) {}

    /**
     * Task 1
     */
    public static List<String> leafFiles(List<File> files) {
        Map<Integer, Integer> fileTally = new HashMap<Integer, Integer>();

        // Tally up the amount of times that a file is referenced as a parent file
        for (File file : files) {
            Integer count = fileTally.getOrDefault(file.id, 0);
            Integer parentCount = fileTally.getOrDefault(file.parent, 0);
            fileTally.put(file.id, count);
            fileTally.put(file.parent, parentCount + 1);
        }

        // Extract files that have a tally of 0 which are leaf files
        List<Integer> leafFileIds = fileTally.entrySet().stream()
            .filter(file -> file.getValue().equals(0))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        // Map leaf file ids to names
        List<String> leafFiles = leafFileIds.stream()
            .map(id -> getFile(id, files).name)
            .sorted()
            .collect(Collectors.toList());

        return leafFiles;
    }

    /**
     * Task 2
     */
    public static List<String> kLargestCategories(List<File> files, int k) {
        // Count the number of times a category appears
        Map<String, Integer> categories = new HashMap<String, Integer>();
        for (File file : files) {
            for (String category : file.categories) {
                Integer count = categories.getOrDefault(category, 0);
                categories.put(category, count + 1);
            }
        }

        // Sort the list of categories from highest -> lowest
        List<String> largestCategories = categories.entrySet().stream()
            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        // Take the first k categories and sort them alphabetically
        List<String> kLargestCategories =  largestCategories.stream().limit(k).sorted().collect(Collectors.toList());

        return kLargestCategories;
    }

    /**
     * Task 3
     */
    public static int largestFileSize(List<File> files) {
        Map<Integer, Integer> parentFiles = new HashMap<Integer, Integer>();
        // Determine the parent file for each file and add to the total file size of the parent
        for (File file : files) {
            Integer parentId = getParentFile(file, files);
            Integer fileSize = parentFiles.getOrDefault(parentId, 0);
            parentFiles.put(getParentFile(file, files), fileSize + file.size);
        }

        // Get the file with the largest file size
        return Collections.max(parentFiles.entrySet(), Map.Entry.comparingByValue()).getValue();
    }

    /**
     * Helper functions
     */

    // Get a file by its file id
    private static File getFile(Integer id, List<File> files) {
        return files.stream()
        .filter(file -> id.equals(file.id))
        .findAny()
        .orElse(null);
    }

    // Get a parent file from a child file
    private static Integer getParentFile(File file, List<File> files) {
        if (file.parent == -1) {
            return file.id;
        }

        Integer parentId = file.parent;
        File parent = getFile(parentId, files);
                    
        return getParentFile(parent, files);
    }

    public static void main(String[] args) {
        List<File> testFiles = List.of(
            new File(1, "Document.txt", List.of("Documents"), 3, 1024),
            new File(2, "Image.jpg", List.of("Media", "Photos"), 34, 2048),
            new File(3, "Folder", List.of("Folder"), -1, 0),
            new File(5, "Spreadsheet.xlsx", List.of("Documents", "Excel"), 3, 4096),
            new File(8, "Backup.zip", List.of("Backup"), 233, 8192),
            new File(13, "Presentation.pptx", List.of("Documents", "Presentation"), 3, 3072),
            new File(21, "Video.mp4", List.of("Media", "Videos"), 34, 6144),
            new File(34, "Folder2", List.of("Folder"), 3, 0),
            new File(55, "Code.py", List.of("Programming"), -1, 1536),
            new File(89, "Audio.mp3", List.of("Media", "Audio"), 34, 2560),
            new File(144, "Spreadsheet2.xlsx", List.of("Documents", "Excel"), 3, 2048),
            new File(233, "Folder3", List.of("Folder"), -1, 4096)
        );
        
        List<String> leafFiles = leafFiles(testFiles);
        leafFiles.sort(null);
        assert leafFiles.equals(List.of(
            "Audio.mp3",
            "Backup.zip",
            "Code.py",
            "Document.txt",
            "Image.jpg",
            "Presentation.pptx",
            "Spreadsheet.xlsx",
            "Spreadsheet2.xlsx",
            "Video.mp4"
        ));

        assert kLargestCategories(testFiles, 3).equals(List.of(
            "Documents", "Folder", "Media"
        ));

        assert largestFileSize(testFiles) == 20992;
    }
}