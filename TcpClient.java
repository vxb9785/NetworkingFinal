/**
 * @Author - Abbey Sands, Carolyn Opres, Duncan Okes
 * Public class TcpClient is a java program that functions as the client for a chat program
 */
import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.net.*;

public class TcpClient extends JFrame
{
   /**
    * All attributes for Client 1 including
    * Everything used in the GUI,
    * The actual string message,
    * and the Socket, Reader, and Writer used in conncecting to the Server.
    */
   private JTextArea jtaLog;
   private JTextArea jtaMessage;
   private JPanel jpLog;
   private JPanel jpMessage;
   private JButton jbSend;
   private JButton jbExit;
   private JPanel buttons;
   private String userName;
   private JPanel space;
   private String message;
   private JLabel jlLog;
   private JLabel jlMessage;
   
   Socket s = null;
   BufferedReader in = null;
   PrintWriter out = null;
   
   /**
    * void methods main 
    * @Param - String[] args
    * Takes in a string used to identify the Server host and creates a new TcpClient object
    */
   public static void main(String [] args)
   {
      String ip = JOptionPane.showInputDialog("Enter the Server's IP Address: ");
      String port = JOptionPane.showInputDialog("Enter the Server's Port Number: ");
      
      if(ip.isEmpty())
      {
         System.out.println("You didn't enter a valid IP Address.");
         System.exit(0);
      }
      else
      { 
         new TcpClient(ip, port);
      }
   }
   
   /**
    * TcpClient Constructor
    * @Param - String host
    * Uses the host from the command line to connect to the server, create a username, set up the GUI,
    * and complete all chat functions.
    */
   public TcpClient(String host, String port)
   {
      userName = JOptionPane.showInputDialog("Please enter your name: "); //Takes in the username
      
      /**
       * Sets up the client GUI
       */    
      setTitle("Group 26 Group Chat");
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      
      jtaLog = new JTextArea("",10,30);
      jtaLog.setLineWrap(true);           
      jtaLog.setWrapStyleWord(true);
      jtaLog.setEditable(false);
      
      jtaMessage = new JTextArea("",5,30);
      jtaMessage.setLineWrap( true );           
      jtaMessage.setWrapStyleWord( true ); 
      
      jpLog = new JPanel();
      jpMessage = new JPanel();
      
      JScrollPane jtaLogPane = new JScrollPane(jtaLog);
   
      JScrollPane jtaMessagePane = new JScrollPane(jtaMessage);
      
      jlLog = new JLabel("Chat Log: ");
      jlMessage = new JLabel("Message: ");
      
      jpLog.add(jlLog);
      jpLog.add(jtaLogPane);
      
      jpMessage.add(jlMessage);
      jpMessage.add(jtaMessagePane);
      
      ChatButtons cb = new ChatButtons();
      
      jbSend = new JButton("Send");
      jbSend.addActionListener(cb);
      jbExit = new JButton("Exit");
      jbExit.addActionListener(cb);
      
      buttons = new JPanel();
      buttons.add(jbSend);
      buttons.add(jbExit);
      
      space = new JPanel();
      
      add(jpLog, BorderLayout.NORTH);
      add(jpMessage, BorderLayout.CENTER);
      add(buttons, BorderLayout.SOUTH);
      
      pack();
      setLocationRelativeTo(null);
      setVisible(true);
      
      /**
       * Connects to the Server here
       */     
      try
      {
         s = new Socket(host, Integer.parseInt(port));
         in = new BufferedReader(new InputStreamReader(s.getInputStream()));
         out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
      } 
      catch(UnknownHostException uhe)
      {
         JOptionPane.showMessageDialog(null, "Error: Unknown Host Exception.");
         return; 
      } 
      catch(IOException ioe)
      {
         JOptionPane.showMessageDialog(null, "Error: IOException connecting to server. You cannot send or receive messages in this state. Please try again when the server is functional.");
         jbSend.setEnabled(false);
         jtaMessage.setEditable(false);
         return;
      }
      
      /**
       * Tells the Server it's username here
       */
      try
      {
         out.println(userName);
         out.flush();
         jtaLog.append(in.readLine() + "\n");
      }
      catch(IOException ioe)
      {
         JOptionPane.showMessageDialog(null, "Error: IOException, Server giving name.");
      }
      
      /**
       * Continuously reads new lines from the server to update the log and keep up with the chat.
       */
      while(true)
      {
         try
         {
            jtaLog.append(in.readLine() + "\n");
         }
         catch(IOException ioe)
         {
            JOptionPane.showMessageDialog(null, "Error: IOException trying to read messages.");
         }
      }
   }
   
   /**
    * Inner class ChatButtons tells the program what to do when the buttons are pressed by using ActionListener.
    */
   class ChatButtons implements ActionListener
   {
      public void actionPerformed(ActionEvent ae)
      {
         String command = ae.getActionCommand(); //Gets the command from the buttons
         
         if(command.equals("Send")) doSend();
         else if(command.equals("Exit")) doExit();    
      }
      
      /**
       * method doSend() happens when the send button is clicked.
       * The message from the bottom JTextArea is taken and sent to the Server which then distributes it to other clients and back
       * to the log of this one.
       */
      public void doSend()
      {       
         message = jtaMessage.getText();
         out.println(message);
         out.flush();
         jtaMessage.setText("");
      }
      
      public void doExit()
      {
         System.exit(0); //Exits the client
      }
   } 
}