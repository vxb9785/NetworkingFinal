/**
 * @Author - Abbey Sands, Matt Maloney, Varun Bhatia
 * A class that creates a multi-threaded server for a chat program
 */
import java.io.*;
import java.net.*;
import java.util.*;

public class Server1
{
   //Vector to store the PrintWriters to allow sending messages to every user
   private Vector<PrintWriter> printWriters = new Vector<PrintWriter>();
   
   /**
    * The main() method creates a new instance of the Server1 class.
    * @Param - String[] args
    */
   public static void main(String[] args)
   {
      new Server1();
   }
    
    /** 
     * Constructor for the Server1 class
     * Sets up a aconnection and a thread for each user
     */
   public Server1()
   {
      //Make Server socket
      ServerSocket ss = null;
      
      //Start try
      try
      {
         //Opening Server Message
         System.out.println("The server has started!\nI am: " + InetAddress.getLocalHost());
         
         ss = new ServerSocket(16789);
      }
      catch(IOException ioe)
      {
         ioe.printStackTrace();
      }
      
      //Creates Thread to allow multiple users
      //Set up while(true) so it can run forever
      
      while(true)
      {
         Socket s = null;
         try
         {
            //make Socket
            s = ss.accept();
         }
         catch(IOException ioe)
         {
            ioe.printStackTrace();
         }
         
         //Set up threads
         InnerThread it = new InnerThread(s);
         
         //Start Threads
         it.start();
      }
   }
    
    /** 
     * InnerThread creates an inner class that extends Thread
     * This class communicates with the users and sends out messages to all the users
     */
   public class InnerThread extends Thread
   {
      //attributes
      private Socket s;
      private String userName;
      private String message;
      private BufferedReader br = null;
      private PrintWriter pw = null;
      
      /**
       * Constructor for InnerThread
       * @param Socket _s
       */
      public InnerThread(Socket _s)
      {
         s = _s;
      }
      
      /**
      * method run() sets up communication with the client and replies appropriately
      */
      public void run()
      {
         try
         {
            //Create BufferedReader and PrintWriter
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
            printWriters.add(pw);
         
            //Read from client
            userName = br.readLine();
         
            System.out.println(userName + " has connected to the server");
         
            //write to clients and flush
            for(int i = 0; i <printWriters.size(); i++)
            {
               printWriters.get(i).println(userName + " has entered the chat!");
               printWriters.get(i).flush();
            }
         }
         catch(IOException ioe)
         {  
            ioe.printStackTrace();
         }
         
         try
         {
            while(true)
            {
               message = br.readLine();
               
               if(message.equals(null)){}
               else
               {
                  message = userName + " says: " + message;
                  
                  for(int i = 0; i < printWriters.size(); i++)
                  {
                     printWriters.get(i).println(message);
                     printWriters.get(i).flush();
                  }
               }
            } //End while(true)
         }//End try
         catch(SocketException se)
         {
            //Lets the other users know that someone has left the server
            String msg = userName + " has left the chat.";
            
            for(int i = 0; i < printWriters.size(); i++)
            {
               printWriters.get(i).println(msg);
               printWriters.get(i).flush();
            }
            
            printWriters.remove(this);
            System.out.println(userName + " has left the server.");
         }
         catch(NullPointerException npe)
         {
            //Lets the other users know that someone has left the server
            String msg = userName + " has left the chat.";
            
            for(int i = 0; i < printWriters.size(); i++)
            {
               printWriters.get(i).println(msg);
               printWriters.get(i).flush();
            }
            
            printWriters.remove(this);
            System.out.println(userName + " has left the server.");
         }
         catch(IOException ioe)
         {
            ioe.printStackTrace();
         }
      } //End run
   } //End InnerThread
} //End Server 1