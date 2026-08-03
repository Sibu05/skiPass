import java.io.IOException;
import java.util.*;
import java.util.regex.*;
import java.io.Console;

public class main {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    public static void main(String[] args) {

        welcomeMsg();

        randomFact(psswordStats.STATS);

        System.out.print("Press \"ENTER\" to continue...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        clearScreen();
        showAuthmen();
        scanner.close();
    }

    public static boolean isValidEmail(String email){
        if(email == null){
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();

    }

    public static void signUp(){
        Scanner word = new Scanner(System.in);
        String emailString, userName;
        char password[], confirmPass[];

        Console console = System.console();


        System.out.println("Fill in the following info.");
        System.out.println("Enter your email: ");
        emailString = word.nextLine().trim();

        while(!isValidEmail(emailString)){
            System.out.println("Enter valid email: ");
            emailString = word.nextLine().trim();
        }

        System.out.println("Enter your username: ");
        userName = word.nextLine();

        if(console != null){
            password = System.console().readPassword("Enter password: ");
            confirmPass = System.console().readPassword("Confirm password: ");

            while(!Arrays.equals(password, confirmPass)){
                System.out.println("Passwords must match!");
                password = System.console().readPassword("Enter password: ");
                confirmPass = System.console().readPassword("Confirm password: ");

            }
        }else{
            System.out.println("Warning!! Secure console not available.");
            System.out.println("Enter password: ");

            password = word.nextLine().toCharArray();
            confirmPass = word.nextLine().toCharArray();

            while(!Arrays.equals(password, confirmPass)){
                System.out.println("Passwords must match!");
                password = word.nextLine().toCharArray();
                confirmPass = word.nextLine().toCharArray();

            }
        }

    }


    public static void showAuthmen(){
        Scanner opt = new Scanner(System.in);
        int choice;

        do{
            System.out.println("=== Welcome to Skipass. ===");
            System.out.println("1. New to Skipass?");
            System.out.println("2. Already Skipassed?");
            System.out.println("3. Exit");
            System.out.println("Please select opts from (1-3)");

            while(!opt.hasNextInt()){
                System.out.print("Invalid input detected.\n");
                System.out.print("Please select opts from (1-3).\n");
                opt.next();
            }

            choice = opt.nextInt();

            switch(choice){
                case 1:
                    signUp();
                    break;
                case 2:
                    System.out.print("Please enter your username.\n");
                    break;
                case 3:
                    System.exit(0);
                default:
                    System.out.println("Invalid Option, try again.");
            }
        } while(choice != 3); 

        opt.close();
    }


    public static void clearScreen(){
        String os = System.getProperty("os.name").toLowerCase();
        if(os.contains("win")){
            try{
                new ProcessBuilder("cmd","/c","cls").inheritIO().start().waitFor();
            }catch (IOException | InterruptedException e){
                e.printStackTrace();
            }
        }else{
            try{
                new ProcessBuilder("clear").inheritIO().start().waitFor();
                }catch (IOException | InterruptedException e){
                    e.printStackTrace();
                }
            }
    }

    public static void randomFact(List<psswordStats.Stat> random) {
        Random rand = new Random();
        int index = rand.nextInt(random.size());
        psswordStats.Stat fact = random.get(index);
        String fax = fact.text();
        int n = fax.length();
        String sep = "=".repeat(n);
        System.out.print(sep + "\n");
        System.out.print(fact.text() + "\n ~ " + fact.source() + "\n");
        System.out.print(sep + "\n");

    }

    public static void welcomeMsg() {
        String message = """
                 ____    __  __   ______   ____    ______  ____    ____
                /\\  _`\\ /\\ \\/\\ \\ /\\__  _\\ /\\  _`\\ /\\  _  \\/\\  _`\\ /\\  _`\\
                \\ \\,\\L\\_\\ \\ \\/'/'\\/_/\\ \\/ \\ \\ \\L\\ \\ \\ \\L\\ \\ \\,\\L\\_\\ \\,\\L\\_\\
                 \\/_\\__ \\\\ \\ , <    \\ \\ \\  \\ \\ ,__/\\ \\  __ \\/_\\__ \\\\/_\\__ \\
                   /\\ \\L\\ \\ \\ \\\\`\\   \\_\\ \\__\\ \\ \\/  \\ \\ \\/\\ \\/\\ \\L\\ \\/\\ \\L\\ \\
                   \\ `\\____\\ \\_\\ \\_\\ /\\_____\\\\ \\_\\   \\ \\_\\ \\_\\ `\\____\\ `\\____\\
                    \\/_____/\\/_/\\/_/ \\/_____/ \\/_/    \\/_/\\/_/\\/_____/\\/_____/
                                                                               """;

        System.out.print(message + "\n");

    }
}