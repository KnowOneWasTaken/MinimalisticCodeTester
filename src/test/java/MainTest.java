import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

class MainTest {
    private static final String OUTPUT_PATH = "src/test/java/output/";
    private static final String INPUT_PATH = "src/test/java/input/";
    private static final String TEST_FILE_PATH = "src/test/java/testFiles/";
    @Test
    @Timeout(1)
    public void Test1() {
        test("test1.txt");
    }

    @Test
    @Timeout(1)
    public void Test2() {
        test("test2.txt");
    }

    public void test(String fileName) {
        // Read the file data
        File file = new File(TEST_FILE_PATH+ fileName);
        List<String> inputData = new ArrayList<>();
        List<String> expectedOutputData = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(">")) { // Input data
                    inputData.add(line.substring(1)); // Remove ">" notation
                } else if (line.startsWith("<")) { // Expected output data
                    expectedOutputData.add(line.substring(1)); // Remove "<" notation
                } else if (line.startsWith("E<")) {
                    expectedOutputData.add("Error,");
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        try {
        BufferedWriter writer = new BufferedWriter(new FileWriter(INPUT_PATH+ fileName));
            for (String input : inputData) {
                writer.write(input);
                writer.newLine();
            }
            writer.close();
            FileInputStream inputStream = new FileInputStream(INPUT_PATH+ fileName);
            System.setIn(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Redirect System.out to capture the output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Run the main method
        Main.main(new String[0]);

        // Restore System.out
        System.setOut(System.out);

        // Capture the actual output into a list
       String[] actualOutput = outContent.toString().split(System.lineSeparator());


        // Check the size of outputs
        if (actualOutput.length != expectedOutputData.size()) {
            throw new AssertionError("Output mismatch, received: "+ System.lineSeparator() + outContent.toString()
                    + " but expected: "+ System.lineSeparator() + String.join("\n", expectedOutputData));
        }

        // Compare each line and check for errors specifically
        for (int i = 0; i < actualOutput.length; i++) {
            String actual = actualOutput[i];
            String expected = expectedOutputData.get(i);

            if (expected.startsWith("Error,")) {
                assertTrue(actual.startsWith("Error,"), "Output mismatch, received: " + actual + " but expected: " + expected);
            } else {
                assertEquals(expected, actual, "Output mismatch, received: " + actual + " but expected: " + expected);
            }


        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_PATH+"output-"+ fileName));
            for (String output : actualOutput) {
                writer.write(output);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}