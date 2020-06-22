package omok;

import java.io.File;
import java.io.IOException;
import java.awt.*;
import java.awt.Image;
import java.awt.event.*;
import java.awt.geom.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;


/**
 * * <code>오목 게임</code> - 일반룰(오목룰)을 이용하는 오목판 및 게임.
 * https://namu.wiki/w/%EC%98%A4%EB%AA%A9(%EA%B2%8C%EC%9E%84)
 *
 * 사용법: jar 파일 더블 클릭
 * @author 박석원, 양보승, 조강현
 * @version 2.9
 */
public class Omok
{
    /**
     * <code>메인</code> - 오목판을 초기화
     * 판 크기는 기본적으로 15지만 커맨드라인에서 실행할 때 설정할 수 있다.
     *
     * @param args a <code>String[]</code> value - command line
     * arguments
     */
    public static void main(String[] args) {

	int size = 15;
	if (args.length > 0)
	    size = Integer.parseInt(args[0]);

	JFrame frame = new JFrame();
	
	final int FRAME_WIDTH = 600;
	final int FRAME_HEIGHT = 650;
	frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
	frame.setTitle("Omok");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
	
	OmokPanel panel = new OmokPanel(size);
	MenuLine modeMenu = new MenuLine();
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.add(panel);
	frame.setJMenuBar(modeMenu);
	
	frame.setVisible(true);
    }
}

class MenuLine extends JMenuBar implements ActionListener {
	private JMenu gameMenu = new JMenu("Mode");
	private JMenuItem singleMode = new JMenuItem("Single");
	private JMenuItem multiMode = new JMenuItem("Multi");
	private JMenuItem localMode = new JMenuItem("Local");
	private JMenuItem exitGame = new JMenuItem("exit");

	public MenuLine() {
		super();
		initialize();
	}

	private void initialize() {
		singleMode.addActionListener(this);
		multiMode.addActionListener(this);
		localMode.addActionListener(this);
		exitGame.addActionListener(this);
		
		add(gameMenu);
		gameMenu.add(singleMode);
		gameMenu.add(multiMode);
		gameMenu.add(localMode);
		gameMenu.add(exitGame);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == exitGame) System.exit(0);
		else if(e.getSource() == localMode)
		{
			System.out.println("not yet...");
		}
	}

}

class OmokState {
	public static final int NONE = 0;
	public static final int BLACK = 1;
	public static final int WHITE = 2;
	private int size;
	private int winner;
	private int currentPlayer;
	private int board[][];
	private boolean isSwitchOK = true;
	public OmokState(int size) {
		this.size = size;
		board = new int[size][size];
		currentPlayer = BLACK;
	}

	public void playPiece(int row, int col) {

		System.out.println("Try Place at row,column " + row +","+ col+" as Player"+currentPlayer);
		if (validMove(row, col))
			board[row][col] = currentPlayer;
		else{// 여기에 둘수 없다고 명령이 뜨면 -> 변수 하나를 추가 해서 currentPlayer - Switch가 false가 되도록
			JOptionPane.showMessageDialog(null, "여기에 둘 수 없습니다.");
			isSwitchOK = false;
		}
		switch (currentPlayer) {	
		
		case BLACK:
			if (isSwitchOK)
				currentPlayer = WHITE;		// 다음 플레이를 결정하는 명령.
			isSwitchOK= true;
			break;
		case WHITE:
			if (isSwitchOK)
			currentPlayer = BLACK;
			isSwitchOK=true;
			break;
		}
	}
	     ////////
	public int getPiece(int row, int col) {
		return board[row][col];
	}
	
