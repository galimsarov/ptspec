import junit.framework.TestCase;

import java.util.*;

public class MainTest extends TestCase {
    List<String> expectedFileNames;
    Map<String, List<Integer>> expectedOutputDate;
    String expectedReport1, expectedReport2, expectedReport3;

    @Override
    public void setUp() {
        expectedFileNames = new ArrayList<>();
        expectedFileNames.add("newsourcefortest1.csv");
        expectedFileNames.add("newsourcefortest2.csv");
        expectedFileNames.add("newsourcefortest3.csv");

        List<Integer> values1 = new ArrayList<>();
        values1.add(5);
        values1.add(5);
        List<Integer> values2 = new ArrayList<>();
        values2.add(5);
        values2.add(5);
        List<Integer> values3 = new ArrayList<>();
        values3.add(7);
        values3.add(3);

        expectedOutputDate = new HashMap<>();
        expectedOutputDate.put("mark1", values1);
        expectedOutputDate.put("mark2", values2);
        expectedOutputDate.put("mark3", values3);

        expectedReport1 = "{\n" +
                "  \"mark1\": 10,\n" +
                "  \"mark2\": 10,\n" +
                "  \"mark3\": 10\n}";

        expectedReport2 = "{\n" +
                "  \"mark01\": null,\n" +
                "  \"mark17\": null,\n" +
                "  \"mark23\": null,\n" +
                "  \"mark35\": null,\n" +
                "  \"markFT\": null,\n" +
                "  \"markFV\": null,\n" +
                "  \"markFX\": null\n}";

        expectedReport3 = "{\n" +
                "  \"mark1\": [\n" +
                "    5,\n" +
                "    5\n" +
                "  ],\n" +
                "  \"mark2\": [\n" +
                "    5,\n" +
                "    5\n" +
                "  ],\n" +
                "  \"mark3\": [\n" +
                "    7,\n" +
                "    3\n" +
                "  ]\n" +
                "}";
    }

    public void testReadArchive() {
        List<String> actual = Main.readArchive("sourcefortest.zip");
        List<String> expected = expectedFileNames;
        assertEquals(expected, actual);
    }

    public void testReadFiles() {
        Map<String, List<Integer>> actual = Main.readFiles(expectedFileNames);
        Map<String, List<Integer>> expected = expectedOutputDate;
        assertEquals(expected, actual);
    }

    public void testGenerateReport1() {
        String actual = Main.generateReport1(expectedOutputDate);
        assertEquals(expectedReport1, actual);
    }

    public void testGenerateReport2() {
        String actual = Main.generateReport2(expectedOutputDate);
        assertEquals(expectedReport2, actual);
    }

    public void testGenerateReport3() {
        String actual = Main.generateReport3(expectedOutputDate);
        assertEquals(expectedReport3, actual);
    }
}
