//import edu.kit.kastel.Main;
import main.Main;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.regex.Pattern;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

class MainTest {
    private static final String OUTPUT_PATH = "src/test/java/output/";
    private static final String INPUT_PATH = "src/test/java/input/";
    private static final String TEST_FILE_PATH = "src/test/java/testFiles/";
    private static final String INPUT_FILE = "in-";
    private static final String OUTPUT_FILE = "out-";

    private static final String ERROR_INDICATOR = "E<";
    private static final String ERROR_START = "Error,";
    private static final String INPUT_DATA_INDICATOR = "> ";
    private static final String OUTPUT_DATA_INDICATOR = "";
    private static final String ARGS_INDICATOR = "$";

    private static final String EXPECTED_MESSAGE = "\n- but expected:\n\n";
    private static final String OUTPUT_MISMATCH_MESSAGE = "\n- Output mismatch with ";
    private static final String AMOUNT_MISMATCH_MESSAGE = "- The amount of output lines does not match the test case! ";
    private static final String PROGRAM_OUTPUT_MESSAGE = "\n- Your programs output:\n\n";

    private static final String ERROR_LINE_SEPERATOR = "##### ERROR #####\n";
    private static final String OUTPUT_LINE_INDICATOR = ": <-  ";
    private static final String INPUT_LINE_INDICATOR = ": ->  ";

    @Test
    public void test1() {
        test("test1.txt");
    }
    @Test
    public void test2() {
        test("test2.txt");
    }

