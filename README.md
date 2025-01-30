# StrassenTCP
This project implements a TCP-based client-server architecture that performs parallel and distributed matrix multiplication using Strassen’s algorithm. The system consists of a TCP client, a server router, and a TCP server, enabling efficient matrix computations across multiple threads.

The TCPClient generates random matrices, which are sent to the TCPServerRouter, acting as a bridge to the TCPServer. The server utilizes a binary tree structure to partition matrix computations across multiple threads, improving performance through parallel execution.

To evaluate system performance, the implementation measures runtime, speedup, and efficiency across various matrix sizes (50x50 to 200x200) and thread counts (1, 3, 7, 15, 31). The results provide insights into the scalability and efficiency of parallel computing in matrix operations.

The files should be run in order of TCPServerRouter.java, TCPServer.java, and finally TCPClient.java.
# Features
✅ TCP-based Client-Server Communication
✅ Strassen’s Algorithm for Matrix Multiplication
✅ Parallel Processing with Multithreading
✅ Binary Tree Structure for Workload Distribution
✅ Performance Metrics: Execution Time, Speedup, Efficiency
✅ Scalability Testing with Different Matrix Sizes and Threads

# Technologies Used
Java (OpenJDK 21)
Sockets & TCP Protocol
Multithreading & Concurrency
Parallel & Distributed Computing
