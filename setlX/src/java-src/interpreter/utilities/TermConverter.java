package interpreter.utilities;

import interpreter.exceptions.TermConversionException;
import interpreter.expressions.Expr;
import interpreter.expressions.ValueExpr;
import interpreter.expressions.Variable;
import interpreter.statements.ExpressionStatement;
import interpreter.statements.Statement;
import interpreter.types.Term;

public class TermConverter {

    public static Expr termToExpr(Term term) throws TermConversionException {
        String functionalCharacter = term.functionalCharacter().getUnquotedString();
        if (functionalCharacter.equals(Variable.FUNCTIONAL_CHARACTER)) {
            return Variable.termToExpr(term);
        } else {
            throw new TermConversionException("Functional character does not represent an expression.");
        }
    }

    public static Statement termToStatement(Term term) throws TermConversionException {
        String functionalCharacter = term.functionalCharacter().getUnquotedString();

        throw new TermConversionException("Functional character does not represent an statement.");
    }

    public static CodeFragment termToCodeFragment(Term term) throws TermConversionException {
        try {
            return termToExpr(term);
        } catch (TermConversionException tce) {
            return termToStatement(term);
        }
    }

    public static Expr termToExprFailSave(Term term) {
        try {
            return termToExpr(term);
        } catch (TermConversionException tce) {
            return new ValueExpr(term);
        }
    }

    public static Statement termToStatementFailSave(Term term) {
        try {
            return termToStatement(term);
        } catch (TermConversionException tce) {
            return new ExpressionStatement(new ValueExpr(term));
        }
    }

    public static CodeFragment termToCodeFragmentFailSave(Term term) {
        try {
            return termToCodeFragment(term);
        } catch (TermConversionException tce) {
            return new ValueExpr(term);
        }
    }

}

