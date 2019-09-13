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
            // Criar Room() pros dois primeiros clientes
            if (roomOne.size() == 2) {
                // Cliente clt11 = roomOne.firstElement();
                // Cliente clt22 = roomOne.lastElement();
                System.out.println("Entrou aqui aaaaaaaa");
                roomBraba = new Room(roomOne);
                Thread room;
                room = new Thread(roomBraba);
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
    volatile int acao = 0; // Açao escolhida
    int dano; // Dano que tu vai dar no prox turno
    int change; // Variavel de troca de pokemon
    String changeMsg;

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

                recieved = ""; // Inicializar pro compilador n reclamar
                if (turno == 0) { // Só considera o primeiro ataque enviado

                    // Troca de pokemon quando o que ta em campo fica fainted
                    if (hand.firstElement().hp > 0) {
                        recieved = in.readUTF();
                    } else {
                        // Troca se o pokemon em campo morrer
                        int count = 0;
                        // Exibe os pokemons na tela
                        out.writeUTF("[" + hand.firstElement().showName() + "] fainted");
                        out.writeUTF("Troque imediatamente");
                        for (Pokemon pok : hand) {
                            if (pok.hp > 0 && pok != hand.firstElement()) {
                                count += 1;
                                out.writeUTF(count + " - " + pok.showName());
                            }
                        }

                        changeMsg = in.readUTF();

                        int changeIndex = Integer.parseInt(changeMsg);
                        Pokemon atual = hand.firstElement();
                        Pokemon troca = hand.get(changeIndex);

                        hand.set(0, troca);
                        hand.set(changeIndex, atual);

                        out.writeUTF("DIGITAE O CODE");
                        recieved = in.readUTF();
                        // Turno 4 para troca de Pokemons antes da battle
                        turno = 1;
                        // acao = 3;
                    }

                }

                // System.out.println(recieved);

                // SISTEMA DE TURNOS
                turno = 1;
                int x = 0;
                for (Cliente cl : room) {
                    if (cl.s != this.s) {
                        // cl.out.writeUTF(recieved);

                        // Verifica se é pra trocar de pokemon
                        if (recieved.equals("3")) {
                            int count = 0;
                            // Exibe os pokemons na tela
                            out.writeUTF("Digite o pokemon que vc queira trocar por [" + hand.firstElement().showName()
                                    + "]");
                            for (Pokemon pok : hand) {
                                if (pok.hp > 0) {
                                    if (pok != hand.firstElement()) {
                                        count += 1;
                                        out.writeUTF(count + " - " + pok.showName());
                                    }

                                }
                            }

                            // Turno 5 para troca de Pokemons
                            turno = 5;
                            acao = 3;
                        }

                        // Espera o pokemon para ser trocado
                        while (turno == 5) {
                            changeMsg = in.readUTF();

                            if (changeMsg.equals("1")) {
                                turno = 1;
                            } else if (changeMsg.equals("2")) {
                                turno = 1;
                            } else {
                                turno = 1;
                            }
                        }

                        // System.out.println("aaaa");
                        // System.out.println("turno do coleguinha: " + cl.turno);
                        // System.out.println("turno meu " + turno);
                        while ((cl.turno == 0) && (this.turno == 1) || (cl.turno == 1) && (this.turno == 0)) {
                            // Ficar esperando o ataque do outro
                        }

                        // cl.out.writeUTF(recieved);
                        if (recieved.equals("1")) {
                            acao = 1; // Ataque 1
                        }

                        if (recieved.equals("7")) {
                            acao = 7; // Ataque 2
                        }

                        if (recieved.equals("8")) {
                            acao = 8; // Buffa defensa
                        }

                        if (recieved.equals("9")) {
                            acao = 9; // Buffa damage
                        }

                        if (recieved.equals("4")) {
                            for (Pokemon pok : hand) {
                                out.writeUTF(pok.showName());
                            }
                        }

                    }

                }
                turno = 2;
                System.out.println("Meu entulho= " + turno);
                while (turno == 2) { // Espernado room ver que os dois tao com turno = 2 pra mudar pra zero dnv
                    // x += 0;
                }
                System.out.println("Meu turno agora eh: " + turno);
                // x=0;

            } catch (IOException e) {

                for (Cliente cl : room) {

                    if (cl.s == this.s) {
                        room.remove(index);
                        break;
                    } else {
                        index++;
                    }

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
    volatile int placebo = 1;
    double dano; // Dano do ataque dado
    double buff = 0; //Valor do buff
    int tipo;

    public Room(Vector<Cliente> vectClients) {
        System.out.println("tttttttttt");
        this.client1 = vectClients.firstElement();
        this.client2 = vectClients.lastElement();
        // System.out.println("zzzzzzzzzzz");
    }

    @Override
    public void run() {
        System.out.println("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");

        while (placebo == 1) {

            if ((client1.turno == 2) && (client2.turno == 2)) { // Ações do turno kk
                // Executar acao//
                // Cliente 1
                if (client1.acao != 0) {

                    // Ataque 1 do cliente 1
                    if (client1.acao == 1) {

                        dano = client1.hand.firstElement().attack1() + client2.hand.firstElement().poisoned; // Dano do ataque 1
                        System.out.println("Tipo do primero: " + client1.hand.firstElement().showType());
                        System.out.println("Cliente 1 deu " + client1.hand.firstElement().showAttack1() + " com o "
                                + client1.hand.firstElement().name + " de " + dano + " de dano");
                        System.out.print("Hp do Cliente 2 foi de " + client2.hand.firstElement().hp);
                        client2.hand.firstElement().hp -= dano; // Diminuir hp do cliente 2
                        
                        if(client1.hand.firstElement().name.equals("Bulbasaur")) client2.hand.firstElement().poisonThis(); //Se for bulba, envenena o camarido
                        
                        System.out.println(" para " + client2.hand.firstElement().hp);
                        client1.acao = 0;
                    }

                    // Ataque 2 do cliente 1
                    if (client1.acao == 7) {

                        dano = client1.hand.firstElement().attack2() + client2.hand.firstElement().poisoned; // Dano do ataque 2
                        System.out.println("Tipo do primero: " + client1.hand.firstElement().showType());
                        System.out.println("Cliente 1 deu " + client1.hand.firstElement().showAttack2() + " com o "
                                + client1.hand.firstElement().name + " de " + dano + " de dano");
                        System.out.print("Hp do Cliente 2 foi de " + client2.hand.firstElement().hp);
                        client2.hand.firstElement().hp -= dano; // Diminuir hp do cliente 2
                        System.out.println(" para " + client2.hand.firstElement().hp);
                        client1.acao = 0;
                    }
                    

                    // Buffa damage cliente 1
                    if (client1.acao == 8) {

                        buff = client1.hand.firstElement().attack3(); 
                        System.out.println("Tipo do primero: " + client1.hand.firstElement().showType());
                        System.out.println("Cliente 1 usou " + client1.hand.firstElement().showAttack3() + " com o "
                                + client1.hand.firstElement().name + " aumentando " + buff + " de damage");
                        //System.out.print("Hp do Cliente 2 foi de " + client2.hand.firstElement().hp);
                        client2.hand.firstElement().hp -= dano; // Diminuir hp do cliente 2
                        //System.out.println(" para " + client2.hand.firstElement().hp);
                        client1.acao = 0;
                    }
                    
                    // Buffa defense cliente 1
                    if (client1.acao == 9) {

                        buff = client1.hand.firstElement().attack4(); 
                        System.out.println("Tipo do primero: " + client1.hand.firstElement().showType());
                        System.out.println("Cliente 1 usou " + client1.hand.firstElement().showAttack4() + " com o "
                                + client1.hand.firstElement().name + " aumentando " + buff + " de defense");
                        //System.out.print("Hp do Cliente 2 foi de " + client2.hand.firstElement().hp);
                        client2.hand.firstElement().hp -= dano; // Diminuir hp do cliente 2
                        System.out.println("Poison: " + client2.hand.firstElement().poisoned);
                        //System.out.println(" para " + client2.hand.firstElement().hp);
                        client1.acao = 0;
                    }

                    // Troca de pokemons do client 1
                    if (client1.acao == 3) {

                        int changeIndex = Integer.parseInt(client1.changeMsg);
                        Pokemon atual = client1.hand.firstElement();
                        Pokemon troca = client1.hand.get(changeIndex);

                        client1.hand.set(0, troca);
                        client1.hand.set(changeIndex, atual);

                        client1.acao = 0;
                    }

                    if (client2.acao != 0) {

                        if (client2.acao == 1) {
                            dano = client2.hand.firstElement().attack1();
                            System.out.println("Tipo do segundo: " + client2.hand.firstElement().showType());
                            System.out.println("Cliente 2 deu " + client2.hand.firstElement().showAttack1() + " com o "
                                    + client2.hand.firstElement().name + " de " + dano + " de dano");
                            System.out.print("Hp do Cliente 1 foi de " + client1.hand.firstElement().hp);
                            client1.hand.firstElement().hp -= dano; // Diminuir hp do cliente 2
                            System.out.println(" para " + client1.hand.firstElement().hp);
                            client1.acao = 0;
                        }

                    }

                    if (client2.acao == 3) {
                        int changeIndex = Integer.parseInt(client1.changeMsg);
                        Pokemon atual = client1.hand.firstElement();
                        Pokemon troca = client1.hand.get(changeIndex);

                        client1.hand.set(0, troca);
                        client1.hand.set(changeIndex, atual);

                        client1.acao = 0;
                    }

                }
                /////////////////
                client1.turno = 0;
                client2.turno = 0;
                System.out.println("Fim do turno");

            }

        }

    }

}

class Pokemon {

    String name;
    int hp = 100;
    double damage = 10;
    double defense = 4;
    String type;
    double poisoned = 0;        //Aumenta o dano que o caba dá
    // String attack1;
    // String attack2;

    public Pokemon(String name) {

        this.name = name;

    }

    public String showName() {
        return this.name;
    }

    public int showID() {

        int count = 1;
        String[] pokemons = { "Charmander", "Squirtle", "Bulbasaur" };

        for (String pok : pokemons) {

            if (name == pok) {
                return (count);
            }

            count += 1;
        }

        return 10;

    }

    public String showType() {

        String name = showName();

        String[] water = { "Squirtle" };
        String[] fire = { "Charmander" };
        String[] grass = { "Bulbasaur" };

        for (String pok : water) {
            if (name == pok) {
                return ("Water");
            }
        }

        for (String pok : fire) {
            if (name == pok) {
                return ("Fire");
            }
        }

        for (String pok : grass) {
            if (name == pok) {
                return ("Grass");
            }
        }

        return ("Unknow");

    }

    public String showAttack1() {

        if (name.equals("Charmander")) {
            return ("Blaze");
        }

        if (name.equals("Squirtle")) {
            return ("Torrent");
        }

        if (name.equals("Bulbasaur")) {
            return ("Overgrow");
        }

        return ("nothing");
    }

    public String showAttack2() {

        if (name.equals("Charmander")) {
            return ("Solar Power");
        }

        if (name.equals("Squirtle")) {
            return ("Rain Dish");
        }

        if (name.equals("Bulbasaur")) {
            return ("Chlorophyll");
        }

        return ("nothing");

    }   

    public String showAttack3() {

        if (name.equals("Charmander")) {
            return ("Dragon Dance");
        }

        if (name.equals("Squirtle")) {
            return ("Energy Drink");
        }

        if (name.equals("Bulbasaur")) {
            return ("Growth");
        }

        return ("nothing");

    }

    public String showAttack4() {

        if (name.equals("Charmander")) {
            return ("Firewall");
        }

        if (name.equals("Squirtle")) {
            return ("Water Wall");
        }

        if (name.equals("Bulbasaur")) {
            return ("Seed Wall");
        }

        return ("nothing");

    }

    public double attack1() {

        // Aletoriaedade do attack
        Random rand = new Random();
        int errou = rand.nextInt(100);
        int n = rand.nextInt(3); // Adicional do dano
        int x = rand.nextInt(2); // Controle de subida ou descida de dano;

        if (errou > 90) {
            return 0;
        }
        if (x == 0) {
            return damage + n;
        } else if (x == 1) {
            return damage - n ;
        } else {
            return damage;
        }
    }

    public double attack2() {

        // Aletoriaedade do attack
        Random rand = new Random();
        int errou = rand.nextInt(100);
        int n = rand.nextInt(5); // Adicional do dano
        int x = rand.nextInt(2); // Controle de subida ou descida de dano;

        if (errou > 75) {
            return 0;
        }
        if (x == 0) {
            return (damage + 2 * n);
        } else if (x == 1) {
            return (damage - n);
        } else {
            return (damage);
        }
    }

    public double attack3() {

        damage = damage * 1.5;

        return damage - (damage/1.5);
    }

    public double attack4() {

        defense = defense * 1.5;

        return defense - (defense/1.5);
    }

    public void poisonThis(){
        this.poisoned = poisoned + 1;
    }


    //Uns getterzinho se for precisar
    public double getDefense(){
        return defense;
    }

    public double getDamage(){
        return damage;
    }

    



}
