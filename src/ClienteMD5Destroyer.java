import java.rmi.Naming;  
import java.rmi.RemoteException;  
import java.rmi.NotBoundException;  
import java.net.MalformedURLException;  
  
public class ClienteMD5Destroyer { 
    private static QuebraMD5Paralelo md5parallel;
    private static String hashAtual;
    private static int processadores;
    private static int escalonador[][];
    private static final String basicDigits = "0123456789abcdefghijklmnopqrstuvwxyz";
    private  static final String completeDigits = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%&*()_-+=[]{}?/\\|><";
    private static int sliceSize;
    private static int sliceSizeCompleteDig;
    
    public static void escalonarTarefas() {
        sliceSize = basicDigits.length() / processadores;
        
        escalonador[0][0]=0;//primeira tarefa inicia na posicao 0
        int proximo = sliceSize;
        for (int i = 1; i < processadores; i++) {
            escalonador[i][0] = proximo;
            proximo+=sliceSize;
        }
        
        sliceSizeCompleteDig = completeDigits.length() / processadores;      
        escalonador[0][1]=0;//primeira tarefa inicia na posicao 0
        proximo = sliceSizeCompleteDig;
        for (int i = 1; i < processadores; i++) {
            escalonador[i][1] = proximo;
            proximo+=sliceSizeCompleteDig;
        }
        //System.out.println(basicDigits.length());
    }

    
   public static void main( String args[] ) throws RemoteException, NotBoundException {  
        try {  
            MD5Destroyer m = (MD5Destroyer) Naming.lookup( "rmi://177.201.157.147:1099/MensageiroService" ); 
            hashAtual = m.queroTrabalho();
            if(hashAtual==null)
            {
                System.err.println("nao tem mais trabalho");
                System.exit(1);
            }
            processadores =Runtime.getRuntime().availableProcessors();
            escalonador = new int[processadores][2];
            System.out.println("voce tem "+processadores+" processadores disponiveis");
            escalonarTarefas();
            System.out.println("trabalhando em: " + hashAtual);  
            for (int i = 0; i < escalonador.length; i++) {
                md5parallel = new QuebraMD5Paralelo(hashAtual,escalonador[i][0],escalonador[i][0]+sliceSize,escalonador[i][1],escalonador[i][1]+sliceSizeCompleteDig);//instancia o objeto paralelo passando a hash e a quantidade de letra
                md5parallel.start();
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