import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;
import java.util.random.RandomGenerator;

public class Server {
    private ServerSocket socket;
    private Sudoku game;
    private HashMap<String, Socket> conns;
    private HashMap<String, PrintWriter> writers;
    private Semaphore gameLock;
    private boolean dirty;

    // private ArrayList
    public Server(int port) throws IOException {
        //
        socket = new ServerSocket(port);
        game = new Sudoku();
        game.fillValues();
        gameLock = new Semaphore(1);
        conns = new HashMap<String, Socket>();
        writers = new HashMap<String, PrintWriter>();

        dirty = false;
    }

    public void startAcceptingConnections() {
        int i = 0;
        ArrayList<Thread> threads = new ArrayList<Thread>();
        while (!game.isBoardFull()) {
            try {
                String id = "socket_" + (i++);
                conns.put(id, socket.accept());
                writers.put(id, new PrintWriter(conns.get(id).getOutputStream()));
                // while(true){
                //     writers.get(id).println("hi");
                // }

                // System.out.println("Created socket: " + id);

                Thread something = new ClientHandler(id);
                threads.add(something);
                something.start();
                // PrintWriter pw = new PrintWriter(conns.get(id).getOutputStream());
                // pw.println(id);
            } catch (IOException e) {
                System.err.println("Server failed to start: " + e.getMessage()); //
            }
        }
        try {
            for (Thread t : threads) {
                t.join();
            }
        } catch (Exception e) {

        }
    }

    private class ClientHandler extends Thread {
        private String id;

        public ClientHandler(String id) {
            this.id = id;
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(conns.get(id).getInputStream()));) {
                PrintWriter out = writers.get(id);
                String input = "";
                // System.out.println(in.readLine());
                while ((input = in.readLine()) != null) {
                    System.out.println("Does this run all of the time?");
                    if (input.equals("show")) {
                        System.out.println("here?");
                        out.write(game.getSudokuString());
                        out.flush();
                        System.out.println("Finish Response");
                    } else if (input.startsWith("update")) {
                        try {
                            String[] command = input.split(" ");
                            int i = Integer.parseInt(command[1]);
                            int j = Integer.parseInt(command[2]);
                            int num = Integer.parseInt(command[3]);
                            gameLock.acquire();
                            if (game.enterNumber(i, j, num)) {
                                System.out.println("Success");
                                // out.println(game.getSudokuString());
                                for (String sureId : writers.keySet()) {
                                    System.out.println(sureId + ":" + writers.get(sureId));
                                    writers.get(sureId).write(game.getSudokuString());
                                    writers.get(sureId).flush();
                                    System.out.println("Finished Response");
                                }
                                gameLock.release();
                            } else {
                                System.out.println("Fail");
                                out.println("Fail");
                            }
                        } catch (NumberFormatException e) {
                            out.println("Invalid update command");
                        }
                    } else {
                        out.println("Unknown command");
                        // out.flush();
                    }
                    // out.flush();
                }
            } catch (IOException e) {
                System.err.println("Server failed to start: " + e.getMessage());
            } catch (Exception e) {
                System.err.println(e);
            } finally {
                System.out.println("Exited");
                conns.remove(id);
                System.out.println(conns);
            }
        }

    }

    // public static class ClientHandler extends T
    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        // // Shared state between each thread
        // Sudoku game = new Sudoku();
        // Semaphore gameLock = new Semaphore(1);

        try {
            Server s = new Server(port);
            s.startAcceptingConnections();
            // ServerSocket serverSocket = new ServerSocket(port);
            // System.out.println("Server successfully started at: " +
            // serverSocket.getLocalSocketAddress());

            // while(true){
            // Socket clientSocket = serverSocket.accept();
            // System.out.println("Connection from client: '" +
            // clientSocket.getInetAddress() + "' established.");
            // // Each client handler needs an instance of the game
            // new ClientHandler(clientSocket).start();
            // }
        } catch (IOException e) {

            System.err.println("Server failed to start: " + e.getMessage()); // err.println can use to signify diff
                                                                             // between regular output

        }

    }

}

// need a way to manage incoming messages/connections with the clients
// themselves, and interpret the data each client sends
// example: put thing at spot 1 or 2, ok!

//
// class ClientHandler extends Thread { // we anticipate multiple clients...
// thus we need multiple threads to take care
// // of them.

// private Socket clientSocket;

// public ClientHandler(Socket s) {
// clientSocket = s;
// }

// public void run() {
// // need to use two parts of the IO built into java to buffer, then print out
// the
// // data stream that the client will send via TCP
// try (
// BufferedReader inStream = new BufferedReader(new
// InputStreamReader(clientSocket.getInputStream()));
// PrintWriter pOut = new PrintWriter(clientSocket.getOutputStream(), true);) {
// // When user connects we send them
// String message;
// // will buffer the input from the specified file.
// // Without buffering, each invocation of read() or readLine() could cause
// bytes
// // to be read from the file, converted into characters, and then returned,
// which
// // can be very inefficient.
// // username grab
// String username = "anon";

// if ((message = inStream.readLine()) != null) {
// username = message;
// System.out.println("Client at " + clientSocket.getInetAddress() +
// "chose name '" + username + "'.");
// }
// while ((message = inStream.readLine()) != null && !message.equals("exit")) {
// // read from client
// System.out.println(username + "> " + message);

// // need arraylist tracking all users here to broadcast, otherwise this will
// only
// // be sent to one client
// pOut.println(username + "> " + message); // TODO half done, currently users
// cannot see other user
// // messages.
// }

// } catch (IOException e) {
// System.err.println(e);
// } finally { // attempt to close the client socket
// try {
// clientSocket.close();
// } catch (IOException e) {
// System.err.println(e);
// }
// }

// }
// // thread class requires a run() function, and to be called with start
// elsewhere
// }