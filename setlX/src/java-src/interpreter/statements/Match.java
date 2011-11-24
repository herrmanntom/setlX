package interpreter.statements;

import interpreter.utilities.Environment;

import java.util.List;

public class Match extends Statement {

    public Match() {
    }

    public void execute() {
    }

    public String toString(int tabs) {
        String result = Environment.getTabs(tabs) + "match (om) {" + Environment.getEndl();
        result += Environment.getTabs(tabs) + "}";
        return result;
    }
}
