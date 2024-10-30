import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CaixaAutomaticoClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            BancoInterface banco = (BancoInterface) registry.lookup("BancoServer");

            // Simulação de operações e injeção de falha
            String idConta = "1234";
            banco.depositar(idConta, 200);
            System.out.println("Depósito realizado. Saldo: " + banco.consultarSaldo(idConta));

            try {
                // Simula uma falha antes da confirmação do saque
                if (banco.sacar(idConta, 50)) {
                    throw new RuntimeException("Falha simulada após saque");
                }
            } catch (RuntimeException e) {
                System.out.println("Erro simulado: " + e.getMessage());
            }

            System.out.println("Saldo após tentativa de saque com falha: " + banco.consultarSaldo(idConta));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
