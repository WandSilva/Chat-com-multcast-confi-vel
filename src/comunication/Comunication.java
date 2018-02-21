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
    private static ArrayList<String> clients;
    private static ArrayList<Integer> numberLastMsg;
    private static ArrayList<String> messages; 

    public Comunication() {
        CONVERSATION = "";
        clients = new ArrayList<>();
        numberLastMsg = new ArrayList<>();
        messages = new ArrayList<>();
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
        messages.add(msg);
        byte dados[] = ("1001" + ";" + id + ";" + msg + ";" + (messages.size()-1)).getBytes();
        DatagramPacket msgPacket = new DatagramPacket(dados, dados.length, MULTCAST_ADDRESS, CLIENT_PORT);
        try {
            GROUP_CONECTION.send(msgPacket);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void requestResend(String id, String idReceiver, int lastNumberMsg){
        byte dados [] = ("1002" + ";" + id + ";"+ idReceiver + ";" + lastNumberMsg).getBytes();
        System.out.println("aqui");
        DatagramPacket msgPacket = new DatagramPacket(dados, dados.length, MULTCAST_ADDRESS, CLIENT_PORT);
        try {
            GROUP_CONECTION.send(msgPacket);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getConversation() {
        return CONVERSATION;
    }

    public static void setConversation(String msg) {
        CONVERSATION = CONVERSATION + msg;
    }

    public ArrayList<String> getClients() {
        return clients;
    }

    public static void addClientOnline(String clientId) {
        if(!clients.contains(clientId))
            clients.add(clientId);
    }

    /**
     *verifica os dados e retorna True para validar. Caso caso contrário, else
     *
     * @param login
     * @param password
     * @return
     */
    public boolean login(String login, String password) {

        Client client = new Client();
        if (client.enviarNivel1(login, password).equals("OK")){
            MY_ID = login;
            return true;
        } else {
            return false;
        }
        
    }

    public static String getMyId() {
        return MY_ID;
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
                        if (!MY_ID.equals(dadosRecebidos[1].trim())) 
                            Comunication.setConversation("~~" + dadosRecebidos[1] + " entrou na conversa ~~\n");
                        numberLastMsg.add(-1); 
                    }
                    if (msg.startsWith("1001")) {
                        String[] dadosRecebidos = msg.split(";");
                        /*Comunication.setConversation(dadosRecebidos[1] + ": " +
                                dadosRecebidos[2] + "\n");*/
                        checkMsg(dadosRecebidos);
                        Comunication.addClientOnline(dadosRecebidos[1].trim());
                    }
                    if(msg.startsWith("1002")) {
                        System.out.println("aqui1");
                        String[] dadosRecebidos = msg.split(";");
                        if(dadosRecebidos[2].trim().equals(MY_ID)){
                            resend(dadosRecebidos[1].trim(), dadosRecebidos[3].trim());
                        }
                    }
                    if(msg.startsWith("1003")){
                        String[] dadosRecebidos = msg.split(";");
                        if(dadosRecebidos[2].equals(MY_ID)){
                            String [] data = dadosRecebidos[3].split("@");
                            for(String d: data){
                                if(!d.trim().equals(""))
                                    Comunication.setConversation(dadosRecebidos[1] + ": " + d.trim()+ "\n");
                            }
                        }
                        
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void resend(String idRequester, String numberLastMsg) {
            StringBuilder msg = new StringBuilder();
            int number = Integer.parseInt(numberLastMsg);
            int t = messages.size();
            
            for(int i = number+1; i<t; i++){
                msg.append(messages.get(i)).append("@");
            }
            byte dados [] = ("1003" + ";" + MY_ID  + ";" + idRequester + ";" + msg.toString()).getBytes();
            DatagramPacket msgPacket = new DatagramPacket(dados, dados.length, MULTCAST_ADDRESS, CLIENT_PORT);
            try {
                GROUP_CONECTION.send(msgPacket);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        private void checkMsg(String[] dadosRecebidos) {
            String idAuthor = dadosRecebidos[1];
            Integer numberMsg = Integer.parseInt(dadosRecebidos[3].trim());
            if(clients.contains(idAuthor.trim())){
                int id = clients.indexOf(idAuthor);
                System.out.println("Author: "+idAuthor+" id is this: "+id);
                if(numberMsg>(numberLastMsg.get(id)+1)){
                    System.out.println("numberMsg: "+numberMsg+"number last: "+numberLastMsg.get(id));
                    requestResend(MY_ID, idAuthor, numberLastMsg.get(id));
                }else{
                    Comunication.setConversation(dadosRecebidos[1] + ": " +
                                dadosRecebidos[2] + "\n");
                    numberLastMsg.set(id, numberMsg);
                }
            }else{
                Comunication.setConversation(dadosRecebidos[1] + ": " +
                                dadosRecebidos[2] + "\n");
                numberLastMsg.add(numberMsg);
            }
        }
    }
}