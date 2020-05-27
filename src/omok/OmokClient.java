package omok;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Label;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class OmokClient extends JFrame implements Runnable{
	
    private BufferedReader reader;                         // 입력 스트림
	private PrintWriter writer;                               // 출력 스트림
	private Socket socket;    
	static int size = 19;
	static OmokClient local;
	static OmokClient client;
	static OmokPanel_mul panel = new OmokPanel_mul(size);
	static OmokState_mul state = new OmokState_mul(19);
	static Label infoView=new Label("대기 중...", 1);
	static final int FRAME_WIDTH = 600;
	static final int FRAME_HEIGHT = 650;
	
	public static void main(String[] args) {

		int size = 15;
		if (args.length > 0)
		    size = Integer.parseInt(args[0]);
		
		MenuLine modeMenu1 = new MenuLine();
		MenuLine modeMenu2 = new MenuLine();
		
		 
	    	
	    int s = size;
	    	
	    if (args.length > 0)
	        s = Integer.parseInt(args[0]);	

        client = new OmokClient("네트워크 오목 게임");
        client.setSize(FRAME_WIDTH, FRAME_HEIGHT+30);
        client.connect();

        infoView.setBounds(0,0,FRAME_WIDTH,30);
        
        panel.setLocation(30,30);
        
        client.setTitle("Omok1");
    	client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    	client.add(infoView);
	    client.add(panel);
	    client.setJMenuBar(modeMenu1);
	    client.setVisible(true);
	    
	    
	    /*local = new OmokClient("네크워크 오목 게임");
		
	    
	    local.setSize(FRAME_WIDTH, FRAME_HEIGHT);
	    local.setTitle("Omok");
	    local.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    OmokPanel panel = new OmokPanel(size);
		
		local.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		local.add(panel);
		local.setJMenuBar(modeMenu2);
		local.setVisible(true);*/
	}
	
	public OmokClient(String title){     
	    super(title);
	    addWindowListener(new WindowAdapter(){
	        public void windowClosing(WindowEvent we){
	        	System.exit(0);
	        }    
	    });
	    
	}

	void connect(){                    // 연결
		try{
			String ip = JOptionPane.showInputDialog("IP");

	        System.out.println("서버에 연결을 요청합니다.\n");

	        socket=new Socket("127.0.0.1", 7777);
	        //192.168.219.100
	        infoView.setText("연결 성공!");
	        System.out.println("---연결 성공--.\n");
	        System.out.println("이름을 입력하고 대기실로 입장하세요.\n");

	        reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));

	        writer=new PrintWriter(socket.getOutputStream(), true);
			writer.println("[START]");
			new Thread(this).start();
			panel.state.setWriter(writer);
			
	        }catch(Exception e){

	        	System.out.println(e+"\n\n연결 실패..\n");  
	        	System.exit(0);
	        }
    }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String msg;                             // 서버로부터의 메시지

	    try{

	    while((msg=reader.readLine())!=null){
	   
	        if(msg.startsWith("[STONE]")){     // 상대편이 놓은 돌의 좌표

	        	String temp=msg.substring(7);

	        	int x=Integer.parseInt(temp.substring(0,temp.indexOf(" ")));

	        	int y=Integer.parseInt(temp.substring(temp.indexOf(" ")+1));

	        	panel.state.putOpponent(x, y);     // 상대편의 돌을 그린다.
	        	panel.repaint();
	        	panel.state.setEnable(true);        // 사용자가 돌을 놓을 수 있도록 한다.
	        }else if(msg.startsWith("[COLOR]")){          // 돌의 색을 부여받는다.

	            String color=msg.substring(7);
	            infoView.setText(color);
	            panel.state.startGame(color);                     // 게임을 시작한다.
	            
	            if(color.equals("BLACK"))
	            	
	              infoView.setText("흑돌을 잡았습니다.");

	            else

	              infoView.setText("백돌을 잡았습니다.");

	          }

	    }   
	    }catch(IOException ie){}
	}

}
class MenuLine extends JMenuBar implements ActionListener {
	OmokClient temp;
	
	private JMenu gameMenu1 = new JMenu("Mode");
	private JMenuItem singleMode = new JMenuItem("Single");
	private JMenuItem multiMode = new JMenuItem("Multi");
	private JMenuItem localMode = new JMenuItem("Local");
	
	
	private JMenu gameMenu2 = new JMenu("Game");
	private JMenuItem gameRestart = new JMenuItem("(re)Start");
	private JMenuItem gameExit = new JMenuItem("exit");

	public MenuLine(OmokClient temp) {
		super();
		initialize();
		this.temp = temp;
	}
	
	public MenuLine() {
		super();
		initialize();
	}

