/*
 * ==============================================
 * debug groovy
 * ==============================================
 * 
 * Project Info: 用于groovy调试;
 *
 */

package gd;

import groovy.lang.GroovyClassLoader;  

import java.security.CodeSource;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.CaseStatement;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.codehaus.groovy.ast.stmt.DoWhileStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.codehaus.groovy.ast.stmt.SynchronizedStatement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.CompilerConfiguration;  
import org.codehaus.groovy.control.Phases;  

/**
 * DebugGroovyClassLoader
 *
 */
public class DebugGroovyClassLoader extends GroovyClassLoader {  
    private final String[] DEFAULT_IMPORT_CLASSES = {  
        "gd.DebugPoint" 
    };  
      
    private boolean isClassValid(String className) {  
        try {  
            loadClass(className, false);  
            return true;  
        } catch (ClassNotFoundException e) {  
            return false;  
        }  
    }  
      
    private static String getClassSimpleName(final String className) {  
        return className.substring(className.lastIndexOf('.') + 1);  
    }  
      
    private static boolean alreadyImported(final String alias, final ModuleNode ast) {  
        return ast.getImport(alias) != null;  
    }  
      
    /**
     * 重载createCompilationUnit
     */
    @Override  
    protected CompilationUnit createCompilationUnit(CompilerConfiguration config, CodeSource source) {  
        CompilationUnit compUnit = super.createCompilationUnit(config, source);  
        
        compUnit.addPhaseOperation(source1 -> { 

	        ModuleNode ast = source1.getAST();  
	        
	        // 处理run内语法树
	        setBreakPoint(ast.getStatementBlock().getStatements());

	        // 处理方法内语法树
	        if (ast.getMethods().size() > 0) {
	        	for(MethodNode mn : ast.getMethods()) {
	      			BlockStatement bs = (BlockStatement)mn.getCode();
		        	setBreakPoint(bs.getStatements());
		        } 
	        }
	        
	        // 加入默认的import
	        for (String className : DEFAULT_IMPORT_CLASSES) {  
	            String simpleClassName = getClassSimpleName(className);  
	            if (isClassValid(className) && !alreadyImported(simpleClassName, ast)) {  
	                ast.addImport(simpleClassName, ClassHelper.make(className));  
	            }
	        } 
        }, Phases.CONVERSION);
        
        return compUnit;  
    }  
    