	public int getWinner() {
		return winner;
	}
	
//	public boolean validMove(int row, int col) {
//		// 	validMove가 	false면 여기에 둘 수 없다는 message를 출력한다.
//		// 				true면 진행		
//		final int NORTH = 0, SOUTH =1, EAST = 2, WEST = 3, NORTH_EAST = 4, SOUTH_WEST = 5, NORTH_WEST = 6, SOUTH_EAST = 7;
//		int r = row, c = col;
//		/*
//		 * step
//		 * 수직: 0(북), 1(남)
//		 * 수평: 2(동), 3(서)
//		 * 사선: 4(동북), 5(서남), 6(서북), 7(동남)
//		 */
//		int step = 0;
//		int[] stepCount = new int[8];	// 오목이 성립하는 모든 조건을(8가지) 검사하는 배열
//		boolean doneCheck = false;
//		while (!doneCheck) {
//
//			switch (step) {
//			// if문에서는 step이 살펴볼 방향을 지정하며, r과 c를 수정하면서 순차적으로 살펴보며 놓여진 돌의 갯수를 stepCount의 결과를 낸다.
//			// else문에서는 step을 다음 단계로 지정하며, r와 c를 초기 row값으로 되돌려 놓는다.
//			// 예시. 흑돌이 놓은 자리 위에 흑돌이 셋, 아래에 흑돌이 하나 있으면 case0는 탐색 세번 = 초기화 한번, case1은 탐색 1번 초기화 
//			//													되어야 하는데.... if문 아래로 전혀 들어가지 않는다.
//			case NORTH:
//				if (!outOfBounds(r-1) && sameColor(--r, c))	// 0 0 0 0 / 1 2 3 4 5 6 6/ 7 
//					stepCount[step]++;						// 
//				else { step++; r = row; c = col; }			// 
//				break;
//			case SOUTH:
//				if (!outOfBounds(r+1) && sameColor(++r, c))
//					stepCount[step]++;
//				else { step++; r = row; c = col; }
//				break;
//			case EAST:
//				if (!outOfBounds(c+1) && sameColor(r, ++c))
//					stepCount[step]++;
//				else { step++; r = row; c = col; }
//				break;
//			case WEST:
//				if (!outOfBounds(c-1) && sameColor(r, --c))
//					stepCount[step]++;
//				else { step++; r = row; c = col; }
//				break;
//			case NORTH_EAST:
//				if (!outOfBounds(r-1) && !outOfBounds(c+1) && sameColor(--r, ++c))
//					stepCount[step]++;
//				else { step++; r = row; c = col; }
//				break;
//			case SOUTH_WEST:
//				if (!outOfBounds(r+1) && !outOfBounds(c-1) && sameColor(++r, --c))
//					stepCount[step]++;
//				else { step++; r = row; c = col; }
//				break;
//			case NORTH_WEST:
//				if (!outOfBounds(r-1) && !outOfBounds(c-1) && sameColor(--r, --c))
//					stepCount[step]++;
//				else { step++; r = row; c = col; }
//				break;
//			case SOUTH_EAST:
//				if (!outOfBounds(r+1) && !outOfBounds(c+1) && sameColor(++r, ++c))
//					stepCount[step]++;
//				else { step++; r = row; c = col; }
//				break;
//			default:
//				doneCheck = true;
//				break;
//			}
//		}
//		// moveResult는 승자를 결정하면 0을 return, 결정되지 않았다면 1과 2를 return
//		// 1과 2는? 2는 육목일 경우. 1은 ???
//		int result = moveResult(stepCount);
//		
//		if (result == 0) winner = currentPlayer;
//		
//		if (result == 1 || result == 2) {
//			if(currentPlayer == WHITE) {
////				winner = currentPlayer;
//				return true;
//			}
//			return false;
//			
//		}
//		return true;
//	}
	
	
	
