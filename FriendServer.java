import java.util.Arrays;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.*;

public class FriendServer {

  private static ServerSocket serverSocket = null;
  private static Socket clientSocket = null;
  // 5 people at most 
  private static final int max = 5;
  private static final clientThread[] threads = new clientThread[max];
  public static void main(String args[]) {

    int portNumber = 8000;
    if (args.length < 1) 
      System.out.println("port = " + portNumber);
    else 
      portNumber = Integer.valueOf(args[0]).intValue();
     
    System.out.println("Server using port number=" + portNumber + "\n");

    try {
      serverSocket = new ServerSocket(portNumber);
    } catch (IOException e) {
      System.out.println(e);
    }

    while (true) {
      try {
        clientSocket = serverSocket.accept();
        System.out.println("Connection Made");
        
        for (int i = 0; i < max; i++) {
          
          if (i == max) {
          PrintStream os = new PrintStream(clientSocket.getOutputStream());
          os.println("Server Full");
          os.close();
          clientSocket.close();
        }
          if (threads[i] == null) {
            String friends[] = new String[] {"","","","",""};
            (threads[i] = new clientThread(clientSocket, threads ,null, friends)).start();
            break;
          }
        
        
      }
      }catch (IOException e) {
        System.out.println(e);
      }
    }
  }
}


class clientThread extends Thread {

  private BufferedReader is = null;
  private PrintStream os = null;
  private Socket clientSocket = null;
  private final clientThread[] threads;
  private int max;
  private String name;
  private String[] friends = new String[] {"","","","",""};
  private int fLength = friends.length;
  
  public clientThread(Socket clientSocket, clientThread[] threads, String name, String[] friends) {
    this.clientSocket = clientSocket;
    this.threads = threads;
    max = threads.length;
    this.name = null; 
    this.friends = friends;
  }

  public void run() {
    int max = this.max;
    clientThread[] threads = this.threads;

    try {
      is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      os = new PrintStream(clientSocket.getOutputStream());
      os.println("What is your name?");
      String name = is.readLine().trim(); 
      this.name = name; 
      os.println("#newuser <" + name+ ">");
      os.println("To leave type #leave");
      
        for (int i = 0; i < max; i++) {
        if (threads[i] != null && threads[i] != this) {
          threads[i].os.println("#newuser <" + name
              + ">");
        }
      }
      while (true) {
        String line = is.readLine();
        int length = line.length();
        if (line.startsWith("#leave")) {
          break;
        }
        
        
  
          if(line.startsWith("@connect")){
          String frname = line.substring(8,length);
          os.println("#friendReqSent");
          for (int i = 0; i < max; ++i) {
            if (threads[i] != null && threads[i] !=this &&threads[i].name.contains(frname)){
              threads[i].os.println("#friendme <" + name + ">");
              }
            else
              continue;
          }
        }
          
          if(line.startsWith("@friend")){
            String reqname = line.substring(7,length);
            int count = 4;
            for (int i = 0; i < max; ++i) {
              for (int p = 0; p <count; ++p){
                  if (threads[i]!=null && !threads[i].friends[p].contains(this.name)&& threads[i].name.contains(reqname))
                  { //requester adds receiver to his list
                    threads[i].os.println("OKfriends " + this.name + " " + reqname);
                    if(threads[i].friends[count]!= ""){
                      threads[i].friends[count] = this.name;
                      System.out.println("Requester's list: " + Arrays.toString(threads[i].friends));
                      --count;
                    }
                    
                  }
                   if (threads[i]!=null)// &&!this.friends[p].contains(reqname) //receiver adds requester to his list
                  {
                    this.friends[count] = reqname;
                    this.os.println("OKfriends " + this.name + " " + reqname);
                    System.out.println("Receiver's list: " + Arrays.toString(this.friends));
                  }
               
                else
                  continue;
                
          }
              }
            
        }
          
            
       
     
        //if the line start with #status then send a copy to everyone else, and send a statusposted back to the sender
        else if(line.startsWith("#status")){
          os.println("#statusPosted");
  
          for (int i = 0; i < max; i++) {
            if (threads[i] != null && threads[i] !=this)
              if(Arrays.asList(this.friends).contains(threads[i].name))
            {
              String message = line.substring(7,length);
              threads[i].os.println("<" + name + "> " + message);
              }
            //
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
      for (int i = 0; i < max; i++) {
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
