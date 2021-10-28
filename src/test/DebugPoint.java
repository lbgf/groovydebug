package test;

public class DebugPoint {
	public static boolean debugLock = false;
	public static Object o = new Object();
	
	public static void pause(int no) {
		System.out.println("中断第" + no + "行");
		
		synchronized(o) {
			DebugPoint.debugLock = true;
		}
		// 等待开锁
		while (DebugPoint.debugLock) {
			try {
				Thread.sleep(100);
				//System.out.println(DebugPoint.debugLock);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void addVar(String defName, Object defValue) {
		if (!defName.equals("")) {
			System.out.println("发现变量" + defName + "值为" + defValue + "");
		}
		
	}

}
