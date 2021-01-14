import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main {
    private final static String archName = "source_archive.zip";

    public static void main(String[] args) {
        List<String> fileNames = readArchive();
        Map<String, List<Integer>> outputDate = readFiles(fileNames);
    }

    /**
     * Метод readArchive()
     * Читаем файлы из архива
     */
    public static List<String> readArchive() {
        List<String> fileNames = new ArrayList<>();
        System.out.println("------");
        System.out.println("Извлекаем файлы из архива:");
        try(ZipInputStream zin = new ZipInputStream(new FileInputStream(archName))) {
            ZipEntry entry;
            String name;
            long size;
            while ((entry = zin.getNextEntry()) != null) {
                name = entry.getName(); // получим название файла
                size = entry.getSize();  // получим его размер в байтах
                System.out.printf("File name: %s \t File size: %d \n", name, size);
                // распаковка
                String newName = "new" + name;
                FileOutputStream fout = new FileOutputStream(newName);
                for (int c = zin.read(); c != -1; c = zin.read()) {
                    fout.write(c);
                }
                fout.flush();
                zin.closeEntry();
                fout.close();
                fileNames.add(newName);
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return fileNames;
    }

    /**
     * Метод readFiles()
     * Читаем данные из файлов, ищем совпадения в
     * @see Label
     */
    public static Map<String, List<Integer>> readFiles(List<String> fileNames) {
        Map<String, List<Integer>> outputDate = new HashMap<>();
        System.out.println("------");
        try {
            for (String fileName : fileNames) {
                System.out.println("Читаем файл: " + fileName);
                List<String> lines = Files.readAllLines(Paths.get(fileName));
                for (String line : lines)
                    if (!line.startsWith("#")) {
                        String[] fragments = line.split(",");
                        String newLabel = fragments[0].substring(0, 4).toLowerCase() +
                                fragments[0].substring(4).toUpperCase();
                        int value = Integer.parseInt(fragments[1]);
                        List<Integer> values;
                        if (outputDate.containsKey(newLabel))
                            values = outputDate.get(newLabel);
                        else
                            values = new ArrayList<>();
                        values.add(value);
                        outputDate.put(newLabel, values);
                    }
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("------");
        for (String key: outputDate.keySet())
            System.out.println(key + " " + outputDate.get(key));
        return outputDate;
    }
}
