package org.gradle;

public class PiConfig {

	public final String localDir;
	public final String remoteDir;
	public final String userName;
	public final String ip;
	public final String port;
	
	public PiConfig( String localDir, String remoteDir, String userName, String ip, String port ){
		
		this.localDir = System.getProperty("user.home") + "/" + localDir;
		this.remoteDir = remoteDir;
		this.userName = userName;
		this.ip = ip;
		this.port = port;
	}
	
	/**
	 * 
	 * @return the rSync command with information from config file
	 */
	public String getRSyncCommand(){
		return "rsync -avz  " + localDir + " -e 'ssh -p" + port + "' " + userName + "@" + ip + ":" + remoteDir;
	}
}
