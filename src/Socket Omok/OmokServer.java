package Omok;

import java.net.*;

import java.io.*;

import java.util.*;

public class OmokServer{

  private ServerSocket server;

  private BManager bMan=new BManager();   // 메시지 방송자

  private Random rnd= new Random();       // 흑과 백을 임의로 정하기 위한 변수

  public OmokServer(){}

  void startServer(){                         // 서버를 실행한다.

	  try{

		  server=new ServerSocket(7777);

		  System.out.println("서버소켓이 생성되었습니다.");

		  while(true){

 

        // 클라이언트와 연결된 스레드를 얻는다.

		  Socket socket=server.accept();

        

        // 스레드를 만들고 실행시킨다.

		  Omok_Thread ot=new Omok_Thread(socket);

		  ot.start();

 

        // bMan에 스레드를 추가한다.

		  bMan.add(ot);

 
        
		  System.out.println("접속자 수: "+bMan.size());
		  }

    }catch(Exception e){

    	System.out.println(e);

    }

}

  public static void main(String[] args){

    OmokServer server=new OmokServer();

    server.startServer();

  }

 

 // 클라이언트와 통신하는 스레드 클래스

  class Omok_Thread extends Thread{

    private String userName=null;       // 사용자 이름

    private Socket socket;              // 소켓

 

    // 게임 준비 여부, true이면 게임을 시작할 준비가 되었음을 의미한다.

    private boolean ready=false;

 

    private BufferedReader reader;     // 입력 스트림

    private PrintWriter writer;           // 출력 스트림

    Omok_Thread(Socket socket){     // 생성자

      this.socket=socket;

    }

    Socket getSocket(){               // 소켓을 반환한다.

      return socket;

    }


    String getUserName(){             // 사용자 이름을 반환한다.

      return userName;

    }

    boolean isReady(){                 // 준비 상태를 반환한다.

      return ready;

    }

    public void run(){

      try{

        reader=new BufferedReader(

                            new InputStreamReader(socket.getInputStream()));

        writer=new PrintWriter(socket.getOutputStream(), true);

 

        String msg;                     // 클라이언트의 메시지

 

        while((msg=reader.readLine())!=null){

 
         // "[STONE]" 메시지는 상대편에게 전송한다.

         if(msg.startsWith("[STONE]"))

            bMan.sendToOthers(this, msg);

                  

         else if(bMan.size()==2){

        	 // 2명이 되면 시작한다.          

              int a=rnd.nextInt(2);

              if(a==0){

                writer.println("[COLOR]BLACK");

                bMan.sendToOthers(this, "[COLOR]WHITE");

              }

              else{

                writer.println("[COLOR]WHITE");

                bMan.sendToOthers(this, "[COLOR]BLACK");

              }           
           }        

        }

      }catch(Exception e){

      }finally{

        try{

          bMan.remove(this);

          if(reader!=null) reader.close();

          if(writer!=null) writer.close();

          if(socket!=null) socket.close();

          reader=null; writer=null; socket=null;

          System.out.println(userName+"님이 접속을 끊었습니다.");

          System.out.println("접속자 수: "+bMan.size());

          // 사용자가 접속을 끊었음을 같은 방에 알린다.

          bMan.sendToOthers(this,"[DISCONNECT]"+userName);

        }catch(Exception e){}

      }

    }

  }

  class BManager extends Vector{       // 메시지를 전달하는 클래스

	  BManager(){}

	  void add(Omok_Thread ot){           // 스레드를 추가한다.

		  super.add(ot);

	  }

	  void remove(Omok_Thread ot){        // 스레드를 제거한다.

		  super.remove(ot);
		  
 	  }
	  
	  Omok_Thread getOT(int i){            // i번째 스레드를 반환한다.

		  return (Omok_Thread)elementAt(i);

	  }

	  Socket getSocket(int i){              // i번째 스레드의 소켓을 반환한다.

		  return getOT(i).getSocket();

	  }
 

    // i번째 스레드와 연결된 클라이언트에게 메시지를 전송한다.

    void sendTo(int i, String msg){

      try{

        PrintWriter pw= new PrintWriter(getSocket(i).getOutputStream(), true);

        pw.println(msg);

      }catch(Exception e){}  

    }

    // ot와 같은 방에 있는 다른 사용자에게 msg를 전달한다.

    void sendToOthers(Omok_Thread ot, String msg){

    	for(int i=0;i<size();i++)
    		if(getOT(i)!=ot)
    			sendTo(i, msg);
    }
   }
}
