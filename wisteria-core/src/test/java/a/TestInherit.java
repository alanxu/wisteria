package a;

public class TestInherit {
	
	public static void main(String[] args) {
		B b = new B();
		b.runA();
		b.runB();
		
	}

}

class A {
	void runA() {
		System.out.println(getClass());
	}
}

class B extends A {
	void runB() {
		runA();
	}
}