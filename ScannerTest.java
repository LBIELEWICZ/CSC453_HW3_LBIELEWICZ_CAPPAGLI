public class ScannerTest {

	public static void main(String [] args) {
		Scanner scan = new Scanner();
		
		String result = scan.extractTokens("int x = 25;");
		String expected = "|INT: int||ID: x||ASSG: =||NUM: 25||END: ;|";
		assert(result.equals(expected));

		result = scan.extractTokens("void main() {                   }");
		expected = "|VOID: void||ID: main||OP: (||CP: )||OB: {||CB: }|";
		assert(result.equals(expected));

		result = scan.extractTokens("while (i <= 100) { i=i+1; }");
		expected = "|WHILE: while||OP: (||ID: i||LTE: <=||NUM: 100||CP: )|" +
			   "|OB: {||ID: i||ASSG: =||ID: i||PLUS: +||NUM: 1||END: ;||CB: }|";
		assert(result.equals(expected));
	}
}
