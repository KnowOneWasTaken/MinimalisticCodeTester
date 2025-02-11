import java.io.*;

public class Test {
    public static void main(String[] args) {
        File file = new File("C:\\Users\\ronge\\IdeaProjects\\JunitTutorial\\test1.txt");
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while ((line = reader.readLine()) != null) {
            System.out.println(line);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}