
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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author pargles 
 * classe que faz a implemtacao de todos os metodos da interface
 * Mensageiro.java
 */
public final class MD5DestroyerImpl extends UnicastRemoteObject implements MD5Destroyer {

    private ArrayList<String[]> hashes;
    private ArrayList<String[]> resultados;
    private String nomeArquivo = "md5.txt";
    private String completeDigits = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%&*()_-+=[]{}?/\\|><^";//o ultimo caracter serve apenas como indicador do fim
    private ArrayList<char[]> escalonador;
    private ArrayList<String> emails;
    private String[] hashAtual;
    private long tempoInicio;
    private long inicioServidorRodando;
    private FileWriter fw;
    private File arquivo;
    private BufferedWriter bw;
    private ResultadoPorEmail email;
    private int maxLetras,minLetras;
    private int totalHashs;
    /*
     * metodo construtor da classe, apenas inicia o Array de hashes
     * e le as hashes do arquivo ou da propria memoria
     */
    public MD5DestroyerImpl() throws RemoteException, FileNotFoundException, IOException {
        super();
        escalonador = new ArrayList<char[]>();
        hashes = new ArrayList<String[]>();
        resultados = new ArrayList<String[]>();
        emails = new ArrayList<String>();
        maxLetras = 6;
        minLetras = 4;
        emails.add("pargles1@gmail.com");
        //emails.add("enicola90@gmail.com");
        //emails.add("adenauer@inf.ufpel.edu.br");
        inicioServidorRodando = System.currentTimeMillis();
        carregarListaDoArquivo(nomeArquivo);
    }

    /* metodo que retorna um hash quando o cliente chama esse metodo
     * @param void
     * @return String hash ou null caso nao tiver mais hashes para serem calculadas
     */
    @Override
    public String[] queroTrabalho(String computer) {
        if (escalonador.isEmpty()) {
            if (!hashes.isEmpty()) {
                escalonar();//se nao tem trabalho no escalonador e a lista de hash nao esta fazia
                hashAtual = hashes.remove(0);//entao remove uma hash e aplica o escalonamento a cima
                tempoInicio = System.currentTimeMillis();//comeceu o processamento de uma hash
                return darTrabalho(computer);
            } else {
                return null;//caso nao tenha mais hashs a serem descobertas
            }
        } else {
            return darTrabalho(computer);//se tem trabalho no escalonador, entao envia o trabalho direto
        }
    }

