//import org.omg.PortableServer.POA;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;


/**
 * @mtm9051 on 10/8/2017.
 * -------------------
 */
public class UdpClient extends JFrame {
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

//    private BufferedReader in = null;
//    private PrintWriter out = null;

    private DatagramSocket clientSocket = null;
    private InetAddress serverInet = null;
    private DatagramPacket packet = null;
    private byte[] buf = null;
    private int PORT = 16788;

    /**
     * main  method
     *       Usage: java UdpClient <hostname>
     *
     * @param args cmd-line args
     */
    public static void main(String[] args) {
      String ip = JOptionPane.showInputDialog("Enter the Server's IP Address: ");
      if(ip.isEmpty())
      {
         System.out.println("You didn't enter a valid IP Address.");
         System.exit(0);
      }
      else
      {
         new UdpClient(ip);
      }
    }


    /**
     * UdpClient constructor
     *
     * @param host hostname
     */
    private UdpClient(String host) {

        buf = new byte[1024];

        /*
           UI initialization
         */
        userName = JOptionPane.showInputDialog("Please enter your name: "); //Takes in the username

        /**
         * Sets up the client GUI
         */
        setTitle("UDP Chatroom");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        jtaLog = new JTextArea("", 10, 30);
        jtaLog.setLineWrap(true);
        jtaLog.setWrapStyleWord(true);
        jtaLog.setEditable(false);

        jtaMessage = new JTextArea("", 5, 30);
        jtaMessage.setLineWrap(true);
        jtaMessage.setWrapStyleWord(true);

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

        UdpClient.ChatButtons cb = new UdpClient.ChatButtons();

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

        /*
           Initialization of destination addresses
         */
        try {
            serverInet = InetAddress.getByName(host);  // throws unknown host
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(null, "Unable to find host.");
            System.exit(0);
        }

        System.out.println("Communicating with UDP::Server");

        try {
            clientSocket = new DatagramSocket();
        } catch (SocketException e) {
            JOptionPane.showMessageDialog(null, "Socket unable to be created");
            System.exit(0);
        }

        /*
           Sends username
         */

        try {
            buf = userName.getBytes();
            packet = new DatagramPacket(buf, buf.length, serverInet, PORT);
            clientSocket.send(packet);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Unable to send message");
            System.exit(0);
        }

        (new Receiver()).start();

    }


    /**
     * Class Receiver is a thread dedicated to reading the incoming packets
     */
    public class Receiver extends Thread {
		
		byte buf[] = new byte[1024];
		
		public Receiver() {}
		
		public void run() {
			while (true) {
				try {
					DatagramPacket packet = new DatagramPacket(buf, buf.length);
					clientSocket.receive(packet);

					String received = new String(packet.getData());
                    jtaLog.append( received + "\n");
				} catch(Exception e) {
                    JOptionPane.showMessageDialog(null, "Unable to receive message");
                    System.exit(0);				}
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

            if (message.equals("logoff"))
                System.exit(0);

            buf = message.getBytes();
            packet = new DatagramPacket(buf, buf.length, serverInet, PORT);

            try {
                clientSocket.send(packet);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Unable to send message");
                System.exit(0);
            }

            jtaMessage.setText("");
        }

        public void doExit()
        {
            System.exit(0); //Exits the client
        }
    }
}
