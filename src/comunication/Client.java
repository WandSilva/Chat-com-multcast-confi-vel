package comunication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    private static String host = "127.0.0.1";
    private BufferedReader entradaDados;
    private DataOutputStream saidaDados;
    private Socket minhaConexao;
    private static String usuario;

    public Client(String endIP) {
        try {
            //Client.host = endIP;
            this.minhaConexao = new Socket(host, 22222);
            this.saidaDados = new DataOutputStream(this.minhaConexao.getOutputStream());
            this.entradaDados = new BufferedReader(new InputStreamReader(this.minhaConexao.getInputStream()));

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Client() {
        try {
            this.minhaConexao = new Socket(host, 22222);
            this.saidaDados = new DataOutputStream(this.minhaConexao.getOutputStream());
            this.entradaDados = new BufferedReader(new InputStreamReader(this.minhaConexao.getInputStream()));

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public synchronized String enviarNivel1(String usuario, String senha) {
        if (minhaConexao.isConnected()) {
            try {
                saidaDados.writeBytes("001" + ";" + usuario + ";" + senha + '\n');
                String pacoteDados = entradaDados.readLine();
                if (pacoteDados.startsWith("100")) {
                    Client.usuario = usuario;
                    return "OK";
                } else {
                    return pacoteDados;
                }
            } catch (IOException ex) {
                //System.out.println(ex.toString());
                return "Falha na conexão com o servidor!";
            }
        } else {
            return "ERRO! Tente novamente...";
        }
    }
    
    public synchronized String enviarNivel2(String token) {       
        if (minhaConexao.isConnected()) {
            try {
                saidaDados.writeBytes("002" + ";" + Client.usuario + ";" + token + '\n');
                String pacoteDados = entradaDados.readLine();

                if (pacoteDados.startsWith("200")) {
                    return "CHAT";
                } else {
                    return pacoteDados;
                }
            } catch (IOException ex) {
                //System.out.println(ex.toString());
                return "Falha na conexão com o servidor!";
            }
        } else {
            return "ERRO! Tente novamente...";
        }
    }
    
    
    public synchronized String sair() {
        System.out.println("Passou!");
        if (minhaConexao.isConnected()) {
            try {
                saidaDados.writeBytes("003" + ";" + Client.usuario + '\n');
                String pacoteDados = entradaDados.readLine();

                if (pacoteDados.startsWith("300")) {
                    return "SAIR";
                } else {
                    return pacoteDados;
                }
            } catch (IOException ex) {
                //System.out.println(ex.toString());
                return "Falha na conexão com o servidor!";
            }
        } else {
            return "ERRO! Tente novamente...";
        }
    }

    public static String getUsuario() {
        return usuario;
    }
}