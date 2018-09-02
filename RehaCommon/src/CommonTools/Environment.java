package CommonTools;

import java.io.File;

public enum Environment {
	Instance;
	
	private static final String C_REHA_VERWALTUNG = "C:/RehaVerwaltung/";
	String proghome="";
	OS currentOS = OS.WIN;
	
	public  String getProghome() {
		return proghome;
	}
	 void setProghome(String proghome) {
		this.proghome = proghome;
	}
	public boolean isLinux() {
		return currentOS.is(OS.Linux);
	}
	public boolean isWindows() {
		return currentOS.is(OS.WIN);
	}
	
	Environment() {
		String osVersion;
		String prog = java.lang.System.getProperty("user.dir");
		String homedir = java.lang.System.getProperty("user.home");
		osVersion = System.getProperty("os.name");
		if(osVersion.contains("Linux")){
			setProghome("/opt/RehaVerwaltung/");
			currentOS=OS.Linux;
		}else if(osVersion.contains("Windows")){
			setProghome(prog.substring(0, 2)+"/RehaVerwaltung/");
			currentOS = OS.WIN;
		}else if(osVersion.contains("Mac OS X")){
			currentOS=OS.MAC;
			/**XXX: es wird zwar geprueft, ob man auf Mac ist, hat aber ausser dem pfad keine weitere Konsequenzen
			welcher Rueckgaberwert im Fall von OSX erfolgt muß durch einen Mac-Anhänger ermittelt werden **/
			System.out.println("Vermutlich MAC, Output = "+osVersion);
			setProghome(homedir+"/RehaVerwaltung/");
		}
		if (! (new File(getProghome() + "Rehaverwaltung").exists())) {
			System.out.println("setting Directory to default");
			setProghome(C_REHA_VERWALTUNG);
		}
		System.out.println("Programmverzeichnis = "+getProghome());
	}
	enum OS{
		WIN ,
		Linux ,
		MAC 
		;
		
		 boolean is(OS toCompare) {
			return this == toCompare;
		}
		
		
	}
}