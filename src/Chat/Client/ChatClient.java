package Chat.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;

/**
 * Created by Анатолий on 10.02.2016.
 */
public class ChatClient extends JFrame implements Runnable{
    protected Socket socket;
    protected DataInputStream inStream;
    protected DataOutputStream outStream;
    protected JTextArea outTextArea;
    protected JTextField inTextField;
    protected boolean isOn;

    public ChatClient(String title, Socket s, DataInputStream dis, DataOutputStream dos){
        super(title);
        socket = s;
        inStream = dis;
        outStream = dos;

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(BorderLayout.CENTER, outTextArea = new JTextArea());
        outTextArea.setEditable(false);
        cp.add(BorderLayout.SOUTH, inTextField = new JTextField());

        inTextField.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    outStream.writeUTF(inTextField.getText());
                    outStream.flush();
                }catch (IOException ex)
                {
                    ex.printStackTrace();
                    isOn = false;
                }
                inTextField.setText("");
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                isOn = false;
                try{
                    outStream.close();
                }catch (IOException ex){
                    ex.printStackTrace();
                }
                try{
                    socket.close();
                }catch (IOException ex2){
                    ex2.printStackTrace();
                }
            }
        });
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400,500);
        setVisible(true);
        inTextField.requestFocus();
        (new Thread(this)).start();
    }
    @Override
    public void run() {
    isOn = true;
        try{
            while (isOn){
                String line = inStream.readUTF();
                outTextArea.append(line+"\n");
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }finally {
            inTextField.setVisible(false);
            validate();
        }
    }
    public static void main(String[] args) throws IOException {
//        if(args.length !=2){
//            throw new RuntimeException("Syntax: Chat.Client.ChatClient <host> <port>");
//        }
        // Socket socket = new Socket(args[0],Integer.parseInt(args[1]));
        String args0 = "localhost";
        String args1 = "8082";
        Socket socket = new Socket(args0,Integer.parseInt(args1));
        DataInputStream dis = null;
        DataOutputStream dos = null;
        try{
            dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
           new ChatClient("Chat "+ args0 + ":" + args1,socket,dis,dos);
            //new ChatClient("Chat locolhost:8083",socket,dis,dos);
        }catch (IOException ex){
            ex.printStackTrace();
            try{
                if (dos != null) dos.close();
            }catch (IOException ex2){
                ex2.printStackTrace();
            }
            try{
                socket.close();
            }catch (IOException ex3) {
                ex3.printStackTrace();
            }
        }


    }
}
