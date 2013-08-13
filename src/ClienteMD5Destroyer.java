import java.net.InetAddress;
import java.rmi.Naming;  
import java.rmi.RemoteException;  
import java.rmi.NotBoundException;  
import java.net.MalformedURLException;  
  /*
   * http://www.codelord.net/2011/06/18/statistics-of-62k-passwords/
   * http://www.schneier.com/blog/archives/2006/12/realworld_passw.html
   */
public class ClienteMD5Destroyer { 
    private static QuebraMD5Paralelo md5parallel;
    private static String[] hashAtual;
    private static int processadores;
    private static String computerName;
    private static String usuario;
    
   public static void main( String args[] ) throws RemoteException, NotBoundException {  
        try {  
            MD5Destroyer m = (MD5Destroyer) Naming.lookup( "rmi://localhost:1099/MensageiroService" ); 
            try {
                computerName = InetAddress.getLocalHost().getHostName();
                usuario = System.getProperty("user.name");
                System.out.println("pc: "+computerName +" user: "+usuario);
                
            } catch (Exception e) {
                System.out.println("Exception caught =" + e.getMessage());
            }
            if(!m.temTrabalho())
            {
                System.err.println("nao tem mais trabalho");
                System.exit(1);
            }
            processadores = Runtime.getRuntime().availableProcessors();
            System.out.println("voce tem "+processadores+" processadores disponiveis");
            hashAtual = m.queroTrabalho(computerName);
            if (hashAtual != null) {
                md5parallel = new QuebraMD5Paralelo(hashAtual, m);
                md5parallel.md5Dictionary();//primeiro tenta sequencial utilizando o dicionario
                md5parallel.join();
                md5parallel = new QuebraMD5Paralelo(hashAtual, m);//se nao achou no dicionario pega a mesma letra e tenta forca bruta
                md5parallel.start();
            }
            for (int i = 0; i < processadores-1; i++) {
                hashAtual = m.queroTrabalho(computerName);
                if(hashAtual!=null)
                {
                    md5parallel = new QuebraMD5Paralelo(hashAtual,m);//instancia o objeto paralelo passando a hash e a quantidade de letra
                    md5parallel.start();  
                }
                
            }
            //m.enviarMensagem( "terminei!" );  
        }  
        catch( MalformedURLException e ) {  
            System.out.println();  
            System.out.println( "Erro, o endereco do servidor esta incorreto" );
            System.exit(1);
        }  
        catch( RemoteException e ) {  
            System.out.println();  
            System.err.println( "Erro, voce deve inicializar o servidor" );  
            System.exit(1);
        }  
        catch( NotBoundException e ) {  
            System.out.println();  
            System.out.println( "NotBoundException: " + e.toString() ); 
            System.exit(1);
        }  
        catch( Exception e ) {  
            System.out.println();  
            System.out.println( "Exception: " + e.toString() );
            System.exit(1);
        }  
    }
}