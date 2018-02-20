package comunication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Nivel1;

public class Server {

    private static int porta = 22222;
    private static Nivel1 nivel1;
    //private static Nivel2 nivel2;
    private static String HostA = "";
    private static String HostB = "";
    private static String HostC = "";

    public static void main(String[] args) {
        try {
            ServerSocket meuServidor = new ServerSocket(porta);
            System.out.println("Servidor executando na porta" + " " + porta);

            while (true) {
                Socket minhaConexao = meuServidor.accept();
                System.out.println("Conexão Estabelecida com:" + " " + minhaConexao.getInetAddress().getHostAddress());
                new ThreadServidor(minhaConexao).start();
            }
        } catch (IOException ex) {
            System.out.println("Conexão Finalizada!");
        }
    }

    private static class ThreadServidor extends Thread {

        private final Socket minhaConexao;
        private final BufferedReader entradaDados;
        private final DataOutputStream saidaDados;

        public ThreadServidor(Socket minhaConexao) throws IOException {
            this.minhaConexao = minhaConexao;
            this.entradaDados = new BufferedReader(new InputStreamReader(this.minhaConexao.getInputStream()));
            this.saidaDados = new DataOutputStream(this.minhaConexao.getOutputStream());
        }

        @Override
        public synchronized void run() {
            while (true) {
                try {
                    if (minhaConexao.isConnected()) {
                        String pacoteDados = entradaDados.readLine();
                        String[] dados = pacoteDados.split(";");
                        if (dados[0].equals("001")) {
                            if (dados[1].equals("HostA") && Server.HostA.equals("")) {
                                if (Server.nivel1 == null) {
                                    nivel1 = new Nivel1(dados[1], dados[2]);
                                }
                                nivel1.setLogin(dados[1]);
                                nivel1.setSenha(dados[2]);

                                if (nivel1.validarAcesso(nivel1)) {
                                    Server.HostA = dados[1];
                                    saidaDados.writeBytes("100\n");
                                } else {
                                    saidaDados.writeBytes("Por favor, verifique informacoes e tente novamente!\n");
                                }

                            } else if (dados[1].equals("HostB") && Server.HostB.equals("")) {
                                if (Server.nivel1 == null) {
                                    nivel1 = new Nivel1(dados[1], dados[2]);
                                }
                                nivel1.setLogin(dados[1]);
                                nivel1.setSenha(dados[2]);

                                if (nivel1.validarAcesso(nivel1)) {
                                    Server.HostB = dados[1];
                                    saidaDados.writeBytes("100\n");
                                } else {
                                    saidaDados.writeBytes("Por favor, verifique informacoes e tente novamente!\n");
                                }
                            } else if (dados[1].equals("HostC") && Server.HostC.equals("")) {
                                if (Server.nivel1 == null) {
                                    nivel1 = new Nivel1(dados[1], dados[2]);
                                }
                                nivel1.setLogin(dados[1]);
                                nivel1.setSenha(dados[2]);

                                if (nivel1.validarAcesso(nivel1)) {
                                    Server.HostC = dados[1];
                                    saidaDados.writeBytes("100\n");
                                } else {
                                    saidaDados.writeBytes("Por favor, verifique informacoes e tente novamente!\n");
                                }
                            } else {
                                saidaDados.writeBytes("Por favor, verifique informacoes e tente novamente!\n");
                            }

////                        } else if (dados[0].equals("002")) {
////                            
////                            if (dados[1].equals("HostA")) {
////                                if (Server.nivel2 == null) {
////                                    nivel2 = new Nivel2(dados[1], dados[2]);
////                                }
////                                nivel2.setLogin(dados[1]);
////                                nivel2.setToken(dados[2]);
////
////                                if (nivel2.validarAcesso(nivel2)) {
////                                    saidaDados.writeBytes("200\n");
////                                } else {
////                                    saidaDados.writeBytes("Por favor, verifique informacoes e tente novamente!\n");
////                                }
////
////                            } else if (dados[1].equals("HostB")) {
////                                if (Server.nivel2 == null) {
////                                    nivel2 = new Nivel2(dados[1], dados[2]);
////                                }
////                                nivel2.setLogin(dados[1]);
////                                nivel2.setToken(dados[2]);
////
////                                if (nivel2.validarAcesso(nivel2)) {
////                                    saidaDados.writeBytes("200\n");
////                                } else {
////                                    saidaDados.writeBytes("Por favor, verifique informacoes e tente novamente!\n");
////                                }
////                            } else {
////                                saidaDados.writeBytes("Por favor, verifique informacoes e tente novamente!\n");
////                            }
                        } else if (dados[0].equals("003")) {
                            if (dados[1].equals("HostA")) {

                                Server.HostA = "";

                                saidaDados.writeBytes("300\n");
                            } else if (dados[1].equals("HostB")) {

                                Server.HostB = "";

                                saidaDados.writeBytes("300\n");
                            } else {
                                Server.HostC = "";
                                saidaDados.writeBytes("300\n");
                            }
                        }
                    }
                } catch (IOException ex) {
                    System.out.println("Conexão Finalizada!");
                }
            }
        }
    }
}