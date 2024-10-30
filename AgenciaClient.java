import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class AgenciaClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            BancoInterface banco = (BancoInterface) registry.lookup("BancoServer");

            // Exemplo de operações
            String idConta = "1234";
            banco.abrirConta(idConta);
            banco.depositar(idConta, 500);
            System.out.println("Saldo atual: " + banco.consultarSaldo(idConta));
            banco.sacar(idConta, 100);
            System.out.println("Saldo após saque: " + banco.consultarSaldo(idConta));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
