package org.gradle;

public class PiConfig {

	public final String serverDir;
	public final String localDir;
	public final String remoteDir;
	public final String userName;
	public final String ip;
	public final String port;

	public PiConfig( String serverDir, String localDir, String remoteDir, String userName, String ip, String port ){

		this.serverDir = serverDir;
		this.localDir = localDir;
		this.remoteDir = remoteDir;
		this.userName = userName;
		this.ip = ip;
		this.port = port;
	}

	/**
	 * 
	 * @return the rSync command with information from config file
	 */
	public String[] getRSyncCommands(){
		String[] commands = {
				"pwd",
				"sqlite3 /var/www/owncloud/data/owncloud.db .dump > /var/www/owncloud/data/owncloud-sqlbkp.bak",
				"rsync -Aaxz  " + localDir + "/data/" + " -e 'ssh -p" + port + "' " + userName + "@" + ip + ":" + remoteDir,
				"rsync -Aaxz  " + serverDir + "/owncloud/config" + " -e 'ssh -p" + port + "' " + userName + "@" + ip + ":" + remoteDir
		};
		return commands;

	}
}
