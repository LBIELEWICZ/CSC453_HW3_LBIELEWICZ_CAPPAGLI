public class SimpleJavaTest{

  public static void TestThreeAddrGen(){
    System.out.println("*******************************************");
    System.out.println("Testing Three Address Generation");
    SimpleJava parser = new SimpleJava();

    String eval = "void blarg() {}";
    String result = "";
    assert(parser.getThreeAddr(eval).equals(result));

    eval = "void main() {int x = 3;}";
    result = "temp0 = 3\nx = temp0\n";
    assert(parser.getThreeAddr(eval).equals(result));

    eval = "void main() { int x = 12; if(3 < 6) { int y = 3; } }";
    result = "temp0 = 12\n"+
             "x = temp0\n"+
             "temp0 = 3\n"+
             "temp1 = 6\n"+
             "IF_LT: temp0, temp1, trueLabel0\n"+
             "GOTO: falseLabel0\n"+
             "trueLabel0\n"+
             "temp0 = 3\n"+
             "y = temp0\n"+
             "falseLabel0\n";
    assert(parser.getThreeAddr(eval).equals(result));

    eval = "void main() { int x = 3; while(3 < 2) { int x = 4;} }";
    result = "temp0 = 3\n"+
             "x = temp0\n"+
             "repeatLabel0\n"+
             "temp0 = 3\n"+
             "temp1 = 2\n"+
             "IF_LT: temp0, temp1, trueLabel0\n"+
             "GOTO: falseLabel0\n"+
             "trueLabel0\n"+
             "temp0 = 4\n"+
             "x = temp0\n"+
             "GOTO: repeatLabel0\n"+
             "falseLabel0\n";
    assert(parser.getThreeAddr(eval).equals(result));

    System.out.println("Congrats: three address generation tests passed! Now make your own test cases "+
                       "(this is only a subset of what we will test your code on)");
    System.out.println("*******************************************");
  }

  public static void MoreTestThreeAddrGen(){
    System.out.println("*******************************************");
    System.out.println("More Testing Three Address Generation");
    SimpleJava parser = new SimpleJava();

     // Test from piazza
    eval = "main(){int x = 3; int y = 3 + x;}";
    result = "temp0 = 3\n"+
             "x = temp0\n"+
             "temp0 = 3\n"+
             "temp1 = temp0 + x\n"+
             "y = temp1\n";
    assert(parser.getThreeAddr(eval).equals(result));

    // Another test from piazza
    eval = "main(){int x = 3; int y = x;}";
    result = "temp0 = 3\n"+
             "x = temp0\n"+
             "y = x\n";
    assert(parser.getThreeAddr(eval).equals(result));

    //example from spec
    String eval = "void blarg() {\n" +
                    "\tif(2 + 3 - 2 < 2){\n" +
                    "\t\tint x = 9+(2*2);\n" +
                    "\t}\n" +
                    "}\n";
    String result = "temp0 = 2\n" +
                    "temp1 = 3\n" +
                    "temp2 = temp0 + temp1\n" +
                    "temp3 = 2\n" +
                    "temp4 = temp2 - temp3\n" +
                    "temp5 = 2\n" +
                    "IF_LT: temp4, temp5, trueLabel0\n" +
                    "GOTO: falseLabel0\n" +
                    "trueLabel0\n" +
                    "temp0 = 9\n" +
                    "temp1 = 2\n" +
                    "temp2 = 2\n" +
                    "temp3 = temp1 * temp2\n" +
                    "temp4 = temp0 + temp3\n" +
                    "x = temp4\n" +
                    "falseLabel0\n";
    assert(parser.getThreeAddr(eval).equals(result));

    // Testing grater than or equal to
    eval = "void main() { int x = 3; while(3 >= 2) { int x = 4;} }";
    result = "temp0 = 3\n"+
             "x = temp0\n"+
             "repeatLabel0\n"+
             "temp0 = 3\n"+
             "temp1 = 2\n"+
             "IF_GTE: temp0, temp1, trueLabel0\n"+
             "GOTO: falseLabel0\n"+
             "trueLabel0\n"+
             "temp0 = 4\n"+
             "x = temp0\n"+
             "GOTO: repeatLabel0\n"+
             "falseLabel0\n";
    assert(parser.getThreeAddr(eval).equals(result));

    // Testing equals and unequals
    eval = "void main() { while(3 == 2) { int x = 4;} while(3 != 2) { int x = 7;} }";
    result = "repeatLabel0\n"+
             "temp0 = 3\n"+
             "temp1 = 2\n"+
             "IF_EQ: temp0, temp1, trueLabel0\n"+
             "GOTO: falseLabel0\n"+
             "trueLabel0\n"+
             "temp0 = 4\n"+
             "x = temp0\n"+
             "GOTO: repeatLabel0\n"+
             "falseLabel0\n"+
             "repeatLabel1\n"+
             "temp0 = 3\n"+
             "temp1 = 2\n"+
             "IF_NQ: temp0, temp1, trueLabel1\n"+
             "GOTO: falseLabel1\n"+
             "trueLabel1\n"+
             "temp0 = 4\n"+
             "x = temp0\n"+
             "GOTO: repeatLabel1\n"+
             "falseLabel1\n";
    assert(parser.getThreeAddr(eval).equals(result));

    // Testing independice of lables
    eval = "void main() { while(3 <= 2) { int x = 4;} if(3 > 6) { int y = 3; } while(3 != 2) { int x = 7;} }";
    result = "repeatLabel0\n"+
             "temp0 = 3\n"+
             "temp1 = 2\n"+
             "IF_LTE: temp0, temp1, trueLabel0\n"+
             "GOTO: falseLabel0\n"+
             "trueLabel0\n"+
             "temp0 = 4\n"+
             "x = temp0\n"+
             "GOTO: repeatLabel0\n"+
             "falseLabel0\n"+
             "temp0 = 3\n"+
             "temp1 = 6\n"+
             "IF_GT: temp0, temp1, trueLabel1\n"+
             "GOTO: falseLabel1\n"+
             "trueLabel1\n"+
             "temp0 = 3\n"+
             "y = temp0\n"+
             "falseLabel1\n";//
             "repeatLabel1\n"+
             "temp0 = 3\n"+
             "temp1 = 2\n"+
             "IF_NQ: temp0, temp1, trueLabel2\n"+
             "GOTO: falseLabel2\n"+
             "trueLabel2\n"+
             "temp0 = 4\n"+
             "x = temp0\n"+
             "GOTO: repeatLabel1\n"+
             "falseLabel2\n";
    assert(parser.getThreeAddr(eval).equals(result));

    System.out.println("Congrats: Extra three address generation tests passed!");
    System.out.println("*******************************************");
  }

  public static void main(String[] args){
    TestThreeAddrGen();
    MoreTestThreeAddrGen();
  }

}
