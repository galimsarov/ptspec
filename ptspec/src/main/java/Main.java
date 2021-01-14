import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
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
        processingResult(outputDate);
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
     * Метод readFiles(List<String> fileNames)
     * Читаем данные из файлов, сводим в один мап
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

    /**
     * Метод processingResult(Map<String, List<Integer>> outputDate)
     * Выводим три варианта итогового отчёта
     */
    public static void processingResult(Map<String, List<Integer>> outputDate) {
        try {
            System.out.println("------");
            FileWriter writer = new FileWriter("src/main/resources/final_report_var1.json");
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.serializeNulls().setPrettyPrinting().create();
            Map<String, Integer> temp = new TreeMap<>();
            for (String label : outputDate.keySet())
                temp.put(label, outputDate.get(label).stream().mapToInt(i -> i).sum());
            String json = gson.toJson(temp);
            writer.write(json);
            writer.flush();
            System.out.println("В json файл записан отчёт по варианту 1");

            writer = new FileWriter("src/main/resources/final_report_var2.json");
            temp = new TreeMap<>();
            for (Label value : Label.values())
                if (outputDate.containsKey(value.toString()))
                    temp.put(value.toString(),
                            outputDate.get(value.toString()).stream().mapToInt(i -> i).sum());
                else
                    temp.put(value.toString(), null);
            json = gson.toJson(temp);
            writer.write(json);
            writer.flush();
            System.out.println("В json файл записан отчёт по варианту 2");

            writer = new FileWriter("src/main/resources/final_report_var3.json");

            Map<String, Integer[]> newTemp = new TreeMap<>();
            for (String label : outputDate.keySet()) {
                int[] primArray = outputDate.get(label).stream().mapToInt(i -> i).toArray();
                Integer[] objArray = Arrays.stream(primArray).boxed().toArray(Integer[]::new);
                Arrays.sort(objArray, Collections.reverseOrder());
                newTemp.put(label, objArray);
            }
            json = gson.toJson(newTemp);
            writer.write(json);
            writer.flush();
            System.out.println("В json файл записан отчёт по варианту 3");

            writer.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
