import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

// Server class that reads data from client through a socket and returns answer after computation
// to the client via socket
// Server will need to be run first, then the Client class is run
public class Server {
    
    public static void main(String[] args) throws Exception {
        // Notify user the server has successfully started running
        System.out.println("Server started");
        // Define port number as a named constant
        final int port = 9806;
        // Server socket object is initialized, at port 9806
        ServerSocket ss = new ServerSocket(port);
        // Notify user we are now just waiting for the client - Please run the Client class
        System.out.println("Waiting for client...");
        // Accept method called - waiting for the client socket connection
        Socket socket = ss.accept();
        // Notify user that socket connection has been established with the client
        System.out.println("Connection established");
        // Buffered reader object used for efficiency - bytes from server, converted to characters,
        // to buffered characters
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // PrintWriter object used to send data to the client - autoflushed
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        //Initialize a Map, using lambdas to evaluate input from client
        Map<Integer, BiFunction<Integer, Integer, Integer>> CALLBACKS = new HashMap<>();
            CALLBACKS.put(1, (x,y) -> x+y);
            CALLBACKS.put(2, (x,y) -> x-y);
            CALLBACKS.put(3, (x,y) -> x*y);
            CALLBACKS.put(4, (x,y) -> x/y);
        // While loop used unless client sends a 5 as option, will break loop below
        while (true)
        {
            // String list captured from the client - 3 components delimited by the ":"
            String[] str = in.readLine().split(":");
            // Option captured at the 0 index of list
            int option = Integer.parseInt(str[0]);
            // First integer captured at the 1 index of list
            int num1 = Integer.parseInt(str[1]);
            // Second integer captured at the 2 index of list
            int num2 = Integer.parseInt(str[2]);
            // Initialize the result string that will be filled below
            if (option == 5) {
                break;
            }
            String result = "";
            var apply = CALLBACKS.get(option).apply(num1, num2);
            // Send the client the result from the computation
            out.println(apply);
        }
        // Once loop broken from 5 being entered, loop terminated, user notified
        System.out.println("Server terminated");
    }
}
