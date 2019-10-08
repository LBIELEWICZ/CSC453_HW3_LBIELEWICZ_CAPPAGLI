import java.util.*;
import java.lang.String;
import java.util.LinkedList; 

public class EvalParser {
  Scanner scan = new Scanner();

  int tempID = 0;
  int tlabelID = 0; // Label id for true
  int flabelID = 0; // Label id for false
  int rlabelID = 0; // Label id for loops

  Token.TokenType last;

  /***************** Three Address Translator ***********************/
  // TODO #2 Continued: Write the functions for E/E', T/T', and F. Return the temporary ID associated with each subexpression and
  //                    build the threeAddressResult string with your three address translation 
  /****************************************/
  public ASTNode threeAddrProg(LinkedList<Token> tokens) {
    ASTNode op = new ASTNode(ASTNode.NodeType.PROG);
    if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.VOID){
      tokens.remove();
    }
    else {
      // Invalid program type
      System.out.println("ERROR: Invalid program type");
      System.exit(1);
    }
    ASTNode left = threeAddrId(tokens); // Left tempID for operation three address generation
    ASTNode currNode = left; 
    if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.OP){
      tokens.remove();
      if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.CP){
        tokens.remove();
        if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.OB){
          tokens.remove();
          op.setLeft(left);
          ASTNode right = threeAddrStmtLst(tokens);
          op.setRight(right);
          currNode = op;
          left = currNode;
          if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.CB){
            tokens.remove();
          }
          else {
            // Check brackets
            System.out.println("ERROR: Check brackets");
            System.exit(1);
          }
        }
        else {
          // Check brackets
          System.out.println("ERROR: Check brackets");
          System.exit(1);
        }
      }
      else {
        // Check brackets
        System.out.println("ERROR: Check brackets");
        System.exit(1);
      }
    }
    else {
      // Check brackets
      System.out.println("ERROR: Check brackets");
      System.exit(1);
    }
    return currNode;
  }

  public ASTNode threeAddrStmtLst(LinkedList<Token> tokens) {
    ASTNode left = threeAddrStmt(tokens);
    ASTNode currNode = left;
    while(true) {
      if (tokens.peek() != null && (tokens.peek().tokenType == Token.TokenType.INT || 
          tokens.peek().tokenType == Token.TokenType.IF || tokens.peek().tokenType == Token.TokenType.WHILE)){
        ASTNode list = new ASTNode(ASTNode.NodeType.LIST);
        list.setLeft(left);
        ASTNode right = threeAddrStmt(tokens);
        list.setRight(right);
        currNode = list;
        left = currNode;
      }
      else {
        break;
      }
    }
    return currNode;
  }

  public ASTNode threeAddrStmt(LinkedList<Token> tokens) {
    ASTNode currNode = null;

    if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.INT){
      currNode = threeAddrAssignment(tokens);
      this.tempID = 0;
    }
    else if (tokens.peek() != null && (tokens.peek().tokenType == Token.TokenType.IF || 
                                       tokens.peek().tokenType == Token.TokenType.WHILE)){
      currNode = threeAddrCf(tokens);
      this.tempID = 0;
    }
    else if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.CB) {
      return null;
    }
    else {
      // Invalid statment
      System.out.println("ERROR: Invalid statment");
      System.exit(1);
    }
    return currNode;
  }

  public ASTNode threeAddrCf(LinkedList<Token> tokens) {
    ASTNode currNode = null;
    ASTNode cf = null;
    if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.IF){
      cf = new ASTNode(ASTNode.NodeType.IF);
      tokens.remove();
    }
    else if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.WHILE){
      cf = new ASTNode(ASTNode.NodeType.WHILE);
      tokens.remove();
    }
    else {
      // Invalid control flow
      System.out.println("ERROR: Invalid control flow");
      System.exit(1);
    }
    if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.OP) {
      tokens.remove();
      
      cf.setLeft(threeAddrS(tokens));
      this.tempID = 0;
      cf.setVal("" + last);
      if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.CP) {
        
        tokens.remove();
        if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.OB) {
          tokens.remove();
          cf.setRight(threeAddrStmtLst(tokens));
          cf.setID(cf.getLeft().getID());
          currNode = cf;
          if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.CB) {
            tokens.remove();
          }
          else {
            // Invalid control flow
            System.out.println("ERROR: Invalid control flow");
            System.exit(1);
          }
        }
        else {
          // Invalid control flow
          System.out.println("ERROR: Invalid control flow");
          System.exit(1);
        }
      }
      else {
        // Invalid control flow
        System.out.println("ERROR: Invalid control flow");
        System.exit(1);
      }
    }
    else {
      // Invalid control flow
      System.out.println("ERROR: Invalid control flow");
      System.exit(1);
    }
    return currNode;
  }

  public ASTNode threeAddrAssignment(LinkedList<Token> tokens) {
    ASTNode op = new ASTNode(ASTNode.NodeType.ASSG);
    if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.INT){
      tokens.remove();
    }
    else {
      // Invalid assignment
      System.out.println("ERROR: Invalid assignment");
      System.exit(1);
    }
    ASTNode left = threeAddrId(tokens); // Left tempID for operation three address generation
    ASTNode currNode = left; 
    if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.ASSG){
      op.setVal("=");
      op.setLeft(left);
      tokens.remove();
      ASTNode right = threeAddrS(tokens);
      op.setRight(right);
      currNode = op;
      left = currNode;
      if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.END){
        tokens.remove();
      }
      else {
        // Invalid assignment
        System.out.println("ERROR: Invalid assignment");
        System.exit(1);
      }
    }
    else {
      // Invalid assignment
      System.out.println("ERROR: Invalid assignment");
      System.exit(1);
    }
    return currNode;
  }

  public ASTNode threeAddrS(LinkedList<Token> tokens) {
    ASTNode left = threeAddrG(tokens); // Left tempID for operation three address generation
    ASTNode currNode = left; 
    while (true) {
      // Handle equality operations
      if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.EQ){
        last = Token.TokenType.EQ;
        ASTNode op = new ASTNode(ASTNode.NodeType.RELOP);
        op.setVal("==");
        op.setLeft(left);
        tokens.remove();
        ASTNode right = threeAddrG(tokens);
        op.setRight(right);
        op.setID(this.tlabelID);
        // Used to keep the original value intact for returns
        tempID++;
        currNode = op;
        left = currNode;
        this.tlabelID++;
        this.flabelID++;
        this.rlabelID++;
      }
      // Handle inequality operations
      else if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.NEQ) {
        last = Token.TokenType.NEQ;
        ASTNode op = new ASTNode(ASTNode.NodeType.RELOP);
        op.setVal("!=");
        op.setLeft(left);
        tokens.remove();
        ASTNode right = threeAddrG(tokens);
        op.setRight(right);
        op.setID(this.tlabelID);
        tempID++;
        currNode = op;
        left = currNode;
        this.tlabelID++;
        this.flabelID++;
        this.rlabelID++;
      }
      else {
        break;
      }
    }    

    return currNode;
  }

  public ASTNode threeAddrG(LinkedList<Token> tokens) {
    ASTNode left = threeAddrE(tokens); // Left tempID for operation three address generation
    ASTNode currNode = left; 
    while (true) {
      // Handle less than operations
      if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.LT){
        last = Token.TokenType.LT;
        ASTNode op = new ASTNode(ASTNode.NodeType.RELOP);
        op.setVal("<");
        op.setLeft(left);
        tokens.remove();
        ASTNode right = threeAddrE(tokens);
        op.setRight(right);
        op.setID(this.tlabelID);
        // Used to keep the original value intact for returns
        tempID++;
        currNode = op;
        left = currNode;
        this.tlabelID++;
        this.flabelID++;
        this.rlabelID++;
      }
      // Handle greater than operations
      else if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.GT){
        last = Token.TokenType.GT;
        ASTNode op = new ASTNode(ASTNode.NodeType.RELOP);
        op.setVal(">");
        op.setLeft(left);
        tokens.remove();
        ASTNode right = threeAddrE(tokens);
        op.setRight(right);
        op.setID(this.tlabelID);
        // Used to keep the original value intact for returns
        tempID++;
        currNode = op;
        left = currNode;
        this.tlabelID++;
        this.flabelID++;
        this.rlabelID++;
      }
      // Handle less than or equal to operations
      else if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.LTE){
        last = Token.TokenType.LTE;
        ASTNode op = new ASTNode(ASTNode.NodeType.RELOP);
        op.setVal("<=");
        op.setLeft(left);
        tokens.remove();
        ASTNode right = threeAddrE(tokens);
        op.setRight(right);
        op.setID(this.tlabelID);
        // Used to keep the original value intact for returns
        tempID++;
        currNode = op;
        left = currNode;
        this.tlabelID++;
        this.flabelID++;
        this.rlabelID++;
      }
      // Handle greater than or equal to operations
      else if (tokens.peek() != null && tokens.peek().tokenType == Token.TokenType.GTE) {
        last = Token.TokenType.GTE;
        ASTNode op = new ASTNode(ASTNode.NodeType.RELOP);
        op.setVal(">=");
        op.setLeft(left);
        tokens.remove();
        ASTNode right = threeAddrE(tokens);
        op.setRight(right);
        op.setID(this.tlabelID);
        tempID++;
        currNode = op;
        left = currNode;
        this.tlabelID++;
        this.flabelID++;
        this.rlabelID++;
      }
      else {
        break;
      }
    }
    return currNode;
  }

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
    }
    else {
      // If a factor has reached this point it is not an operation supported by this parser
      System.out.println("ERROR: Expression not supported by grammar");
      System.exit(1);
    } 
    
    return currNode;
  }

  public ASTNode threeAddrId(LinkedList<Token> tokens) {
    ASTNode id = new ASTNode(ASTNode.NodeType.ID);
    
    // Create a node that holds the name of the ID
    if (tokens.peek().tokenType == Token.TokenType.ID) {
      id.setVal(tokens.peek().tokenVal);
      tokens.remove();
    }
    else {
      System.out.println("ERROR: Invalid assignment");
      System.exit(1);
    }
    return id;
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
    this.tempID = 0;
    LinkedList<Token> tokens = scan.extractTokenList(eval);
    
    return postorder(threeAddrProg(tokens), "");
  }

  private String postorder(ASTNode root, String str) {
    if (root == null) {
      return str;
    }

    if (root.getType() == ASTNode.NodeType.WHILE) {
      str += "repeatLabel" + root.getID() + "\n";
    }

    str = postorder(root.getLeft(), str);
    if (root.getType() == ASTNode.NodeType.IF || root.getType() == ASTNode.NodeType.WHILE) {
      if (root.getLeft().getVal().equals("<")) {
        str += "IF_LT: temp" + root.getLeft().getLeft().getID() + ", temp" +
               root.getLeft().getRight().getID() + ", trueLabel" + root.getID() + "\n";
      }
      else if (root.getLeft().getVal().equals(">")) {
        str += "IF_GT: temp" + root.getLeft().getLeft().getID() + ", temp" +
               root.getLeft().getRight().getID() + ", trueLabel" + root.getID() + "\n";
      }
      else if (root.getLeft().getVal().equals("<=")) {
        str += "IF_LTE: temp" + root.getLeft().getLeft().getID() + ", temp" +
               root.getLeft().getRight().getID() + ", trueLabel" + root.getID() + "\n";
      }
      else if (root.getLeft().getVal().equals(">=")) {
        str += "IF_GTE: temp" + root.getLeft().getLeft().getID() + ", temp" +
               root.getLeft().getRight().getID() + ", trueLabel" + root.getID() + "\n";
      }
      else if (root.getLeft().getVal().equals("==")) {
        str += "IF_EQ: temp" + root.getLeft().getLeft().getID() + ", temp" +
               root.getLeft().getRight().getID() + ", trueLabel" + root.getID() + "\n";
      }
      else if (root.getLeft().getVal().equals("!=")) {
        str += "IF_NE: temp" + root.getLeft().getLeft().getID() + ", temp" +
               root.getLeft().getRight().getID() + ", trueLabel" + root.getID() + "\n";
      }
      str += "GOTO: falseLabel" + root.getID() + "\n";
      str += "trueLabel" + root.getID() + "\n";
    }

    str = postorder(root.getRight(), str);
    if (root.getType() == ASTNode.NodeType.OP) {
      str += "temp" + root.getID() + " = temp" + root.getLeft().getID() +
             " " + root.getVal() + " temp" + root.getRight().getID() + "\n";
    }
    else if (root.getType() == ASTNode.NodeType.NUM) {
      str += "temp" + root.getID() + " = " + root.getVal() + "\n";
    }
    else if (root.getType() == ASTNode.NodeType.ASSG) {
      str += root.getLeft().getVal() + " = temp" + root.getRight().getID() + "\n";
    }
    else if (root.getType() == ASTNode.NodeType.IF) {
      str += "falseLabel" + root.getID() + "\n";
    }
    else if (root.getType() == ASTNode.NodeType.WHILE) {
      str += "GOTO: repeatLabel" + root.getID() + "\n";
      str += "falseLabel" + root.getID() + "\n";
    }
    return str;
  }

}
