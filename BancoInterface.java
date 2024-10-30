import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BancoInterface extends Remote {
    boolean abrirConta(String idConta) throws RemoteException;
    boolean fecharConta(String idConta) throws RemoteException;
    double consultarSaldo(String idConta) throws RemoteException;
    boolean depositar(String idConta, double valor) throws RemoteException;
    boolean sacar(String idConta, double valor) throws RemoteException;
}

