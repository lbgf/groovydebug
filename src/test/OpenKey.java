package test;

public class OpenKey {
	public static boolean debugLock = true;
	
	public void test() throws Exception {
		Runnable m = new Runnable() {

			public void run() {
				try {
					while (OpenKey.debugLock) {
						Thread.sleep(500);
						synchronized(DebugPoint.o) {
							DebugPoint.debugLock = false;
						}
						//System.out.println(DebugPoint.debugLock);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
  	
  	Thread thread1 = new Thread(m);
  	thread1.start();
  	
	}

	// test
	public static void main(String[] args) throws Exception {
		new OpenKey().test();
		
	}




}
