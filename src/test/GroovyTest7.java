package test;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;
import groovy.lang.GroovyClassLoader.InnerLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map.Entry;

import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.classgen.Verifier;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.Phases;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.runtime.InvokerHelper;

/**
 * Groovyҵ���߼�������.
 *  
 *  ��ʼ���׶Σ�Initialization������Դ�ļ������û���������
    �﷨�����׶Σ�Parsing����ʹ���﷨��������ʾԴ�������������
    ת���׶Σ�Conversion�������������д��������﷨����AST����
    ��������׶Σ�Semantic Analysis�������һ���Լ���Ч�Խ��м�飬�����﷨����鲻�˵ģ�Ȼ������ࡣ
    �淶���׶Σ�Canonicalization���������ع��� AST��
    ָ��ѡ��׶Σ�Instruction Selection����ѡ��ָ��ϣ����磺Java 6 �� Java 7 �ֽ��뼶��
    �����ɽ׶Σ�Class Generation�������ڴ��д�������ֽ��롣
    ����׶Σ�Output������д������������ļ�ϵͳ��
    ��ֹ�׶Σ�Finalization����ִ�������������ռ���������
 */
public class GroovyTest7 {

	// test
	public static void main(String[] args) throws Exception {
		File f = new File("D:\\projects\\java\\e_64\\gdtest\\src\\test\\test3.groovy");
		new OpenKey().test();
		CustomGroovyClassLoader groovyLoader = new CustomGroovyClassLoader();
		Class<Script> groovyClass = (Class<Script>) groovyLoader.parseClass(f);
		Script script = groovyClass.newInstance();
		Binding bind = new Binding();
		bind.setVariable("a", 1);
		script.setBinding(bind);
		script.run();
		OpenKey.debugLock = false;
		System.out.println("...");
		
		for(Object o :script.getBinding().getVariables().entrySet()) {
			System.out.println(((Entry)o).getKey());
		}
		
		// System.out.println(script.getMetaClass().getProperties().get(1).getProperty("c"));

	}

}