    /**
     * 设置断点
     * 
     * @param stmtList
     */
    public void setBreakPoint(List<Statement> stmtList) {
    	int cnt = stmtList.size();
      for (int i = cnt - 1; i >= 0; i--) {
      	Statement stmt = stmtList.get(i);
      	if (stmt instanceof ForStatement) {
      		
      		modifyAst(stmtList, i, stmt, null);
      		
      		BlockStatement bs = (BlockStatement)((ForStatement)stmt).getLoopBlock();
      		setBreakPoint(bs.getStatements());
      	} else if (stmt instanceof DoWhileStatement) {
      		
      		modifyAst(stmtList, i, stmt, null);
      		
      		BlockStatement bs = (BlockStatement)((DoWhileStatement)stmt).getLoopBlock();
      		setBreakPoint(bs.getStatements());
      	} else if (stmt instanceof WhileStatement) {
      		
      		modifyAst(stmtList, i, stmt, null);
      		
      		BlockStatement bs = (BlockStatement)((WhileStatement)stmt).getLoopBlock();
      		setBreakPoint(bs.getStatements());
      	} else if (stmt instanceof IfStatement) {
      		
      		modifyAst(stmtList, i, stmt, null);
      		
      		BlockStatement ibs = (BlockStatement)((IfStatement)stmt).getIfBlock();
      		setBreakPoint(ibs.getStatements());
      		if (!(((IfStatement)stmt).getElseBlock() instanceof EmptyStatement)) {
      			Statement tmp = ((IfStatement)stmt).getElseBlock();
      			if (tmp instanceof IfStatement) {
      				      				
      				setElseBreakPoint((IfStatement)tmp);
      			} else {
      				BlockStatement ebs = (BlockStatement)tmp;
      				setBreakPoint(ebs.getStatements());
      			}
      		}
      	} else if (stmt instanceof SwitchStatement) {
      		
      		modifyAst(stmtList, i, stmt, null);
      		
      		for(CaseStatement cstmt : ((SwitchStatement)stmt).getCaseStatements()) {
      			BlockStatement cbs = (BlockStatement)((CaseStatement)cstmt).getCode();
	        	setBreakPoint(cbs.getStatements());
	        } 
      		if (!(((SwitchStatement)stmt).getDefaultStatement() instanceof EmptyStatement)) {
      			BlockStatement dbs = (BlockStatement)((SwitchStatement)stmt).getDefaultStatement();
      			setBreakPoint(dbs.getStatements());
      		}
      	} else if (stmt instanceof SynchronizedStatement) {
      		BlockStatement ibs = (BlockStatement)((SynchronizedStatement)stmt).getCode();
      		setBreakPoint(ibs.getStatements());
      	} else if (stmt instanceof TryCatchStatement) {
      		BlockStatement tbs = (BlockStatement)((TryCatchStatement)stmt).getTryStatement();
      		setBreakPoint(tbs.getStatements());

      		if (((TryCatchStatement)stmt).getCatchStatements().size() > 0) {
      			for(CatchStatement cstmt : ((TryCatchStatement)stmt).getCatchStatements()) {
        			BlockStatement cbs = (BlockStatement)((CatchStatement)cstmt).getCode();
  	        	setBreakPoint(cbs.getStatements());
  	        } 
      		}
      		
      		if (!(((TryCatchStatement)stmt).getFinallyStatement() instanceof EmptyStatement)) {
      			BlockStatement fbs = (BlockStatement)((TryCatchStatement)stmt).getFinallyStatement();
      			setBreakPoint(fbs.getStatements());
      		}
      	} else {
      		
      		// 该处为变量声明的位置
      		if (stmt instanceof ExpressionStatement && ((ExpressionStatement)stmt).getExpression() instanceof DeclarationExpression) {
      			DeclarationExpression de = (DeclarationExpression)((ExpressionStatement)stmt).getExpression();
      			modifyAst(stmtList, i, stmt, de.getVariableExpression().getText());
      		} else if (stmt instanceof ExpressionStatement && ((ExpressionStatement)stmt).getExpression() instanceof BinaryExpression) { 
      			BinaryExpression be = (BinaryExpression)((ExpressionStatement)stmt).getExpression();
      			modifyAst(stmtList, i, stmt, be.getLeftExpression().getText());
      		}else {
      			modifyAst(stmtList, i, stmt, null);
      		}
      		
      	}
      	// if (i==1) break;
      }
    	
    }
    
   /**
    * elseif的情况特别处理
    * 
    * @param stmt
    */
    public void setElseBreakPoint(IfStatement stmt) {
    	BlockStatement ibs = (BlockStatement)((IfStatement)stmt).getIfBlock();
  		setBreakPoint(ibs.getStatements());
  		if (!(((IfStatement)stmt).getElseBlock() instanceof EmptyStatement)) {
  			Statement tmp = ((IfStatement)stmt).getElseBlock();
  			if (tmp instanceof IfStatement) {
  				setElseBreakPoint((IfStatement)tmp);
  			} else {
  				BlockStatement ebs = (BlockStatement)tmp;
  				setBreakPoint(ebs.getStatements());
  			}
  		}
    }
    
    /**
     * 修改语法树
     * 
     * @param stmtList
     * @param i
     * @param stmt
     * @param def
     */
    public void modifyAst(List<Statement> stmtList, int i, Statement stmt, String def) {
    	ASTNode n = new AstBuilder().buildFromString(CompilePhase.SEMANTIC_ANALYSIS,false,"DebugPoint.pause("+stmt.getLineNumber()+")").get(0); //Thread.sleep(3000);
    	// 注：buildFromString直接用会产生return,需要使用CompilePhase
    	Statement breakPointStmt = (Statement)n;
  		stmtList.add(i, breakPointStmt);
  		
  		String defName = "";
    	if (def != null) {
    		defName = def;
    		ASTNode n1 = new AstBuilder().buildFromString(CompilePhase.SEMANTIC_ANALYSIS,false,"DebugPoint.addVar(\"" + defName + "\"," + def + ")").get(0);
    		Statement varStmt = (Statement)n1;
    		stmtList.add(i+2, varStmt);
    	}
  		
    }
}
