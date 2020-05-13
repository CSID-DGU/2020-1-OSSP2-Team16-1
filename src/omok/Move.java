package omok;

public class Move {
	private final int SIZE = Board.SIZE;
	private final char stone;
	private final String pos;
	
	public Move(char stone, String pos) {
		this.stone = stone;
		this.pos = pos;
	}
	
	public int getX() {
		return SIZE-(1+(int)(pos.charAt(0))-97);
	}
	
	public int getY() {
		return Integer.parseInt(pos.substring(1))-1;
	}
	
	public char getStone() {
		return stone;
	}
	
	public String getPos() {
		return pos;
	}
	
	public static char rowToPos(int row) {
		return (char)(Board.SIZE-row+97-1);
	}
	
	public static int colToPos(int col) {
		return col+1;
	}
}