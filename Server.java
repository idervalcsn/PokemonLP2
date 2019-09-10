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


public class Server {

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
    static Room roomBraba;
    
        
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
            //Criar Room() pros dois primeiros clientes
            if (roomOne.size() == 2){
             //  Cliente clt11 = roomOne.firstElement();
             //  Cliente clt22 = roomOne.lastElement();
                System.out.println("Entrou aqui aaaaaaaa");
                roomBraba = new Room(roomOne);
                Thread room;
                room= new Thread(roomBraba);
                room.start();
              
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

    
    int hp;
    Vector<Pokemon> hand = new Vector<>();
    volatile int turno = 0;
    volatile int acao = 0; //Açao escolhida 
    int dano; //Dano que tu vai dar no prox turno
    
    
    
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
                
                recieved =""; //Inicializar pro compilador n reclamar
                if (turno == 0){ //Só considera o primeiro ataque enviado
                recieved = in.readUTF();
                }
                //System.out.println(recieved);                                
                
                
                //SISTEMA DE TURNOS
                turno = 1;
                int x =0;
                for (Cliente cl : room) {
                    if (cl.s != this.s) {
                        //cl.out.writeUTF(recieved); 
                        
                        System.out.println("aaaa");
                        System.out.println("turno do coleguinha: " + cl.turno);
                        System.out.println("turno meu " + turno);
                        while ((cl.turno == 0) && (this.turno == 1) ||(cl.turno == 1) && (this.turno == 0)){
                        //Ficar esperando o ataque do outro
                        }
                        
                         System.out.println("bbbbbbbbb");

                            cl.out.writeUTF(recieved); 
                            if (recieved.equals("1")){
                            acao = 1;
                            }
                            
                            /*
                            //Se ele tiver digitado 1, mandar o seu pokemon dar o ataque 1
                                if (recieved.equals("1")){
                                 dano = hand.firstElement().attack1();
                                 System.out.println("Dano da lapada ="+dano);
                                 //Diminuir hp do outro bixo
                                 System.out.println(cl.s.hand.firstElement());
                                 cl.s.hand.firstElement().hp-=dano; //Talvez usar a room pra acessar o hp do outro bixo
                                                                    //e dar dano
                                }
                            */
                    }

                }
                turno = 2;
                System.out.println("Meu entulho="+turno);
                while(turno == 2){  //Espernado room ver que os dois tao com turno = 2 pra mudar pra zero dnv
                  //   x += 0;        
                }
                System.out.println("Meu turno agora eh:"+turno);
               // x=0;
                


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
    volatile int placebo=1;
    int dano; //Dano do ataque dado
    
    public Room(Vector<Cliente> vectClients){
         System.out.println("tttttttttt");
        this.client1 = vectClients.firstElement();
        this.client2 = vectClients.lastElement();
        //System.out.println("zzzzzzzzzzz");
    }
    
    @Override
    public void run(){
        System.out.println("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");
        
        while (placebo == 1){  
     
           if((client1.turno ==2) && (client2.turno ==2)){ //Ações do turno kk 
           //Executar acao//    
           //Cliente 1
           if(client1.acao == 1){//Ataque 1 do cliente 1 
               dano = client1.hand.firstElement().attack1(); //Dano do ataque 1 
               System.out.println("Cliente 1 deu"+ client1.hand.firstElement().showAttack1() + "com o "+ client1.hand.firstElement().name +" de"+ dano+" de dano");
               System.out.print("Hp do Cliente 2 foi de "+client2.hand.firstElement().hp);
               client2.hand.firstElement().hp -= dano; //Diminuir hp do cliente 2
               System.out.println(" para "+client2.hand.firstElement().hp);
               client1.acao =0;
           }
           /////////////////    
           client1.turno =0;
           client2.turno =0;
           System.out.println("Fim do turno");
           
           }
                
        }
        
        
    }
    
    
}

class Pokemon {
    
    String name;
    int hp = 100;
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
            return (10);
        }
         
        if (name.equals("Squirtle")){
            return (10);   
        }
        
        if (name.equals("Bulbasaur")){
            return (10);
        }
        
        return 10;
    }
}
