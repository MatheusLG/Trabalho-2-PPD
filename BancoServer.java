import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;

public class BancoServer extends UnicastRemoteObject implements BancoInterface {
    private ConcurrentHashMap<String, Double> contas = new ConcurrentHashMap<>();

    public BancoServer() throws RemoteException {
        super();
    }

    @Override
    public synchronized boolean abrirConta(String idConta) throws RemoteException {
        if (!contas.containsKey(idConta)) {
            contas.put(idConta, 0.0);
            return true;
        }
        return false; // conta já existe
    }

    @Override
    public synchronized boolean fecharConta(String idConta) throws RemoteException {
        return contas.remove(idConta) != null;
    }

    @Override
    public double consultarSaldo(String idConta) throws RemoteException {
        return contas.getOrDefault(idConta, -1.0); // -1.0 indica conta inexistente
    }

    @Override
    public synchronized boolean depositar(String idConta, double valor) throws RemoteException {
        if (contas.containsKey(idConta) && valor > 0) {
            contas.put(idConta, contas.get(idConta) + valor);
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean sacar(String idConta, double valor) throws RemoteException {
        if (contas.containsKey(idConta) && contas.get(idConta) >= valor) {
            contas.put(idConta, contas.get(idConta) - valor);
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            BancoServer server = new BancoServer();
            java.rmi.registry.LocateRegistry.createRegistry(1099).rebind("BancoServer", server);
            System.out.println("Servidor Banco pronto.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
