package test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import antlr.Token;
import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import groovy.ui.GroovyMain;
import groovy.lang.GroovyClassLoader.ClassCollector;
import groovy.lang.GroovyClassLoader.InnerLoader;

import static org.codehaus.groovy.runtime.InvokerHelper.MAIN_METHOD_NAME;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.groovy.plugin.GroovyRunner;
import org.apache.groovy.plugin.GroovyRunnerRegistry;
import org.apache.groovy.util.ScriptRunner;
import org.codehaus.groovy.antlr.AntlrParserPlugin;
import org.codehaus.groovy.antlr.AntlrParserPluginFactory;
import org.codehaus.groovy.antlr.SourceBuffer;
import org.codehaus.groovy.antlr.UnicodeEscapingReader;
import org.codehaus.groovy.antlr.parser.GroovyLexer;
import org.codehaus.groovy.antlr.parser.GroovyRecognizer;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.classgen.Verifier;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.Phases;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.runtime.EncodingGroovyMethods;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.tools.GroovyClass;
import org.codehaus.groovy.tools.GroovyStarter;
import org.codehaus.groovy.tools.LoaderConfiguration;
import org.codehaus.groovy.tools.RootLoader;

/**
 * Groovy业务逻辑处理类.
 */
public class GroovyTest4 {

	// test
	public static void main(String[] args) throws Exception {
		File f = new File("D:\\projects\\java\\j_64\\gdemo\\src\\test\\test.groovy");
		
		/*GroovyRecognizer.tracing = true;
		GroovyLexer.tracing = true;

		SourceBuffer sourceBuffer = new SourceBuffer();

		UnicodeEscapingReader unicodeReader = new UnicodeEscapingReader(
				new FileReader(f), sourceBuffer);
		GroovyLexer lexer = new GroovyLexer(unicodeReader);
		unicodeReader.setLexer(lexer);
		GroovyRecognizer parser = GroovyRecognizer.make(lexer);
	    parser.setSourceBuffer(sourceBuffer);
	    parser.setFilename(f.getName());
	    GroovyLexer lexer1 = parser.getLexer();
	    lexer1.setWhitespaceIncluded(true);
	    while (true) {
	    	groovyjarjarantlr.Token t = lexer1.nextToken();
	        System.out.println(t);
	        if (t == null || t.getType() == Token.EOF_TYPE)  break;
	    }
	    parser.compilationUnit();
	    System.out.println("parseFile "+f+" => "+parser.getAST());*/
		
    //AntlrParserPlugin app = (AntlrParserPlugin)(new AntlrParserPluginFactory().createParserPlugin());
    CompilerConfiguration configuration = null;
    CompilationUnit unit = new CompilationUnit(configuration);
    SourceUnit su = unit.addSource(f);
    unit.addFirstPhaseOperation(TimestampAdder1.INSTANCE, CompilePhase.CLASS_GENERATION.getPhaseNumber());
    
    ClassCollector1 collector = createCollector(unit, su, new GroovyClassLoader());
    unit.setClassgenCallback(collector);
    int goalPhase = Phases.CLASS_GENERATION;
    // if (config != null && config.getTargetDirectory() != null) goalPhase = Phases.OUTPUT;
    unit.compile(goalPhase);
    Class answer = collector.generatedClass;
    /*String mainClass = su.getAST().getMainClassName();
    for (Object o : collector.getLoadedClasses()) {
        Class clazz = (Class) o;
        String clazzName = clazz.getName();
        //definePackageInternal(clazzName);
        //setClassCacheEntry(clazz);
        if (clazzName.equals(mainClass)) answer = clazz;
    }*/
    System.out.println(answer);
    Binding context = new Binding();
    Script script = InvokerHelper.newScript(answer, context);
    script.run();
    // System.out.println("parseFile "+f+" => "+unit.getAST().getModules());
    
    /*List<GroovyClass> classes = (List<GroovyClass>) unit.getClasses();
    for (GroovyClass groovyClass : classes) {
        System.out.println(groovyClass.getName());
    }*/
    
    // ScriptRunner.runScript(f);
    /*String a[] = new String[] {"--main", "groovy.ui.GroovyMain", f.getCanonicalPath()};
    int argsOffset = 2;
    String[] newArgs = new String[a.length-argsOffset];
    System.arraycopy(a, 0 + argsOffset, newArgs, 0, newArgs.length);
    
    
    LoaderConfiguration lc = new LoaderConfiguration();
    lc.setMainClass("groovy.ui.GroovyMain");
    ClassLoader loader = AccessController.doPrivileged((PrivilegedAction<RootLoader>) () -> new RootLoader(lc));
    Method m=null;
    try {
      Class c = loader.loadClass(lc.getMainClass());
	      m = c.getMethod("main", String[].class);
	  } catch (ClassNotFoundException | NoSuchMethodException | SecurityException e1) {
	      e1.printStackTrace();
	  }
	  try {
	      m.invoke(null, new Object[]{newArgs}); // args
	  } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e3) {
	  	e3.printStackTrace();
	  }
	  
	  */
	  
	  // GroovyMain.main(new String[] {"-n", f.getCanonicalPath()});
	  
	  /*ClassLoader parent = GroovyTest.class.getClassLoader();
		String cmd = "";
		GroovyShell gs = new GroovyShell(parent);

		Binding binding = new Binding();
		gs = new GroovyShell(binding);
		try {
			cmd = "import com.csp.workroom.oe.domain.LogicNode;\n";
			cmd += "println '1'\n";
			cmd += "x = 123;";
			gs.evaluate(cmd);
			Script s = gs.parse(cmd);
			String line = "println 'a'";
      String lineCountName = "count";
      s.setProperty(lineCountName, BigInteger.ZERO);
      String autoSplitName = "split";
      boolean autoSplit = true;
      PrintWriter writer = new PrintWriter(System.out);
      s.setProperty("out", writer);
      try {
        //InvokerHelper.invokeMethod(s, "begin", null);
	    } catch (Exception mme) {
	    	mme.printStackTrace();
	    }
      s.setProperty("line", line);
      s.setProperty(lineCountName, ((BigInteger)s.getProperty(lineCountName)).add(BigInteger.ONE));

      if(autoSplit) {
          s.setProperty(autoSplitName, line.split("\\s"));
      }

      Object o = s.run();

      if ( o != null) {
      	writer.println(o);
      }
			s.run();

		} catch (Exception e) {
			System.err.println(e.getMessage());

		}
	}*/
	/*
	 List<GroovyClass> classes = (List<GroovyClass>) cu.getClasses();
  	for (GroovyClass groovyClass : classes) {
    	if (groovyClass.getName().equals(name)) {
        return loader.defineClass(name, groovyClass.getBytes());
    	}
  	}
	 */

	}
	
