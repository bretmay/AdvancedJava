// Fig. 28.3: Server.java
// Server portion of a client/server stream-socket connection. 
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Server extends JFrame 
{
   private JTextField enterField; // inputs message from user
   private JTextArea displayArea; // display information to user
   private ServerSocket server; // server socket
   private int counter = 1; // counter of number of connections
   private ExecutorService runClient; // will run players
   private Customer customers; // array of Players

   // set up GUI
   public Server()
   {
      super("Server");
      
      runClient = Executors.newCachedThreadPool();
      
      enterField = new JTextField(); // create enterField
      enterField.setEditable(false);
      
      try
      {
         server = new ServerSocket(12345, 2); // set up ServerSocket
      } 
      catch (IOException ioException) 
      {
         ioException.printStackTrace();
         System.exit(1);
      } 

      add(enterField, BorderLayout.NORTH);

      displayArea = new JTextArea(); // create displayArea
      add(new JScrollPane(displayArea), BorderLayout.CENTER);

      setSize(300, 150); // set size of window
      setVisible(true); // show window
   }

   // set up and run server 
   public void runServer()
   {
      try // set up server to receive connections; process connections
      {
         
         while (true) 
         {
            try 
            {
            	displayMessage("Waiting for connection\n");
                customers = new Customer(server.accept());
                runClient.execute(customers); // execute customer runnable
            } 
            catch (EOFException eofException) 
            {
               displayMessage("\nServer terminated connection");
            } 

         } 
      } 
      catch (IOException ioException) 
      {
         ioException.printStackTrace();
      } 
   }
  
   private class Customer implements Runnable 
   {

      // set up Player thread
      private ObjectOutputStream output; // output stream to client
      private ObjectInputStream input; // input stream from client
      private Socket connection; // connection to client
      
      public Customer(Socket socket)
      {
         connection = socket; // store socket for client
         
         try // obtain streams from Socket
         {
            input = new ObjectInputStream(connection.getInputStream());
            output = new ObjectOutputStream(connection.getOutputStream());
            displayMessage("Connection " + counter + " received from: " +
                    connection.getInetAddress().getHostName());
         } 
         catch (IOException ioException) 
         {
            ioException.printStackTrace();
            System.exit(1);
         } 
      }
      
      // send message to client
      private void sendData(String message)
      {
         try // send object to client
         {
            output.writeObject("SERVER>>> " + message);
            output.flush(); // flush output to client
            displayMessage("\nSERVER>>> " + message);
         } 
         catch (IOException ioException) 
         {
            displayArea.append("\nError writing object");
         } 
      }
      
   // process connection with client
      public void run()
      {
         String message = "Connection successful";
         sendData(message); // send connection successful message

         // enable enterField so server user can send messages
         setTextFieldEditable(true);

         do // process messages sent from client
         { 
            try // read message and display it
            {
           	
            	String DELIMITER = ",";
            	String line;
            	String request = (String) input.readObject();
               displayMessage("\n" + request); // display message               

           	if (request.equals("CLIENT>>> airlines")) {
           		BufferedReader list = Files.newBufferedReader(Paths.get("C:\\development\\airlines.csv"));
           		line = list.readLine();
           		String[] columns = line.split(DELIMITER);
           		displayMessage("\n" + line); // display message
           		sendData(line);
           	} else if (request.equals("CLIENT>>> planes")) {
           		System.out.print("\nHere");
           		BufferedReader list = Files.newBufferedReader(Paths.get("C:\\development\\passengerJetPlanes.csv"));
           		line = list.readLine();
           		String[] columns = line.split(DELIMITER);
           		displayMessage("\n" + columns); // display message
           		sendData(line);
           	} else {
           		sendData("Please select from airlines or planes");
           	}
               
            } 
            catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

         } while (!message.equals("CLIENT>>> TERMINATE"));
      }

   }

   // manipulates displayArea in the event-dispatch thread
   private void displayMessage(final String messageToDisplay)
   {
      SwingUtilities.invokeLater(
         new Runnable() 
         {
            public void run() // updates displayArea
            {
               displayArea.append(messageToDisplay); // append message
            } 
         } 
      ); 
   } 

   // manipulates enterField in the event-dispatch thread
   private void setTextFieldEditable(final boolean editable)
   {
      SwingUtilities.invokeLater(
         new Runnable()
         {
            public void run() // sets enterField's editability
            {
               enterField.setEditable(editable);
            } 
         } 
      ); 
   } 
}  

/**************************************************************************
 * (C) Copyright 1992-2018 by Deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 *************************************************************************/