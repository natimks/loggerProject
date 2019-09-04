import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;
import java.util.StringTokenizer;

/*
 * Criar mensagem de log contendo:
 * (1) IP requisitante
 * (2) Arquivo solicitado
 * (3) Cod/status
 * 
 * Ex: 127.0.0.1 - GET / HTTP/1.1 - 200 OK - (Nome da thread que atendeu)
 */

public class HttpRequest {
	private Socket clientConn;

	public HttpRequest(Socket clientConn) throws Exception {
		this.clientConn = clientConn;

	}

	public void process() throws Exception {
		
		Logger logger = Logger.getInstance();
		
		Reader reader = new InputStreamReader(clientConn.getInputStream());
		BufferedReader din = new BufferedReader(reader);
		
		BufferedOutputStream out = new BufferedOutputStream(clientConn.getOutputStream());

		String request = din.readLine();
		if(request == null){
			din.close();
			out.close();
			clientConn.close();
			return;
		}
		
		request = request.trim();
		
		System.out.println(request+clientConn.getInetAddress().getLocalHost());
		StringTokenizer st = new StringTokenizer(request);

		String header = st.nextToken();

		if (header.equals(HTTP.GET.toString())) {
			String fileName = st.nextToken();
			FileInputStream fin = null;
			boolean fileExist = true;
			
			try {
				if(fileName.equals("/")){
					fileName = HTTP.INDEX_PAGE_NAME.toString();
				}
				fin = new FileInputStream(fileName.substring(1));
			} catch (Exception ex) {
				fileExist = false;
			}

			String serverLine = "Simple HTTP Server";
			String statusLine = null;
			String contentTypeLine = null;
			String contentLengthLine = null;
			String contentBody = null;

			if (fileExist) {
				statusLine = HTTP.OK.toString();
				contentTypeLine = HTTP.TEXT_CONTENT.toString();
				contentLengthLine = HTTP.CONTENT_LENGTH.toString() + getLengthLine(fin);
			} else {
				statusLine = HTTP.NOT_FOUND.toString();
				contentTypeLine = HTTP.TEXT_CONTENT.toString();
				contentBody = HTTP.NOT_FOUND_PAGE.toString();
				contentLengthLine = HTTP.CONTENT_LENGTH.toString()+ getLengthLine(contentBody);
			}

			out.write(statusLine.getBytes());
			out.write(serverLine.getBytes());
			out.write(contentTypeLine.getBytes());
			out.write(contentLengthLine.getBytes());
			out.write(HTTP.CLOSE_CONNECTION.toString().getBytes());
			
			if (fileExist) {

				byte[] buffer = new byte[1024];
				int bytes = 0;
				while ((bytes = fin.read(buffer)) != -1) {
					out.write(buffer, 0, bytes);
				}

				fin.close();
			} else {
				out.write(contentBody.getBytes());
			}
			
			logger.putMessage("nada ainda");
			
			out.close();
			clientConn.close();
		}
	}

	private String getLengthLine(FileInputStream fin) throws IOException {
		return new Integer(fin.available()).toString()+"\n";
	}
	
	private String getLengthLine(String contentBody) {
		return new Integer(contentBody.length()).toString()+"\n";
	}
}