	public boolean validMove(int row, int col) {
		// 	validMove가 	false면 여기에 둘 수 없다는 message를 출력한다.
		// 				true면 진행		
		final int NORTH = 0, SOUTH =1, EAST = 2, WEST = 3, NORTH_EAST = 4, SOUTH_WEST = 5, NORTH_WEST = 6, SOUTH_EAST = 7;
		int r = row, c = col;
		/*
		 * step
		 * 수직: 0(북), 1(남)
		 * 수평: 2(동), 3(서)
		 * 사선: 4(동북), 5(서남), 6(서북), 7(동남)
		 */
		int step = 0;
		int[] stepCount = new int[8];	// 오목이 성립하는 모든 조건을(8가지) 검사하는 배열
		boolean [] opponentAtEnd = new boolean [8];
		boolean [] skip = new boolean[8];
		boolean doneCheck = false;
		while (!doneCheck) {			// while(step<8)
			final int boundsCheckMax = 4;
			int boundsCheck=0;
			switch (step) {
			// NORTH ~ SOUTH_EAST의 경우로 각각 몇번을 가는지 테스팅 하는 케이스.
			case NORTH:
				if (!outOfBounds(r-1) && sameColor(--r, c))
					stepCount[step]++;				
				else {
					// 진행이 끊겼을 때
					if(differentColor(r,c)) { // 상대가 막아서 끊겼니? 
					opponentAtEnd[step] = true;
					}
					if(empty(r,c)) { // 그냥 비어서 끊겼니?
						if (r == row -1) {// 그것도 바로 다음에 끊겼니?
							if(skip[step] == false) {
								skip[step] = true;	// 맞냐?
								continue;	
								
							}
						}
					}	
					step++; r = row; c = col; // in else: toTheNextStep - set r and c as first state
					}			
				break;
			case SOUTH:
				if (!outOfBounds(r+1) && sameColor(++r, c))
					stepCount[step]++;
				else {
					if(differentColor(r,c)) {
					opponentAtEnd[step] = true;
					}
					if(empty(r,c)) {
						if (r == row +1) {
							if(skip[step] == false) {
								skip[step] = true;	// 맞냐?
								continue;	
								
							}
						}
							
					}
					 step++; r = row; c = col; }
				break;
			case EAST:
				if (!outOfBounds(c+1) && sameColor(r, ++c))
					stepCount[step]++;
				else {
					if(differentColor(r,c)) {
					opponentAtEnd[step] = true;
					}
					if(empty(r,c)) {
						if (c == col+1){
							if(skip[step] == false) {
								skip[step] = true;	// 맞냐?
								continue;
							}
						}
					}
					 step++; r = row; c = col; }
				break;
			case WEST:
				if (!outOfBounds(c-1) && sameColor(r, --c))
					stepCount[step]++;
				else {
					if(differentColor(r,c)) {
					opponentAtEnd[step] = true;
					}
					if(empty(r,c)) {
						if (c == col-1){
							if(skip[step] == false) {
								skip[step] = true;	// 맞냐?
								continue;
							}
						}
					}
					 step++; r = row; c = col; }
				break;
			case NORTH_EAST:
				if (!outOfBounds(r-1) && !outOfBounds(c+1) && sameColor(--r, ++c))
					stepCount[step]++;
				else {
					if(differentColor(r,c)) {
					opponentAtEnd[step] = true;
					}
					if(empty(r,c)) {
						if (c == col+1 && r == row-1){
							if(skip[step] == false) {
								skip[step] = true;	// 맞냐?
								continue;
							}
						}
					}
					 step++; r = row; c = col; }
				break;
			case SOUTH_WEST:
				if (!outOfBounds(r+1) && !outOfBounds(c-1) && sameColor(++r, --c))
					stepCount[step]++;
				else { 
					if(differentColor(r,c)) {
					opponentAtEnd[step] = true;
					}
					if(empty(r,c)) {
						if (c == col-1 && r == row+1) {
							if(skip[step] == false) {
								skip[step] = true;	// 맞냐?
								continue;
							}
						}
					}
					step++; r = row; c = col; }
				break;
			case NORTH_WEST:
				if (!outOfBounds(r-1) && !outOfBounds(c-1) && sameColor(--r, --c))
					stepCount[step]++;
				else { 
					if(differentColor(r,c)) {
					opponentAtEnd[step] = true;
					}
					if(empty(r,c)) {
						if (c == col-1 && r == row-1) {
							if(skip[step] == false) {
								skip[step] = true;	// 맞냐?
								continue;
							}
						}
					}
					step++; r = row; c = col;
					}
				break;
			case SOUTH_EAST:
				if (!outOfBounds(r+1) && !outOfBounds(c+1) && sameColor(++r, ++c))
					stepCount[step]++;
				else  { 
					if(differentColor(r,c)) {
					opponentAtEnd[step] = true;
					}
					if(empty(r,c)) {
						if (c == col+1 && r == row+1) {
							if(skip[step] == false) {
								skip[step] = true;	// 맞냐?
								continue;
							}
						}
					}
					step++; r = row; c = col;
					}
				break;
			default:
				doneCheck = true;
				break;
			}
		}
		// moveResult는 승자를 결정하면 0을 return, 결정되지 않았다면 1과 2를 return
		// 1과 2는? 2는 육목일 경우. 1은 ???
		int result = moveResult_FIX(stepCount,opponentAtEnd,skip);
		
		if (result == 0) winner = currentPlayer;
		
		if (result == 1 || result == 2) {
			if(currentPlayer == WHITE) {
//				winner = currentPlayer;
				return true;
			}
			return false;
			
		}
		return true;
	}
	
