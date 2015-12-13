package org.thera_pi.updates;

import java.io.File;
import java.net.UnknownHostException;

public class UpdateConfig {
	private String updateDir = "";
	private String updateHost = "";
	private String updateUser = "";
	private String updatePasswd = "";
	private boolean useActiveMode = false;
	private boolean developerMode = false;
	private boolean checkUpdates = false;
	
	private static UpdateConfig instance = null;

	private static String proghome = null;
	private static boolean testphase = false;
	
	
	static {
		if(testphase || TheraPiUpdates.devcomputer.contains("rtaadmin")){
			proghome = "C:/RehaVerwaltung/";
		}else{
			proghome = java.lang.System.getProperty("user.dir").replace("\\","/")+"/";	
		}

	}

	
	private UpdateConfig()
	{
		readIniFile();
	}
	
	public static UpdateConfig getInstance()
	{
		if(instance == null)
		{
			instance = new UpdateConfig();
		}
		return instance;
	}
	
	private void readIniFile(){

		File f = new File(proghome+"ini/tpupdateneu.ini");
		if(!f.exists()){
			TheraPiUpdates.isrta = true;
			INIFile ini = new INIFile(proghome + "/ini/tpupdate.ini");
			//if()
			System.out.println(ini.getFileName());

			updateHost = ini.getStringProperty("TheraPiUpdates", "UpdateFTP");
			updateDir = ini.getStringProperty("TheraPiUpdates", "UpdateVerzeichnis");
			updateUser = ini.getStringProperty("TheraPiUpdates", "UpdateUser");

			String pw = ""; 
			pw = ini.getStringProperty("TheraPiUpdates", "UpdatePasswd");
			Verschluesseln man = Verschluesseln.getInstance();
			man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
			if(pw.length() <= 20){
				ini.setStringProperty("TheraPiUpdates", "UpdatePasswd", man.encrypt(String.valueOf(pw)),null);
				ini.save();
				updatePasswd = String.valueOf(pw);
			}else{
				updatePasswd = man.decrypt(ini.getStringProperty("TheraPiUpdates", "UpdatePasswd"));
			}
			developerMode = ("1".equals(ini.getStringProperty("TheraPiUpdates", "UpdateEntwickler")) ? true : false);
			useActiveMode = ("1".equals(ini.getStringProperty("TheraPiUpdates", "UseFtpActiveMode")) ? true : false);
			checkUpdates = ("0".equals(ini.getStringProperty("TheraPiUpdates", "UpdateChecken")) ? false : true);
			//checkUpdates = ("1".equals(ini.getStringProperty("TheraPiUpdates", "IsRta")) ? true : false);
		}else{
			TheraPiUpdates.isrta = false;
			INIFile ini = new INIFile(proghome + "/ini/tpupdateneu.ini");


			Verschluesseln man = Verschluesseln.getInstance();
			man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
			/*
			updateHost = "www.thera-pi.org";
			ini.setStringProperty("TheraPiUpdates", "UpdateFTP", man.encrypt(String.valueOf(updateHost)),null);
			updateDir = "/";
			ini.setStringProperty("TheraPiUpdates", "UpdateVerzeichnis", man.encrypt(String.valueOf(updateDir)),null);
			updateUser = "u37262724-nouveaux";
			ini.setStringProperty("TheraPiUpdates", "UpdateUser", man.encrypt(String.valueOf(updateUser)),null);
			updatePasswd = "neuertpexplorer";
			ini.setStringProperty("TheraPiUpdates", "UpdatePasswd", man.encrypt(String.valueOf(updatePasswd)),null);
			System.out.println(ini.getFileName());
			ini.save();
			*/
			updateHost = man.decrypt(ini.getStringProperty("TheraPiUpdates", "UpdateFTP"));
			updateDir = man.decrypt(ini.getStringProperty("TheraPiUpdates", "UpdateVerzeichnis"));
			updateUser = man.decrypt(ini.getStringProperty("TheraPiUpdates", "UpdateUser"));
			updatePasswd = man.decrypt(ini.getStringProperty("TheraPiUpdates", "UpdatePasswd"));
			developerMode = ("1".equals(ini.getStringProperty("TheraPiUpdates", "UpdateEntwickler")) ? true : false);
			useActiveMode = ("1".equals(ini.getStringProperty("TheraPiUpdates", "UseFtpActiveMode")) ? true : false);
			checkUpdates = ("0".equals(ini.getStringProperty("TheraPiUpdates", "UpdateChecken")) ? false : true);
			//checkUpdates = ("1".equals(ini.getStringProperty("TheraPiUpdates", "IsRta")) ? true : false);
			//ini.setStringProperty("TheraPiUpdates", "DummyPw", man.encrypt(String.valueOf("lummerlandistabgebrand")),null);
			//System.out.println(man.encrypt("lummerlandistabgebrand"));
			//System.out.println("fertig mit einlesen");
			//ini.save();
		}
/*
		try{
			
			INIFile ini = new INIFile(proghome + "/ini/tpupdate.ini");
			
			//System.out.println(proghome+"update.ini");

			updateHost = ini.getStringProperty("TheraPiUpdates", "UpdateFTP");
			updateDir = ini.getStringProperty("TheraPiUpdates", "UpdateVerzeichnis");
			updateUser = ini.getStringProperty("TheraPiUpdates", "UpdateUser");

			String pw = ""; 
			pw = ini.getStringProperty("TheraPiUpdates", "UpdatePasswd");
			Verschluesseln man = Verschluesseln.getInstance();
			man.init(Verschluesseln.getPassword().toCharArray(), man.getSalt(), man.getIterations());
			if(pw.length() <= 20){
				ini.setStringProperty("TheraPiUpdates", "UpdatePasswd", man.encrypt(String.valueOf(pw)),null);
				ini.save();
				updatePasswd = String.valueOf(pw);
			}else{
				updatePasswd = man.decrypt(ini.getStringProperty("TheraPiUpdates", "UpdatePasswd"));
			}
			developerMode = ("1".equals(ini.getStringProperty("TheraPiUpdates", "UpdateEntwickler")) ? true : false);
			useActiveMode = ("1".equals(ini.getStringProperty("TheraPiUpdates", "UseFtpActiveMode")) ? true : false);
			checkUpdates = ("0".equals(ini.getStringProperty("TheraPiUpdates", "UpdateChecken")) ? false : true);
		}catch(Exception ex){
			ex.printStackTrace();

		}
		*/
	}


	public String getUpdateDir() {
		return updateDir;
	}

	public String getUpdateHost() {
		return updateHost;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public String getUpdatePasswd() {
		return updatePasswd;
	}

	public boolean isUseActiveMode() {
		return useActiveMode;
	}

	public static String getProghome() {
		return proghome;
	}

	public boolean isDeveloperMode() {
		return developerMode;
	}

	public boolean isCheckUpdates() {
		return checkUpdates;
	}

}
