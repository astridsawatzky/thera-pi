package Environment;

import java.io.File;

public enum Path {
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
	
	Path() {
		currentOS = determineOS();
			
		switch (currentOS) {
		case WIN:
			String prog = java.lang.System.getProperty("user.dir");
			setProghome(prog.substring(0, 2)+"/RehaVerwaltung/");
			break;
		case Linux:
			setProghome("/opt/RehaVerwaltung/");
			break;
		case MAC:
			String homedir = java.lang.System.getProperty("user.home");
			setProghome(homedir+"/RehaVerwaltung/");
			/**welcher Rueckgaberwert im Fall von OSX erfolgt muß durch einen Mac-Anhänger ermittelt werden **/
			break;
		case UNKNOWN:
			setProghome(C_REHA_VERWALTUNG);
			break;
		default:
			System.out.println("setting Directory to default");
			setProghome(C_REHA_VERWALTUNG);
			break;
		}
		if (!new File(getProghome()).exists()) {
			//program wasn't started from within its installation directory, probably developer.
			setProghome(C_REHA_VERWALTUNG);
			
		}
		System.out.println("Programmverzeichnis = "+getProghome());
	}
	private OS determineOS() {
		String osVersion = System.getProperty("os.name");
		
		if(osVersion.contains("Linux")){
			return OS.Linux;
		}else if(osVersion.contains("Windows")){
			return OS.WIN;
		}else if(osVersion.contains("Mac OS X")){
			return OS.MAC;
		}
		//this should not happen
		return OS.UNKNOWN;
		
	}
	enum OS{
		WIN ,
		Linux ,
		MAC,
		UNKNOWN
		;
		
		 boolean is(OS toCompare) {
			return this == toCompare;
		}
		
		
	}
}