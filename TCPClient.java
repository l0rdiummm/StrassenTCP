import java.io.*;
import java.net.*;

public class TCPClient {
    private static int[][] generateRandomMatrix(int n) {
        int[][] matrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = (int)(Math.random() * 100);
            }
        }
        return matrix;
    }
    
    private static String matrixToString(int[][] matrix) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                sb.append(matrix[i][j]);
                if (j < matrix.length - 1) sb.append(",");
            }
            if (i < matrix.length - 1) sb.append(";");
        }
        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        // Matrix configuration
        int numMatrices = 8;  // Number of matrices to send 
        int n = 100;           // Dimension of square matrix (nxn)
        
        // Variables for setting up connection and communication
        Socket Socket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        InetAddress addr = InetAddress.getLocalHost();
        String host = addr.getHostAddress();
        String routerName = "localhost";
        int SockNum = 5555;
        
        // Tries to connect to the ServerRouter
        try {
            Socket = new Socket("", SockNum); //Enter the host computer's IP address in the ""
            out = new PrintWriter(Socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about router: " + routerName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + routerName);
            System.exit(1);
        }
        
        String fromServer;
        String address = "127.0.0.1"; // destination IP (Server)
        
        // Initial communication with router
        out.println(address);
        fromServer = in.readLine();
        System.out.println("ServerRouter: " + fromServer);
        out.println(host);
        fromServer = in.readLine();
        if (fromServer == null) {
            System.out.println("No response from server");
            return;
        }
        
        // Send configuration parameters
        System.out.println("Sending matrix dimension (N): " + n);
        out.println("SIZE " + n);
        
        System.out.println("Sending number of matrices: " + numMatrices);
        out.println("COUNT " + numMatrices);
        
        // Generate and send matrices
        for (int i = 0; i < numMatrices; i++) {
            int[][] matrix = generateRandomMatrix(n);
            String matrixString = matrixToString(matrix);
            System.out.println("Sending matrix string: " + matrixString);
            out.println("MATRIX " + matrixString);
            
            fromServer = in.readLine();
            System.out.println("Server Response: " + fromServer);
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Sleep interrupted");
            }
        }
        
        // Wait for and display performance metrics
        fromServer = in.readLine();

        // Send exit signal
        out.println("Bye.");
        
        // Close connections
        out.close();
        in.close();
        Socket.close();
    }
}
