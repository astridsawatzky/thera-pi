package mandant;

public class Mandant {
	private IK ik;
	public Mandant(String ik, String name) {
		this.ik = new IK(ik);
		this.name = name;
	}
	private String name;
	public String name() {
		return name;
	}
	public String ik() {
		return ik.asString();
	}

	@Override
	public String toString() {

		return name + " - IK"+ ik.asString();
	}

}
