package test;

import java.math.*;

public class BotAlgorithm {
	
	private int board[][];
	private int weight[][];
	private int botPlayer;
	private int boardSize;
	
	public BotAlgorithm() { this(new OmokState(19)); }
	public BotAlgorithm( OmokState input)
	{
		board = input.board;
		botPlayer = input.botChoose;
		boardSize = input.size;
	}
	
	private void cal_weight()
	{
		for(int i = 0; i< boardSize-1; i++)
		{
			
		}
	}
}
