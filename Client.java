import java.io.*;
import java.net.*;

public class Client {
    public static class SudokuReaderThread extends Thread {
        private BufferedReader in;
        Socket s;

        public SudokuReaderThread(InputStreamReader is, Socket s) {
            in = new BufferedReader(is);
            this.s = s;
        }

        public void run() {
            try {
                String line = "";

                while (s.isConnected() && (line = in.readLine()) != null) {
                    if (line.length() > 0) {
                        System.out.println(line);
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                try {
                    // System.out.println("Cleaning up");
                    in.close();
                    System.exit(0);
                } catch (IOException e) {
                    System.out.println("error trying to close the BufferReader in");
                }
            }
        }

    }

    public static boolean isValidCommand(String cmd) {
        if (cmd.equals("show") || cmd.equals("exit")) {
            return true;
        } else if (cmd.startsWith("update")) {
            try {
                String[] command = cmd.split("( )+");
                if (command.length != 4) {
                    System.out.println(
                            "Wrong Number of Arguments.\nUsage: update <i> <j> <num>. i, j, and num are integers.");
                    return false;
                }
                int i = Integer.parseInt(command[1]);
                int j = Integer.parseInt(command[2]);
                int num = Integer.parseInt(command[3]);
                if (!(i >= 0 && i <= 8) || !(j >= 0 && j <= 8)) {
                    System.out.println("");
                    return false;
                }
                if (!(num >= 1 && num <= 9)) {
                    System.out.println("Error: Number you're trying to insert must be between 1 and 9!");
                    return false;
                }
                return true;
            } catch (NumberFormatException e) {
                System.out.println("One of your 3 arguments is not a integer.");
            } catch (Exception e) {
                System.out.println("Something went wrong");
            }
        }
        return false;
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println(
                    "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        Thread reader = null;
        try (
                Socket echoSocket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
                InputStreamReader inSReader = new InputStreamReader(echoSocket.getInputStream());
                BufferedReader stdIn = new BufferedReader(
                        new InputStreamReader(System.in))) {
            System.out.println("Established connection to server " + echoSocket.getInetAddress() + ":"
                    + echoSocket.getPort());
            String userInput = null;
            reader = new SudokuReaderThread(inSReader, echoSocket);
            reader.start();
            while (!echoSocket.isClosed() && (userInput = stdIn.readLine()) != null && !userInput.equals("exit")) {
                if (isValidCommand(userInput.trim())) {
                    out.println(userInput);
                    System.out.println("test");
                } else {
                    System.out.println("Invalid Command From Client Validation?");
                }
            }
            echoSocket.close();
            reader.join();
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        } catch (InterruptedException e) {
            System.out.println(e);
            System.exit(1);
        } finally {
            try {
                reader.join();
            } catch (InterruptedException e) {
                System.exit(1);
            }
        }
    }
}