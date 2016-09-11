package cn.lightfish.ast;

import cn.lightfish.core.ASTList;
import cn.lightfish.core.ASTree;

import java.util.List;

/**
 * Created by karak on 16-8-28.
 */
public class Switch extends ASTList {

    public Switch(List<ASTree> c) {
        super(c);
    }


    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("(switch (").append(this.child(0)).append(")");
        int i = 1;
        int size=this.children.size();
        for (;i<size;++i) {
            stringBuilder.append(child(i).toString()).append(" ");
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

   public static class Case extends ASTList {


       public Case(List<ASTree> c) {
            super(c);
        }
        public ASTree getCaseToken() {
            return this.children.get(0);
        }

        public ASTree getBlockStmnt() {
            return this.children.get(1);
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("(case ").append(getCaseToken()).append(" :").append(getBlockStmnt().toString());
            sb.append(')');
            return sb.toString();
        }
    }
}
