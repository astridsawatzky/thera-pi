package environment;

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
