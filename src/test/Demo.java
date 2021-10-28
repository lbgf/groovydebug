package test;

import java.lang.reflect.Method;

public class Demo {
	public static void main(String[] args) {
		Class c = String.class;
		Method[] m =c.getMethods();
		for (int i = 0; i < m.length; i++) {
			System.out.print(m[i].getName() + " ");
			Class params[] = m[i].getParameterTypes();
			for (int j = 0; j < params.length; j++) {
				System.out.print(params[j] + ",");
			}
			System.out.println("");
		}
	}

}
