package interpreter.statements;

import interpreter.exceptions.SetlException;
import interpreter.types.SetlList;
import interpreter.types.Term;
import interpreter.utilities.Environment;

import java.util.LinkedList;
import java.util.List;

/*
grammar rules:
initBlock
    : statement+
    ;

block
    : statement*
    ;

implemented here as:
      =========
     mStatements
*/

public class Block extends Statement {
    private List<Statement>  mStatements;

    public Block() {
        this(new LinkedList<Statement>());
    }

    public Block(List<Statement> statements) {
        mStatements = statements;
    }

    public void add(Statement stmnt) {
        mStatements.add(stmnt);
    }

    public void execute() throws SetlException {
        for (Statement stmnt : mStatements) {
            stmnt.execute();
        }
    }

    /* string operations */

    public String toString(int tabs) {
        return toString(tabs, false);
    }

    public String toString(int tabs, boolean brackets) {
        String endl      = Environment.getEndl();
        int    stmntTabs = tabs;
        if (brackets) {
            stmntTabs += 1;
        }
        String result = "";
        if (brackets) {
            result += "{" + endl;
        }
        int count = 1;
        for (Statement stmnt: mStatements) {
            result += stmnt.toString(stmntTabs);
            if (count < mStatements.size()) {
                result += endl;
            }
            count++;
        }
        if (brackets) {
            result += endl + Environment.getTabs(tabs) + "}";
        }
        return result;
    }

    /* term operations */

    public Term toTerm() throws SetlException {
        Term result = new Term("'block");

        SetlList stmntList = new SetlList();
        for (Statement s: mStatements) {
            stmntList.addMember(s.toTerm());
        }
        result.addMember(stmntList);

        return result;
    }
}

