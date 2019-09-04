import java.net.Socket;

public class HttpWorker implements Runnable {

	Socket socket;

	public HttpWorker(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try {
			HttpRequest request = new HttpRequest(socket);
			request.process();
		} catch (Exception e) {
		}
	}
}
