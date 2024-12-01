import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client{

    public static void main(String[] args){
        
        int port = Integer.parseInt(args[0]);
        Scanner sc = new Scanner(System.in);
        try{
            Socket s = new Socket("localhost", port);
            System.out.println("Established connection to server on port " + port + ".");

            PrintWriter pOut = new PrintWriter(s.getOutputStream(), true); //autoflush
            System.out.println("Enter a username: ");
            String username = sc.nextLine();
            pOut.println(username);
            while(true){
                
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