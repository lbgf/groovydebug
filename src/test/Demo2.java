package test;

public class Demo2 {
	interface GreetingService {
		void sayMessage(String message);
	}

	public static void main(String[] args) {
		GreetingService greetService1 = message -> System.out.println("Hello " + message);

		greetService1.sayMessage("Runoob");
	}

}
