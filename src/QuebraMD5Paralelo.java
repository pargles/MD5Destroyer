
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

 /**
 * @author pargles
 * @author eduardo
 * @author stephano
 * classe que faz todos os calculos e tambem
 * e paralela extendo a classe Thread
 */ 
public class QuebraMD5Paralelo extends Thread {
    private  final String alfabeto;
    private BufferedReader data;
    private int letrasASeremCalculadas;
    private  int inicio;
    private int fim;
    private MD5Destroyer servidor;
    private TreeMap tree;
    
    public QuebraMD5Paralelo(String[] hash, MD5Destroyer server) throws FileNotFoundException, IOException, NoSuchAlgorithmException {
        tree = new TreeMap();
        letrasASeremCalculadas = Integer.parseInt(hash[0]);
        alfabeto = hash[3];
        char ini = hash[1].charAt(0);
        char end = hash[2].charAt(0);
        inicio = alfabeto.indexOf(ini);
        fim = alfabeto.indexOf(end);
        servidor = server;
        carregarListaDoArquivo("md5.txt");
        data=null;
    }
    
    @Override
    public void run() {
        try{
            switch( letrasASeremCalculadas)
            {
                case 4:
                    md5Cracker4Letras(inicio,fim,alfabeto.length());
                    //System.out.println(getStringTempo(System.currentTimeMillis() - tempoInicio));
                    break;
                case 5:
                    md5Cracker5Letras(inicio,fim,alfabeto.length());
                    //System.out.println(getStringTempo(System.currentTimeMillis() - tempoInicio));
                    break;
                case 6:
                    md5Cracker6Letras(inicio,fim,alfabeto.length());
                    //System.out.println(getStringTempo(System.currentTimeMillis() - tempoInicio));
                    break;
            }           
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(QuebraMD5Paralelo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(QuebraMD5Paralelo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(QuebraMD5Paralelo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(QuebraMD5Paralelo.class.getName()).log(Level.SEVERE, null, ex);
        }
        

    }
    private synchronized void  heureka(String iKey,String hash) throws NoSuchAlgorithmException, IOException {
        /*if ((s = md5Cracker(hash, iKey, iKey))!= null){*/
        servidor.encontreiResultado(hash,iKey);
        
        System.out.println("EBA, ENCONTROU: " + iKey);
        //System.exit(0);
        //}
    }
    
    public void md5Cracker4Letras(int inicio, int parte,int fim) throws java.security.NoSuchAlgorithmException, UnsupportedEncodingException, FileNotFoundException, IOException {       
        StringBuffer sb = new StringBuffer();
        for (int i = inicio; i < parte; i++) {
            for (int j = 0; j < fim; j++) {
                for (int k = 0; k < fim; k++) {
                    for (int l = 0; l < fim; l++) {
                        sb.delete(0, sb.length());
                        sb.append(alfabeto.charAt(i));
                        sb.append(alfabeto.charAt(j));
                        sb.append(alfabeto.charAt(k));
                        sb.append(alfabeto.charAt(l));
                        verificaSolucao(sb);
                    }
                }
            }
        }
    }
    
    public void md5Cracker5Letras(int inicio, int parte,int fim) throws java.security.NoSuchAlgorithmException, UnsupportedEncodingException, FileNotFoundException, IOException {
        StringBuffer sb = new StringBuffer();
        for (int i = inicio; i < parte; i++) {
            for (int j = 0; j < fim; j++) {
                for (int k = 0; k < fim; k++) {
                    for (int l = 0; l < fim; l++) {
                        for (int m = 0; m < fim; m++) {
                            sb.delete(0, sb.length());
                            sb.append(alfabeto.charAt(i));
                            sb.append(alfabeto.charAt(j));
                            sb.append(alfabeto.charAt(k));
                            sb.append(alfabeto.charAt(l));
                            sb.append(alfabeto.charAt(m));
                            verificaSolucao(sb);
                        }
                    }
                }
            }
        }
    }
    
    public void md5Cracker6Letras(int inicio, int parte,int fim) throws java.security.NoSuchAlgorithmException, UnsupportedEncodingException, FileNotFoundException, IOException {      
        StringBuffer sb = new StringBuffer();
        for (int i = inicio; i < parte; i++) {
            for (int j = 0; j < fim; j++) {
                for (int k = 0; k < fim; k++) {
                    for (int l = 0; l < fim; l++) {
                        for (int m = 0; m < fim; m++) {
                            for (int n = 0; n < fim; n++) {
                                sb.delete(0, sb.length());
                                sb.append(alfabeto.charAt(i));
                                sb.append(alfabeto.charAt(j));
                                sb.append(alfabeto.charAt(k));
                                sb.append(alfabeto.charAt(l));
                                sb.append(alfabeto.charAt(m));
                                sb.append(alfabeto.charAt(n));
                                verificaSolucao(sb);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void verificaSolucao(StringBuffer sb) throws NoSuchAlgorithmException, FileNotFoundException, IOException{ 
        String temp = cryptWithMD5(sb.toString());
        if(tree.containsKey(temp))
        {
            heureka(sb.toString(),temp);
        }
    }
     /*
         * http://docs.oracle.com/javase/1.4.2/docs/guide/security/CryptoSpec.html#AppA
         * http://www.twmacinta.com/myjava/fast_md5.php
         */
    public  String cryptWithMD5(String message) throws NoSuchAlgorithmException {
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(message.getBytes("UTF-8")); //converting byte array to Hexadecimal String
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            digest = sb.toString();
        } catch (UnsupportedEncodingException ex) {
           System.err.println("erro");
        }
        return digest;
    }
    
     /* metodo que carrega todas as hash de um arquiivo chamado "md5.txt"
     * @param void
     * @return String nome do arquivo
     */
    public void carregarListaDoArquivo(String nomeArquivo) throws FileNotFoundException, IOException, NoSuchAlgorithmException {
        System.out.println("carregando hashs");
        String line;
        BufferedReader data = new BufferedReader(new InputStreamReader(new FileInputStream(nomeArquivo)));
        while ((line = data.readLine()) != null) {
            tree.put( line,1);
        }
        //System.out.println(tree.size() + " hashes carregadas,");
        data.close();
    }
    
}
