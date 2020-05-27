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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * <code>오목 게임</code> - 일반룰(오목룰)을 이용하는 오목판 및 게임.
 * https://namu.wiki/w/%EC%98%A4%EB%AA%A9(%EA%B2%8C%EC%9E%84)
 *
 * 사용법: 커맨드라인에서 java Omok [<판 크기>]
 * @author 꿀쥐
 * @version 1.0
 */
public class Omok
{
	/**
     * <code>메인</code> - 오목판을 초기화
     * 판 크기는 기본적으로 15지만 커맨드라인에서 실행할 때 설정할 수 있다.
     *
     * @param args a <code>String[]</code> value - command line
     * arguments
     * 
     */
	
	private BufferedReader reader;                         // 입력 스트림
	private PrintWriter writer;                               // 출력 스트림

	private Socket socket;    
	static int size = 15;
	static OmokPanel_mul panel = new OmokPanel_mul(size);
	private OmokState_mul state = new OmokState_mul(15);
	static Label infoView=new Label("대기 중...", 1);
		
    
    
    
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
		else{// ���⿡ �Ѽ� ���ٰ� ����� �߸� -> ���� �ϳ��� �߰� �ؼ� currentPlayer - Switch�� false�� �ǵ���
			JOptionPane.showMessageDialog(null, "���⿡ �� �� �����ϴ�.");
			isSwitchOK = false;
		}
		switch (currentPlayer) {	
		
		case BLACK:
			if (isSwitchOK)
			currentPlayer = WHITE;		// ���� �÷��̸� �����ϴ� ���.
			isSwitchOK= true;
			break;
		case WHITE:
			if (isSwitchOK)
			currentPlayer = BLACK;
			isSwitchOK=true;
			break;
		}
	}
	
	public int getPiece(int row, int col) {
		return board[row][col];
	}
	
	public int getWinner() {
		return winner;
	}
	
	public boolean validMove(int row, int col) {
		// 	validMove�� 	false�� ���⿡ �� �� ���ٴ� message�� ����Ѵ�.
		// 				true�� ����		
		int r = row, c = col;
		/*
		 * step
		 * ����: 0(��), 1(��)
		 * ����: 2(��), 3(��)
		 * �缱: 4(����), 5(����), 6(����), 7(����)
		 */
		int step = 0;
		int[] stepCount = new int[8];	// ������ �����ϴ� ��� ������(8����) �˻��ϴ� �迭
		boolean doneCheck = false;
		while (!doneCheck) {

			switch (step) {
			// if�������� step�� ���캼 ������ �����ϸ�, r�� c�� �����ϸ鼭 ���������� ���캸�� ������ ���� ������ stepCount�� ����� ����.
			// else�������� step�� ���� �ܰ�� �����ϸ�, r�� c�� �ʱ� row������ �ǵ��� ���´�.
			// ����. �浹�� ���� �ڸ� ���� �浹�� ��, �Ʒ��� �浹�� �ϳ� ������ case0�� Ž�� ���� = �ʱ�ȭ �ѹ�, case1�� Ž�� 1�� �ʱ�ȭ 
			//													�Ǿ�� �ϴµ�.... if�� �Ʒ��� ���� ���� �ʴ´�.
			case 0:
				if (!outOfBounds(r-1) && sameColor(--r, c))	//���� �� r--? ������ �� ���ű� ������ --- case 0�� ������ ȣ��ȴ�.
					stepCount[step]++;						// if�� ������ ���� �ʱ� ������ stepCount�� ���� ���� �ʴ´� - ���ǹ��� ���� Ȯ��.
				else { step++; r = row; c = col; }			// ���� �ذ�: �� ���ǹ� �Լ����� �̻�X. �μ��� ���캽 --- �̻� �߰�
				break;
			case 1:
				if (!outOfBounds(r+1) && sameColor(++r, c))
					stepCount[step]++;
				else { step++; r = row; c = col; }
				break;
			case 2:
				if (!outOfBounds(c+1) && sameColor(r, ++c))
					stepCount[step]++;
				else { step++; r = row; c = col; }
				break;
			case 3:
				if (!outOfBounds(c-1) && sameColor(r, --c))
					stepCount[step]++;
				else { step++; r = row; c = col; }
				break;
			case 4:
				if (!outOfBounds(r-1) && !outOfBounds(c+1) && sameColor(--r, ++c))
					stepCount[step]++;
				else { step++; r = row; c = col; }
				break;
			case 5:
				if (!outOfBounds(r+1) && !outOfBounds(c-1) && sameColor(++r, --c))
					stepCount[step]++;
				else { step++; r = row; c = col; }
				break;
			case 6:
				if (!outOfBounds(r-1) && !outOfBounds(c-1) && sameColor(--r, --c))
					stepCount[step]++;
				else { step++; r = row; c = col; }
				break;
			case 7:
				if (!outOfBounds(r+1) && !outOfBounds(c+1) && sameColor(++r, ++c))
					stepCount[step]++;
				else { step++; r = row; c = col; }
				break;
			default:
				doneCheck = true;
				break;
			}
		}
		// moveResult�� ���ڸ� �����ϸ� 0�� return, �������� �ʾҴٸ� 1�� 2�� return
		// 1�� 2��? 2�� ������ ���. 1�� ???
		int result = moveResult(stepCount);
		
		if (result == 0) winner = currentPlayer;
		
		if (result == 1 || result == 2)
			return false;
		
		return true;
	}
	
	// switch case������ stepCount[]�� �����ֱ� ���� ���� �Լ� 2��
	public boolean outOfBounds(int n) {
		// �Լ� ���� �̻� ��
		return !(n >= 0 && n < size);
	}
	
	public boolean sameColor(int r, int c) {
		// �Լ� ���� �̻� ��
		return board[r][c] == currentPlayer;
	}
	
	
	/*
	 * �̱�� ��(5): 0
	 * �ݼ�(33 Ȥ�� 44): 1
	 * ���(6�̻�): 2
	 * ��: 3
	 */
	public int moveResult(int[] stepCount) {	// return���� 1,2�� false, 0�̸� ���� ����.
		final int checkBugOn33 = 0;
		final int checkBugOn44 = 1;
		int countTwo = 0, countThree = 0;
		boolean win = false;
		for (int i=0; i<8; i++) {
			// 1. moveResult 2 Ȥ�� 0�� �����ϴ� if-else
			if (i % 2 == 1 && (stepCount[i-1] + stepCount[i] > 5-1)) 
								// sc[0] + sc[1] > 5, sc[3] + sc[4] > 6 .... 
								// -> ��+���� 6,7,8,... ��+���� 6,7,8... �̷���� = 6�� �϶� 
				return 2;		// 6��, 7�� 
			else 
				if (i % 2 == 1 && (stepCount[i-1] + stepCount[i] == 5-1))
								// �� + �� = 5, �� + �� = 5, 
					win = true;
			
			// moveresult 1�� �����ϴ� if-else
			if (stepCount[i] == 2-1) 
				countTwo++;
			else
				if (stepCount[i] == 3-1) 
					countThree++;
		}
		
		// �Ʒ��� return���� �����ϴ� if����
		if (countTwo >= 2-checkBugOn33 || countThree >= 2-checkBugOn44)
			return 1;
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
		File URLOfImage1 = new File("image\\500px-Go_b_no_bg.svg.png");
		File URLOfImage2 = new File("image\\500px-Go_W_no_bg.svg.png");
		
		
		
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
			File URLOfSound1 = new File("sound\\350343__nettimato__tap-stone.wav");
			
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