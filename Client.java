package CommunicationTCP;

import java.io.*;  
import java.net.*; 
import java.util.*;

// Client class 
class Client { 
	 
	public static void main(String[] args) 
	{ 
		try 
		(
			//creation d'un socket de connexion au serveur sur localhost et le port 1230
			Socket socket = new Socket("localhost", 1230)) { 
			
			//generation d'objet PrintWriter pour envoyer des messages au serveur
			PrintWriter out = new PrintWriter( 
				socket.getOutputStream(), true); 

			//generation d'objet BufferedReader pour lire les messages reçus du serveur
			BufferedReader in 
				= new BufferedReader(new InputStreamReader( 
					socket.getInputStream())); 

			Scanner sc = new Scanner(System.in); 
			
            System.out.print("écrire votre nom : ");
            
         // Envoie le nom du client au serveur
            String clientName = sc.nextLine();
            out.println(clientName);
              
         // Creation du thread pour lire les messages du serveur
            MessageReader messageReader = new MessageReader(in, clientName);
         //demarrage du thread
            new Thread(messageReader).start();
            
            
            String line= null;
            while (true) {
            	System.out.print("écrire votre message : ");
                line = sc.nextLine();
                
                if ("exit".equalsIgnoreCase(line)) {
                    System.out.println("Déconnexion...");
                    break;  // Sortir de la boucle
                }
                
                // Envoie du message au serveur
                out.println(line);
                //le message est envoye immediatement.
                out.flush(); 
               
            } 
            sc.close();
            socket.close();
            System.out.println("Client déconnecte...");
            } 
		
         catch (IOException e) { 
            e.printStackTrace(); 
        } 
	}

    private static class MessageReader implements Runnable {
    	private final BufferedReader in;
        private final String clientName;
        
        public MessageReader(BufferedReader in, String clientName) {
            this.in = in;
            this.clientName = clientName;
        }
        
        @Override
        public void run() {
            try {
                String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                	System.out.print("\r" + " ".repeat(50));
                	System.out.println("\rL'autre client "+serverMessage+"\n");
                    System.out.print("ecrire votre message : "); 
                }
            } catch (IOException e) {
                System.err.println("Erreur lors de la lecture du message du serveur.");
                e.printStackTrace();
            }
        }
    }
}