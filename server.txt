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


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ChatComSala {

    /**
     * @param args the command line arguments
     */

    static Vector<Cliente> clients = new Vector<>();
    // counter for clients
    static int i = 0;

    static Vector<Cliente> roomOne = new Vector<>();
    static Vector<Cliente> roomTwo = new Vector<>();
    static Vector<Cliente> roomThree = new Vector<>();

    static Vector<String> entries = new Vector<>();
    static Vector<String> jose = new Vector<>();
    
        
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        // server is listening on port 1234
        ServerSocket ss = new ServerSocket(1234);

        Socket s;

        System.out.println("bora fiote");
        while (true) {

            s = ss.accept();
            Cliente clt = null;

            System.out.println("New client request received : " + s);

            // obtain input and output streams
            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());

            if (roomOne.size() < 2) {
                clt = new Cliente(s, "Cliente " + i, in, out, roomOne);
                System.out.println("Cliente alocado para a sala 1");
                roomOne.add(clt);

            } else if (roomTwo.size() < 2) {
                clt = new Cliente(s, "Cliente " + i, in, out, roomTwo);
                System.out.println("Cliente alocado para a sala 2");
                roomTwo.add(clt);
                
            } else if (roomThree.size() < 2) {
                clt = new Cliente(s, "Cliente " + i, in, out, roomThree);
                System.out.println("Cliente alocado para a sala 3");
                roomThree.add(clt);
            }


            if (clt != null) {
                Thread t = new Thread(clt);
                // add this client to active clients list
                clients.add(clt);

                t.start();
                i++;
            }

            // if (falaTino.equals("Bye")) break;
        }

        // ss.close();
        // s.close();

    }
}

class Cliente implements Runnable {

    private String name;
    final DataInputStream in;
    final DataOutputStream out;
    Socket s;
    boolean isloggedin;
    Vector<Cliente> room;

    
    int life;
    Vector<Pokemon> hand = new Vector<>();
    int turno = 0;
    
    
    
    public Cliente(Socket s, String name, DataInputStream in, DataOutputStream out, Vector<Cliente> room) {

        this.s = s;
        this.name = name;
        this.in = in;
        this.out = out;
        this.room = room;

    }

    @Override
    public void run() {

        String recieved;
        int index = 0;

        Pokemon pokemon1 = new Pokemon("Charmander");
        Pokemon pokemon2 = new Pokemon("Squirtle");
        Pokemon pokemon3 = new Pokemon("Bulbasaur");
        
        
        hand.add(pokemon1);
        hand.add(pokemon2);
        hand.add(pokemon3);
        
        while (true) {

            try {              
                
                /*
                out.writeUTF("\n");
                out.writeUTF("Voce tem esses pokemons:");
                out.writeUTF("[" + hand.get(0).showName() + "]");
                out.writeUTF("Escolha: ");
                out.writeUTF("1 - " + hand.get(0).showAttack1());
                out.writeUTF("2 - " + hand.get(0).showAttack2());
                */
                
                recieved = in.readUTF();
                //System.out.println(recieved);                                
                
                turno = 1;
                
                
                int x;
                for (Cliente cl : room) {
                    if (cl.s != this.s) {
                        //cl.out.writeUTF(recieved); 
                        
                        System.out.println("aaaa");
                        System.out.println("turno do coleguinha: " + cl.turno);
                        System.out.println("turno meu " + turno);
                        while ((cl.turno != 1) || (this.turno != 1)){
                            x = 0;
                            //System.out.println("bl");
                        }
                        
                         System.out.println("bbbbbbbbb");
                        
                        if (((cl.turno == turno))){
                            cl.out.writeUTF(recieved);
                            
                        }
                        
                    }

                }
                 
                
                turno = 0;
                
                
                // For itera na sala para encontrar index atual do client e
                // depois mata-lo
                /*
                 * for(Cliente cl : room){
                 * 
                 * if (cl.s == this.s){ room.remove(index); cl.out.writeUTF("died"); break; }
                 * else { index++; }
                 * 
                 * }
                 */
                // break;

            } catch (IOException e) {
                
                for(Cliente cl : room){
                  
                 if (cl.s == this.s){ room.remove(index); break; }
                 else { index++; }
                  
                 }
                
                try {
                    in.close();
                    out.close();
                } catch (IOException ex) {
                    Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
               
            }

        }

    }

}

class Room implements Runnable {
    
    Cliente client1;
    Cliente client2;
    
    public Room(Vector<Cliente> vectClients){
        this.client1 = vectClients.get(0);
        this.client2 = vectClients.get(1);
    }
    
    @Override
    public void run(){
        
        while (true){
 
            
            
            
            
        }
        
        
    }
    
    
}

class Pokemon {
    
    String name;
    int life;
    int damage;
    int defense;
    String type;
    //String attack1;
    //String attack2;
    
    public Pokemon(String name){
        
        this.name = name;
        
    }
    
    public String showName() {
        return this.name;
    }
    
    public String showAttack1(){
        
        if (name.equals("Charmander")){
            return ("Blaze");
        }
         
        if (name.equals("Squirtle")){
            return ("Torrent");    
        }
        
        if (name.equals("Bulbasaur")){
            return ("Overgrow");   
        }
        
        return ("nothing");
    }
    
    public String showAttack2(){
        
        if (name.equals("Charmander")){
            return ("Solar Power");
        }
         
        if (name.equals("Squirtle")){
            return ("Rain Dish");    
        }
        
        if (name.equals("Bulbasaur")){
            return ("Chlorophyll");   
        }
        
        return ("nothing");
        
    }
    
    public int attack1(){
        if (name.equals("Charmander")){
            return (100);
        }
         
        if (name.equals("Squirtle")){
            return (100);   
        }
        
        if (name.equals("Bulbasaur")){
            return (100);
        }
        
        return 100;
    }
}
