import java.io.FileWriter;
import java.util.concurrent.LinkedBlockingQueue;

public class LoggerThread implements Runnable {
	LinkedBlockingQueue<String> buffer;
	FileWriter fw;

	public LoggerThread(FileWriter fw, LinkedBlockingQueue<String> buffer) {
		this.buffer = buffer;
		this.fw = fw;
	}

	public void run() {
		while (true) {
			try {
				String msg = buffer.take();
				fw.write(msg);
				fw.flush();
			} catch (Exception e) {
				System.out.println("exc " + e.getMessage());
			}
		}
	}
}
