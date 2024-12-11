import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;

public class EchoClient {
    public static class SudokuReaderThread extends Thread {
        private BufferedReader in;

        public SudokuReaderThread(InputStreamReader is) {
            in = new BufferedReader(is);
        }

        public void run() {
            try {
                String line;
                // this.wait();
                while (true) {
                    while ((line = in.readLine()) != null && line.length() > 0) {
                        System.out.println(line);
                    }
                }

            } catch (IOException e) {
                System.out.println(e);
            } finally {
                try {
                    System.out.println("Cleaning up");
                    in.close();
                } catch (IOException e) {
                    System.out.println("error trying to close this");
                }
            }
        }

    }

    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println(
                    "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        // Semaphore
        Thread reader = null;
        try (
                Socket echoSocket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
                InputStreamReader inSReader = new InputStreamReader(echoSocket.getInputStream());
                BufferedReader stdIn = new BufferedReader(
                        new InputStreamReader(System.in))) {
            String userInput = null;
            reader = new SudokuReaderThread(inSReader);
            reader.start();
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
            }
            reader.join();
            System.out.println("Escaped");
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        } catch (InterruptedException e) {
            System.out.println(e);
        } finally {
            try {
                reader.join();
            } catch (InterruptedException e) {

            } catch (Exception e) {
            }
        }
    }
}