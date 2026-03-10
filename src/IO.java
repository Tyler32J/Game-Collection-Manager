import java.util.Scanner;

public class IO {
    private static final Scanner scanner = new Scanner(System.in);

    public static void println(String message) {
        System.out.println(message);
    }

    public static void print(String message) {
        System.out.print(message);
    }

    public static String readString() {
        return scanner.nextLine();
    }

    public static int readInt() {
        try {
            int val = scanner.nextInt();
            scanner.nextLine(); // consume newline
            return val;
        } catch (Exception e) {
            scanner.nextLine(); // consume invalid input
            return -1;
        }
    }
}