    /* metodo que retorna uma string de 5 posicoes contendo trabalho para o nodo
     * str[0]= hash
     * str[1]= quantidade de letras a serem calculadas (4,5 ou 6)
     * str[2]= letra inicial
     * str[3]= letra final
     * str[4]= alfabeto
     * @param String nome do computador
     * @return vetor de String com os dados a serem calculados
     */
    public String[] darTrabalho(String computer) {
        char tempC[];
        String[] tempS = new String[5];
        tempC = escalonador.remove(0);//sempre pega a primeira letra
        //tirou o ultimo, quer dizer que chegou no fim e nguem encontrou o resultado  
        if (escalonador.isEmpty()) {
            devolverTrabalho();
        }
        tempS[0] = hashAtual[0];//hash
        tempS[1] = hashAtual[1];//quantidade de letras a ser avaliadas
        tempS[2] = Character.toString(tempC[0]);//letra inicial
        tempS[3] = Character.toString(tempC[1]);//letra final
        tempS[4] = completeDigits;//este e o alfabeto
        String message = computer + " esta trabalhando na hash " + tempS[0] + " na letra " + tempC[0];
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
    /* metodo que carrega todas as hash de um arquiivo chamado "md5.txt"
     * @param void
     * @return String nome do arquivo
     */
    public void carregarListaDoArquivo(String nomeArquivo) throws FileNotFoundException, IOException {
        System.out.println("carregando hashs");
        String line;
        BufferedReader data = new BufferedReader(new InputStreamReader(new FileInputStream(nomeArquivo)));
        while ((line = data.readLine()) != null) {
            String[] temp = new String[2];
            temp[0] = line;// a hash que leu
            temp[1] = "4";//comeca tentando com 4 letras
            System.out.println(line);
            hashes.add(temp);
        }
        totalHashs = hashes.size();
        System.out.println(hashes.size() + " hashes carregadas, servidor pronto!");
        data.close();
    }

    /* primeiro sao calculadas as hashs com 4 letras, uma hash com 4 letras nao foi
     * encontrada ela volta para o final da lista e passara a ser de 5 letras e assim sucessivamente
     * para 6 letras
     * @param void
     * @return void
     */
    public void devolverTrabalho() {
        String[] temp = new String[2];
        temp[0] = hashAtual[0];
        int palavras = Integer.parseInt(hashAtual[1]) + 1;
        palavras = palavras>maxLetras?minLetras:palavras;//se tiver mais letras que o permitido, volta para o inicio da computacao
        temp[1] = String.valueOf(palavras);//se tinha testado com 4 letras, o proximo PC que pegar a hash vai testa com 5 letras
        System.out.println("hash: " + temp[0] + " devolvida, com " + temp[1] + " letras");
        hashes.add(temp);//coloca o novo trabalho no final da lista
    }

    /* metodo que divide a tarefa entre os computadores
     * ele bota num vetor de char qual a letra inicial que 
     * um computador tera de calcular e seus calculos vao ate
     * uma letra final que tambem e inserida no vetor
     * @param void
     * @return void
     */
    public void escalonar() {
        for (int i = 0; i < completeDigits.length() - 1; i++) {
            char[] temp = new char[2];
            temp[0] = completeDigits.charAt(i);//letra inicial
            temp[1] = completeDigits.charAt(i + 1);//letra final
            escalonador.add(temp);
        }
    }

    /* se a lista de hashs nao estiver vazia e o escalonador tambem 
     * nao estiver vazio, entao, ainda tem trabalho
     * @param void
     * @return boolena temTrabalho
     */
    @Override
    public boolean temTrabalho() throws RemoteException {
        boolean vazio = hashes.isEmpty() && escalonador.isEmpty();
        return !vazio;
    }

    /* metodo que recebe uma mensagem do nodo indicando que encontrou o resultado
     * metodo responsavel por salvar o resultado em arquivo e tambem por enviar por email
     * @param String hash atual e String resultado
     * @return void
     */
    @Override
    public void encontreiResultado(String hash, String resultado) throws RemoteException {
        long tempo = (System.currentTimeMillis() - tempoInicio);
        String t = getStringTempo(tempo);
        for (int i = hashes.size() - 1; i >= 0; i--) {
                if (hash.equals(hashes.get(i)[0])) {
                    hashes.remove(i);
                }
        }
        if (hash.equals(hashAtual[0])) {
            escalonador = new ArrayList<char[]>();//o equivalente a dar um removeAll no escalonador
        }
        String result[] = new String[2];
        result[0]=hash;
        result[1]=resultado;
        resultados.add(result);
        try {
            salvarEmArquivo(resultado, hash, t,"resultado.txt");
        } catch (IOException ex) {
            Logger.getLogger(MD5DestroyerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        enviarEmail(resultado, hash, t);
        System.out.println("----------------------------------------");
        System.out.println("RESULTADO: " + hash + " = " + resultado);
        System.out.println(hashes.size() + " hashs restantes");
        System.out.println("----------------------------------------");
        try {
            salvarEmArquivo(  hash+" = "+resultado , "---------------------------------------\nRESULTADO: ",hashes.size() + " hashs restantes\n---------------------------------------","log.txt");
        } catch (IOException ex) {
            Logger.getLogger(MD5DestroyerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(totalHashs==resultados.size())//ja encontrou todos os resultados
        {
            String listaResultado = resultados.size()+" hashs quebradas\n\n";
            for(int i=0;i<resultados.size();i++)
            {
                listaResultado= listaResultado+resultados.get(i)[0]+" = "+resultados.get(i)[1]+"\n";
            }
            listaResultado = listaResultado+"\n\n"+getStringTempo(System.currentTimeMillis()-inicioServidorRodando);
            enviarEmail(listaResultado);
        }
    }

    /* metodo que recebe o tempo que foi necessario para calcular a hash
     * e devolve o tempo em formato de string contendo horas, minutos e segundos necessarios
     * @param long tempo necesario
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

    /* metodo que salva o resultado no arquivo "resultados.txt"
     * @param String resultado, String hash, String tempo
     * @return void
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

    /* metodo que envia um email contendo o resultado encontrado
     * como se trata de um processamento distruido em diferentes computadores
     * torna-se necessaria o envio de informacao utilizando este meio
     * @param String resultado, String hash, String tempo
     * @return void
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
    
    /*
     * @param String mensagem
     * @return void
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
}
