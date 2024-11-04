import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class BancoServer extends UnicastRemoteObject implements BancoInterface {
    private ConcurrentHashMap<String, Double> contas = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Boolean> completedTransactions = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    public BancoServer() throws RemoteException {
        super();
    }

    private String generateTransactionID() {
        return UUID.randomUUID().toString();
    }

    private boolean isTransactionCompleted(String transactionID) {
        return completedTransactions.containsKey(transactionID);
    }

    private void markTransactionCompleted(String transactionID) {
        completedTransactions.put(transactionID, true);
    }

    @Override
    public synchronized boolean abrirConta(String idConta) throws RemoteException {
        if (!contas.containsKey(idConta)) {
            contas.put(idConta, 0.0);
            locks.put(idConta, new ReentrantLock()); // Cria um lock para a nova conta
            return true;
        }
        return false; // conta já existe
    }

    @Override
    public synchronized boolean fecharConta(String idConta) throws RemoteException {
        ReentrantLock lock = locks.get(idConta);
        if (lock != null) {
            lock.lock();
            try {
                if (contas.remove(idConta) != null) {
                    locks.remove(idConta); // Remove o lock associado à conta
                    return true;
                }
            } finally {
                lock.unlock();
            }
        }
        return false;
    }

    @Override
    public double consultarSaldo(String idConta) throws RemoteException {
        return contas.getOrDefault(idConta, -1.0); // -1.0 indica conta inexistente
    }

    @Override
    public boolean depositar(String idConta, double valor) throws RemoteException {
        String transactionID = generateTransactionID();
        if (isTransactionCompleted(transactionID)) {
            return false; // Transação duplicada; já processada
        }

        ReentrantLock lock = locks.computeIfAbsent(idConta, k -> new ReentrantLock());
        lock.lock(); // Bloqueia a conta durante o depósito
        try {
            if (contas.containsKey(idConta) && valor > 0) {
                contas.put(idConta, contas.get(idConta) + valor);
                markTransactionCompleted(transactionID);
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean sacar(String idConta, double valor) throws RemoteException {
        String transactionID = generateTransactionID();
        if (isTransactionCompleted(transactionID)) {
            return false; // Transação duplicada; já processada
        }

        ReentrantLock lock = locks.computeIfAbsent(idConta, k -> new ReentrantLock());
        lock.lock(); // Bloqueia a conta durante o saque
        try {
            if (contas.containsKey(idConta) && contas.get(idConta) >= valor) {
                contas.put(idConta, contas.get(idConta) - valor);
                markTransactionCompleted(transactionID);
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.hostname", "26.25.131.244");

            BancoServer server = new BancoServer();

            // Registra o servidor RMI na porta 1099
            LocateRegistry.createRegistry(1099).rebind("BancoServer", server);

            System.out.println("Servidor Banco pronto na rede.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
