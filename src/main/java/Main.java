import java.util.Scanner;

public class Main {
    static boolean falseInput = false;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.println("Enter a number:");
            String numberEntered = scanner.nextLine();
            if (numberEntered.equals("quit")) {
                break;
            }
            String[] numbers = numberEntered.split(" ");
            int sum = 0;
            for (String number : numbers) {
                try {
                    sum += Integer.parseInt(number);
                } catch (NumberFormatException e) {
                    System.out.println("Error, Invalid input");
                    falseInput = true;
                    break;
                    //return;

                }
            }
            if(!falseInput) {
                System.out.println(sum);
            } else {
                falseInput = false;
            }
        }
        scanner.close();
    }
}