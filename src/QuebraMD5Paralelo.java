
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

 /**
 * @author pargles
 * classe que faz todos os calculos e tambem
 * e paralela extendo a classe Thread
 */ 
public class QuebraMD5Paralelo extends Thread {
    private  final String completeDigits = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%&*()_-+=[]{}?/\\|><";
    private  final String basicDigits = "0123456789abcdefghijklmnopqrstuvwxyz";
    private  String hashAtual;
    private  int inicio;
    private  int porcao;
    private int iniComplete;
    private int porcaoComplete;
    private FileWriter fw;
    private File arquivo;
    private BufferedWriter bw;
    private ResultadoPorEmail email;
    private long tempoInicio = System.currentTimeMillis();
    
    public QuebraMD5Paralelo(String hash) {
        hashAtual = hash;
    }
      
    public QuebraMD5Paralelo(String hash,int ini,int slice,int ini2,int slice2) throws IOException {
        hashAtual = hash;
        inicio = ini;
        porcao = slice;
        iniComplete=ini2;
        porcaoComplete=slice2;     
    }
    
    @Override
    public void run() {
        try{
            boolean todosDigitos= false;
            md5Cracker4Letras(hashAtual,inicio,porcao,basicDigits.length(),todosDigitos);
            md5Cracker5Letras(hashAtual,inicio,porcao,basicDigits.length(),todosDigitos);
            //md5Cracker6Letras(hashAtual,inicio,porcao,basicDigits.length(),todosDigitos);
            todosDigitos = true;//comeca da metade, nao repete os que ja foram
            System.out.println("verificando simbolos especiais");
            md5Cracker4Letras(hashAtual,iniComplete,porcaoComplete,completeDigits.length(),todosDigitos);
            md5Cracker5Letras(hashAtual,iniComplete,porcaoComplete,completeDigits.length(),todosDigitos);
            //md5Cracker6Letras(hashAtual,iniComplete,porcaoComplete,completeDigits.length(),todosDigitos);
            
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
    private void heureka(final String iKey,final String hash) throws NoSuchAlgorithmException, IOException {
        /*if ((s = md5Cracker(hash, iKey, iKey))!= null){*/
        long tempo = (System.currentTimeMillis() - tempoInicio);  
        
        long segundos = tempo / 1000;  
        //System.out.println(segundos);
        long minutos = segundos / 60; 
        long horas = minutos / 60;
        minutos = horas>=1? minutos-horas*60:minutos;//se ja foi mais de uma hora (60 min) tenho q descontar dos minutos, 
        segundos = minutos>=1?segundos-minutos*60:segundos;//se ja foi mais de um minuto (60s) tenho que descontar dos segundos
        String t = "em: "+horas+" horas e "+minutos+" minutos e "+segundos+" segundos";
        System.out.println("EBA, ENCONTROU: " + iKey);
        salvarEmArquivo(iKey,hash,t);
        enviarEmail(iKey,hash,t);
        System.exit(0);
        //}
    }
    
    public void md5Cracker4Letras(String hash, int inicio, int parte,int fim,boolean todosDigitos) throws java.security.NoSuchAlgorithmException, UnsupportedEncodingException, FileNotFoundException, IOException {
        StringBuffer sb = new StringBuffer();
        int ini2 = todosDigitos?36:0;//37 e onde comeca o ABCDEF....
        for (int i = inicio; i < parte; i++) {
            for (int j = 0; j < fim; j++) {
                for (int k = 0; k < fim; k++) {
                    for (int l = ini2; l < fim; l++) {
                        sb.delete(0, sb.length());
                        sb.append(completeDigits.charAt(i));
                        sb.append(completeDigits.charAt(j));
                        sb.append(completeDigits.charAt(k));
                        sb.append(completeDigits.charAt(l));
                        verificaSolucao(sb,hash);
                    }
                    ini2=0;//agora pode repetir as letras
                }
            }
        }
    }
    
    public void md5Cracker5Letras(String hash, int inicio, int parte,int fim,boolean todosDigitos) throws java.security.NoSuchAlgorithmException, UnsupportedEncodingException, FileNotFoundException, IOException {
        StringBuffer sb = new StringBuffer();
        int ini2 = todosDigitos?36:0;//37 e onde comeca o ABCDEF....
        for (int i = inicio; i < parte; i++) {
            for (int j = 0; j < fim; j++) {
                for (int k = 0; k < fim; k++) {
                    for (int l = 0; l < fim; l++) {
                        for (int m = ini2; m < fim; m++) {
                            sb.delete(0, sb.length());
                            sb.append(completeDigits.charAt(i));
                            sb.append(completeDigits.charAt(j));
                            sb.append(completeDigits.charAt(k));
                            sb.append(completeDigits.charAt(l));
                            sb.append(completeDigits.charAt(m));
                            verificaSolucao(sb,hash);
                        }
                        ini2=0;//agora pode repetir as letras
                    }
                }
            }
        }
    }
    
    public void md5Cracker6Letras(String hash, int inicio, int parte,int fim,boolean todosDigitos) throws java.security.NoSuchAlgorithmException, UnsupportedEncodingException, FileNotFoundException, IOException {
        StringBuffer sb = new StringBuffer();
        int ini2 = todosDigitos?36:0;//37 e onde comeca o ABCDEF....
        for (int i = inicio; i < parte; i++) {
            for (int j = 0; j < fim; j++) {
                for (int k = 0; k < fim; k++) {
                    for (int l = 0; l < fim; l++) {
                        for (int m = 0; m < fim; m++) {
                            for (int n = ini2; n < fim; n++) {
                                sb.delete(0, sb.length());
                                sb.append(completeDigits.charAt(i));
                                sb.append(completeDigits.charAt(j));
                                sb.append(completeDigits.charAt(k));
                                sb.append(completeDigits.charAt(l));
                                sb.append(completeDigits.charAt(m));
                                sb.append(completeDigits.charAt(n));
                                verificaSolucao(sb, hash);
                            }
                            ini2=0;//agora pode repetir as letras
                        }
                    }
                }
            }
        }
    }
    
    public void verificaSolucao(StringBuffer sb,String hash) throws NoSuchAlgorithmException, FileNotFoundException, IOException{  
        String result;
        String temp;
        result = sb.toString();
        temp = cryptWithMD5(result.toString());
        if (temp.equals(hash)) {
            heureka(result,hash);//should return iKey for part of liar detection
        } else {
            //bw.write(result);
            //bw.newLine();
            //bw.flush();
            //System.out.println("not " + result);
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

    private void salvarEmArquivo(String resultado, String hash,String tempo) throws IOException {
        arquivo = new File("resultados.txt");
        fw = new FileWriter(arquivo,true); 
        bw = new BufferedWriter(fw);
        bw.write(hash+" = "+resultado+" "+tempo);
        bw.newLine();
        bw.flush();
        bw.close();
    }

    private void enviarEmail(String resultado,String hash,String tempo) {
        email = new ResultadoPorEmail();
        email.adicionarDestinatario("pargles1@gmail.com");
        email.adicionarDestinatario("enicola90@gmail.com");
        email.setAssunto("MD5Destroyer, Email automatico ");
        email.setTexto("EBA! mais uma hash descoberta\n\n"+hash+" = "+resultado+"\n\n"+tempo);
        email.mandarEmail();
    }
}
