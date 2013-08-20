package md5DestroyerPack;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author pargles 
 * author Eduardo 
 * author Stephano 
 * @see http://reusablesec.blogspot.ca/2009/05/character-frequency-analysis-info.html
 * @see http://www.skullsecurity.org/wiki/index.php/Passwords
 * @see http://www.mat.uc.pt/~pedro/lectivos/CodigosCriptografia1011/interTIC07pqap.pdf
 * @see 
 * Faz a implemtacao de todos os metodos da interface
 * Mensageiro.java
 */
public final class MD5DestroyerImpl extends UnicastRemoteObject implements MD5Destroyer {

    private ArrayList<String[]> resultados;
    private TreeMap hashs;
    private String completeDigits = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%&*()_-+=[]{}?/\\|><^";//o ultimo caracter serve apenas como indicador do fim
    private ArrayList<char[]> escalonador;
    private ArrayList<String> emails;
    private long tempoInicio;
    private FileWriter fw;
    private File arquivo;
    private BufferedWriter bw;
    private ResultadoPorEmail email;
    
    /**
     * Inicia o Array de hashes e le as hashes do arquivo ou da propria memoria
     * @throws RemoteException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws NoSuchAlgorithmException 
     */
    public MD5DestroyerImpl() throws RemoteException, FileNotFoundException, IOException, NoSuchAlgorithmException {
        super();
        escalonador = new ArrayList<char[]>();
        resultados = new ArrayList<String[]>();
        hashs = new TreeMap();
        emails = new ArrayList<String>();
        //emails.add("adenauer@inf.ufpel.edu.br");
        emails.add("pargles1@gmail.com");
        //emails.add("enicola90@gmail.com");
        //emails.add("smmgoncalves@inf.ufpel.edu.br");
        tempoInicio = System.currentTimeMillis();
        escalonar();
        carregarListaDoArquivo("md5.txt");
        
        //enviarEmail("teste", "teste", "teste");
    }

