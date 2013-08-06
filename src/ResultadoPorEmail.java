import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;

public class ResultadoPorEmail
{
    private Properties props;
    private String  email = "md5destroyer@gmail.com",
            senha = "achoqueabarradeespacoquebrou",
            host = "smtp.gmail.com",
            porta  = "465",
            assunto = "Mais uma hash descoberta",
            texto = "Eba! descobrimos";
            ArrayList<String> destinatario;

    public  ResultadoPorEmail()
    {
        destinatario = new ArrayList<String>();
        props = new Properties();
        props.put("mail.smtp.user", email);
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", porta);
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.debug", "true");
        props.put("mail.smtp.socketFactory.port", porta);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
    }
    
    public void setTexto(String textoEmail)
    {
        texto = textoEmail;
    }
    
    public void setAssunto(String subject)
    {
        assunto = subject;
    }
    
    public void adicionarDestinatario(String email)
    {
        destinatario.add(email);
    }
    
    public void mandarEmail() {
        try {
            Authenticator auth = new SMTPAuthenticator();
            Session session = Session.getInstance(props, auth);
            session.setDebug(true);
            MimeMessage msg = new MimeMessage(session);
            msg.setText(texto);
            msg.setSubject(assunto);
            msg.setFrom(new InternetAddress(email));
            InternetAddress[] addressTo = new InternetAddress[destinatario.size()];
            for (int i = 0; i < destinatario.size(); i++)
            {
                addressTo[i] = new InternetAddress(destinatario.get(i));
            }
            msg.setRecipients(Message.RecipientType.TO, addressTo); 
            Transport.send(msg);
        } catch (Exception mex) {
            mex.printStackTrace();
        }
    }

    private class SMTPAuthenticator extends javax.mail.Authenticator
    {
        public PasswordAuthentication getPasswordAuthentication()
        {
            return new PasswordAuthentication(email, senha);
        }
    }
}