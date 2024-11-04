import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class CaixaAutomaticoClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("26.25.131.244", 1099);
            BancoInterface banco = (BancoInterface) registry.lookup("BancoServer");
            
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("1. Depositar");
                System.out.println("2. Sacar");
                System.out.println("3. Consultar Saldo");
                System.out.println("4. Sair");
                System.out.print("Escolha uma opção: ");
                int choice = scanner.nextInt();
                
                switch (choice) {
                    case 1: // Depositar
                        System.out.print("Digite o ID da conta: ");
                        String depositAccountId = scanner.next();
                        System.out.print("Digite o valor do depósito: ");
                        double depositAmount = scanner.nextDouble();

                        if (banco.depositar(depositAccountId, depositAmount)) {
                            System.out.println("Depósito realizado com sucesso!");
                        } else {
                            System.out.println("Falha no depósito.");
                        }
                        break;
                    case 2: // Sacar
                        System.out.print("Digite o ID da conta: ");
                        String withdrawAccountId = scanner.next();
                        System.out.print("Digite o valor do saque: ");
                        double withdrawAmount = scanner.nextDouble();
                        
                        if (banco.sacar(withdrawAccountId, withdrawAmount)) {
                            System.out.println("Saque realizado com sucesso!");
                        } else {
                            System.out.println("Falha no saque. Saldo insuficiente ou conta inexistente.");
                        }
                        break;
                    case 3: // Consultar saldo
                        System.out.print("Digite o ID da conta: ");
                        String balanceAccountId = scanner.next();
                        double balance = banco.consultarSaldo(balanceAccountId);
                        if (balance != -1.0) {
                            System.out.println("Saldo atual: " + balance);
                        } else {
                            System.out.println("Conta não encontrada!");
                        }
                        break;
                    case 4:
                        System.out.println("Saindo...");
                        scanner.close();
                        System.exit(0);
                    default:
                        System.out.println("Opção inválida!");
                }
            }
        } catch (Exception e) {
            System.err.println("Exceção no cliente Caixa Automático: " + e.toString());
            e.printStackTrace();
        }
    }
}