import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Scanner;

class MinhaPrimeiraThread extends Thread {
    private BancoInterface banco;
    public MinhaPrimeiraThread(BancoInterface banco) {
        this.banco = banco;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 5; i++) {
            try {
                banco.sacar("1234", 20);
            } catch (RemoteException ex) {
            }
        }
    }
}

class MinhaSegundaThread extends Thread {
    private BancoInterface banco;
    public MinhaSegundaThread(BancoInterface banco) {
        this.banco = banco;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 5; i++) {
            try {
                banco.depositar("1234", 20);
            } catch (RemoteException ex) {
            }
        }
    }
}

public class AgenciaClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int option;
        String idConta = "1234"; // ID de conta padrão
        double valor;

        try {
            String serverAddress = "26.25.131.244"; // IP do servidor
            String serverName = "BancoServer";        // Nome do serviço

            BancoInterface banco = (BancoInterface) Naming.lookup("rmi://" + serverAddress + ":1099/" + serverName);
            banco.abrirConta(idConta);
            
            
            MinhaPrimeiraThread mpt = new MinhaPrimeiraThread(banco);
            MinhaSegundaThread mst = new MinhaSegundaThread(banco);

       

            do {
                System.out.println("\n--- Menu de Operações ---");
                System.out.println("1. Abrir Conta");
                System.out.println("2. Fechar Conta");
                System.out.println("3. Depósito");
                System.out.println("4. Retirada");
                System.out.println("5. Consulta de Saldo");
                System.out.println("0. Sair");
                System.out.print("Escolha uma opção: ");
                option = scanner.nextInt();
                System.out.println();

                if (option != 0) {
                    idConta = getId(scanner);
                }

                switch (option) {
                    case 1:
                        if (banco.abrirConta(idConta)) {
                            System.out.println("Conta criada com sucesso!");
                        } else {
                            System.out.println("Conta já existe!");
                        }
                        break;
                    case 2:
                        if (banco.fecharConta(idConta)) {
                            System.out.println("Conta fechada com sucesso!");
                        } else {
                            System.out.println("Conta não encontrada!");
                        }
                        break;
                    case 3:
                        valor = getValor(scanner, "depositar");
                        if (executeWithLock(banco, idConta, valor, "depositar")) {
                            System.out.println("Depósito realizado com sucesso!");
                        } else {
                            System.out.println("Falha no depósito. Outro cliente está realizando uma operação de depósito.");
                        }
                        break;
                    case 4:
                        valor = getValor(scanner, "sacar");
                        if (executeWithLock(banco, idConta, valor, "sacar")) {
                            System.out.println("Saque realizado com sucesso!");
                        } else {
                            System.out.println("Falha no saque. Saldo insuficiente ou conta inexistente.");
                        }
                        break;
                    case 5:
                        double saldo = consultarSaldo(banco, idConta);
                        if (saldo != -1.0) {
                            System.out.println("Saldo atual: " + saldo);
                        } else {
                            System.out.println("Conta não encontrada!");
                        }
                        break;
                    case 6:
                    	 mpt.start();
                         mst.start();
                         mpt.join();
                         mst.join();
                         break;
                    	
                    case 0:
                        System.out.println("Saindo...");
                        break;
                    default:
                        System.out.println("Opção inválida! Tente novamente.");
                }

            } while (option != 0);
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getId(Scanner scanner) {
        System.out.print("Digite o ID da conta: ");
        scanner.nextLine();
        return scanner.nextLine();
    }

    public static double getValor(Scanner scanner, String operacao) {
        System.out.print("Digite o valor da operação de " + operacao + ": ");
        return scanner.nextDouble(); // deve ser double, já que estamos lidando com valores monetários
    }

    // Método para executar operações com bloqueio
    private static boolean executeWithLock(BancoInterface banco, String idConta, double valor, String operacao) {
        try {
            switch (operacao) {
                case "depositar":
                    return banco.depositar(idConta, valor);
                case "sacar":
                    return banco.sacar(idConta, valor);
                default:
                    System.out.println("Operação desconhecida.");
                    return false;
            }
        } catch (Exception e) {
            System.out.println("Erro ao executar a operação: " + e.getMessage());
            return false;
        }
    }

    // Método para consultar saldo
    private static double consultarSaldo(BancoInterface banco, String idConta) {
        try {
            return banco.consultarSaldo(idConta); // retorna o saldo
        } catch (Exception e) {
            System.out.println("Erro ao consultar saldo. Tente novamente.");
            return -1.0; // conta não encontrada
        }
    }
    
}

