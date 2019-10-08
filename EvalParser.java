import java.util.*;
import java.lang.String;
import java.util.LinkedList; 

public class EvalParser {
  Scanner scan = new Scanner();

  int tempID = 0;
  String threeAddressResult = "";

  /***************** Three Address Translator ***********************/
  // TODO #2 Continued: Write the functions for E/E', T/T', and F. Return the temporary ID associated with each subexpression and
  //                    build the threeAddressResult string with your three address translation 
  /****************************************/
  public int threeAddrE(LinkedList<Token> tokens) {
    int leftID = threeAddrT(tokens); // Left tempID for operation three address generation
    int currID = -1; 
    while (true) {
      // Handle addition operations
      if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.PLUS){
        String op = tokens.peek().tokenVal; // Retrieve operator
        tokens.remove();
        int rightID = threeAddrT(tokens);
        operationThreeAddr(leftID, op, rightID);
        currID = tempID;
        leftID = currID;
        // Used to keep the original value intact for returns
        tempID++;
      }
      // Handle subtraction operations
      else if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.MINUS) {
        String op = tokens.peek().tokenVal;
        tokens.remove();
        int rightID = threeAddrT(tokens);
        operationThreeAddr(leftID, op, rightID);
        currID = tempID;
        leftID = currID;
        tempID++;
      }
      else {
        // Return the left value when coming from recursive calls
        currID = tempID-1;
        break;
      }
    }
    return currID;
  }

  public int threeAddrT(LinkedList<Token> tokens) {
    int leftID = threeAddrF(tokens);
    int currID = -1;
    while (true) {
      // Handle multiplication operations
      if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.MUL) {
        String op = tokens.peek().tokenVal;
        tokens.remove();
        int rightID = threeAddrF(tokens);
        operationThreeAddr(leftID, op, rightID);
        currID = tempID;
        leftID = currID;
        tempID++;
      }
      // Handle division operations
      else if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.DIV) {
        String op = tokens.peek().tokenVal;
        tokens.remove();
        int rightID = threeAddrF(tokens);
        operationThreeAddr(leftID, op, rightID);
        currID = tempID;
        leftID = currID;
        tempID++;
      }
      else {
        currID = tempID-1;
        break;
      }
    }
    return currID;
  }

  public int threeAddrF(LinkedList<Token> tokens) {
    int currID = -1;
    // Handle recursion into expressions contained in parentheses
    if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.OP) {
      tokens.remove();
      if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.CP) {
        System.out.println("ERROR: Expression not supported by grammar");
        System.exit(1);
      }
      currID = threeAddrE(tokens);
      if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.CP) {
        tokens.remove();
      }
      else {
        // Handle invalid sequences of tokens (i.e. 1++1)
        System.out.println("ERROR: Expression not supported by grammar");
        System.exit(1);
      }
    }
    // Handle numbers
    else if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.NUM) {
      constantThreeAddr(tokens.peek().tokenVal);
      currID = tempID;
      this.tempID++;
      tokens.remove();
      if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.OP) {
        System.out.println("ERROR: Expression not supported by grammar");
        System.exit(1);
      }
    }
    else {
      // If a factor has reached this point it is not an operation supported by this parser
      System.out.println("ERROR: Expression not supported by grammar");
      System.exit(1);
    }
    return currID;
  }
  
  public void constantThreeAddr(String val) {
    this.threeAddressResult += "temp" + this.tempID + " = " + val + "\n";
  }

  public void operationThreeAddr(int val1, String op, int val2) {
    this.threeAddressResult += "temp" + this.tempID + " = temp" + val1 + " " + op + " temp" + val2 + "\n";
  }
  /***************** Simple Expression Evaluator ***********************/
  // TODO #1 Continued: Write the functions for E/E', T/T', and F. Return the expression's value
  /****************************************/

  /* TODO #1: Write a parser that can evaluate expressions */
  public int evaluateExpression(String eval){
    LinkedList<Token> tokens = scan.extractTokenList(eval);
    int result = evaluateE(tokens);
    return result;
  }

  // Evaluating E/E'
  private int evaluateE(LinkedList<Token> tokens){
    int r;
    r = evaluateT(tokens);
    while(true){ // E'
      if(tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.PLUS){
        tokens.remove(); //match('+');
        r = r + evaluateT(tokens);
      }else if(tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.MINUS){
        tokens.remove(); //match('-');
        r = r - evaluateT(tokens);
      }else{
        break;
      }
    }
    return r;
  }

  // Evaluating T/T'
  private int evaluateT(LinkedList<Token> tokens){
    int r;
    r = evaluateF(tokens);
    while(true){ // T'
      if(tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.MUL){
        tokens.remove(); //match('*');
        r = r * evaluateF(tokens);
      }else if(tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.DIV){
        tokens.remove(); //match('/');
        r = r / evaluateF(tokens);
      }else{
        break;
      }
    }
    return r;
  }

  // Evaluating F
  private int evaluateF(LinkedList<Token> tokens){
    int r = 0;
    if(tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.OP){
      tokens.remove(); //match('(');
      if (tokens.peek().tokenType == Token.TokenType.CP) {
        System.out.println("ERROR: Not in the grammar.");
        System.exit(1);
      }
      r = evaluateE(tokens);
      if(tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.CP){
        tokens.remove(); //match(')');
      }else{
        System.out.println("ERROR: Not in the grammer.");
        System.exit(1);
      }
    }else if(tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.NUM){
      r = Integer.parseInt(tokens.remove().tokenVal); //match(number);
    }else{
       System.out.println("ERROR: Not in the grammer.");
       System.exit(1);
    }
    return r;
  }

  /* TODO #2: Now add three address translation to your parser*/
  public String getThreeAddr(String eval){
    this.threeAddressResult = "";
    this.tempID = 0;
    LinkedList<Token> tokens = scan.extractTokenList(eval);
    threeAddrE(tokens);
    return this.threeAddressResult;
  } 

}
