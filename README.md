# os_sudoku

<h1>TCP/IP Multiplayer Sudoku</h1>

<h3>Server.java</h3>
<p>Server.java contains the server socket code for the sudoku game, and handles multiple client threads. </p>




<h4>Sudoku Game User Manual</h4>

This is an application that allows multiple users to play a Sudoku game at the same time. A server host initializes a Sudoku board and listens on a specific port to keep track of progress. A client connects to the server through a TCP/IP connection. They can request the current board state or send a message to the server to update the Sudoku board, a request that can either be accepted or rejected, returning an error to the client if rejected. 


<h4>Compiling the Code</h4>

- Place Server.java, Client.java, and Sudoku.java in the same directory.
- Open a terminal/command prompt in that directory.
- Compile all Java files: ‘javac Server.java Client.java Sudoku.java’

<h4>Starting the Server</h4>

-	Choose a port number that is not currently in use (i.e. 8000)
-	Run the following, making sure to use the correct port number: java Server 8000
-	Once the Server is successfully started, the following message will appear: “Server successfully started at: 0.0.0.0/0.0.0.0.”, The server is now waiting for clients to connect. 

<h4>Connecting a Client</h4>

-	Make sure the server is running. If running on the same machine as the server, use localhost. Otherwise, use the same server’s host and port number. 
- Run the following, making sure to use the correct port number:
  * For localhost: ‘java Client localhost 8000’ 
  * For different machines, replace localhost with the IP address: ‘java Client 192.168.1.10 8000’
-	Make sure to replace ‘8000’ with the correct server port and replace ‘192.168.5.18’ with the correct IP address 

<h4>Client Commands</h4>

Once the client is connected you can use the following commands in the terminal:
- Show: display the current Sudoku board.
- Update i j num: places the number num into the Sudoku board at row i and column j:
  * i and j must be between 0 and 8 (inclusive).
  * num must be between 1 and 9 (inclusive).
  * Example: ‘update 1 2 8’
- exit: disconnects from the server.


