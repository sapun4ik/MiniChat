package Chat.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Анатолий on 10.02.2016.
 */
public class ChatServer {
    public ChatServer(int port) throws IOException{
        ServerSocket service = new ServerSocket(port);
        try{
            while (true){
                // принимать
                Socket s = service.accept();
                System.out.println("Accepted from "+s.getInetAddress());
                ChatHandler handler = new ChatHandler(s);
                handler.start();
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }finally {
            service.close();
        }

    }

    public static void main(String[] args) throws IOException {
//        if (args.length !=1){
//            throw new RuntimeException("Syntax: Chat.Server.ChatServer <port>");
//        }
//        new Chat.Server.ChatServer(Integer.parseInt(args[0]));
        try {
            String args0 = "8082";
            new ChatServer(Integer.parseInt(args0));
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
}
