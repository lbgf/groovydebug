package test;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;
import groovy.lang.GroovyClassLoader.InnerLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;

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
public class GroovyTest6 {

	// test
	public static void main(String[] args) throws Exception {
		File f = new File("D:\\projects\\java\\j_64\\gdemo\\src\\test\\test2.groovy");

		CompilerConfiguration configuration = null;
		CompilationUnit unit = new CompilationUnit(configuration);
		SourceUnit su = unit.addSource(f);
		unit.addFirstPhaseOperation(TimestampAdder1.INSTANCE, CompilePhase.CLASS_GENERATION.getPhaseNumber());

		ClassCollector1 collector = createCollector(unit, su, new GroovyClassLoader());
		unit.setClassgenCallback(collector);
		int goalPhase = Phases.CLASS_GENERATION;
		// if (config != null && config.getTargetDirectory() != null) goalPhase =
		// Phases.OUTPUT;
		unit.compile(goalPhase);
		Class answer = collector.generatedClass;
		
		saveFile(new File("D:\\projects\\java\\j_64\\gdemo\\test2.class"), collector.classCode);
		
		/*
		 * String mainClass = su.getAST().getMainClassName(); for (Object o :
		 * collector.getLoadedClasses()) { Class clazz = (Class) o; String clazzName =
		 * clazz.getName(); //definePackageInternal(clazzName);
		 * //setClassCacheEntry(clazz); if (clazzName.equals(mainClass)) answer = clazz;
		 * }
		 */
		System.out.println(answer);
		Binding context = new Binding();
		Script script = InvokerHelper.newScript(answer, context);
		script.run();
		// System.out.println("parseFile "+f+" => "+unit.getAST().getModules());

	}

	public static ClassCollector1 createCollector(CompilationUnit unit, SourceUnit su, GroovyClassLoader gcl) {
		InnerLoader loader = AccessController.doPrivileged((PrivilegedAction<InnerLoader>) () -> new InnerLoader(gcl));
		return new ClassCollector1(loader, unit, su);
	}

	public static class TimestampAdder1
			implements CompilationUnit.IPrimaryClassNodeOperation, groovyjarjarasm.asm.Opcodes {
		private static final TimestampAdder1 INSTANCE = new TimestampAdder1();

		private TimestampAdder1() {
		}

		protected void addTimeStamp(ClassNode node) {
			if (node.getDeclaredField(Verifier.__TIMESTAMP) == null) { // in case if verifier visited the call already
				FieldNode timeTagField = new FieldNode(Verifier.__TIMESTAMP, ACC_PUBLIC | ACC_STATIC | ACC_SYNTHETIC,
						ClassHelper.long_TYPE,
						// "",
						node, new ConstantExpression(System.currentTimeMillis()));
				// alternatively, FieldNode timeTagField = SourceUnit.createFieldNode("public
				// static final long __timeStamp = " + System.currentTimeMillis() + "L");
				timeTagField.setSynthetic(true);
				node.addField(timeTagField);

				timeTagField = new FieldNode(Verifier.__TIMESTAMP__ + System.currentTimeMillis(),
						ACC_PUBLIC | ACC_STATIC | ACC_SYNTHETIC, ClassHelper.long_TYPE,
						// "",
						node, new ConstantExpression((long) 0));
				// alternatively, FieldNode timeTagField = SourceUnit.createFieldNode("public
				// static final long __timeStamp = " + System.currentTimeMillis() + "L");
				timeTagField.setSynthetic(true);
				node.addField(timeTagField);
			}
		}

		@Override
		public void call(final SourceUnit source, final GeneratorContext context, final ClassNode classNode)
				throws CompilationFailedException {
			if ((classNode.getModifiers() & groovyjarjarasm.asm.Opcodes.ACC_INTERFACE) > 0) {
				// does not apply on interfaces
				return;
			}
			if (!(classNode instanceof InnerClassNode)) {
				addTimeStamp(classNode);
			}
		}
	}

	public static void saveFile(File file, byte[] data) throws Exception {
		if (!file.exists()) {
			file.createNewFile(); // 如果文件不存在，则创建
		}
		FileOutputStream fos = new FileOutputStream(file);
		int size = 0;
		if (data.length > 0) {
			fos.write(data, 0, data.length);
		}
		fos.close();

	}

}
