/*
 * ==============================================
 * debug groovy
 * ==============================================
 * 
 * Project Info: ����groovy����;
 *
 */

package gd;

/**
 * ���ڲ��ԵĿ����߳�
 *
 */
public class OpenKey {
	public static boolean debugLock = true;
	
	public void test() throws Exception {
		Runnable m = new Runnable() {

			public void run() {
				try {
					while (OpenKey.debugLock) {
						Thread.sleep(3000); // 3��
						synchronized(DebugPoint.o) {
							DebugPoint.debugLock = false;
						}
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
