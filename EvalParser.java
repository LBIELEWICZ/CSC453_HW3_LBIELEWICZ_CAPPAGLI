import java.util.*;
import java.lang.String;
import java.util.LinkedList; 

public class EvalParser {
  Scanner scan = new Scanner();

  int tempID = 0;
  String threeAddressResult = "";
  ASTNode treeRoot = null;

  /***************** Three Address Translator ***********************/
  // TODO #2 Continued: Write the functions for E/E', T/T', and F. Return the temporary ID associated with each subexpression and
  //                    build the threeAddressResult string with your three address translation 
  /****************************************/
  public ASTNode threeAddrE(LinkedList<Token> tokens) {
    ASTNode left = threeAddrT(tokens); // Left tempID for operation three address generation
    ASTNode currNode = left; 
    while (true) {
      // Handle addition operations
      if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.PLUS){
        ASTNode op = new ASTNode(ASTNode.NodeType.OP);
        op.setVal("+");
        op.setLeft(left);
        tokens.remove();
        ASTNode right = threeAddrT(tokens);
        op.setRight(right);
        op.setID(tempID);
        // Used to keep the original value intact for returns
        tempID++;
        currNode = op;
        left = currNode;
      }
      // Handle subtraction operations
      else if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.MINUS) {
        ASTNode op = new ASTNode(ASTNode.NodeType.OP);
        op.setVal("-");
        op.setLeft(left);
        tokens.remove();
        ASTNode right = threeAddrT(tokens);
        op.setRight(right);
        op.setID(tempID);
        tempID++;
        currNode = op;
        left = currNode;
      }
      else {
        break;
      }
    }
    return currNode;
  }

  public ASTNode threeAddrT(LinkedList<Token> tokens) {
    ASTNode left = threeAddrF(tokens);
    ASTNode currNode = left;
    while (true) {
      // Handle multiplication operations
      if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.MUL) {
        ASTNode op = new ASTNode(ASTNode.NodeType.OP);
        op.setVal("*");
        op.setLeft(left);
        tokens.remove();
        ASTNode right = threeAddrF(tokens);
        op.setRight(right);
        op.setID(tempID);
        tempID++;
        currNode = op;
        left = currNode;
      }
      // Handle division operations
      else if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.DIV) {
        ASTNode op = new ASTNode(ASTNode.NodeType.OP);
        op.setVal("/");
        op.setLeft(left);
        tokens.remove();
        ASTNode right = threeAddrF(tokens);
        op.setRight(right);
        op.setID(tempID);
        tempID++;
        currNode = op;
        left = currNode;
      }
      else {
        break;
      }
    }
    return currNode;
  }

  public ASTNode threeAddrF(LinkedList<Token> tokens) {
    ASTNode currNode = null;
    // Handle recursion into expressions contained in parentheses
    if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.OP) {
      tokens.remove();
      if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.CP) {
        System.out.println("ERROR: Expression not supported by grammar");
        System.exit(1);
      }
      currNode = threeAddrE(tokens);
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
      currNode = new ASTNode(ASTNode.NodeType.NUM);
      currNode.setVal("" + tokens.peek().tokenVal);
      currNode.setID(tempID);
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
    
    return currNode;
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
    
    return postorder(threeAddrE(tokens), "");
  }

  private String postorder(ASTNode root, String str) {
    if (root == null) {
      return str;
    }
    
    str = postorder(root.getLeft(), str);
    str = postorder(root.getRight(), str);
    if (root.getType() == ASTNode.NodeType.OP) {
      str += "temp" + root.getID() + " = temp" + root.getLeft().getID() +
             " " + root.getVal() + " temp" + root.getRight().getID() + "\n";
    }
    else {
      str += "temp" + root.getID() + " = " + root.getVal() + "\n";
    }
    return str;
  }

}
