package test;

import java.util.ArrayList;
import java.util.Collection;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.control.BytecodeProcessor;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.SourceUnit;

import groovyjarjarasm.asm.ClassVisitor;
import groovyjarjarasm.asm.ClassWriter;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyClassLoader.InnerLoader;

public class ClassCollector1 implements CompilationUnit.ClassgenCallback {
    public Class generatedClass;
    private final GroovyClassLoader cl;
    private final SourceUnit su;
    private final CompilationUnit unit;
    private final Collection<Class> loadedClasses;
    public byte[] classCode;

    protected ClassCollector1(InnerLoader cl, CompilationUnit unit, SourceUnit su) {
        this.cl = cl;
        this.unit = unit;
        this.loadedClasses = new ArrayList<Class>();
        this.su = su;
    }

    public GroovyClassLoader getDefiningClassLoader() {
        return cl;
    }

    protected Class createClass(byte[] code, ClassNode classNode) {
        BytecodeProcessor bytecodePostprocessor = unit.getConfiguration().getBytecodePostprocessor();
        byte[] fcode = code;
        if (bytecodePostprocessor!=null) {
            fcode = bytecodePostprocessor.processBytecode(classNode.getName(), fcode);
        }
        classCode = fcode;
        GroovyClassLoader cl = getDefiningClassLoader();
        Class theClass = cl.defineClass(classNode.getName(), fcode);//cl.defineClass(classNode.getName(), fcode, 0, fcode.length, unit.getAST().getCodeSource());
        this.loadedClasses.add(theClass);

        if (generatedClass == null) {
            ModuleNode mn = classNode.getModule();
            SourceUnit msu = null;
            if (mn != null) msu = mn.getContext();
            ClassNode main = null;
            if (mn != null) main = mn.getClasses().get(0);
            if (msu == su && main == classNode) generatedClass = theClass;
        }

        return theClass;
    }

    protected Class onClassNode(ClassWriter classWriter, ClassNode classNode) {
        byte[] code = classWriter.toByteArray();
        return createClass(code, classNode);
    }

    public void call(ClassVisitor classWriter, ClassNode classNode) {
        onClassNode((ClassWriter) classWriter, classNode);
    }

    public Collection getLoadedClasses() {
        return this.loadedClasses;
    }
}
