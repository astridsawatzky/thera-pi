package mandant;

public class Mandant {
	private String ik;
	public Mandant(String ik, String name) {
		super();
		this.ik = ik;
		this.name = name;
	}
	private String name;
	public String name() {
		return name;
	}
	public String ik() {
		return ik;
	}

	@Override
	public String toString() {

		return name + " - IK"+ ik;
	}

}
