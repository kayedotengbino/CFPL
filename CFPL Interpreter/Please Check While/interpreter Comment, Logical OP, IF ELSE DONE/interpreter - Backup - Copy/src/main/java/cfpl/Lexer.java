package cfpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import static cfpl.TestScanner.*;

public class Lexer {

    private static final Interpreter interpreter = new Interpreter();

    static boolean hadError = false;
    static boolean hadRuntimeError = false;

    public static void main(String[] args) throws IOException{
        System.out.print("FILE: " + Paths.get(".").toAbsolutePath().normalize().toString() + "/src/main/java/cfpl/testCase.txt\n");
        runFile(String.valueOf(Paths.get("A:\\GitHubDesktop\\Repositories\\CS322\\Week 9\\1 Comma Update\\Please Check While\\interpreter Comment, Logical OP, IF ELSE DONE\\interpreter - Backup - Copy\\src\\main\\java\\cfpl\\testCase.txt")));
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        // Indicate an error in the exit code.
        if (hadError) System.exit(65);
        if (hadRuntimeError) System.exit(70);
    }
    
      private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
    
        for (;;) { 
          System.out.print("> ");
          String line = reader.readLine();
          if (line == null) break;
          run(line);
          hadError = false;
      }
    }

    private static void run(String source) {
      TestScanner scanner = new TestScanner(source);
      List<Token> tokens = scanner.scanTokens();
  
      TestParser parser = new TestParser(tokens);
      List<Stmt> statements = parser.parse();

      // Stop if there was a syntax error.
      if (hadError) return;

      interpreter.interpret(statements);
      //System.out.println(new AstPrinter().print(expression));
    }

    static void error(int line, String message) {
        report(line, "", message);
    }
    
    private static void report(int line, String where,
                                 String message) {
        System.err.println(
            "[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

    static void error(Token token, String message) {
      if (token.type == TokenType.EOF) {
        report(token.line, " at end", message);
      } else {
        report(token.line, " at '" + token.lexeme + "'", message);
      }
    }

    static void runtimeError(RuntimeError error) {
      System.err.println(error.getMessage() +
          "\n[line " + error.token.line + "]");
      hadRuntimeError = true;
    }
}
