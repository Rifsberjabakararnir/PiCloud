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
	public String[] getRSyncCommands(){
		//		return "rsync -Aaxz  " + localDir + " -e 'ssh -p" + port + "' " + userName + "@" + ip + ":" + remoteDir;


		//rsync -Aaxz owncloud/ owncloud-dirbkp_`date +"%Y%m%d"`/
		//sqlite3 data/owncloud.db .dump > owncloud-sqlbkp_`date +"%Y%m%d"`.bak
		//rsync 

		String[] commands = {
				"sqlite3 /var/www/owncloud/data/owncloud.db .dump > /var/www/owncloud/data/owncloud-sqlbkp.bak",
				"rsync -Aaxz  " + localDir + " -e 'ssh -p" + port + "' " + userName + "@" + ip + ":" + remoteDir,
				""
		};
		return commands;

	}
}
