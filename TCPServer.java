import java.io.*;
import java.net.*;
import java.util.*;

public class TCPServer {
    //node class for tree implementation
    static class Node {
        int[][] matrix;
        Node left;
        Node right;
        boolean isDone;
        Thread multiplyThread;

        Node(int[][] matrix) {
            this.matrix = matrix;
            this.isDone = matrix != null;
        }
    }
    //threads for strassen multiplicaation
    static class StrassensThread extends Thread {
        Node node;

        StrassensThread(Node node) {
            this.node = node;
        }
        //run function that calls strassens multiplication
        public void run() {
            try {
                while (!node.left.isDone || !node.right.isDone) {
                    try { Thread.sleep(100); } catch (Exception e) { }
                }
                node.matrix = multiply(node.left.matrix, node.right.matrix);
                node.isDone = true;
            } finally {}
        }
        //helper method
        private void addMatrix(int[][] matrixA, int[][] matrixB, int[][] resultMatrix, int size) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    resultMatrix[i][j] = matrixA[i][j] + matrixB[i][j];
                }
            }
        }
        //helper method
        private void initWithZeros(int[][] matrix, int rows, int cols) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    matrix[i][j] = 0;
                }
            }
        }
        //strassens multiplication implementation
        private int[][] multiply(int[][] matrixA, int[][] matrixB) {
            int colA = matrixA[0].length;
            int rowA = matrixA.length;
            int colB = matrixB[0].length;
            int rowB = matrixB.length;

            if (colA != rowB) {
                System.out.println("\nError: The number of columns in Matrix A must be equal to the number of rows in Matrix B\n");
                int[][] temp = new int[1][1];
                temp[0][0] = 0;
                return temp;
            }

            int[][] resultMatrix = new int[rowA][colB];
            initWithZeros(resultMatrix, rowA, colB);

            if (colA == 1) {
                resultMatrix[0][0] = matrixA[0][0] * matrixB[0][0];
            } else {
                int splitIndex = colA / 2;

                int[][] result00 = new int[splitIndex][splitIndex];
                int[][] result01 = new int[splitIndex][splitIndex];
                int[][] result10 = new int[splitIndex][splitIndex];
                int[][] result11 = new int[splitIndex][splitIndex];

                int[][] a00 = new int[splitIndex][splitIndex];
                int[][] a01 = new int[splitIndex][splitIndex];
                int[][] a10 = new int[splitIndex][splitIndex];
                int[][] a11 = new int[splitIndex][splitIndex];
                int[][] b00 = new int[splitIndex][splitIndex];
                int[][] b01 = new int[splitIndex][splitIndex];
                int[][] b10 = new int[splitIndex][splitIndex];
                int[][] b11 = new int[splitIndex][splitIndex];

                int[][] matrices[] = {result00, result01, result10, result11,
                                    a00, a01, a10, a11, b00, b01, b10, b11};

                for (int[][] matrix : matrices) {
                    initWithZeros(matrix, splitIndex, splitIndex);
                }

                for (int i = 0; i < splitIndex; i++) {
                    for (int j = 0; j < splitIndex; j++) {
                        a00[i][j] = matrixA[i][j];
                        a01[i][j] = matrixA[i][j + splitIndex];
                        a10[i][j] = matrixA[splitIndex + i][j];
                        a11[i][j] = matrixA[i + splitIndex][j + splitIndex];
                        b00[i][j] = matrixB[i][j];
                        b01[i][j] = matrixB[i][j + splitIndex];
                        b10[i][j] = matrixB[splitIndex + i][j];
                        b11[i][j] = matrixB[i + splitIndex][j + splitIndex];
                    }
                }

                addMatrix(multiply(a00, b00), multiply(a01, b10), result00, splitIndex);
                addMatrix(multiply(a00, b01), multiply(a01, b11), result01, splitIndex);
                addMatrix(multiply(a10, b00), multiply(a11, b10), result10, splitIndex);
                addMatrix(multiply(a10, b01), multiply(a11, b11), result11, splitIndex);

                for (int i = 0; i < splitIndex; i++) {
                    for (int j = 0; j < splitIndex; j++) {
                        resultMatrix[i][j] = result00[i][j];
                        resultMatrix[i][j + splitIndex] = result01[i][j];
                        resultMatrix[splitIndex + i][j] = result10[i][j];
                        resultMatrix[i + splitIndex][j + splitIndex] = result11[i][j];
                    }
                }
            }
            return resultMatrix;
        }
    }
    //converts recieved matrix from a string into a matrix
    private static int[][] stringToMatrix(String str, int n) {
        String[] rowStrings = str.split(";");
        int[][] matrix = new int[n][n];
        
        for (int i = 0; i < n; i++) {
            String[] values = rowStrings[i].split(",");
            for (int j = 0; j < n; j++) {
                matrix[i][j] = Integer.parseInt(values[j]);
            }
        }
        return matrix;
    }
    //creates tree of matrices
    private static Node buildTree(List<int[][]> matrices) {
        if (matrices.isEmpty()) return null;
        if (matrices.size() == 1) return new Node(matrices.get(0));
        
        Queue<Node> nodes = new LinkedList<>();
        for (int[][] matrix : matrices) {
            nodes.add(new Node(matrix));
        }

        while (nodes.size() > 1) {
            Node left = nodes.poll();
            Node right = nodes.poll();
            Node parent = new Node(null);
            parent.left = left;
            parent.right = right;
            parent.isDone = false;
            nodes.add(parent);
        }
        return nodes.poll();
    }
    //starts initializes threads aand starts them
    private static int[][] startMultiplication(Node node) throws InterruptedException {
        if (node == null) return null;
        if (node.isDone) return node.matrix;

        startMultiplication(node.left);
        startMultiplication(node.right);
        
        node.multiplyThread = new StrassensThread(node);
        node.multiplyThread.start();
        
        // Wait for this node's multiplication to complete
        while (!node.isDone) {
            Thread.sleep(100);
        }
        
        return node.matrix;
    }

    public static void main(String[] args) throws IOException {
        Socket Socket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        
        // Connection details
        try {
            Socket = new Socket("localhost", 5555);
            out = new PrintWriter(Socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Could not connect to router");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection");
            System.exit(1);
        }
        
        String fromClient;
        String address = "192.168.56.1"; // Server's address
        
        // Initial router communication
        out.println(address);
        fromClient = in.readLine();
        System.out.println("ServerRouter: " + fromClient);
        out.println("Connected to server");
        
        int n = 0;
        int numMatrices = 0;
        List<int[][]> matrices = new ArrayList<>();
        boolean processingComplete = false;
        
        while ((fromClient = in.readLine()) != null && !processingComplete) {            
            String[] parts = fromClient.split(" ", 2);
            if (parts.length < 2) continue;
            
            String command = parts[0];
            String data = parts[1];


            //each string has an identifier infront of in being size, count, or matrix
            switch (command) {
                case "SIZE":
                    n = Integer.parseInt(data);
                    System.out.println("Matrix dimension (N): " + n + "x" + n);
                    out.println("Size received");
                    break;
                    
                case "COUNT":
                    numMatrices = Integer.parseInt(data);
                    System.out.println("Expected matrices: " + numMatrices);
                    matrices = new ArrayList<>(numMatrices);
                    out.println("Count received");
                    break;
                    
                case "MATRIX":
                try {
                    int[][] matrix = stringToMatrix(data, n);
                    matrices.add(matrix);
                    System.out.println("Matrix " + matrices.size() + "/" + numMatrices + " received");
                    out.println("Matrix " + matrices.size() + " stored");
                    
                    if (matrices.size() == numMatrices) {
                        Node root = buildTree(matrices);
                        
                        long startParallel = System.nanoTime();
                        int[][] resultMatrix = null;
                        try {
                            resultMatrix = startMultiplication(root);
                        } catch (InterruptedException e) {
                            System.out.println("Multiplication interrupted: " + e.getMessage());
                        }
                        double parallelTime = (System.nanoTime() - startParallel) / 1e9;

                        int threadsUsed = numMatrices - 1;
                        long startSeq = System.nanoTime();
                        int[][] seqResult = matrices.get(0);
                        for (int i = 1; i < matrices.size(); i++) {
                            seqResult = new StrassensThread(null).multiply(seqResult, matrices.get(i));
                        }
                        double seqTime = (System.nanoTime() - startSeq) / 1e9;
                        double speedup = seqTime / parallelTime;
                        double efficiency = speedup / threadsUsed;

                        // Print the result matrix
                        
                        System.out.println("Sequential time: " + seqTime);
                        System.out.println("Parallel time: " + parallelTime);
                        System.out.println("Speedup: " + speedup);
                        System.out.println("Threads used: " + threadsUsed);
                        System.out.println("Efficiency: " + efficiency * 100);
                        processingComplete = true;
                    }
                } catch (Exception e) {
                    System.out.println("Error processing matrix: " + e.getMessage());
                    out.println("Error processing matrix");
                }
                break;
            }
        }
        
        System.out.println("Server shutting down");
        out.close();
        in.close();
        Socket.close();
    }
}