	private void initialize() {
		singleMode.addActionListener(this);
		multiMode.addActionListener(this);
		localMode.addActionListener(this);
		gameRestart.addActionListener(this);
		gameExit.addActionListener(this);
		
		add(gameMenu1);
		gameMenu1.add(singleMode);
		gameMenu1.add(multiMode);
		gameMenu1.add(localMode);
		
		add(gameMenu2);
		gameMenu2.add(gameRestart);
		gameMenu2.add(gameExit);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == gameExit) System.exit(0);
		else if(e.getSource() == localMode)
		{
				MenuLine modeMenu2 = new MenuLine();
				OmokClient.local = new OmokClient("네트워크 오목 게임");
				OmokClient.local.setSize(OmokClient.FRAME_WIDTH, OmokClient.FRAME_HEIGHT);
				OmokClient.local.setTitle("Omok");
				OmokClient.local.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			    
			    OmokPanel panel = new OmokPanel(OmokClient.size);
				
			    OmokClient.local.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			    OmokClient.local.add(panel);
			    OmokClient.local.setJMenuBar(modeMenu2);
			    OmokClient.local.setVisible(true);
			    OmokClient.client.dispose();
		}
		else if(e.getSource() == multiMode)
		{
				MenuLine modeMenu1 = new MenuLine();
				OmokClient.client = new OmokClient("네트워크 오목 게임");
				OmokClient.client.connect();
				
				OmokClient.client.setSize(OmokClient.FRAME_WIDTH, OmokClient.FRAME_HEIGHT);
				OmokClient.client.setTitle("Omok");
				OmokClient.client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			    
			    OmokPanel panel = new OmokPanel(OmokClient.size);
				
			    OmokClient.client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			    OmokClient.client.add(panel);
			    OmokClient.client.setJMenuBar(modeMenu1);
			    OmokClient.client.setVisible(true);
			    OmokClient.local.dispose();
		}
	}

}

class OmokState_mul {
	public static final int NONE = 0;
	public static final int BLACK = 1;
	public static final int WHITE = -1;
	public boolean isSwitchOK = true;
	private int size;
	private int winner;
	private int currentPlayer;
	private int board[][];
    private String info="게임 중지";           // 게임의 진행 상황을 나타내는 문자열
    private PrintWriter writer;  
    // true이면 사용자가 돌을 놓을 수 있는 상태를 의미하고,

    // false이면 사용자가 돌을 놓을 수 없는 상태를 의미한다.

    boolean enable=false;

    private int color=BLACK;                 // 사용자의 돌 색깔    

    private boolean running=false;       // 게임이 진행 중인가를 나타내는 변수

   
	public OmokState_mul(int size) {
		this.size = size;
		board = new int[size][size];
		currentPlayer = BLACK;
	}

	public void playPiece(int row, int col) {	
		          // 상대편에게 메시지를 전달하기 위한 스트림			
		System.out.println("Try Place at row,column " + row +","+ col+" as"				+ " Player"+currentPlayer);
		if (validMove(row, col)) {
			writer.println("[STONE]"+row+" "+col);		
			board[row][col] = currentPlayer;	
			}
		else{// 여기에 둘수 없다고 명령이 뜨면 -> 변수 하나를 추가 해서 currentPlayer - Switch가 false가 되도록
			JOptionPane.showMessageDialog(null, "여기에 둘 수 없습니다.");
			//isSwitchOK = false;
		}
		
		/*switch (currentPlayer) {	
		
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
		}*/
	}

    public void putOpponent(int x, int y){       // 상대편의 돌을 놓는다.

     board[x][y]= -currentPlayer;

      OmokClient.infoView.setText("상대가 두었습니다. 두세요.");
    }
    public boolean isRunning(){           // 게임의 진행 상태를 반환한다.

        return running;

    }

    public void startGame(String col){     // 게임을 시작한다.
        running=true;
        OmokClient.infoView.setText("멀티모드임");
        if(col.equals("BLACK")){              // 흑이 선택되었을 때

          enable=true; color=BLACK;

          info="게임 시작... 두세요.";

        }   

        else{                                // 백이 선택되었을 때

          enable=false; color=WHITE;
          currentPlayer = WHITE;

          info="게임 시작... 기다리세요.";

        }

      }

      public void stopGame(){              // 게임을 멈춘다.

        reset();                              // 오목판을 초기화한다.

        writer.println("[STOPGAME]");        // 상대편에게 메시지를 보낸다.

        enable=false;

        running=false;

      }


      public void setEnable(boolean enable){

        this.enable=enable;

      }

      public void setWriter(PrintWriter writer){

        this.writer=writer;

      }
      public void reset(){                         // 오목판을 초기화시킨다.

          for(int i=0;i<board.length;i++)

            for(int j=0;j<board[i].length;j++)

              board[i][j]=0;

          info="게임 중지";

          

        }
  	  
	public int getPiece(int row, int col) {
		return board[row][col];
	}
	
	public int getWinner() {
		return winner;
	}
	
