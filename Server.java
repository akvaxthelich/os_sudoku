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
                // writers.get(id).println("hi");
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
            System.out.println("Cleaning up the threads");
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
                while ((input = in.readLine()) != null) {
                    if (input.equals("show")) {
                        out.write(game.getSudokuString());
                        out.flush();
                    } else if (input.startsWith("update")) {
                        try {
                            String[] command = input.split(" ");
                            int i = Integer.parseInt(command[1]);
                            int j = Integer.parseInt(command[2]);
                            int num = Integer.parseInt(command[3]);
                            gameLock.acquire();
                            if (game.enterNumber(i, j, num)) {
                                out.println("Success");
                                out.flush();
                                for (String sureId : writers.keySet()) {
                                    System.out.println(sureId + ":" + writers.get(sureId));
                                    PrintWriter pw = writers.get(sureId);
                                    pw.println("Updated Sudoku Board:");
                                    pw.write(game.getSudokuString());
                                    pw.flush();;

                                    System.out.println("Finished Response");
                                }
                                gameLock.release();
                            } else {
                                System.out.println("Fail");
                                out.write("Fail");
                                out.flush();
                            }
                        } catch (NumberFormatException e) {
                            out.println("Invalid update command");
                        }
                    } else if (input.equals("broadcast")) {
                        for (String sureId : writers.keySet()) {
                            System.out.println(sureId + ":" + writers.get(sureId));
                            writers.get(sureId).println("hi");
                            writers.get(sureId).flush();
                            System.out.println("Finished Response");
                        }
                    } else {
                        System.out.println("Unknown Command!:"+input);
                        out.println("Unknown command");
                    }
                }
            } catch (IOException e) {
                System.err.println("Server failed to start: " + e.getMessage());
            } catch (Exception e) {
                System.err.println(e);
            } finally {
                System.out.println("Exited");
                conns.remove(id);
                writers.remove(id);
                System.out.println(conns);
            }
        }

    }
    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        try {
            Server s = new Server(port);
            // Since the server shares state with all of the threads we needed a function to spin up the threads within the class itself.
            s.startAcceptingConnections();
        } catch (IOException e) {
            System.err.println("Server failed to start: " + e.getMessage()); // err.println can use to signify diff
                                                                             // between regular output

        }

    }

}