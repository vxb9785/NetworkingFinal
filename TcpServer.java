//import classes
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;

/**
 * @author Abbey Sands
 * @version 11/28/17
 *
 * A class that creates a multi-threaded server for a chat program
 */
public class TcpServer extends JFrame
{
   //Vector to store the PrintWriters to allow sending messages to every user and GUI components
   private Vector<PrintWriter> printWriters = new Vector<PrintWriter>();
   
   private JTextArea jtaServLog;
   private JPanel jpServLog;
   private JLabel jlServLog;
   
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
         setTitle("Final Project TCP Server");
         setDefaultCloseOperation(EXIT_ON_CLOSE);
         
         jtaServLog = new JTextArea("",10,30);
         jtaServLog.setLineWrap(true);           
         jtaServLog.setWrapStyleWord(true);
         jtaServLog.setEditable(false);
         
         jpServLog = new JPanel();
         
         JScrollPane jtaLogPane = new JScrollPane(jtaServLog);
         
         jlServLog = new JLabel("Server Log: ");
         
         jpServLog.add(jlServLog);
         jpServLog.add(jtaLogPane);
         
         add(jpServLog);
         
         pack();
         setLocationRelativeTo(null);
         setVisible(true);
         
         //Opening server message
         jtaServLog.append("The server has started!\nI am: "+InetAddress.getLocalHost() + "\n");
         
         //Gets port number here
         String port = JOptionPane.showInputDialog("Enter the port you'd like the server to run on: ");
         int portNum = Integer.parseInt(port);
         jtaServLog.append("Server is now running on port: " + portNum + "\n");
         
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
            
            jtaServLog.append(userName + " has connected to the server\n");
            
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
            jtaServLog.append(userName + " has left the server\n");

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
            jtaServLog.append(userName + " has left the server\n");
         }
         catch(IOException ioe)
         {
            ioe.printStackTrace();
         } 
      }//end run      
   }//end inner thread
}//end TcpServer