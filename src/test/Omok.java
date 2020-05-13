package test;

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
 * <code>���� ����</code> - �Ϲݷ�(�����)�� �̿��ϴ� ������ �� ����.
 * https://namu.wiki/w/%EC%98%A4%EB%AA%A9(%EA%B2%8C%EC%9E%84)
 *
 * ����: Ŀ�ǵ���ο��� java Omok [<�� ũ��>]
 * @author ����
 * @version 1.0
 */
public class Omok
{
    /**
     * <code>����</code> - �������� �ʱ�ȭ
     * �� ũ��� �⺻������ 15���� Ŀ�ǵ���ο��� ������ �� ������ �� �ִ�.
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
	public OmokState(int size) {
		this.size = size;
		board = new int[size][size];
		currentPlayer = BLACK;
	}
	
	public void playPiece(int row, int col) {
		if (validMove(row, col))
			board[row][col] = currentPlayer;
		else
			JOptionPane.showMessageDialog(null, "���⿡ �� �� �����ϴ�.");
		
		switch (currentPlayer) {
		case BLACK:
			currentPlayer = WHITE;
			break;
		case WHITE:
			currentPlayer = BLACK;
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
		int r = row, c = col;
		/*
		 * step
		 * ����: 0(��), 1(��)
		 * ����: 2(��), 3(��)
		 * �缱: 4(����), 5(����), 6(����), 7(����)
		 */
		int step = 0;
		int[] stepCount = new int[8];
		boolean doneCheck = false;
		while (!doneCheck) {
			switch (step) {
			case 0:
				if (!outOfBounds(r-1) && sameColor(--r, c))
					stepCount[step]++;
				else { step++; r = row; c = col; }
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
		int result = moveResult(stepCount);
		if (result == 0) winner = currentPlayer;
		if (result == 1 || result == 2) return false;
		return true;
	}
	
	public boolean outOfBounds(int n) {
		return !(n >= 0 && n < size);
	}
	
	public boolean sameColor(int r, int c) {
		return board[r][c] == currentPlayer;
	}
	
	/*
	 * �̱�� ��(5): 0
	 * �ݼ�(33 Ȥ�� 44): 1
	 * ���(6�̻�): 2
	 * ��: 3
	 */
	public int moveResult(int[] stepCount) {
		int countTwo = 0, countThree = 0;
		boolean win = false;
		for (int i=0; i<8; i++) {
			if (i % 2 == 1 && (stepCount[i-1] + stepCount[i] > 5)) return 2;
			else if (i % 2 == 1 && (stepCount[i-1] + stepCount[i] == 5)) win = true;
			if (stepCount[i] == 2) countTwo++;
			else if (stepCount[i] == 3) countThree++;
		}
		if (countTwo >= 2 || countThree >= 2) return 1;
		if (win) return 0;
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