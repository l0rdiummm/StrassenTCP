import java.io.*;
import java.net.*;
import java.lang.Exception;

	
public class SThread extends Thread 
{
    private Object [][] RTable;
    private PrintWriter out, outTo;
    private BufferedReader in;
    private String inputLine, outputLine, destination, addr;
    private Socket outSocket;
    private int ind;

    public SThread(Object [][] Table, Socket toClient, int index) throws IOException
    {
        out = new PrintWriter(toClient.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(toClient.getInputStream()));
        RTable = Table;
        addr = toClient.getInetAddress().getHostAddress();
        RTable[index][0] = addr;
        RTable[index][1] = toClient;
        ind = index;
    }
    
    public void run()
    {
        try {
            destination = in.readLine();
            System.out.println("Forwarding to " + destination);
            out.println("Connected to the router.");
            
            try {
                Thread.currentThread().sleep(10000); 
            } catch(InterruptedException ie) {
                System.out.println("Thread interrupted");
            }
            
            for (int i=0; i<10; i++) {
                if (destination.equals((String) RTable[i][0])) {
                    outSocket = (Socket) RTable[i][1];
                    System.out.println("Found destination: " + destination);
                    outTo = new PrintWriter(outSocket.getOutputStream(), true);
                }
            }
            
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.equals("Bye."))
                    break;
                outputLine = inputLine;
                
                if (outSocket != null) {
                    outTo.println(outputLine);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not listen to socket.");
            System.exit(1);
        }
    }
}