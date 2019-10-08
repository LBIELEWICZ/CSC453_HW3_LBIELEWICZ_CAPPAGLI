public class ASTNode {
	
	public enum NodeType {
		OP, NUM;
	}
	
	private ASTNode left = null;
	private ASTNode right = null;
	private String val = null;
	private NodeType type;
        private int id;

	public ASTNode(ASTNode.NodeType type) {
		this.type = type;
	}

	public NodeType getType() {
		return this.type;
	}

	public ASTNode getLeft() {
		return left;
	}

	public ASTNode getRight() {
		return right;
	}

	public String getVal() {
		return val;
	}

	public int getID() {
		return id;
	}

	public void setVal(String val) {
		this.val = val;
	}

	public void setLeft(ASTNode node) {
		left = node;
	}

	public void setRight(ASTNode node) {
		right = node;
	}

	public void setID(int id) {
		this.id = id;
	}
}
