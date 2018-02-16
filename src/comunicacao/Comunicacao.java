package comunicacao;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by wanderson on 27/06/17.
 */

/**
 * classe responsável por fazer a comunicação entre os clientes
 */
public class Comunicacao {

    private static InetAddress enderecoMulticast;
    private static MulticastSocket conexaoGrupo;
    private final static int PORTA_CLIENTE = 44444;
    private static String meuID;

    public Comunicacao(String id) {
        meuID = id;
    }

    /**
     * inicia o grupo multicast
     */
    public void iniciarGrupo() {
        try {
            enderecoMulticast = InetAddress.getByName("235.0.0.1");
            conexaoGrupo = new MulticastSocket(PORTA_CLIENTE);
            conexaoGrupo.joinGroup(enderecoMulticast);
            new ThreadCliente(conexaoGrupo).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void notificarEntrada(){
        byte dados[] = ("1000"+";"+meuID).getBytes();
        DatagramPacket msgPacket = new DatagramPacket(dados, dados.length, enderecoMulticast, PORTA_CLIENTE);
        try {
            conexaoGrupo.send(msgPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enviarMsg(String msg, String id) {
        byte dados[] = ("1001" + ";" + id +";"+msg).getBytes();
        DatagramPacket msgPacket = new DatagramPacket(dados, dados.length, enderecoMulticast, PORTA_CLIENTE);
        try {
            conexaoGrupo.send(msgPacket);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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

                    if (msg.startsWith("1000")){
                        String[] dadosRecebidos = msg.split(";");
                        if(!meuID.equals(dadosRecebidos[1].trim()))
                            System.out.println(dadosRecebidos[1]+" entrou na conversa \n");
                    }
                    if (msg.startsWith("1001")) {
                        String[] dadosRecebidos = msg.split(";");
                        if(!meuID.equals(dadosRecebidos[2].trim()))
                        System.out.println("Mensagem de "+dadosRecebidos[2]+": "+
                        dadosRecebidos[1]+"\n");
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}