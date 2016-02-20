package Chat.Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Анатолий on 10.02.2016.
 */
public class ChatHandler extends Thread {
    protected Socket socket;
    protected DataInputStream inStream;
    protected DataOutputStream outStream;
    protected boolean isOn;

    protected static List<ChatHandler> handlers = Collections.synchronizedList(new ArrayList<ChatHandler>());

    public ChatHandler(Socket s) throws IOException{
        socket = s;
        //DataInputStream -позволяет записывать сразу в UTF
        inStream = new DataInputStream(new BufferedInputStream(s.getInputStream()));
        outStream = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
    }

    @Override
    public void run() {
        isOn = true;
        try{
            handlers.add(this);
            while (isOn) {
                String msg = inStream.readUTF();
                broadcast(msg);
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }finally {
            handlers.remove(this);
            try {
                outStream.close();
            }catch (IOException ex2){
                ex2.printStackTrace();
            }
            try{
                socket.close();
            }catch (IOException ex3)
            {
                ex3.printStackTrace();
            }
        }
    }
    protected static void broadcast(String message){
        synchronized (handlers){
            Iterator<ChatHandler> it = handlers.iterator();
            while (it.hasNext()){
                ChatHandler c = it.next();
                try{
                    synchronized (c.outStream){
                        c.outStream.writeUTF(message);
                    }
                    c.outStream.flush();
                }catch (IOException ex){
                    ex.printStackTrace();
                    c.isOn = false;
                }

            }
        }
    }
}
