package demo;

import groovy.lang.Binding;
import groovy.lang.Script;

import java.io.File;

import gd.DebugGroovyClassLoader;
import gd.OpenKey;

public class GroovyTest {

	// test
	public static void main(String[] args) throws Exception {
		File f = new File(System.getProperty("user.dir") + "\\src\\demo\\test.groovy");
		new OpenKey().test();
		DebugGroovyClassLoader groovyLoader = new DebugGroovyClassLoader();
		Class<Script> groovyClass = (Class<Script>) groovyLoader.parseClass(f);
		Script script = groovyClass.newInstance();
		Binding bind = new Binding();
		bind.setVariable("a", 1); // 外部变量
		script.setBinding(bind);
		script.run();
		OpenKey.debugLock = false;
		System.out.println("结束");
		
	}

}
