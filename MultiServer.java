import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.ServerSocket;

public class MultiServer {

  private static ServerSocket sSocket = null;
  private static Socket cSocket = null;
  //max is five connections, hard coded in this variable 
  private static final int max = 5;
  private static final cThread[] threads = new cThread[max];

  public static void main(String args[]) {

    // this portion of the code is adapted from the server.java / very similar
    int portNumber = 8000;
    if (args.length < 1) 
      System.out.println("port = " + portNumber);
    else 
      portNumber = Integer.valueOf(args[0]).intValue();
     
    System.out.println("Server using port number=" + portNumber + "\n");

    try {
      sSocket = new ServerSocket(portNumber);
    } catch (IOException e) {
      System.out.println(e);
    }

    // each incoming connection gets a new socket
    while (true) {
      try {
        cSocket = sSocket.accept();
        System.out.println("Connection Made");
        for (int i = 0; i < max; ++i) {
          if (threads[i] == null) {
            (threads[i] = new cThread(cSocket, threads, 0)).start();
            break;
          }
        
        if (i == max) {
          PrintStream os = new PrintStream(cSocket.getOutputStream());
          os.println("Server at max");
          os.close();
          cSocket.close();
        }
      }
      }catch (IOException e) {
        System.out.println(e);
      }
    }
  }
}



class cThread extends Thread {

  private BufferedReader is = null;
  private PrintStream os = null;
  private Socket clientSocket = null;
  private final cThread[] threads;
  private int max;
  private int length;

  public cThread(Socket clientSocket, cThread[] threads, int length) {
    this.clientSocket = clientSocket;
    this.threads = threads;
    max = threads.length;
    this.length =length;
  }

  
  
  
  public void run() {
    int max = this.max;
    cThread[] threads = this.threads;

    try {
      is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      os = new PrintStream(clientSocket.getOutputStream());
      os.println("Welcome to the future of messaging, please enter your name below");
      String name = is.readLine().trim(); 
      os.println("#newuser <" + name+ ">");
      os.println("To leave type #leave");
      
        for (int i = 0; i < max; ++i) {
        if (threads[i] != null && threads[i] != this) {
          threads[i].os.println("#newuser <" + name+ ">");
        }
      }
      while (true) {
        String line = is.readLine();
        int length = line.length();
        if (line.startsWith("#leave")) {
          break;
        }
        
           
        //if the line start with #status then send a copy to everyone else, and send a statusposted back to the sender
        if(line.startsWith("#status")){
          os.println("#statusPosted");
          for (int i = 0; i < max; i++) {
            if (threads[i] != null && threads[i] !=this){
              String message = line.substring(7,length);
              threads[i].os.println("<" + name + "> " + message);
              }
            //makes it so it the user can keep on trying until he posts a status with #status in the front
            else
              continue;
          }
        }
        
      }
      for (int i = 0; i < max; i++) {
        if (threads[i] != null && threads[i] != this) {
          threads[i].os.println("#Leave <" + name + ">");
        }
      }
      os.println("#Bye " + name);

      //after he leaves you set current thread to null
      for (int i = 0; i < max; ++i) {
        if (threads[i] == this) {
          threads[i] = null;
        }
      }
       //Close the streams and the socket
      is.close();
      os.close();
      clientSocket.close();
    } catch (IOException e) {
    }
  }
}
