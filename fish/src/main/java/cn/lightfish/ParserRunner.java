package cn.lightfish;

import cn.lightfish.ast.NullStmnt;
import cn.lightfish.core.ASTree;
import cn.lightfish.core.Lexer;
import cn.lightfish.core.Token;
import cn.lightfish.exception.ParseException;
import cn.lightfish.parser.BasicParser;
import cn.lightfish.parser.TypedParser;
import cn.lightfish.runtime.Natives;
import cn.lightfish.visitor.LookupVisitor;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ParserRunner {
    public static void main(String[] args) throws ParseException {
        BufferedReader strin = new BufferedReader(new InputStreamReader(System.in));
        Lexer l = new Lexer(strin);
        LookupVisitor visitor=(LookupVisitor) new Natives().environment(new LookupVisitor());
        visitor.setRoot(true);
        BasicParser bp = new TypedParser();
        Token peek;
        while ((peek=l.peek(0)) != Token.EOF) {

            try {
                ASTree ast = bp.parse(l);
                System.out.println("ast   => " + ast.toString());
                if (!(ast instanceof NullStmnt)) {
                    Object r = ast.accept(visitor);
                    System.out.println("res => " + r);
                }
            }catch (Exception e){
                e.printStackTrace();
                visitor.printEnv();
            }

        }


    }

}
