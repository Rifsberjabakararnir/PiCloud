package org.gradle;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import it.sauronsoftware.cron4j.Scheduler;

/**
 * @author jfjclarke
 * @date 25.07.14
 */
public class PiCloud {

	private PiConfig config;

	public PiCloud() {
		try {
			config = getConfig();
			runProgram();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void runProgram() {
		Scheduler s = new Scheduler();
		s.schedule("* * * * *", new Runnable() {
			public void run() {
				executeCommand(config.getRSyncCommand());
			}
		});
		// Starts the scheduler.
		s.start();
	}

	/**
	 * 
	 * @return the config for this system in PiConfig Object
	 */
	@SuppressWarnings("deprecation")
	private PiConfig getConfig() throws FileNotFoundException,IOException{
		String home = System.getProperty("user.home");
		File file = new File(home + "/PiCloud/.config");

		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;

		fis = new FileInputStream(file);
		bis = new BufferedInputStream(fis);
		dis = new DataInputStream(bis);

		PiConfig config = null; 
		while (dis.available() != 0) {
			config = new PiConfig(dis.readLine(), dis.readLine(), dis.readLine(), dis.readLine(), dis.readLine());
		}

		fis.close();
		bis.close();
		dis.close();

		return config;
	}

	/**
	 * 
	 * @param command - the command to execute
	 * @param waitForResponse
	 * @return the response of the command
	 */
	private String executeCommand(String command) {
		String response = "";
		ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
		
		pb.redirectErrorStream(true);
		Process shell;
		try {
			shell = pb.start();
			// To capture output from the shell
			InputStream shellIn = shell.getInputStream();
			
			// Wait for the shell to finish and get the return code
			shell.waitFor();
			response = convertStreamToStr(shellIn);
			shellIn.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;
	}

	/**
	 * To convert the InputStream to String we use the Reader.read(char[]
	 * buffer) method. We iterate until the Reader return -1 which means
	 * there's no more data to read. We use the StringWriter class to
	 * produce the string.
	 */
	private String convertStreamToStr(InputStream is) throws IOException {

		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is,
						"UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		}
		else {
			return "";
		}
	}

	public static void main(String[] args) {
		new PiCloud();
	}
}