	public int moveResult_FIX(int[] stepCount,boolean enemyAtEnd[], boolean skip[]) {	// return값이 1,2면 false, 0이면 승자 결정.
		
		// 금수는 총 네개로,  oxo, xoo, oxoo, x_oo가 있다.
		// 이 때, 금수의 끝이 막혔는지는 enemyAtEnd가 false일 때 뚫렸음을 의미한다.
		// skip은 네번째 금수를 위한 boolean형 변수로, x_oo의 _부분을 의미한다.
		int [] forbiddenCases = new int [4];
		// 마지막에 계산할 때, forbiddenCases의 초
		
		for (int i=0; i<8; i++) {
			// 
			if (i % 2 == 1 && (stepCount[i-1]  == 1 && stepCount[i] == 1)) // 첫번째 금수.
			{
				if(!enemyAtEnd[i-1]&&!enemyAtEnd[i]&&!skip[i-1]&&!skip[i]) // 양끝이 막혀있거나 빈칸을 허용한 상태면 안된다.
					forbiddenCases[0]++;
			}

			if (i % 2 == 1 && (stepCount[i-1]  == 0 && stepCount[i] == 2)) // 두번째 금수.
			{
				if(!enemyAtEnd[i-1]&&!enemyAtEnd[i]&&skip[i-1]&&!skip[i]) // 양끝이 막혀있거나 빈칸을 허용한 상태면 안된다.
					forbiddenCases[1]++;
			}
			if (i % 2 == 1 && (stepCount[i-1]  == 2 && stepCount[i] == 0)) // 두번째 금수.
			{
				if(!enemyAtEnd[i-1]&&!enemyAtEnd[i]&&!skip[i-1]&&skip[i]) // 양끝이 막혀있거나 빈칸을 허용한 상태면 안된다.
					forbiddenCases[1]++;
			}
			
			if (i % 2 == 1 && (stepCount[i-1]  == 1 && stepCount[i] == 2)) // 세번째 금수.
			{
				if(!enemyAtEnd[i-1]&&!enemyAtEnd[i]&&!skip[i-1]&&!skip[i]) // 양끝이 막혀있거나 빈칸을 허용한 상태면 안된다.
					forbiddenCases[2]++;
			}
			if (i % 2 == 1 && (stepCount[i-1]  == 2 && stepCount[i] == 1)) // 세번째 금수.
			{
				if(!enemyAtEnd[i-1]&&!enemyAtEnd[i]&&!skip[i-1]&&!skip[i]) // 양끝이 막혀있거나 빈칸을 허용한 상태면 안된다.
					forbiddenCases[2]++;
			}

			if (i % 2 == 1 && (stepCount[i-1]  == 0 && stepCount[i] == 2)) // 네번째 금수.
			{
				if(!enemyAtEnd[i-1]&&!enemyAtEnd[i]&&skip[i-1]&&skip[i]) // 이번에는 빈칸을 허용해도 된다. 단, 2쪽인 쪽에.
					forbiddenCases[3]++;
			}
			if (i % 2 == 1 && (stepCount[i-1]  == 2 && stepCount[i] == 0)) // 네번째 금수.
			{
				if(!enemyAtEnd[i-1]&&!enemyAtEnd[i]&&skip[i-1]&&skip[i]) // 이번에는 빈칸을 허용해도 된다. 단, 2쪽인 쪽에.
					forbiddenCases[3]++;
			}
			
			// ! 아직은 서로 맞닿는 금수의 경우를 판단하지 못하는 코드. 서로 직교하는 식으로 만날때 제대로 동작하는지 확인 할 예정. --- 한쪽이 끝나는 case 2와 case4를 망하는 부분.
		}
		int caseSum=0;
		for(int i=0;i<4;i++) {
			caseSum+=forbiddenCases[i];
		}

		if(caseSum>1) {
			return 1;
		}
		if(moveResultWin(stepCount,skip) == 0) {
			winner = currentPlayer;
		}
		return 3;
	}
	
