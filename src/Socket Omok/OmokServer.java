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

    private int roomNumber=-1;        // 방 번호

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

    int getRoomNumber(){             // 방 번호를 반환한다.

      return roomNumber;

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

        

          // "[START]" 메시지이면


            // 다른 사용자도 게임을 시작한 준비가 되었으면

         else if(bMan.size()==2){
        	 System.out.println("shifoot");
           ready=true;   // 게임을 시작할 준비가 되었다.
  // 다른 사용자도 게임을 시작한 준비가 되었으면

          

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

 

 

          // 사용자가 게임을 중지하는 메시지를 보내면

          else if(msg.startsWith("[STOPGAME]"))

            ready=false;

 

          // 사용자가 게임을 기권하는 메시지를 보내면

          else if(msg.startsWith("[DROPGAME]")){

            ready=false;

            // 상대편에게 사용자의 기권을 알린다.

            bMan.sendToOthers(this, "[DROPGAME]");

          }

 

          // 사용자가 이겼다는 메시지를 보내면

          else if(msg.startsWith("[WIN]")){

            ready=false;

            // 사용자에게 메시지를 보낸다.

            writer.println("[WIN]");

 

            // 상대편에는 졌음을 알린다.

            bMan.sendToOthers(this, "[LOSE]");

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

          bMan.sendToRoom(roomNumber,"[DISCONNECT]"+userName);

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

    int getRoomNumber(int i){            // i번째 스레드의 방 번호를 반환한다.

      return getOT(i).getRoomNumber();

    }

    synchronized boolean isFull(int roomNum){    // 방이 찼는지 알아본다.

      if(roomNum==0)return false;                 // 대기실은 차지 않는다.

 

      // 다른 방은 2명 이상 입장할 수 없다.

      int count=0;

      for(int i=0;i<size();i++)

        if(roomNum==getRoomNumber(i))count++;

      if(count>=2)return true;

      return false;

    }

 

    // roomNum 방에 msg를 전송한다.

    void sendToRoom(int roomNum, String msg){

      for(int i=0;i<size();i++)

        if(roomNum==getRoomNumber(i))

          sendTo(i, msg);

    }

 

    // ot와 같은 방에 있는 다른 사용자에게 msg를 전달한다.

    void sendToOthers(Omok_Thread ot, String msg){

      for(int i=0;i<size();i++)

        if(getRoomNumber(i)==ot.getRoomNumber() && getOT(i)!=ot)

          sendTo(i, msg);

    }

 

    // 게임을 시작할 준비가 되었는가를 반환한다.

    // 두 명의 사용자 모두 준비된 상태이면 true를 반환한다.

    synchronized boolean isReady(){
    	return true;
    	}

 

    // roomNum방에 있는 사용자들의 이름을 반환한다.

    String getNamesInRoom(int roomNum){

      StringBuffer sb=new StringBuffer("[PLAYERS]");

      for(int i=0;i<size();i++)

        if(roomNum==getRoomNumber(i))

          sb.append(getOT(i).getUserName()+"\t");

      return sb.toString();

    }

  }

}
