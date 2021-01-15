import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main {
    // При необходимости указать полный путь к файлу: пробовал на win - в таком виде ругается,
    // а при указании полного пути - норм
    private final static String archName = "source_archive.zip";

    public static void main(String[] args) {
        List<String> fileNames = readArchive(archName);
        Map<String, List<Integer>> outputDate = readFiles(fileNames);
        String finalReport1 = generateReport1(outputDate);
        String finalReport2 = generateReport2(outputDate);
        String finalReport3 = generateReport3(outputDate);
        System.out.println("------");
        saveResult(finalReport1, 1);
        saveResult(finalReport2, 2);
        saveResult(finalReport3, 3);
    }

    /**
     * Метод readArchive()
     * Читаем файлы из архива
     */
    static List<String> readArchive(String archName) {
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
     * Читаем данные из файлов, сводим в один мап, удаляем файлы
     */
    static Map<String, List<Integer>> readFiles(List<String> fileNames) {
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

        for (String fileName : fileNames) {
            File file = new File(fileName);
            file.delete();
        }

        return outputDate;
    }

    /**
     * Метод generateReport1(Map<String, List<Integer>> outputDate)
     * Формируем данные для итогового отчёта по варианту 1:
     * JSON по тем меткам, которые есть в исходных данных: одна метка - итоговое количество
     */
    static String generateReport1(Map<String, List<Integer>> outputDate) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Map<String, Integer> temp = new TreeMap<>();
        for (String label : outputDate.keySet())
            temp.put(label, outputDate.get(label).stream().mapToInt(i -> i).sum());
        return gson.toJson(temp);
    }

    /**
     * Метод generateReport2(Map<String, List<Integer>> outputDate)
     * Формируем данные для итогового отчёта по варианту 2:
     * Как первый JSON, но используется заранее подготовленный список меток, метки без
     * количества - null
     */
    static String generateReport2(Map<String, List<Integer>> outputDate) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.serializeNulls().setPrettyPrinting().create();
        Map<String, Integer> temp = new TreeMap<>();
        for (Label value : Label.values())
            if (outputDate.containsKey(value.toString()))
                temp.put(value.toString(),
                        outputDate.get(value.toString()).stream().mapToInt(i -> i).sum());
            else
                temp.put(value.toString(), null);
        return gson.toJson(temp);
    }

    /**
     * Метод generateReport3(Map<String, List<Integer>> outputDate)
     * Формируем данные для итогового отчёта по варианту 3:
     * JSON по тем меткам, которые есть в исходных данных: одна метка -
     * массив всех значений, по убыванию
     */
    static String generateReport3(Map<String, List<Integer>> outputDate) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Map<String, Integer[]> newTemp = new TreeMap<>();
        for (String label : outputDate.keySet()) {
            int[] primArray = outputDate.get(label).stream().mapToInt(i -> i).toArray();
            Integer[] objArray = Arrays.stream(primArray).boxed().toArray(Integer[]::new);
            Arrays.sort(objArray, Collections.reverseOrder());
            newTemp.put(label, objArray);
        }
        return gson.toJson(newTemp);
    }

    /**
     * Метод saveResult
     * Сохраняем файл
     *
     */
    private static void saveResult(String finalReport, int reportVersion) {
        try {
            FileWriter writer = new FileWriter(
                    "final_report_var" + reportVersion + ".json");
            writer.write(finalReport);
            writer.flush();
            System.out.println("В json файл записан отчёт по варианту " + reportVersion);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