	public boolean sameColor(int r, int c) {
		// 함수 내부 이상 무
		return board[r][c] == currentPlayer;
	}
	
	public boolean empty(int r,int c) {
		return board[r][c] == 0;
	}
	public boolean differentColor(int r, int c) {
		// 함수 내부 이상 무
		if(currentPlayer == BLACK)
			return board[r][c] == WHITE;
		else if(currentPlayer == WHITE)
			return board[r][c] == BLACK;
		return false;
	}
	
	
	// switch case문에서 stepCount[]를 더해주기 위한 조건 함수 2개
	public boolean outOfBounds(int n) {
		// 함수 내부 이상 무
		return !(n >= 0 && n < size);
	}

	
	
	/*
	 * 이기는 수(5): 0
	 * 금수(33 혹은 44): 1
	 * 장목(6이상): 2
	 * 수: 3
	 */
	public int moveResultWin(int[] stepCount,boolean skip[]) {	// return값이 1,2면 false, 0이면 승자 결정.

		boolean win = false;
		for (int i=0; i<8; i++) {
				if (i % 2 == 1 && (stepCount[i-1] + stepCount[i] >= 5-1))
					//if(skip[i] == false && skip[i-1] == false)			// 북 + 남 = 5, 동 + 서 = 5, 
					win = true;
		}
		
		if (win)
			return 0;
		return 3;
	}
	
	
}
class OmokPanel extends JPanel
{
    private final int MARGIN = 5;
    private final double PIECE_FRAC = 0.9;

    private int size = 19;
    private OmokState state;
    
    private AudioInputStream dropSound = null;
    
	
	private Image stoneBlack = null;
	private Image stoneWhite = null;
    private Clip clip = null;
    
    
    public OmokPanel() 
    {
	this(15);
    }

    public OmokPanel(int size) 
    {
	super();
	this.size = size;
	state = new OmokState(size);
	addMouseListener(new GomokuListener());
	
	try {
		File URLOfImage1 = new File("image/500px-Go_b_no_bg.svg.png");
		File URLOfImage2 = new File("image/500px-Go_w_no_bg.svg.png");
		
		
		
		if (!URLOfImage1.canRead() && !URLOfImage2.canRead())
		{
			System.out.println("File doesn't exist!!\n");
		}
		else
		{
			System.out.println("File exist!!\n");
		}
		
		stoneBlack = ImageIO.read(URLOfImage1);
		stoneWhite = ImageIO.read(URLOfImage2);
		
	}catch(Exception e) 
	{
		System.out.println("error!!\n");
		System.out.println(e.getMessage());
	}
    }

