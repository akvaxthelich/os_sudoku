import java.io.*;
import java.net.*;

public class Server{

    public static void main(String[] args){
        int port = 8000;

        try{
            ServerSocket serverSocket = new ServerSocket(port);

            while(true){
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Connection with: '" + clientSocket.getInetAddress() + "' established.");

                    new ClientHandler(clientSocket).start(); 
                    
            }
            
        }
        catch(IOException e){

            System.err.println("Server failed to start: " + e.getMessage()); //err.println can use to signify diff between regular output

        }

        
    }

    
}

//need a way to manage incoming messages/connections with the clients themselves, and interpret the data each client sends
//example: put thing at spot 1 or 2, ok!
class ClientHandler extends Thread{ //we anticipate multiple clients... thus we need multiple threads to take care of them.

    private Socket clientSocket;

    public ClientHandler(Socket s){
        clientSocket = s;
    }

    public void run(){
        //need to use two parts of the IO built into java to buffer, then print out the data stream that the client will send via TCP
        try{
            BufferedReader inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter pOut = new PrintWriter(clientSocket.getOutputStream(), true);
            String message;
            //will buffer the input from the specified file. 
            //Without buffering, each invocation of read() or readLine() could cause bytes to be read from the file, converted into characters, and then returned, which can be very inefficient. 
            
            while((message = inStream.readLine()) != null){ //read from client
                System.out.println(message); //
            
                pOut.println("You said: " + message);
            }

        }
        catch(IOException e){
            System.err.println(e);
        }
        finally{ //attempt to close the client socket 
            try{
                clientSocket.close();
            }
            catch(IOException e){
                System.err.println(e);
            }
        }
       
    }
    //thread class requires a run() function, and to be called with start elsewhere
}