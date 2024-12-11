import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ServerDriver{

    public static Server server;

    public static void main(String[] args){
        int port = Integer.parseInt(args[0]);

        try{
            ServerSocket serverSocket = new ServerSocket(port);
            
            System.out.println("Server successfully started at: " + serverSocket.getInetAddress());
            
            while(true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection from client: '" + clientSocket.getInetAddress() + "' established.");

                
                ClientHandler c = new ClientHandler(clientSocket, this);
                clientHandlers.add(c);
                c.start(); 
            }
                    
                    

            
        }
        catch(IOException e){

            System.err.println("Server failed to start: " + e.getMessage()); //err.println can use to signify diff between regular output

        }

        
    }

    class ClientHandler extends Thread{ //we anticipate multiple clients... thus we need multiple threads to take care of them.

        private Socket clientSocket;
        private Server ref;
    
        public PrintWriter pOut;
        public BufferedReader inStream;
        public ClientHandler(Socket s, Server se){
            clientSocket = s;
            ref = se;
        }
    
        public void run(){
            //need to use two parts of the IO built into java to buffer, then print out the data stream that the client will send via TCP
            try{
                inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                pOut = new PrintWriter(clientSocket.getOutputStream(), true);
                String message;
                //will buffer the input from the specified file. 
                //Without buffering, each invocation of read() or readLine() could cause bytes to be read from the file, converted into characters, and then returned, which can be very inefficient. 
                //username grab
                String username = "anon";
                
                //first message
                if((message = inStream.readLine()) != null){
                    username = message;
                    //only log to server when a client chooses a name.
                    System.out.println("Client at " + clientSocket.getInetAddress() + 
                    "chose name '" + username + "'.");
                }
                while((message = inStream.readLine()) != null && !message.equals("exit")){ //read from client            
                    
                    System.out.println(username + "> " + message);
                    
                    //need arraylist tracking all users here to broadcast, otherwise this will only be sent to one client
                    for(int i = 0; i < ref.clientHandlers.size(); i++){
                        ref.clientHandlers.get(i).pOut.println(username + "> " + message);
                    }
                    
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

}