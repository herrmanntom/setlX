package interpreter.utilities;

import grammar.*;

import interpreter.exceptions.EndOfFileException;
import interpreter.exceptions.FileNotReadableException;
import interpreter.exceptions.ParserException;
import interpreter.exceptions.SyntaxErrorException;
import interpreter.expressions.Expr;
import interpreter.statements.Block;
import interpreter.utilities.InputReader;

import org.antlr.runtime.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

public class ParseSetlX {

    public static Block parseFile(String fileName) throws ParserException {
        try {
            // parse the file contents (Antlr will print its parser errors into stderr ...)
            return parseBlock(new ANTLRFileStream(fileName));

        } catch (IOException e) {
            throw new FileNotReadableException("File '" + fileName + "' could not be read.");
        }
    }

    public static Block parseInteractive() throws ParserException {
        try {
            InputStream         stream = InputReader.getStream();

            // parse the input (Antlr will print its parser errors into stderr ...)
            return parseBlock(new ANTLRInputStream(stream));

        } catch (IOException ioe) {
            throw new EndOfFileException("eof");
        }
    }

    public static Block parseStringToBlock(String input) throws ParserException {
        try {
            InputStream         stream = new ByteArrayInputStream(input.getBytes());

            // parse the input (Antlr will print its parser errors into stderr ...)
            return parseBlock(new ANTLRInputStream(stream));

        } catch (IOException ioe) {
            throw new EndOfFileException("eof");
        }
    }

    public static Expr parseStringToExpr(String input) throws ParserException {
        try {
            InputStream         stream = new ByteArrayInputStream(input.getBytes());

            // parse the input (Antlr will print its parser errors into stderr ...)
            return parseExpr(new ANTLRInputStream(stream));

        } catch (IOException ioe) {
            throw new EndOfFileException("eof");
        }
    }

    private static Block parseBlock(ANTLRStringStream input) throws SyntaxErrorException, IOException {
        SetlXgrammarParser  parser  = null;
        try {
            SetlXgrammarLexer   lexer  = new SetlXgrammarLexer(input);
            CommonTokenStream   ts     = new CommonTokenStream(lexer);
                                parser = new SetlXgrammarParser(ts);

            // parse the input
            Block               blk    = parser.initBlock();

            // now Antlr will print its parser errors into stderr ...

            if (parser.getNumberOfSyntaxErrors() > 0) {
                throw new NullPointerException(); // different problem, but same handling as NullPointerException
            }

            return blk;
        } catch (RecognitionException re) {
            throw new SyntaxErrorException(re.getMessage());
        } catch (NullPointerException npe) {
            if (parser != null && parser.getNumberOfSyntaxErrors() > 0) {
                // NullPointerException caused by syntax error (or thrown because of errors)
                throw new SyntaxErrorException("" + parser.getNumberOfSyntaxErrors() + " syntax errors encountered.");
            } else { // NullPointer in parse tree itself
                throw new SyntaxErrorException("Parsed tree contains nullpointer.");
            }
        }
    }

    private static Expr parseExpr(ANTLRStringStream input) throws SyntaxErrorException, IOException {
        SetlXgrammarParser  parser  = null;
        try {
            SetlXgrammarLexer   lexer  = new SetlXgrammarLexer(input);
            CommonTokenStream   ts     = new CommonTokenStream(lexer);
                                parser = new SetlXgrammarParser(ts);

            // parse the input
            Expr                exp    = parser.anyExpr();

            // now Antlr will print its parser errors into stderr ...

            if (parser.getNumberOfSyntaxErrors() > 0) {
                throw new NullPointerException(); // different problem, but same handling as NullPointerException
            }

            return exp;
        } catch (RecognitionException re) {
            throw new SyntaxErrorException(re.getMessage());
        } catch (NullPointerException npe) {
            if (parser != null && parser.getNumberOfSyntaxErrors() > 0) {
                // NullPointerException caused by syntax error (or thrown because of errors)
                throw new SyntaxErrorException("" + parser.getNumberOfSyntaxErrors() + " syntax errors encountered.");
            } else { // NullPointer in parse tree itself
                throw new SyntaxErrorException("Parsed tree contains nullpointer.");
            }
        }
    }

}
