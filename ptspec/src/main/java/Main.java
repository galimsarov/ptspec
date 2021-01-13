import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main {
    private final static String archName = "source_archive.zip";

    public static void main(String[] args) {
        readArchive();
    }

    /**
     * Метод readArchive()
     * Читаем файлы из архива
     */
    public static void readArchive() {
        try(ZipInputStream zin = new ZipInputStream(new FileInputStream(archName))) {
            ZipEntry entry;
            String name;
            long size;
            while ((entry = zin.getNextEntry()) != null) {
                name = entry.getName(); // получим название файла
                size = entry.getSize();  // получим его размер в байтах
                System.out.printf("File name: %s \t File size: %d \n", name, size);
                // распаковка
                FileOutputStream fout = new FileOutputStream("new" + name);
                for (int c = zin.read(); c != -1; c = zin.read()) {
                    fout.write(c);
                }
                fout.flush();
                zin.closeEntry();
                fout.close();
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
