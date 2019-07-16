import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class MultiClient implements Runnable {
 
  private static boolean var = false;
  private static Socket cSocket = null;
  private static PrintStream os = null;
  private static BufferedReader is = null;
  private static BufferedReader line = null;
  
  //closely adapted to the original user.java
  public static void main(String[] args) {

    int portNumber = 8000;
    String host = "localhost";
    if (args.length < 2) {
      System.out.println("host=" + host + "port=" + portNumber);
    } else {
      host = args[0];
      portNumber = Integer.valueOf(args[1]).intValue();
    }
     //open a socket
    try {
      cSocket = new Socket(host, portNumber);
      line = new BufferedReader(new InputStreamReader(System.in));
      os = new PrintStream(cSocket.getOutputStream());
      is = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
    }
      catch (UnknownHostException e) {} 
      catch (IOException e) {}

      //time to put some data in with this 
    
    if (cSocket != null) {
      if (os != null && is != null){
      try {
        new Thread(new MultiClient()).start();
        while (!var) {
          os.println(line.readLine().trim());
        }
       
        //close everything
        os.close();
        is.close();
        cSocket.close();
      } catch (IOException e) {
        System.err.println("IOException:  " + e);
      }
    }
  }
  }


  public void run() {

    String rLine;
    try {
      while ((rLine = is.readLine()) != null) {
        //takes the response from the system
        System.out.println(rLine);
        if (rLine.indexOf("Bye") != -1)
          break;
      }
      var = true;
    } catch (IOException e) {
      System.err.println("IOException:  " + e);
    }
  }
}