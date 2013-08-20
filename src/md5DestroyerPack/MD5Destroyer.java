package md5DestroyerPack;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;  
import java.rmi.RemoteException;  
  
/** @author pargles
 * @author eduardo
 * author Stephano
 * interface que extende a classe Remote
 * Contem os metodos que sao viseis 
 * no cliente e no servidor do RMI
 */
public interface MD5Destroyer extends Remote {  
  
    public void enviarMensagem( String msg ) throws RemoteException;  
    public String lerMensagem() throws RemoteException; 
    public String[] queroTrabalho(String computer)throws RemoteException;
    public boolean temTrabalho() throws RemoteException;
    public void encontreiResultado(String hash,String resultado) throws RemoteException;
}