import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by wanderson on 14/02/18.
 */
public class MainC {


    public static void main(String args[]){

        String id;
        Scanner scanner = new Scanner(System.in);

        System.out.println("DIGITE SEU ID");
        id = scanner.nextLine();

        Comunicacao c = new Comunicacao(id);
        c.iniciarGrupo();
        c.notificarEntrada();
        System.out.println("Chat iniciado");
        while (true){

            String msg = scanner.nextLine();
            c.enviarMsg(id, msg);

        }

    }
}


