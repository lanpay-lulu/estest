package antlr_test.drink;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * Created by lanpay on 2017/7/3.
 */
public class AntlrDrinkListener extends DrinkBaseListener {
    @Override
    public void enterDrink(DrinkParser.DrinkContext ctx) {
        System.out.println(ctx.getText());
    }

    public static void printDrink(String drinkSentence) {
        // Get our lexer
        DrinkLexer lexer = new DrinkLexer(new ANTLRInputStream(drinkSentence));

        // Get a list of matched tokens
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // Pass the tokens to the parser
        DrinkParser parser = new DrinkParser(tokens);

        // Specify our entry point
        DrinkParser.DrinkSentenceContext drinkSentenceContext = parser.drinkSentence();

        // Walk it and attach our listener
        ParseTreeWalker walker = new ParseTreeWalker();
        AntlrDrinkListener listener = new AntlrDrinkListener();
        walker.walk(listener, drinkSentenceContext);
    }

    public static void main(String[] args) {
        printDrink("a cup of tea");
    }
}