    /**
     * Retorna a próxima hash disponível do escalonador
     * @param computer
     * @return String hash ou null caso nao tiver mais hashes para serem calculadas
     */
    @Override
    public String[] queroTrabalho(String computer) {
        char tempC[];
        String[] tempS = new String[4];
        tempC = escalonador.remove(0);//sempre pega a primeira letra
        //tirou o ultimo, quer dizer que chegou no fim e nguem encontrou o resultado  
        
        tempS[0] = Character.toString(tempC[2]);//quantidade de letras a ser avaliadas
        tempS[1] = Character.toString(tempC[0]);//letra inicial
        tempS[2] = Character.toString(tempC[1]);//letra final
        tempS[3] = completeDigits;//este e o alfabeto
        String message = computer + " esta trabalhando na letra " + tempC[0]+" com "+tempC[2]+" letras";
        try {
            salvarEmArquivo(message,"","","log.txt");
        } catch (IOException ex) {
            Logger.getLogger(MD5DestroyerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(message);
        return tempS;//retorna a hash para alguma maquina trabalhar e incrementa o indicie para a proxima maquina
        
    }

    @Override
    public void enviarMensagem(String msg) throws RemoteException {
        System.out.println(msg);
    }

    @Override
    public String lerMensagem() throws RemoteException {
        return "This is not a Hello World! message";
    }


    /** Divide a tarefa entre os computadores.
     * Coloac em um vetor de char qual a letra inicial que 
     * um computador tera de calcular e seus calculos vao ate
     * uma letra final que tambem e inserida no vetor
     * @param void
     */
    public void escalonar() {
        char[] temp;
        //temp = new char[3];
        //temp[0] = completeDigits.charAt(0);//letra inicial
        //temp[1] = completeDigits.charAt(completeDigits.length()-1);//letra final
        //temp[2] = '4';//o primeiro nucleo vai receber toda computacao dos quadtro digitos
        //escalonador.add(temp);
        for (int j = 4; j <= 6; j++) {  //j representa o numero de letras da senha
            for (int i = 0; i < completeDigits.length() - 1; i++) {
                temp = new char[3];
                temp[0] = completeDigits.charAt(i);//letra inicial
                temp[1] = completeDigits.charAt(i + 1);//letra final
                temp[2] = (char) ('0' + j); // numero de letras da senha (4,5 ou 6)
                escalonador.add(temp);
            }
        }
    }

    /**
     * @param void
     * @return true, se a lista de hash não estiver vazia, false caso contrário
     */
    @Override
    public boolean temTrabalho() throws RemoteException {
        boolean vazio = hashs.isEmpty()||escalonador.isEmpty();
        return !vazio;
    }

    /**
     * Recebe uma mensagem do nodo indicando que encontrou o resultado.
     * Responsavel por salvar o resultado em arquivo e tambem por enviar por email
     * @param hash 
     * @param resultado
     * @throws RemoteException 
     */
    @Override
    public synchronized void encontreiResultado(String hash, String resultado) throws RemoteException {
        long tempo = (System.currentTimeMillis() - tempoInicio);
        String t = getStringTempo(tempo);
        String result[] = new String[3];
        result[0]=hash;
        result[1]=resultado;
        result[2]= t;
        resultados.add(result);
        hashs.remove(hash);
        try {
            salvarEmArquivo(resultado, hash, t,"resultado.txt");
       } catch (IOException ex) {
            Logger.getLogger(MD5DestroyerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        enviarEmail(resultado, hash, t);
        System.out.println("----------------------------------------");
        System.out.println("RESULTADO: " + hash + " = " + resultado+" "+t);
        System.out.println(hashs.size()+ " hashs restantes");
        System.out.println("----------------------------------------");
        try {
            salvarEmArquivo(  hash+" = "+resultado , "---------------------------------------\nRESULTADO: ","\n---------------------------------------","log.txt");
        } catch (IOException ex) {
            Logger.getLogger(MD5DestroyerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(hashs.isEmpty())//ja encontrou todos os resultados
        {
            String listaResultado = resultados.size()+" hashs quebradas\n\n";
            for(int i=0;i<resultados.size();i++)
            {
                listaResultado= listaResultado+resultados.get(i)[0]+" = "+resultados.get(i)[1]+"  "+resultados.get(i)[2]+"\n";
            }
            listaResultado = listaResultado+"\n\n"+getStringTempo(System.currentTimeMillis()-tempoInicio);
            enviarEmail(listaResultado);
            System.exit(1);
        }
    }

    /**
     *  Recebe o tempo que foi necessario para calcular a hash
     * e devolve o tempo em formato de string contendo horas, minutos e segundos
     * @param tempo tempo que foi necesario para quebrar a hash
     * @return String tempo formatado
     */
    private String getStringTempo(long tempo) {
        long segundos = tempo / 1000;
        long minutos = segundos / 60;
        long horas = minutos / 60;
        minutos = horas >= 1 ? minutos - horas * 60 : minutos;//se ja foi mais de uma hora (60 min) tenho q descontar dos minutos, 
        segundos = minutos >= 1 ? segundos - minutos * 60- horas*3600 : segundos;//se ja foi mais de um minuto (60s) tenho que descontar dos segundos
        char h = horas>1?'s':' ';
        char m = minutos>1?'s':' ';
        return "em: " + horas + " hora"+h+ " e " + minutos + " minuto"+m+" e " + segundos + " segundos";
    }

    /**
     * Salva o resultado no arquivo "resultados.txt"
     * @param resultado
     * @param hash
     * @param tempo
     * @param nomeArq
     * @throws IOException 
     */
    private void salvarEmArquivo(String resultado, String hash, String tempo,String nomeArq) throws IOException {
        arquivo = new File(nomeArq);
        fw = new FileWriter(arquivo, true);
        bw = new BufferedWriter(fw);
        bw.write(hash + " = " + resultado + " " + tempo);
        bw.newLine();
        bw.flush();
        bw.close();
    }

    /**
     * Envia um email contendo o resultado encontrado.
     * Como se trata de um processamento distruido em diferentes computadores
     * torna-se necessario o envio de informacao utilizando este meio
     * @param resultado
     * @param hash
     * @param tempo 
     */
    private void enviarEmail(String resultado, String hash, String tempo) {
        email = new ResultadoPorEmail();
        for (int i = 0; i < emails.size(); i++) {
            email.adicionarDestinatario(emails.get(i));
        }
        email.setAssunto("MD5Destroyer, Email automatico ");
        email.setTexto("EBA! mais uma hash descoberta\n\n" + hash + " = " + resultado + "\n\n" + tempo);
        email.mandarEmail();
    }
    
    /**
     * Envia mensagem por email
     * @param mensagem
     */
    private void enviarEmail(String mensagem) {
        email = new ResultadoPorEmail();
        for (int i = 0; i < emails.size(); i++) {
            email.adicionarDestinatario(emails.get(i));
        }
        email.setAssunto("MD5Destroyer, Email automatico ");
        email.setTexto(mensagem);
        email.mandarEmail();
    }
    
    /**
     * Carrega todas as hash de um arquiivo chamado "md5.txt"
     * @param nomeArquivo
     */
    public void carregarListaDoArquivo(String nomeArquivo) throws FileNotFoundException, IOException, NoSuchAlgorithmException {
        System.out.println("carregando hashs");
        String line;
        BufferedReader data = new BufferedReader(new InputStreamReader(new FileInputStream(nomeArquivo)));
        while ((line = data.readLine()) != null) {
            hashs.put(line, "");
        }
        System.out.println(hashs.size() + " hashes carregadas,");
        data.close();
    }
}
