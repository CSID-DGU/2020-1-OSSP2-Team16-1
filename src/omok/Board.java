package omok;

import java.util.ArrayList;

public class Board {
	public final static int SIZE = 15;
	private ArrayList<String> moveList;
	private Move[][] board;
	
	public Board() {
		moveList = new ArrayList<String>();
		board = new Move[SIZE][SIZE];
	}
	
	public boolean update(Move move) {
		if (!moveList.contains(move.getPos())) {
			board[move.getX()][move.getY()] = move;
			return true;
		}
		return false;
	}
	
	public String toString() {
		StringBuilder strBoard = new StringBuilder();
		for (int r=0; r<SIZE; r++) {
			for (int c=0; c<SIZE; c++) {
				if (board[r][c] == null) {
					strBoard.append('[' + Move.rowToPos(r));
					if (c < 9) strBoard.append(0);
					strBoard.append(Move.colToPos(c) + "] ");
				} else {
					strBoard.append("[ " + board[r][c].getStone() + " ] ");
				}
			}
			strBoard.append("\n");
		}
		return strBoard.toString();
	}
	
}
