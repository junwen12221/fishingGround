package cn.lightfish.core;

import cn.lightfish.ast.*;

/**
 * Created by karak on 16-8-27.
 */
public interface Visitor<S, E,D>  {
    public E visit(Arguments value) throws Exception;

    public E visit(ArrayLiteral value) throws Exception;

    public E visit(ArrayRef value) throws Exception;

    public E visit(ASTLeaf value) throws Exception;

    public E visit(ASTList value) throws Exception;

    public E visit(ASTree value) throws Exception;

    public E visit(BinaryExpr value) throws Exception;


    public E visit(NumberLiteral value) throws Exception;

    public E visit(ParameterList value) throws Exception;

    public E visit(Postfix value) throws Exception;

    public E visit(PrimaryExpr value) throws Exception;

    public E visit(StringLiteral value) throws Exception;

    public E visit(TypeTag value) throws Exception;


    public S visit(BlockStmnt value) throws Exception;



    public S visit(ClassStmnt value) throws Exception;

    public S visit(DefStmnt value) throws Exception;

    public E visit(Dot value) throws Exception;

    public D visit(ClassBody value) throws Exception;
    public S visit(IfStmnt value) throws Exception;

    public E visit(Name value) throws Exception;

    public E visit(NegativeExpr value) throws Exception;

    public S visit(NullStmnt value) throws Exception;

    public S visit(VarStmnt value) throws Exception;

    public S visit(WhileStmnt value) throws Exception;

    public S visit(ForStmnt value) throws Exception;
    public S visit(PrimaryExprList value) throws Exception;

}
