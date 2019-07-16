import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.io.IOException;
import java.net.Socket;

public class FriendClient implements Runnable {
 
  private static BufferedReader is = null;
  private static BufferedReader line = null;
  private static Socket cSocket = null;
  private static PrintStream os = null;
  private static boolean var = false;
  
  
  public static void main(String[] args) {

    int portNumber = 8000;
    String host = "localhost";

    if (args.length < 2) {
      System.out
          .println("host=" + host + "port=" + portNumber);
    } else {
      host = args[0];
      portNumber = Integer.valueOf(args[1]).intValue();
    }

    //open socket and input output
    try {
      cSocket = new Socket(host, portNumber);
      line = new BufferedReader(new InputStreamReader(System.in));
      os = new PrintStream(cSocket.getOutputStream());
      is = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
      
    } catch (UnknownHostException e) {} 
    catch (IOException e) {}

    //writing some data (similar to before)
    if (cSocket != null){
      if (os != null && is != null) {
      try {


        new Thread(new FriendClient()).start();
        while (!var) {
          os.println(line.readLine().trim());
        }
       
        //closes the streams and the socket
        
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
    // break when you see bye
    String responseLine;
    try {
      while ((responseLine = is.readLine()) != null) {
        System.out.println(responseLine);
        if (responseLine.indexOf("Bye") != -1)
          break;
      }
      var = true;
    } catch (IOException e) {
      System.err.println("IOException:  " + e);
    }
  }
}