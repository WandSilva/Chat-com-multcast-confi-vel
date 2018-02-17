package comunication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;

/**
 * Created by wanderson on 27/06/17.
 */

/**
 * classe responsável por fazer a comunicação entre os clientes
 */
public class Comunication {

    private static InetAddress MULTCAST_ADDRESS;
    private static MulticastSocket GROUP_CONECTION;
    private final static int CLIENT_PORT = 44444;
    private static String MY_ID;
    private static String CONVERSATION;
    private static String clients;

    public Comunication(String id) {
        MY_ID = id;
        CONVERSATION = "";
        clients = "";
    }

    /**
     * inicia o grupo multicast
     */
    public void iniciarGrupo() {
        try {
            MULTCAST_ADDRESS = InetAddress.getByName("235.0.0.1");
            GROUP_CONECTION = new MulticastSocket(CLIENT_PORT);
            GROUP_CONECTION.joinGroup(MULTCAST_ADDRESS);
            new ThreadCliente(GROUP_CONECTION).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void notificarEntrada() {
        byte dados[] = ("1000" + ";" + MY_ID).getBytes();
        DatagramPacket msgPacket = new DatagramPacket(dados, dados.length, MULTCAST_ADDRESS, CLIENT_PORT);
        try {
            GROUP_CONECTION.send(msgPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enviarMsg(String id, String msg) {
        byte dados[] = ("1001" + ";" + id + ";" + msg).getBytes();
        DatagramPacket msgPacket = new DatagramPacket(dados, dados.length, MULTCAST_ADDRESS, CLIENT_PORT);
        try {
            GROUP_CONECTION.send(msgPacket);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void enviarlistaConectados() {
        byte dados[] = ("1002" + ";" + clients).getBytes();
        DatagramPacket msgPacket = new DatagramPacket(dados, dados.length, MULTCAST_ADDRESS, CLIENT_PORT);
        try {
            GROUP_CONECTION.send(msgPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getConversation() {
        return CONVERSATION;
    }

    public static void setConversation(String msg) {
        CONVERSATION = CONVERSATION + msg;
    }

    public String getClients() {
        return clients;
    }

    public static void addClientOnline(String clientId) {
        clients = clients + "," + clientId;
    }

    /**
     * Classe interna responsável por criar a thread e ficar sempre
     * esperando as solicitações dos clientes
     */
    private static class ThreadCliente extends Thread {

        private final MulticastSocket socketMulticast;


        public ThreadCliente(MulticastSocket socketMulticast) {
            this.socketMulticast = socketMulticast;
        }

        /**
         * Método responsável por executar a Thread criada.
         *
         * @author Wanderson
         */
        @Override
        public void run() {
            try {
                while (true) {
                    byte dados[] = new byte[1024];
                    DatagramPacket datagrama = new DatagramPacket(dados, dados.length);
                    socketMulticast.receive(datagrama);
                    String msg = new String(datagrama.getData());

                    if (msg.startsWith("1000")) {
                        String[] dadosRecebidos = msg.split(";");
                        Comunication.addClientOnline(dadosRecebidos[1].trim());
                        if (!MY_ID.equals(dadosRecebidos[1].trim())) {
                            Comunication.setConversation("~~" + dadosRecebidos[1] + " entrou na conversa ~~\n");
                            Comunication.enviarlistaConectados();
                        }
                    }
                    if (msg.startsWith("1001")) {
                        String[] dadosRecebidos = msg.split(";");
                        Comunication.setConversation(dadosRecebidos[1] + ": " +
                                dadosRecebidos[2] + "\n");
                    }
                    if (msg.startsWith("1002")) {
                        String[] dadosRecebidos = msg.split(";");
                        String aux = dadosRecebidos[1].trim();
                        if (clients.length() < aux.length()) {
                            clients = aux;
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}