import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;  
import java.rmi.RemoteException;  
  
/** @author pargles
 * interface que extende a classe Remote
 * classe que contem os metodos que sao viseis 
 * no cliente e no servidor do RMI
 * deve implementar
 */
public interface MD5Destroyer extends Remote {  
  
    public void enviarMensagem( String msg ) throws RemoteException;  
    public String lerMensagem() throws RemoteException; 
    public String getHash()throws RemoteException;
    public void setHash(String senha)throws RemoteException;
    public String queroTrabalho()throws RemoteException;
}