	public boolean validMove(int row, int col) {
		// 	validMove가 	false면 여기에 둘 수 없다는 message를 출력한다.
		// 				true면 진행		
		int r = row, c = col;
		/*
		 * step
		 * 수직: 0(북), 1(남)
		 * 수평: 2(동), 3(서)
		 * 사선: 4(동북), 5(서남), 6(서북), 7(동남)
		 */
		int step = 0;
		int[] stepCount = new int[8];	// 오목이 성립하는 모든 조건을(8가지) 검사하는 배열
		boolean doneCheck = false;
		while (!doneCheck) {

			switch (step) {
			// if문에서는 step이 살펴볼 방향을 지정하며, r과 c를 수정하면서 순차적으로 살펴보며 놓여진 돌의 갯수를 stepCount의 결과를 낸다.
			// else문에서는 step을 다음 단계로 지정하며, r와 c를 초기 row값으로 되돌려 놓는다.
			// 예시. 흑돌이 놓은 자리 위에 흑돌이 셋, 아래에 흑돌이 하나 있으면 case0는 탐색 세번 = 초기화 한번, case1은 탐색 1번 초기화 
			//													되어야 하는데.... if문 아래로 전혀 들어가지 않는다.
			case 0:
				if (!outOfBounds(r-1) && sameColor(--r, c))	//여긴 왜 r--? 북쪽을 쭉 갈거기 때문에 --- case 0은 여러번 호출된다.
					stepCount[step]++;						// if문 안으로 가질 않기 때문에 stepCount가 증가 되지 않는다 - 조건문에 문제 확인.
				else { step++; r = row; c = col; }			// 문제 해결: 두 조건문 함수에는 이상X. 인수를 살펴봄 --- 이상 발견
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
		// moveResult는 승자를 결정하면 0을 return, 결정되지 않았다면 1과 2를 return
		// 1과 2는? 2는 육목일 경우. 1은 ???
		int result = moveResult(stepCount);
		
		if (result == 0) winner = currentPlayer;
		
		if (result == 1 || result == 2)
			return false;
		
		return true;
	}
	
	// switch case문에서 stepCount[]를 더해주기 위한 조건 함수 2개
	public boolean outOfBounds(int n) {
		// 함수 내부 이상 무
		return !(n >= 0 && n < size);
	}
	
	public boolean sameColor(int r, int c) {
		// 함수 내부 이상 무
		return board[r][c] == currentPlayer;
	}
	
	
	/*
	 * 이기는 수(5): 0
	 * 금수(33 혹은 44): 1
	 * 장목(6이상): 2
	 * 수: 3
	 */
	public int moveResult(int[] stepCount) {	// return값이 1,2면 false, 0이면 승자 결정.
		final int checkBugOn33 = 0;
		final int checkBugOn44 = 0;
		int countTwo = 0, countThree = 0;
		boolean win = false;
		for (int i=0; i<8; i++) {
			// 1. moveResult 2 혹은 0을 결정하는 if-else
			if (i % 2 == 1 && (stepCount[i-1] + stepCount[i] > 5-1)) 
								// sc[0] + sc[1] > 5, sc[3] + sc[4] > 6 .... 
								// -> 북+남이 6,7,8,... 동+서가 6,7,8... 이런경우 = 6목 일때 
				return 2;		// 6목, 7목 
			else 
				if (i % 2 == 1 && (stepCount[i-1] + stepCount[i] == 5-1))
								// 북 + 남 = 5, 동 + 서 = 5, 
					win = true;
			
			// moveresult 1을 결정하는 if-else
			if (stepCount[i] == 2-checkBugOn33) 
				countTwo++;
			else
				if (stepCount[i] == 3-checkBugOn44) 
					countThree++;
		}
		
		// 아래는 return값을 결정하는 if문들
		if (countTwo >= 2 || countThree >= 2)
			return 1;
		if (win)
			return 0;
		return 3;
	}
	
	
}

class OmokPanel_mul extends JPanel
{
    private final int MARGIN = 5;
    private final double PIECE_FRAC = 0.9;

    private int size = 19;
    public OmokState_mul state;
    
    private AudioInputStream dropSound = null;

	private Image stoneBlack = null;
	private Image stoneWhite = null;
    private Clip clip = null;
    

 
    public OmokPanel_mul() 
    {
    	this(15);
    }

    public OmokPanel_mul(int size) 
    {
	super();
	this.size = size;
	state = new OmokState_mul(size);
	addMouseListener(new GomokuListener_mul());
	try {
		File URLOfImage1 = new File("image\\500px-Go_b_no_bg.svg.png");
		File URLOfImage2 = new File("image\\500px-Go_w_no_bg.svg.png");
		
		
		
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

    class GomokuListener_mul extends MouseAdapter 
    {
	public void mouseReleased(MouseEvent e) 
	{
		if(!state.enable)return;  
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
		OmokClient.infoView.setText("상대가 두기를 기다리는 중입니다...");
		state.enable = false;
		
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

	g2.setColor(new Color(0.925f, 0.670f, 0.34f)); 
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
