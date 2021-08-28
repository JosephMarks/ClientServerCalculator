import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// Client class captures inputs from user and sends to the client for computation via
// a socket. After receiving the answer from the server via socket, provide to the program user
public class Client {
    public static void main(String[] args) throws Exception {
        // Print statement to notify program has started
        System.out.println("Client started");
        // Define port number as a named constant
        final int port = 9806;
        // Create socket object to connect to the server at port 9806
        Socket soc = new Socket("localhost", port);
        // Create input stream reader to read input from the user for calculator parameters/inputs
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        // Read in through socket data provided by the server - buffered reader used to provide efficient reading of characters
        BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
        // Object to send data to the server, and using parameter to automatically flush the stream
        PrintWriter out = new PrintWriter(soc.getOutputStream(), true);

        // Declare variables to capture input from user below
        int option, num1, num2;

        // Provide user options for operators
        while (true) {
            System.out.println("Please enter your option, from the list below:");
            System.out.println("1. Addition");
            System.out.println("2. Subtraction");
            System.out.println("3. Multiplication");
            System.out.println("4. Division");
            System.out.println("5. Exit");
            System.out.println("Please enter an option..");
            // Option variable captured, converted from string to Integer value
            // I have added a try/exception block in case someone enters value outside
            // selection menu - will gracefully reloop to ask for appropriate input.
            try {
                option = Integer.parseInt(userInput.readLine());
            } catch (Exception e) {
                continue;
            }
            // If input is out of options available numerically, ask again for appropriate input
            if (option > 5 || option < 1) {
                continue;
            }
            // If appropriate option other than 5 selected, prompt user to input values
            if (option != 5) {
                // Prompt user to input first integer, converting from string to Integer value
                System.out.println("Enter first number");
                num1 = Integer.parseInt(userInput.readLine());

                // Prompt user to input second integer, converting from string to Integer value
                System.out.println("Enter second number");
                num2 = Integer.parseInt(userInput.readLine());

                // Send the input values to the server. Formatted in a string, but with ":" as delimiter
                // to allow for server to easily parse each input piece
                out.println(option+":"+num1+":"+num2);
            }
            // If 5 was entered, dummy data sent to the server, but while loop is broken
            else {
                out.println(option+":0:0");
                break;
            }
            // answer read in from the server as a string after being computed, assigned to answer variable
            String answer = in.readLine();
            // Provide user with the answer provided from server
            System.out.println("Server says... "+answer);
        }
        // If option 5 was input to terminate, print status to user after while loop terminated
        System.out.println("Client terminated");
    }
}
