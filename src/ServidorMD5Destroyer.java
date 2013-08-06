import java.rmi.Naming;  
  
public class ServidorMD5Destroyer {
    //private static  String hash;
  
    public ServidorMD5Destroyer() {  
        try {  
            MD5Destroyer m = new MD5DestroyerImpl();  
            Naming.rebind("rmi://localhost:1099/MensageiroService", m);
            //m.setHash(hash);
        }  
        catch( Exception e ) {  
            System.out.println( "Trouble: " + e );  
        }  
    }
    
  
    public static void main(String[] args) {  
        try {  
         java.rmi.registry.LocateRegistry.createRegistry(1099);  
         System.out.println("RMI registry ready.");  
      } catch (Exception e) {  
         System.out.println("Exception starting RMI registry:");  
         e.printStackTrace();  
      }  
        //hash=args[0];
        new ServidorMD5Destroyer();  
    }  
}