package org.gradle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
		s.schedule("30 * * * *", new Runnable() {
			public void run() {
				try {
					executeCommand(config.getRSyncCommands());
				} catch (Exception e) {
					new MailLog(e.toString());
				}
			}
		});
		// Starts the scheduler.
		s.start();
	}

	/**
	 * 
	 * @return the config for this system in PiConfig Object
	 */
	private PiConfig getConfig() throws FileNotFoundException,IOException{
		String home = System.getProperty("user.home");
		File file = new File(home + "/PiCloud/config.json");

		JSONParser parser = new JSONParser();
		try {

			Object obj = parser.parse(new FileReader(file));
			JSONObject jsonObject = (JSONObject) obj;

			config = new PiConfig( (String) jsonObject.get("serverDir"), (String) jsonObject.get("localDir"), 
					(String) jsonObject.get("remoteDir"), (String) jsonObject.get("userName"), 
					(String) jsonObject.get("ip"), (String) jsonObject.get("port") );

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return config;
	}

	/**
	 * 
	 * @param command - the command to execute
	 * @param waitForResponse
	 * @return the response of the command
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	private void executeCommand(String... command) throws IOException, InterruptedException {
		for(int i = 0; i < command.length; i++) {
			System.out.println(command[i]);
			String response = "";
			ProcessBuilder pb = new ProcessBuilder("bash", "-c", command[i]);

			pb.redirectErrorStream(true);
			Process shell;
			shell = pb.start();
			// To capture output from the shell
			InputStream shellIn = shell.getInputStream();

			// Wait for the shell to finish and get the return code
			shell.waitFor();
			response = convertStreamToStr(shellIn);
			System.out.println(response);
			shellIn.close();
		}
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
