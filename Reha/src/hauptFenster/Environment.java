package hauptFenster;

import java.io.File;

public enum Environment {
	Instance;
	
	String proghome="";
	
	private boolean isLinux;
	private boolean isWindows;
	
	public  String getProghome() {
		return proghome;
	}
	 void setProghome(String proghome) {
		this.proghome = proghome;
	}
	public boolean isLinux() {
		return this.isLinux;
	}
	public boolean isWindows() {
		return this.isWindows;
	}
	
	Environment() {
		String osVersion;
		String prog = java.lang.System.getProperty("user.dir");
		String homedir = java.lang.System.getProperty("user.home");
		osVersion = System.getProperty("os.name");
		if(osVersion.contains("Linux")){
			setProghome("/opt/RehaVerwaltung/");
			isLinux = true;
		}else if(osVersion.contains("Windows")){
			setProghome(prog.substring(0, 2)+"/RehaVerwaltung/");
			isWindows = true;
		}else if(osVersion.contains("Mac OS X")){
			//welcher Rückgaberwert im Fall von OSX erfolgt muß durch einen Mac-Anhänger ermittelt werden
			System.out.println("Vermutlich MAC, Output = "+osVersion);
			setProghome(homedir+"/RehaVerwaltung/");
		}
		if (! (new File(getProghome() + "Rehaverwaltung").exists())) {
			System.out.println("setting Directory to default");
			setProghome("C:/RehaVerwaltung/");
		}
		System.out.println("Programmverzeichnis = "+getProghome());
	}
}