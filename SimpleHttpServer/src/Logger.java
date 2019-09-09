import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
/*
 * O que o logger deve fazer:
 * (1) deverah possuir um buffer compartilhado (sugestao: LinkedBlockingQueue)
 * (2) deverah possuir uma thread para gravacao em arquivo (consumidor)
 * (3) para inserir uma mensagem no log, deve-se utilizar o metodo putMessage (produtor)
 * (4) cada mensagem, ao ser gravada em arquivo, deverah conter o numero (contador) e a hora do evento
 * (5) inclua o que for necessario (metodos e atributos)
 */

public class Logger {

	private static Logger instance = null;
	private final static String logFileName = "serverlog.txt";
	private int contador;
	private Date hora;
	private LinkedBlockingQueue<String> buffer;
	private static File arquivoLog;
	private static FileWriter fw;
	// incluir campos necessarios

	// singleton
	public static synchronized Logger getInstance() {

		if (instance == null) {
			instance = new Logger();
			try {
				arquivoLog = new File("log.txt");
				if (!arquivoLog.exists()) {
					arquivoLog.createNewFile();
				}
					
				fw = new FileWriter(arquivoLog.getAbsoluteFile(), true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return instance;
	}

	private Logger() {
		buffer = new LinkedBlockingQueue<>();
	}

	public void putMessage(String message) {
		StringBuilder sb = new StringBuilder();
		contador++;
		sb.append("[ ").append(contador).append(" ] ");
		sb.append("[ ").append(new Date()).append(" ] ");
		sb.append("[ ").append(message).append(" ] \n");
		try {
			buffer.put(sb.toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public String getMessage() {
		try {
			String msg  = buffer.take();
			fw.write(msg);
			return msg ;
		} catch (Exception e) {

		}
		return "";
	}

	public void closeLog() {
		try {
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
