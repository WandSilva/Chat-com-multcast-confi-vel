package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Nivel1 {

    private String login;
    private String senha;
    private boolean criouArquivos = false;
    private BufferedWriter gravarArquivos;
    private String senhaA;
    private String senhaB;
    private String senhaC;

    public Nivel1(String login, String senha) {
        this.login = login;
        this.senha = senha;
    }

    public boolean validarAcesso(Nivel1 obj) {

        System.out.println("OK3");
        
        lerArquivos();
        pegarSenha();

        MessageDigest algorithm;

        try {
            algorithm = MessageDigest.getInstance("SHA-256");
            byte messageDigest[] = algorithm.digest(obj.getSenha().getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                hexString.append(String.format("%02X", 0xFF & b));
            }

            String senha = hexString.toString();

            if (obj.getLogin().equals("HostA") && senha.equals(senhaA)) {
                System.out.println("A");
                return true;
            } else if (obj.getLogin().equals("HostB") && senha.equals(senhaB)) {
                System.out.println("B");
                return true;
            } else if (obj.getLogin().equals("HostC") && senha.equals(senhaC)) {
                System.out.println("C");
                return true;
            }
            return false;
        } catch (Exception ex) {
            Logger.getLogger(Nivel1.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }

    public String getLogin() {
        return login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    private void lerArquivos() {
        System.out.println("OK1!");
        if (!criouArquivos) {
            System.out.println("OK!");
            File listaArquivos = new File("n1");
            FileWriter output;
            try {
                output = new FileWriter(new File(listaArquivos, "Nx"));
                gravarArquivos = new BufferedWriter(output);
                MessageDigest algorithm;
                try {
                    algorithm = MessageDigest.getInstance("SHA-256");
                    byte messageDigest[] = algorithm.digest("hosta".getBytes("UTF-8"));
                    StringBuilder hexString = new StringBuilder();
                    for (byte b : messageDigest) {
                        hexString.append(String.format("%02X", 0xFF & b));
                    }
                    String senha = hexString.toString();
                    gravarArquivos.write(senha);
                    gravarArquivos.newLine();

                    algorithm = MessageDigest.getInstance("SHA-256");
                    byte messageDigest2[] = algorithm.digest("hostb".getBytes("UTF-8"));
                    StringBuilder hexString2 = new StringBuilder();
                    for (byte b : messageDigest2) {
                        hexString2.append(String.format("%02X", 0xFF & b));
                    }

                    String senha2 = hexString2.toString();
                    gravarArquivos.write(senha2);
                    gravarArquivos.newLine();

                    algorithm = MessageDigest.getInstance("SHA-256");
                    byte messageDigest3[] = algorithm.digest("hostc".getBytes("UTF-8"));
                    StringBuilder hexString3 = new StringBuilder();
                    for (byte b : messageDigest3) {
                        hexString3.append(String.format("%02X", 0xFF & b));
                    }

                    String senha3 = hexString3.toString();
                    gravarArquivos.write(senha3);
                    gravarArquivos.newLine();

                    gravarArquivos.close();
                    criouArquivos = true;

                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(Nivel1.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (IOException ex) {
                Logger.getLogger(Nivel1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void pegarSenha() {

        File listaArquivos = new File("n1");
        File[] arquivos = listaArquivos.listFiles();
        BufferedReader bf;
        try {
            bf = new BufferedReader(new FileReader(arquivos[0]));
            String linha = bf.readLine();
            senhaA = linha;
            linha = bf.readLine();
            senhaB = linha;
            linha = bf.readLine();
            senhaC = linha;
        } catch (Exception ex) {
            Logger.getLogger(Nivel1.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
