/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatcomsala;

/**
 *
 * @author plrf1
 */


import java.io.*; 
import java.util.*; 
import java.net.*; 
import java.util.Scanner; 
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    
   
    
    public static void main(String[] args) throws IOException {
    
        // getting localhost ip
        InetAddress ip = InetAddress.getByName("localhost");

        // establish the connection
        Socket s = new Socket(ip, 1234);

        // obtaining input and out streams
        DataInputStream in = new DataInputStream(s.getInputStream());
        DataOutputStream out = new DataOutputStream(s.getOutputStream());

        Thread sendMessage = new Thread(new Runnable() {

            @Override
            public void run() {

                try {

                    while (true) {

                        Scanner scn = new Scanner(System.in);
                        // read the message to deliver.
                        String msg = scn.nextLine();

                        out.writeUTF(msg);
                    }

                } catch (IOException ex) {

                }
            }

        });

        Thread readMessage = new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    while (true) {

                        String msg;

                        msg = in.readUTF();
                        System.out.println(msg);
                        if (msg == "died") {
                            in.close();
                            out.close();
                            s.close();

                            break;
                        }

                    }
                } catch (IOException ex) {

                }

                System.out.println("asssssssssssssssssssssssssssssssssssssssssssssssssssssaa");
                System.exit(0);
            }

        });

        sendMessage.start();
        readMessage.start();

    }
}