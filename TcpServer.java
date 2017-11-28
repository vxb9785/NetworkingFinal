//import classes
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;

/**
 * @author Duncan Okes, Abbey Sands, Carolyn Opre
 * @version 3/30/17
 *
 * A class that creates a multi-threaded server for a chat program
 */
public class TcpServer
{
   //Vector to store the PrintWriters to allow sending messages to every user
   private Vector<PrintWriter> printWriters = new Vector<PrintWriter>();
   
   /**
    * The main() method creates a new instance of the TcpServer class
    * @ Param- String[] args
    */
   public static void main(String [] args)
   {
      new TcpServer();
   }
   
   /**
    * Constructor for the TcpServer class
    * Sets up a connection and a thread for each user
    */
   public TcpServer()
   {
      //make server socket         
      ServerSocket ss = null;
      //start try
      try
      {
         //Opening server message
         System.out.println("The server has started!\nI am: "+InetAddress.getLocalHost());
         
         String port = JOptionPane.showInputDialog("Enter the port you'd like the server to run on: ");
         int portNum = Integer.parseInt(port);
         
         ss = new ServerSocket(portNum);
      }
      catch(IOException ioe)
      {
         ioe.printStackTrace();
      }
      
      //Creates threads to allow multiple users
      //set up while(true) so it can run forever
      while(true)
      {
         Socket s = null;
         try 
         { 
            //make socket
            s = ss.accept();
         }
         catch(IOException ioe)
         {
            ioe.printStackTrace();
         }
         //setup threads(InnerThread)
         InnerThread it = new InnerThread(s);
               
         //start threads
         it.start();
      }//end while loop
   }//end TcpServer constructor
   
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
       * Contructor for InnerThread
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
         //try
         try
         {
            //create bufferedReader and printWriter
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
            printWriters.add(pw);
            
            //read from client
            userName = br.readLine();
            
            System.out.println(userName + " has connected to the server");
            
            //write to clients and flush
            for(int i = 0; i < printWriters.size(); i++)
            {
               printWriters.get(i).println(userName + " has entered the chat!");
               printWriters.get(i).flush();
            }
           
         }//end try
         catch(IOException ioe)
         {
            ioe.printStackTrace();
         }
         
         try
         {
            //while loop that keeps reading in messages and prints them out to all threads
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
            }//end while true
         }//end try  
         catch(SocketException se)
         {
            //lets the other users know that someone has left the server
            String msg = userName + " has left the chat";
            
            for(int i = 0; i < printWriters.size(); i++)
            {
               printWriters.get(i).println(msg);
               printWriters.get(i).flush();
            } 
                       
            printWriters.remove(this);
            System.out.println(userName + " has left the server");

         }
         catch(NullPointerException npe)
         {
            //lets the other users know that someone has left the server
            String msg = userName + " has left the chat";
            
            for(int i = 0; i < printWriters.size(); i++)
            {
               printWriters.get(i).println(msg);
               printWriters.get(i).flush();
            }
            
            printWriters.remove(this);
            System.out.println(userName + " has left the server");
         }
         catch(IOException ioe)
         {
            ioe.printStackTrace();
         } 
      }//end run      
   }//end inner thread
}//end TcpServer