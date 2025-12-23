import java.io.File;
import java.util.*;

public class Utils {

    private static final long LARGE_FILE_THRESHOLD = 100L * 1024 * 1024;
    private static final Set<String> ARCHIVE_EXTS = new HashSet<>(Arrays.asList(
            "zip", "rar", "7z", "tar", "gz", "bz2", "xz", "jar", "tgz", "tbz2"
    ));

    public static class FileInfo {
        String name;
        long size;
        boolean isDirectory;

        FileInfo(String name, long size, boolean isDirectory) {
            this.name = name;
            this.size = size;
            this.isDirectory = isDirectory;
        }

        boolean isArchive() {
            if (isDirectory) return false;
            int dot = name.lastIndexOf('.');
            if (dot == -1) return false;
            String ext = name.substring(dot + 1).toLowerCase();
            return ARCHIVE_EXTS.contains(ext);
        }

        boolean isLarge() {
            return size > LARGE_FILE_THRESHOLD;
        }
    }

    private static List<FileInfo> getAllFilesSorted(String directory) {
        File dir = new File(directory);
        File[] files = dir.listFiles();
        if (files == null) files = new File[0];

        List<FileInfo> list = new ArrayList<>();
        for (File f : files) {
            if (!f.isHidden()) {
                list.add(new FileInfo(f.getName(), f.length(), f.isDirectory()));
            }
        }

        list.sort((a, b) -> Long.compare(b.size, a.size));
        return list;
    }

    public static List<List<FileInfo>> getGroupedFiles(String directory) {
        List<FileInfo> all = getAllFilesSorted(directory);

        List<FileInfo> largeArchives = new ArrayList<>();
        List<FileInfo> largeFiles = new ArrayList<>();
        List<FileInfo> smallArchives = new ArrayList<>();
        List<FileInfo> others = new ArrayList<>();

        for (FileInfo fi : all) {
            if (fi.isDirectory) {
                others.add(fi);
            } else if (fi.isLarge() && fi.isArchive()) {
                largeArchives.add(fi);
            } else if (fi.isLarge()) {
                largeFiles.add(fi);
            } else if (fi.isArchive()) {
                smallArchives.add(fi);
            } else {
                others.add(fi);
            }
        }

        return Arrays.asList(largeArchives, largeFiles, smallArchives, others);
    }


    public static List<FileInfo> getAllFilesInOrder(String directory) {
        List<List<FileInfo>> groups = getGroupedFiles(directory);
        List<FileInfo> ordered = new ArrayList<>();
        ordered.addAll(groups.get(0));
        ordered.addAll(groups.get(1));
        ordered.addAll(groups.get(2));
        ordered.addAll(groups.get(3));
        return ordered;
    }
}