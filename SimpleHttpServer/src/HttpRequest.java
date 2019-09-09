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
	String mensagem;

	public HttpRequest(Socket clientConn) throws Exception {
		this.clientConn = clientConn;

	}

	public void process() throws Exception {
		
		Logger logger = Logger.getInstance();
		mensagem="";
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
		mensagem +=clientConn.getInetAddress().getLocalHost()+ " ";
		mensagem += request;
		System.out.println(request);
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
				mensagem+=" 200 OK ";
				statusLine = HTTP.OK.toString();
				contentTypeLine = HTTP.TEXT_CONTENT.toString();
				contentLengthLine = HTTP.CONTENT_LENGTH.toString() + getLengthLine(fin);
			} else {
				mensagem+=" 404 NOT FOUND ";
				statusLine = HTTP.NOT_FOUND.toString();
				contentTypeLine = HTTP.TEXT_CONTENT.toString();
				contentBody = HTTP.NOT_FOUND_PAGE.toString();
				contentLengthLine = HTTP.CONTENT_LENGTH.toString()+ getLengthLine(contentBody);
			}
		
			mensagem += fileName;
			out.write(statusLine.getBytes());
			out.write(serverLine.getBytes());
			out.write(contentTypeLine.getBytes());
			out.write(contentLengthLine.getBytes());
			out.write(HTTP.CLOSE_CONNECTION.toString().getBytes());
			logger.putMessage(mensagem);
			logger.getMessage();
			if (fileExist) {

				byte[] buffer = new byte[1024];
				int bytes = 0;
				while ((bytes = fin.read(buffer)) != -1) {
					try {
						out.write(buffer, 0, bytes);
					} catch (Exception e) {
						System.out.println("Connection aborted");
						fin.close();
						clientConn.close();
						return;
					}
				}
				fin.close();
			} else {
				out.write(contentBody.getBytes());
			}
			
			logger.closeLog();
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