	public static ClassCollector1 createCollector(CompilationUnit unit, SourceUnit su, GroovyClassLoader gcl) {
        InnerLoader loader = AccessController.doPrivileged((PrivilegedAction<InnerLoader>) () -> new InnerLoader(gcl));
        return new ClassCollector1(loader, unit, su);
    }
	
	public static class TimestampAdder1 implements CompilationUnit.IPrimaryClassNodeOperation, groovyjarjarasm.asm.Opcodes {
        private static final TimestampAdder1 INSTANCE = new TimestampAdder1();

        private TimestampAdder1() {}

        protected void addTimeStamp(ClassNode node) {
            if (node.getDeclaredField(Verifier.__TIMESTAMP) == null) { // in case if verifier visited the call already
                FieldNode timeTagField = new FieldNode(
                        Verifier.__TIMESTAMP,
                        ACC_PUBLIC | ACC_STATIC | ACC_SYNTHETIC,
                        ClassHelper.long_TYPE,
                        //"",
                        node,
                        new ConstantExpression(System.currentTimeMillis()));
                // alternatively, FieldNode timeTagField = SourceUnit.createFieldNode("public static final long __timeStamp = " + System.currentTimeMillis() + "L");
                timeTagField.setSynthetic(true);
                node.addField(timeTagField);

                timeTagField = new FieldNode(
                        Verifier.__TIMESTAMP__ + System.currentTimeMillis(),
                        ACC_PUBLIC | ACC_STATIC | ACC_SYNTHETIC,
                        ClassHelper.long_TYPE,
                        //"",
                        node,
                        new ConstantExpression((long) 0));
                // alternatively, FieldNode timeTagField = SourceUnit.createFieldNode("public static final long __timeStamp = " + System.currentTimeMillis() + "L");
                timeTagField.setSynthetic(true);
                node.addField(timeTagField);
            }
        }

        @Override
        public void call(final SourceUnit source, final GeneratorContext context, final ClassNode classNode) throws CompilationFailedException {
            if ((classNode.getModifiers() & groovyjarjarasm.asm.Opcodes.ACC_INTERFACE) > 0) {
                // does not apply on interfaces
                return;
            }
            if (!(classNode instanceof InnerClassNode)) {
                addTimeStamp(classNode);
            }
        }
    }


}
