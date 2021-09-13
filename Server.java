import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;


// Server class that reads data from client through a socket and returns answer after computation
// to the client via socket
// Server will need to be run first, then the Client class is run
public class Server {
    // Named functions for use with BiFunction
    private static Integer Add(Integer num1, Integer num2) { return num1 + num2; }
    private static Integer Subtract(Integer num1, Integer num2) { return num1 - num2; }
    private static Integer Multiply(Integer num1, Integer num2) { return num1 * num2; }
    private static float Divide(Integer num1, Integer num2) { return ((float)num1) / num2; }

    public static void main(String[] args) throws Exception {

        Class.forName("org.h2.Driver");
        Connection connection = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test","sa","");
        Statement statement = connection.createStatement();

        Random r = new Random( System.currentTimeMillis() );
        int randomVal  = 10000 + r.nextInt(20000);
        String databaseName = "dataBase" + randomVal;


        //PreparedStatement createMemoryDataBase;

        //createMemoryDataBase = connection.prepareStatement("CREATE TABLE ? (UserName varchar(255), Password varchar(255))");

        //createMemoryDataBase.setString(1, databaseName);

        //createMemoryDataBase.executeUpdate();

        //statement.executeUpdate("CREATE TABLE memoryTable2 (name varchar(255), operation varchar(255), value varchar(255))");

        //System.out.println("Table has been created successfully");





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
        //Initialize a Map, using defined named functions to evaluate input from client
        Map<Integer, BiFunction<Integer, Integer,? extends  Number>> CALLBACKS = new HashMap<>();
            CALLBACKS.put(1, Server::Add);
            CALLBACKS.put(2, Server::Subtract);
            CALLBACKS.put(3, Server::Multiply);
            CALLBACKS.put(4, Server::Divide);



        String[] credentials = in.readLine().split(":");

        if (credentials[0].equals("returning")) { //This is a returning user
            String userName = credentials[1];
            String password = credentials[2];


            // Using a prepared statement for SQL query if exists in database
            PreparedStatement myLoginStatement;
            myLoginStatement = connection.prepareStatement("SELECT count(*) FROM DBNAME1 WHERE USERNAME = ? AND PASSWORD = ?");

            myLoginStatement.setString(1, userName);
            myLoginStatement.setString(2, password);

            ResultSet loginResults = myLoginStatement.executeQuery();
            loginResults.next(); // This has to be used to get into the resultset first row
            int val = loginResults.getInt(1); // first column is index 1, not 0 in this case



            if (val == 0) {
                out.println("terminate");
            }
            else {
                out.println("continue"); // doesn't matter what it says
            }
        }
        else if (credentials[0].equals("new")) { // This is a new user
            String userName = credentials[1];
            String password = credentials[2];
            // Need to use prepared statements for the SQL query
            PreparedStatement myStatement;
            myStatement = connection.prepareStatement("INSERT INTO credentialsDB(UserName, Password) VALUES(?, ?)");

            myStatement.setString(1, userName);
            myStatement.setString(2, password);

            myStatement.executeUpdate();

            out.println("continue");
        }





        // Need to use prepared statements for the SQL query


        // While loop used unless client sends a 5 as option, will break loop below
        while (true)
        {
            // String list captured from the client - 3 components delimited by the ":"
            String[] str = in.readLine().split(":");
            String stringFirst = str[0];

            if (stringFirst.equals("recall")) {
                PreparedStatement recallValue;
                recallValue = connection.prepareStatement("SELECT * FROM memoryTable2 WHERE NAME = ?");
                String nameString = str[1];
                recallValue.setString(1, nameString);

                ResultSet recallResults = recallValue.executeQuery();
                recallResults.next(); // This has to be used to get into the resultset first row
                String stringValue = recallResults.getString(2); // first column is index 1, not 0 in this case
                out.println(stringValue);
            }
            else {
                int option = Integer.parseInt(str[0]);
                // First integer captured at the 1 index of list
                int num1 = Integer.parseInt(str[1]);
                // Second integer captured at the 2 index of list
                int num2 = Integer.parseInt(str[2]);
                // Name to store value in database
                String memoryName = str[3];
                // Initialize the result string that will be filled below
                if (option == 5) {
                    break;
                }
                String result = "";
                var apply = CALLBACKS.get(option).apply(num1, num2);
                String applyString = apply.toString();
                PreparedStatement myMemoryStatement;
                myMemoryStatement = connection.prepareStatement("INSERT INTO memoryTable2(name, operation, value) VALUES(?, ?, ?)");


                String operationString = "";
                operationString = str[1] + " " + str[0] + " " + str[2];

                myMemoryStatement.setString(1, memoryName);
                myMemoryStatement.setString(2, operationString);
                myMemoryStatement.setString(3, applyString);


                myMemoryStatement.executeUpdate();


                // Send the client the result from the computation
                out.println("Result is: " + apply);            }

        }
        // Once loop broken from 5 being entered, loop terminated, user notified
        System.out.println("Server terminated");
    }
}
