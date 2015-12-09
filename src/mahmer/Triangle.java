package mahmer;

public class Triangle {
	double a;
	double b;
	double c;

	double ab;
	double bc;
	double ca;

	public Triangle(double a, double b, double ab) {
		this.a = a;
		this.b = b;
		this.ab = ab;
		init_c();
		init_bc();
		init_ca();
	}

	private void init_ca() {
		ca = 180 - ab - bc;
	}

	private void init_c() {
		c = Math.sqrt(a * a + b * b - 2 * a * b * Math.cos(ab));
	}

	private void init_bc() {
		bc = Math.acos((b * b + c * c - a * a) / 2 * b * c);
	}

	public double getC() {
		return c;
	}

	public double getBc() {
		return bc;
	}

	public double getCa() {
		return ca;
	}

}