    public void test(String fileName) {
        // Read the file data
        File file = new File(TEST_FILE_PATH+ fileName);
        List<String> inputData = new ArrayList<>();
        List<String> expectedOutputData = new ArrayList<>();
        HashMap<Integer, Boolean> inputOutputMap = new HashMap<>();
        ArrayList<String> arguments = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineValue = 0;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(INPUT_DATA_INDICATOR)) { // Input data
                    inputData.add(line.substring(INPUT_DATA_INDICATOR.length())); // Remove ">" notation
                    inputOutputMap.put(lineValue, Boolean.TRUE);
                } else if (line.startsWith(ERROR_INDICATOR)) {
                    expectedOutputData.add(ERROR_START);
                    inputOutputMap.put(lineValue, Boolean.FALSE);
                } else if (line.startsWith(ARGS_INDICATOR)) {
                    arguments.add(line.substring(ARGS_INDICATOR.length()));
                } else {
                    expectedOutputData.add(line.substring(OUTPUT_DATA_INDICATOR.length())); // Remove "<" notation
                    inputOutputMap.put(lineValue, Boolean.FALSE);
                }
                lineValue++;
            }
        } catch (FileNotFoundException e) {
            System.err.println("Could not find Test-File! " + System.lineSeparator() + e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.err.println("There was some issue with loading the Test-File! " + System.lineSeparator() + e.getMessage());
            throw new RuntimeException(e);
        }


        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(INPUT_PATH + INPUT_FILE + fileName));
            for (String input : inputData) {
                writer.write(input);
                writer.newLine();
            }
            writer.close();
            FileInputStream inputStream = new FileInputStream(INPUT_PATH + INPUT_FILE + fileName);
            System.setIn(inputStream);
        } catch (IOException e) {
            System.err.println("There was some issue while writing the input file! " + System.lineSeparator() + e.getMessage());
            throw new RuntimeException(e);
        }

        // Redirect System.out to capture the output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Run the main method
        String[] args = arguments.toArray(new String[0]);
        Main.main(args);

        // Restore System.out
        System.setOut(System.out);

        // Capture the actual output into a list
        String[] actualOutput = outContent.toString().split(System.lineSeparator());


        // Check the size of outputs
        if (actualOutput.length != expectedOutputData.size()) {
            throw new AssertionError(AMOUNT_MISMATCH_MESSAGE + System.lineSeparator() + System.lineSeparator() + outContent.toString()
                    + EXPECTED_MESSAGE + System.lineSeparator() + String.join("\n", expectedOutputData));
        }

        // Compare each line and check for errors specifically
        for (int i = 0; i < actualOutput.length; i++) {
            String actual = actualOutput[i];
            String expected = expectedOutputData.get(i);
            try {
                Pattern pattern = Pattern.compile(expected);

                if (expected.startsWith(ERROR_START)) {
                    assertTrue(actual.startsWith(ERROR_START),
                            getErrorMessage(i, expected, actual, outContent, expectedOutputData, inputOutputMap, inputData, fileName));
                } else {
                    assertTrue(pattern.matcher(actual).matches() || (actual.equals(expected)),
                            getErrorMessage(i, expected, actual, outContent, expectedOutputData, inputOutputMap, inputData, fileName));
                }
            } catch (PatternSyntaxException e) {
                if (expected.startsWith(ERROR_START)) {
                    assertTrue(actual.startsWith(ERROR_START),
                            getErrorMessage(i, expected, actual, outContent, expectedOutputData, inputOutputMap, inputData, fileName));
                } else {
                    assertEquals(actual, expected,
                            getErrorMessage(i, expected, actual, outContent, expectedOutputData, inputOutputMap, inputData, fileName));
                }
            }
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_PATH + OUTPUT_FILE + fileName));
            for (String output : actualOutput) {
                writer.write(output);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            System.err.println("There was some issue while writing the output file! " + System.lineSeparator() + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    static String getErrorMessage(int line, String expected, String actual, ByteArrayOutputStream outContent,
                                  List<String> expectedOutputData, HashMap<Integer, Boolean> inputOutputMap,
                                  List<String> inputData, String fileName) {
        int errorLine = line;
        StringBuilder builder = new StringBuilder();
        builder.append(OUTPUT_MISMATCH_MESSAGE).append(fileName).append(PROGRAM_OUTPUT_MESSAGE).append(actual).append(System.lineSeparator()).append(EXPECTED_MESSAGE).append(expected).append(System.lineSeparator()).append(PROGRAM_OUTPUT_MESSAGE);
        int inputCount = 0;
        int outputCount = 0;
        for (int i = 0; i < inputOutputMap.size(); i++) {
            if (inputOutputMap.get(i)) {
                builder.append((i+1) + INPUT_LINE_INDICATOR + (i < 9 ? " |" : "|") + inputData.get(inputCount)).append(System.lineSeparator());
                inputCount++;
            } else {
                boolean isErrorLine = false;
                if (outputCount == line) {
                    errorLine = i;
                    builder.append(ERROR_LINE_SEPERATOR);
                    isErrorLine = true;
                }
                builder.append((i+1) + OUTPUT_LINE_INDICATOR + (i < 9 ? " |" : "|") + outContent.toString().split(System.lineSeparator())[outputCount]).append(System.lineSeparator());
                if (isErrorLine) {
                    builder.append(ERROR_LINE_SEPERATOR);
                    isErrorLine = false;
                }
                outputCount++;
            }
        }
        builder.append(EXPECTED_MESSAGE);
        inputCount = 0;
        outputCount = 0;
        for (int i = 0; i < inputOutputMap.size(); i++) {
            if (inputOutputMap.get(i)) {
                builder.append((i+1) + INPUT_LINE_INDICATOR + (i < 9 ? " |" : "|") + inputData.get(inputCount)).append(System.lineSeparator());
                inputCount++;
            } else {
                boolean isErrorLine = false;
                if (i == errorLine) {
                    builder.append(ERROR_LINE_SEPERATOR);
                    isErrorLine = true;
                }
                builder.append((i+1) + OUTPUT_LINE_INDICATOR + (i < 9 ? " |" : "|") + expectedOutputData.get(outputCount)).append(System.lineSeparator());
                if (isErrorLine) {
                    builder.append(ERROR_LINE_SEPERATOR);
                    isErrorLine = false;
                }
                outputCount++;
            }
        }
        builder.append("\nThe error is in line " + (errorLine + 1));
        return builder.toString();
    }
}