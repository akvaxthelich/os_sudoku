import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client extends Thread{

    static Socket s;
    static BufferedReader inStream;
    public static void main(String[] args){
        
        int port = Integer.parseInt(args[0]);
        Scanner sc = new Scanner(System.in);
        try{
            s = new Socket("localhost", port);

            inStream = new BufferedReader(new InputStreamReader(s.getInputStream())); //input
            PrintWriter pOut = new PrintWriter(s.getOutputStream(), true); //output, with autoflush
            
            System.out.println("Established connection to server on port " + port + ".");

            
            
            System.out.println("Enter a username: ");
            String username = sc.nextLine();
            pOut.println(username);
            
            System.out.println("Welcome, " + username + ".");
            new ClientFunctionHandler(s).start();
            while(true){ //sending stuff out
                
                String message = sc.nextLine();
                
                if(message.equals("exit")){
                    pOut.println("client exited");
                    break;
                }
                pOut.println(message);
                
            }
            
            pOut.close();
            s.close();
            sc.close();
            
        }
        catch(IOException e){
            System.err.println("Failed to create socket.");
        }

    }


}

//made a handler class to hopefully clean up the driver code.

class ClientFunctionHandler extends Thread{

    BufferedReader inStream;
    public ClientFunctionHandler(Socket s){
        try{
            inStream = new BufferedReader(new InputStreamReader(s.getInputStream()));
        }
        catch(IOException e){
            System.err.println(e.getMessage());
        }
    }
    public void waitForMessages(){
        try{
            String message = inStream.readLine();
            if(message != null){
                System.out.println(message);
            }
        }
        catch(IOException e){

        }
    }

    public void run(){
        while(true){
            waitForMessages();
        }

    }
    
}