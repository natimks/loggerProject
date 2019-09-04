import java.net.*;
import java.util.concurrent.*;
/*
 * O que servidor irah fazer:
 * (1) deverah aceitar cada conexao (isto estah implementado)
 * (2) deverah possuir um pool estatico de threads para atendimento
 * (3) devarah delegar cada atendimento para uma thread
 */

public class Server {
	
	public static void main(String args[]) throws Exception {
		ServerSocket serverSocket = new ServerSocket(1236);
		boolean cont = true;
		ExecutorService application = Executors.newFixedThreadPool(1);
		while (cont) {
			Socket inSoc = serverSocket.accept();
			application.execute(new HttpWorker(inSoc));
		}
		serverSocket.close();
		application.shutdown();
	}
}