    class GomokuListener extends MouseAdapter 
    {
	public void mouseReleased(MouseEvent e) 
	{
	    double panelWidth = getWidth();
	    double panelHeight = getHeight();
	    double boardWidth = Math.min(panelWidth, panelHeight) - 2 * MARGIN;
	    double squareWidth = boardWidth / size;
	    double pieceDiameter = PIECE_FRAC * squareWidth;
	    double xLeft = (panelWidth - boardWidth) / 2 + MARGIN;
	    double yTop = (panelHeight - boardWidth) / 2 + MARGIN;
	    int col = (int) Math.round((e.getX() - xLeft) / squareWidth - 0.5);
	    int row = (int) Math.round((e.getY() - yTop) / squareWidth - 0.5);
	    if (row >= 0 && row < size && col >= 0 && col < size
		&& state.getPiece(row, col) == OmokState.NONE
		&& state.getWinner() == OmokState.NONE) {
		state.playPiece(row, col);
		repaint();
		int winner = state.getWinner();
		if (winner != OmokState.NONE)
		    JOptionPane.showMessageDialog(null,
                      (winner == OmokState.BLACK) ? "Black wins!" 
						    : "White wins!");
		
		try {
			File URLOfSound1 = new File("sound/350343__nettimato__tap-stone.wav");
			
			dropSound = AudioSystem.getAudioInputStream(URLOfSound1);
			clip = AudioSystem.getClip();
		    clip.open(dropSound);
		    clip.start();
		    }catch(Exception a){}
	    }
	}    
    }
    public void paintComponent(Graphics g) 
    {
	Graphics2D g2 = (Graphics2D) g;
	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			    RenderingHints.VALUE_ANTIALIAS_ON);
	
	double panelWidth = getWidth();
	double panelHeight = getHeight();

	g2.setColor(new Color(0.925f, 0.670f, 0.34f)); // ������
	g2.fill(new Rectangle2D.Double(0, 0, panelWidth, panelHeight));

	
	double boardWidth = Math.min(panelWidth, panelHeight) - 2 * MARGIN;
	double squareWidth = boardWidth / size;
	double gridWidth = (size - 1) * squareWidth;
	double pieceDiameter = PIECE_FRAC * squareWidth;
	boardWidth -= pieceDiameter;
	double xLeft = (panelWidth - boardWidth) / 2 + MARGIN;
	double yTop = (panelHeight - boardWidth) / 2 + MARGIN;
	
	stoneBlack = stoneBlack.getScaledInstance
			((int)pieceDiameter, (int)pieceDiameter, stoneBlack.SCALE_DEFAULT);

	stoneWhite = stoneWhite.getScaledInstance
			((int)pieceDiameter, (int)pieceDiameter, stoneWhite.SCALE_DEFAULT);
	
	

	g2.setColor(Color.BLACK);
	for (int i = 0; i < size; i++) {
	    double offset = i * squareWidth;
	    g2.draw(new Line2D.Double(xLeft, yTop + offset, 
				      xLeft + gridWidth, yTop + offset));
	    g2.draw(new Line2D.Double(xLeft + offset, yTop,
				      xLeft + offset, yTop + gridWidth));
	}
	
	for (int row = 0; row < size; row++) 
	    for (int col = 0; col < size; col++) {
		int piece = state.getPiece(row, col);
		if (piece != OmokState.NONE) {
		   Color c = (piece == OmokState.BLACK) ? Color.BLACK : Color.WHITE;
		    g2.setColor(c);
		    double xCenter = xLeft + col * squareWidth;
		    double yCenter = yTop + row * squareWidth;
		    
		    if(piece == OmokState.BLACK)
		    	g2.drawImage(stoneBlack, (int)(xCenter - pieceDiameter / 2), (int)(yCenter - pieceDiameter / 2), null);
		    else
		    	g2.drawImage(stoneWhite, (int)(xCenter - pieceDiameter / 2), (int)(yCenter - pieceDiameter / 2), null);
		}
	    }
    }
}