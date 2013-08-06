import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;  
import java.rmi.server.UnicastRemoteObject;  
import java.util.ArrayList;
 /**
 * @author pargles
 * classe que faz a implemtacao de todos os metodos
 * da interface Mensageiro.java
 */ 
public final class MD5DestroyerImpl extends UnicastRemoteObject implements MD5Destroyer {  
  private static  String hash;
  private static ArrayList<String> hashes;
  private static int posicaoVetor=0;
  private static String nomeArquivo = "md5.txt";
    
  /*
   * metodo construtor da classe, apenas inicia o Array de hashes
   * e le as hashes do arquivo ou da propria memoria
   */
    public MD5DestroyerImpl() throws RemoteException, FileNotFoundException, IOException {  
        super(); 
        hashes = new ArrayList<String>();       
        carregarListaDoArquivo(nomeArquivo);
        //carregarListaHashes();
    }  
  
    /* metodo que insere algumas hashes na lista,
     * apenas para testes especificos
     * @param void
     * @return void
     */
     public void carregarListaHashes()
    { 
           
        //hashes.add("476b57ddb0c4cb8101727206ce3dcc30");//cabo
        //hashes.add("fc931ee5ff90c5ae8df045137b633d83");//Anao
        //hashes.add("13993a492d8ecd14ae5e7accdb246de2");//m@ca
        //hashes.add("70be7c22767d5412972bf4e7ff4e059c");//p*ta 
        //hashes.add("1e48c4420b7073bc11916c6c1de226bb");//1010
        //hashes.add("8977ecbb8cb82d77fb091c7a7f186163");//5050
        //hashes.add("c59b756920368254c670e9f464ba54f6");//zona
        //hashes.add("7b5a67574d8b1d77d2803b24946950f0");//juca
        hashes.add("1100c31b51a863d8567f56578615d15f");//jucao
        hashes.add("a763a66f984948ca463b081bf0f0e6d0");//marta
        hashes.add("5c12a4bcabe8958990462298b5510893");//10141
        hashes.add("b81b58570d618d06bcdf81cdd18e1b85");//assim
        hashes.add("2689dfa848b0e3ba84a0e515d8418a73");//acatar
        hashes.add("4320bbb8aec5b2e3b5bac330174fb7cc");//anseao
        hashes.add("01a8649e264e14d4215065fb3eee2112");
        hashes.add("05968f80e5d2f335bddb6faf9cbe23ee");
        hashes.add("22aadb26447d87b550b155a4d764fad0");
        hashes.add("22d5f5321072a715ae3d76907dda5c2f");
        hashes.add("27805b6edb985a5cb9e4912f21775a9b");
        hashes.add("31a468920cff1aad10753b5f14042824");
        hashes.add("3f5469ea889c05ea7e22153216c769d3");
       
    }
       /* metodo que retorna um hash quando o cliente chama esse metodo
     * @param void
     * @return String hash ou null caso nao tiver mais hashes para serem calculadas
     */   
    @Override
    public String queroTrabalho() {
        if (posicaoVetor <= hashes.size()) {
            System.out.println("trabalhando na hash " + posicaoVetor);
            return hashes.get(posicaoVetor++);//retorna a hash para alguma maquina trabalhar e incrementa o indicie para a proxima maquina
        }
        else
        {
            return null;//caso nao tenha mais hashs a serem descobertas
        }

    }
    
  @Override
    public void enviarMensagem( String msg ) throws RemoteException {  
        System.out.println( msg );  
    }  
  
  @Override
    public String lerMensagem() throws RemoteException {  
        return "This is not a Hello World! message";  
    } 
    
  @Override
      public String getHash()throws RemoteException
    {
        return hash;
    }
    
  @Override
    public void setHash( String senha)throws RemoteException
    {
        hash  = senha;
    }

    public static void carregarListaDoArquivo(String nomeArquivo) throws FileNotFoundException, IOException {
        System.out.println("carregando hashs");
        String line;
        BufferedReader data = new BufferedReader(new InputStreamReader(new FileInputStream(nomeArquivo)));
        while ((line = data.readLine()) != null) {
            System.out.println(line);
            hashes.add(line);//cabo
        }
        System.out.println(hashes.size()+" hashes carregadas, servidor pronto!");
        data.close();
        
    }
}
