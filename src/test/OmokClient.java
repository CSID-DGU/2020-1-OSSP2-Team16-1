package test;

import java.awt.Frame;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Random;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class OmokClient extends JFrame implements Runnable{
	
    private BufferedReader reader;                         // 입력 스트림
	private PrintWriter writer;                               // 출력 스트림

	private Socket socket;
	private static int mode;
	
	static int size = 15;
	static OmokPanel panel = new OmokPanel(size);
	private OmokState state = new OmokState(15);
	static Label infoView=new Label("대기 중...", 1);
    public static void main(String[] args) {
    	
    int s = size;
	
	if (args.length > 0)
	    s = Integer.parseInt(args[0]);	
	
	final int FRAME_WIDTH = 600;
	final int FRAME_HEIGHT = 650;


    OmokClient client=new OmokClient("Omok");
  

    client.setSize(FRAME_WIDTH, FRAME_HEIGHT+30);
 	
	MenuLine modeMenu = new MenuLine();
    infoView.setBounds(0,0,FRAME_WIDTH,30);
    
    panel.setLocation(30,30);

    client.add(infoView);
	client.add(panel);
	client.setJMenuBar(modeMenu);
	client.setVisible(true);
    }

	public OmokClient(String title){                       // 생성자
	    super(title);
	    addWindowListener(new WindowAdapter(){	    	
	        public void windowClosing(WindowEvent we){
	        	System.exit(0);
	        }    
	    });
	}

	void change_player()
	{
		panel.state.mode = 1;
		panel.state.botChoose = (new Random().nextInt(1)) == 1? 1 : -1;
		panel.bot = new BotAlgorithm(panel.state);
	}
	
	void connect(){                    // 연결
		try{
			String ip = JOptionPane.showInputDialog("IP");

	        System.out.println("서버에 연결을 요청합니다.\n");

	        socket=new Socket(ip, 7777);
	        System.out.println(ip);
	        //192.168.219.100
	        infoView.setText("연결 성공!");
	        System.out.println("---연결 성공--.\n");
	        System.out.println("이름을 입력하고 대기실로 입장하세요.\n");

	        reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));

	        writer=new PrintWriter(socket.getOutputStream(), true);
			writer.println("[START]");
			new Thread(this).start();
			panel.state.setWriter(writer);
			panel.state.mode = 2;
			panel.state.reset();
			panel.repaint();
			
	        }catch(Exception e){

	        	System.out.println(e+"\n\n연결 실패..\n");  
	        	System.exit(0);	        	
	        }
    }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String msg;                             // 서버로부터의 메시지
		String my_color = null;
	    try{
	    while((msg=reader.readLine())!=null){
	   
	        if(msg.startsWith("[STONE]")){     // 상대편이 놓은 돌의 좌표

	        	String temp=msg.substring(7);

	        	int x=Integer.parseInt(temp.substring(0,temp.indexOf(" ")));

	        	int y=Integer.parseInt(temp.substring(temp.indexOf(" ")+1));

	        	panel.state.putOpponent(x, y);     // 상대편의 돌을 그린다.
	        	panel.repaint();
	        	panel.state.setEnable(true);        // 사용자가 돌을 놓을 수 있도록 한다.	        	
	        }
	        else if(msg.startsWith("[COLOR]")){          // 돌의 색을 부여받는다.

	            String color=msg.substring(7);
	            infoView.setText(color);
	            panel.state.startGame(color);                     // 게임을 시작한다.
	            
	            if(color.equals("BLACK")) {	            	
	            	infoView.setText("흑돌을 잡았습니다.");
	            		            	
	            	my_color = "Black";
	            	}
	            else
	            {
	            	infoView.setText("백돌을 잡았습니다.");
	            	my_color = "White";
	            }
	        }
	        else if(msg.startsWith("[FULL]")){    
	            	JOptionPane.showMessageDialog(null, 
							"이미 게임중입니다.", "Message", 
							JOptionPane.ERROR_MESSAGE); 
	            	System.exit(0);	            	
	        }
	        else if(msg.startsWith("[DISCONNECT]")){              
	        	  infoView.setText("상대가 나갔습니다. 승리!");
	        	  JOptionPane.showMessageDialog(null, my_color+" wins!" );
	        	  panel.state.setEnable(false); 			    		
	        	  panel.state.reset();
	        }
	    }   
	    }catch(IOException ie){}
	}

}
