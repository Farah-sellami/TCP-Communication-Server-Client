package CommunicationTCP;

import java.io.*;  
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList; 

class Server { 
	// Liste pour stocker les noms des clients connect�s
    private static CopyOnWriteArrayList<String> clientsConnecte = new CopyOnWriteArrayList<>();
    
    //Liste clientsMsg stocke les objets PrintWriter de chaque client
    private static CopyOnWriteArrayList<PrintWriter> clientsMsg = new CopyOnWriteArrayList<>();
    
    public static void main(String[] args) 
	{ 
		ServerSocket server = null; 
		try {   
			//instantiation d'un serveur sur le port port 1233 
			 server = new ServerSocket(1230); 
			 System.out.println("Le serveur est en �coute sur le port : "+ server.getLocalPort());
			 server.setReuseAddress(true);
			 
			while (true) { 
 
				Socket client = server.accept(); 
				
			//cr�ation d'une instance de ClientHandler
				ClientHandler clientSock 
					= new ClientHandler(client); 

			// d�marrage d'un nouveau thread 
				new Thread(clientSock).start(); 
			} 
		} 
		catch (IOException e) { 
			e.printStackTrace(); 
		} 
		finally { 
			if (server != null) { 
				try { 
					server.close(); 
				} 
				catch (IOException e) { 
					e.printStackTrace(); 
				} 
			} 
		} 
	} 
	// ClientHandler class 
	private static class ClientHandler implements Runnable { 
		private final Socket clientSocket; 
		private String clientName;
		private PrintWriter out; 
		
		public ClientHandler(Socket socket) 
		{ 
			this.clientSocket = socket; 
		} 

		public void run() 
		{ 
			BufferedReader in = null; 
			try { 
					
				//generation de l'objet PrintWriter
				out = new PrintWriter( 
					clientSocket.getOutputStream(), true); 

				//generation de objet BufferedReader
				in = new BufferedReader( 
					new InputStreamReader( 
						clientSocket.getInputStream())); 
				
				clientName = in.readLine();
                System.out.println("Le nom du client connect� est : " + clientName 
                		+ "\nSon adresse "+ clientSocket.getInetAddress().getHostAddress()
                		+"\nSur le port "+clientSocket.getPort());
                
                /* la liste des clients connect�s */
                clientsConnecte.add(clientName);
                System.out.println("\n*** Les clients connect�s *** \n" + clientsConnecte+"\n");
                
                //la liste contenant les messages
                clientsMsg.add(out);
                
				String line; 
				while ((line = in.readLine()) != null) 
				{ 
					
					System.out.println("Le client " + clientName + " a envoy� : " + line);
					if ("exit".equalsIgnoreCase(line)) 
					{
	                    System.out.println("Le client " + clientName + " se d�connecte...");
	                    break;
	                }
					
					 //Diffusion du message � tous les clients connect�s
                    for (PrintWriter writer : clientsMsg) {
                    	if (writer != out) {  
                            writer.println(clientName + " a envoy� : " + line);
                            writer.flush();
                        }
                    }
                }
			} 
			catch (IOException e) { 
				e.printStackTrace(); 
			} 
			finally { 
				 if (clientName != null) {
					 	//retire le nom du client d�connect� de cette liste
	                    clientsConnecte.remove(clientName);
	                    //retire le flux de sortie du client qui se d�connecte
	                    clientsMsg.remove(out);
	                    System.out.println("Le client "+ clientName +" est d�connect�" );
	                    System.out.println("\n*** Les clients connect�s apr�s d�connexion ***\n " + clientsConnecte);
	                }
				
				try { 
					if (out != null) { 
						out.close(); 
					} 
					if (in != null) { 
						in.close(); 
						clientSocket.close(); 
					} 
				} 
				catch (IOException e) { 
					e.printStackTrace(); 
				} 
			} 
		} 
	} 
}

