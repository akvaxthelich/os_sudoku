import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class Server{
        public static void main(String[] args){
        int port = Integer.parseInt(args[0]);

        ArrayList<ClientHandler> clientHandlers = new ArrayList<ClientHandler>();     

        try{
            ServerSocket serverSocket = new ServerSocket(port);
            
            System.out.println("Server successfully started at: " + serverSocket.getInetAddress());
            
            while(true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection from client: '" + clientSocket.getInetAddress() + "' established.");

                ClientHandler c = new ClientHandler(clientSocket, clientHandlers);
                clientHandlers.add(c);
                c.start(); 
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

    public PrintWriter pOut;
    public BufferedReader inStream;
    private Socket clientSocket;
    private ArrayList<ClientHandler> clientHandlersArr;
    public ClientHandler(Socket s, ArrayList<ClientHandler> chs){
        clientSocket = s;
        clientHandlersArr = chs;
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
                
                if(!isCommand(message)){
                    System.out.println(username + "> " + message);
                
                    //need arraylist tracking all users here to broadcast, otherwise this will only be sent to one client
                    //broadcast
                    for(int i = 0; i < clientHandlersArr.size(); i++){
                        clientHandlersArr.get(i).pOut.println(username + "> " + message);
                    }
                        
                }
                else{
                    interpretCommand(parseTokens(message));
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

    //take a message. if it is a command, interpret and handle the command
    // if it is not a command, return false.
    public boolean isCommand(String phrase){
        if(phrase.charAt(0) == '/' && phrase.length() > 1){
            return true;
        }
        return false;
    }

    //Precondition: a slash is found and we interpret each following word as a token
    public boolean interpretCommand(Queue<String> tokens){

        String command = tokens.poll();
        //Place: /place {int: value} {int: x} {int: y}        
        if(command.equals("place") || command.equals("update")){
            int value = Integer.parseInt(tokens.poll());

            int[] location = {Integer.parseInt(tokens.poll()), Integer.parseInt(tokens.poll()) };
                
            updateBoard(value, location);
            return true;
        }
        //Show sudoku board.
        else if(command.equals("show") || command.equals("board")){
            showBoard();
            return true;
        }
    return false;
    }

    public Queue<String> parseTokens(String command){

        Queue<String> tokens = new LinkedList<String>();

        String currToken = "";
        //1 to skip the slash.
        for(int i = 1; i < command.length(); i++){
            if(command.charAt(i) == ' ' && !currToken.equalsIgnoreCase("")){
                tokens.add(currToken);
                currToken = "";
                continue;
            }
            else{
                currToken += command.charAt(i);
            }
        }
        return tokens;
    }

    public void showBoard(){
        System.out.println("Show board code reached.");
        //simple print. works with either show or board.
    }

    public void updateBoard(int value, int[] pos){

        System.out.println("Board update code reached.");
        //called by both "place" and "update"
    }
    //thread class requires a run() function, and to be called with start elsewhere
}

