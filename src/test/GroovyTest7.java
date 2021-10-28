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
 * Groovy业务逻辑处理类.
 *  
 *  初始化阶段（Initialization）：打开源文件，配置环境参数。
    语法解析阶段（Parsing）：使用语法来产生表示源代码的令牌树。
    转换阶段（Conversion）：从令牌树中创建抽象语法树（AST）。
    语义分析阶段（Semantic Analysis）：针对一致性及有效性进行检查，这是语法所检查不了的，然后解析类。
    规范化阶段（Canonicalization）：完整地构建 AST。
    指令选择阶段（Instruction Selection）：选择指令集合，比如：Java 6 或 Java 7 字节码级别。
    类生成阶段（Class Generation）：在内存中创建类的字节码。
    输出阶段（Output）：编写二进制输出到文件系统。
    终止阶段（Finalization）：执行最后的垃圾回收及清理工作。
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
