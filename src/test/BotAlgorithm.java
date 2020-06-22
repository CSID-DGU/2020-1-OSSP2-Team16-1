package test;

import java.math.*;

public class BotAlgorithm {
	
	private int board[][];
	private int weight[][];
	private int botPlayer;
	private int boardSize;
	private int isFirstStep;
	private OmokState prohibition;
	
	public BotAlgorithm() { this(new OmokState(19)); }
	public BotAlgorithm( OmokState input)//Default set of weight_array
	{
		board = input.board;
		boardSize = input.size;
		weight = new int [boardSize][boardSize];
		botPlayer = input.botChoose;
		prohibition = input;
		
		//default position of bot player's first step
		weight[boardSize/2][boardSize/2] = 1;
		weight[boardSize/2][boardSize/2+1] = 1;
		isFirstStep = 0;
	}
	
	//set default weight for reset
	protected void reset_bot()
	{
		weight[boardSize/2][boardSize/2] = 1;
		weight[boardSize/2][boardSize/2+1] = 1;
	}
	
	//decide position of stone
	protected int[] choose_position()
	{
		int[] result = {0, 0};
		calcul_weight();
		result =  calcul_defense_position();
		return result;
	}
	
	//calculate weight ( only Consider bot's stone state )
	protected int[] calcul_weight()
	{
		int step = 0;
		int[] stepCount = {1, 1, 1, 1, 1, 1, 1, 1};
		int[] isBlock = {0, 0, 0, 0, 0, 0, 0, 0};
		boolean doneCheck = false;
		int r, c;
		int sumOfWeight = 0;
		int possibility[] = { 0, 0, 0 };
		int currentStone = 0;;
		
		for(int row = 0; row < boardSize; row++)
			for(int col = 0; col < boardSize; col++)
				if(board[row][col] != 0)
					currentStone++;
		if(currentStone <= 1)
			reset_bot();
		
		for(int row = 0; row < boardSize; row++) {
			for(int col = 0; col < boardSize; col++) {
				
				r = row;
				c = col;
				
				if(board[row][col] != 0) {
					weight[row][col] = -100;
					continue;
				}
						
				while (!doneCheck) {
					//checking state of connected(adjacent) stone. count connected stone's number and check it is blocked 
					//similar to validMove function
					switch (step) {							
					case 0:
						if (OutOfRange(r-1) && sameColor(--r, c))	
							stepCount[step]*=5;						
						else if (OutOfRange(r) && diffColor(r, c) == 1)
						{ isBlock[step] = 1; step++; r = row; c = col;}
						else { step++; r = row; c = col; }			
						break;
					case 1:
						if (OutOfRange(r+1) && sameColor(++r, c))
							stepCount[step]*=5;
						else if (OutOfRange(r) && diffColor(r, c) == 1)
						{ isBlock[step] = 1; step++; r = row; c = col;}
						else { step++; r = row; c = col; }
						break;
					case 2:
						if (OutOfRange(c+1) && sameColor(r, ++c))
							stepCount[step]*=5;
						else if (OutOfRange(c) && diffColor(r, c) == 1)
						{ isBlock[step] = 1; step++; r = row; c = col;}
						else { step++; r = row; c = col; }
						break;
					case 3:
						if (OutOfRange(c-1) && sameColor(r, --c))
							stepCount[step]*=5;
						else if (OutOfRange(c) && diffColor(r, c) == 1)
						{ isBlock[step] = 1; step++; r = row; c = col;}
						else { step++; r = row; c = col; }
						break;
					case 4:
						if (OutOfRange(r-1) && OutOfRange(c+1) && sameColor(--r, ++c))
							stepCount[step]*=5;
						else if ( OutOfRange(c) && OutOfRange(r) && diffColor(r, c) == 1)
						{ isBlock[step] = 1; step++; r = row; c = col;}
						else { step++; r = row; c = col; }
						break;
					case 5:
						if (OutOfRange(r+1) && OutOfRange(c-1) && sameColor(++r, --c))
							stepCount[step]*=5;
						else if ( OutOfRange(c) && OutOfRange(r) && diffColor(r, c) == 1)
						{ isBlock[step] = 1; step++; r = row; c = col;}
						else { step++; r = row; c = col; }
						break;
					case 6:
						if (OutOfRange(r-1) && OutOfRange(c-1) && sameColor(--r, --c))
							stepCount[step]*=5;
						else if ( OutOfRange(c) && OutOfRange(r) && diffColor(r, c) == 1)
						{ isBlock[step] = 1; step++; r = row; c = col;}
						else { step++; r = row; c = col; }
						break;
					case 7:
						if (OutOfRange(r+1) && OutOfRange(c+1) && sameColor(++r, ++c))
							stepCount[step]*=5;
						else if ( OutOfRange(c) && OutOfRange(r) && diffColor(r, c) == 1)
						{ isBlock[step] = 1; step++; r = row; c = col;}
						else { step++; r = row; c = col; }
						break;
					default:
						doneCheck = true;
						break;
					}
				}
				
				doneCheck = false;
				step = 0;
						
				// check 3 position and 4 position. set strong value on 4 position
				for(int i = 0; i< 8; i++) {
					//special combination of 4
					if(i % 2 == 0)
					{
						if(stepCount[i] == 25 && stepCount[i+1] == 25)
							stepCount[i] = 2000;
						else if(stepCount[i] == 125 && stepCount[i+1] == 5)
							stepCount[i] = 2000;
						else if(stepCount[i+1] == 125 && stepCount[i] == 5)
							stepCount[i]  = 2000;
						
						if(stepCount[i] == 25 && stepCount[i+1] == 5 && isBlock[i] != 1 && isBlock[i+1] != 1)
							stepCount[i] = 30;
						else if(stepCount[i] == 5 && stepCount[i+1] == 25 && isBlock[i] != 1 && isBlock[i+1] != 1)
							stepCount[i] = 30;
					}
					
					//3 and normal 4 position
					if(isBlock[i] == 1)
						stepCount[i] /= 2;
					if(stepCount[i] == 1)
						stepCount[i] = 0;
					if(stepCount[i] >= 125)
						stepCount[i] *= 3;
					if((stepCount[i] >= 250 && isBlock[i] == 1) || stepCount[i] >= 1875)
						stepCount[i] *= 10;
					sumOfWeight += stepCount[i];
					stepCount[i] = 1;
					isBlock[i] = 0;
				}
				//exception for first step
				if(weight[row][col] != 1)
					weight[row][col] = sumOfWeight;
				else
				{
					possibility[2] = 1;
					possibility[1] = col;
					possibility[0] = row;
				}
				
				//change stone position when state of new position is stronger than previous one
				if(possibility[2] < sumOfWeight) {
					possibility[2] = sumOfWeight;
					possibility[1] = col;
					possibility[0] = row;
				}
						
				sumOfWeight = 0;
			}
		}
		return possibility;
	}
	
	//calculate player's stone weight and add bot's stone weight
	// almost same to calcul_weight function. only some constants of weight is different.  
	protected int[] calcul_defense_position()
	{
		int step = 0;
		int sumOfWeight = 0;
		int[] stepCount = {0, 0, 0, 0, 0, 0, 0, 0};
		int[] isBlock = {0, 0, 0, 0, 0, 0, 0, 0};
		boolean doneCheck = false;
		int possibility[] = { 0, 0, 0 };
		
		int r, c;
		
		for(int row = 0; row< boardSize; row++)
		{
			for(int col = 0; col< boardSize; col++)
			{
				r = row;
				c = col;
				
				if(board[row][col] != 0) {
					continue;
				}
				while (!doneCheck) {
					switch (step) {												
					case 0:
						if (OutOfRange(r-1) && diffColor(--r, c) == 1)
							stepCount[step]+=10;
						else if (OutOfRange(r) && sameColor(r, c))
						{ isBlock[step] = 1; step++; r = row; c = col;}
						else { step++; r = row; c = col; }
						break;
					case 1:
						if (OutOfRange(r+1) && diffColor(++r, c) == 1)
							stepCount[step]+=10;
						else if (OutOfRange(r) && sameColor(r, c))
						{ isBlock[step] = 1; step++; r = row; c = col;}
						else { step++; r = row; c = col; }
						break;
					case 2:
						if (OutOfRange(c+1) && diffColor(r, ++c) == 1)
							stepCount[step]+=10;
						else if (OutOfRange(c) && sameColor(r, c))
						{ isBlock[step] = 1; step++; r = row; c = col;}
						else { step++; r = row; c = col; }
						break;
					case 3:
						if (OutOfRange(c-1) && diffColor(r, --c) == 1)
							stepCount[step]+=10;
						else if (OutOfRange(c) && sameColor(r, c))
						{ isBlock[step] = 1; step++; r = row; c = col;}
						else { step++; r = row; c = col; }
						break;
					case 4:
						if (OutOfRange(r-1) && OutOfRange(c+1) && diffColor(--r, ++c) == 1)
							stepCount[step]+=10;
						else if ( OutOfRange(c) && OutOfRange(r) && sameColor(r, c))
						{ isBlock[step] = 1; step++; r = row; c = col;}
						else { step++; r = row; c = col; }
						break;
					case 5:
						if (OutOfRange(r+1) && OutOfRange(c-1) && diffColor(++r, --c) == 1)
							stepCount[step]+=10;
						else if ( OutOfRange(c) && OutOfRange(r) && sameColor(r, c))
						{ isBlock[step] = 1; step++; r = row; c = col;}
						else { step++; r = row; c = col; }
						break;
					case 6:
						if (OutOfRange(r-1) && OutOfRange(c-1) && diffColor(--r, --c) == 1)
							stepCount[step]+=10;
						else if ( OutOfRange(c) && OutOfRange(r) && sameColor(r, c))
						{ isBlock[step] = 1; step++; r = row; c = col;}
						else { step++; r = row; c = col; }
						break;
					case 7:
						if (OutOfRange(r+1) && OutOfRange(c+1) && diffColor(++r, ++c) == 1)
							stepCount[step]+=10;
						else if ( OutOfRange(c) && OutOfRange(r) && sameColor(r, c))
						{ isBlock[step] = 1; step++; r = row; c = col;}
						else { step++; r = row; c = col; }
						break;
					default:
						doneCheck = true;
						break;
					}
				}
				doneCheck = false;
				step = 0;
					
				for(int i = 0; i< 8; i++) {
					if(i % 2 == 0)
					{
						if(stepCount[i] == 20 && stepCount[i+1] == 20)
							stepCount[i] = 40;
						else if(stepCount[i] == 30 && stepCount[i+1] == 10)
							stepCount[i] = 40;
						else if(stepCount[i+1] == 30 && stepCount[i] == 10)
							stepCount[i] = 40;
						
						if(stepCount[i] == 20 && stepCount[i+1] == 10 && isBlock[i] != 1 && isBlock[i+1] != 1)
							stepCount[i] = 30;
						else if(stepCount[i] == 10 && stepCount[i+1] == 20 && isBlock[i] != 1 && isBlock[i+1] != 1)
							stepCount[i] = 30;
					}
						
					if(isBlock[i] == 1)
						stepCount[i] /= 2;
					if(stepCount[i] == 1)
						stepCount[i] = 0;
					if((stepCount[i] >= 30 && isBlock[i] == 0)||(stepCount[i] >= 20 && isBlock[i] == 1))
						stepCount[i] *= 10;
					if((stepCount[i] >= 200 && isBlock[i] == 1)||stepCount[i] >= 400)
						stepCount[i] *= 10;
					sumOfWeight += stepCount[i];
					stepCount[i] = 0;
					isBlock[i] = 0;
				}
					
				weight[row][col] += sumOfWeight;
					
				if(possibility[2] < weight[row][col] && prohibition.validMove(row, col)) {
					possibility[2] = weight[row][col];
					possibility[1] = col;
					possibility[0] = row;
					prohibition.winner = 0;
				}
							
				sumOfWeight = 0;
			}
		}
		return possibility;
	}
	
	//checking adjacent stone's color is same
	private boolean sameColor(int a, int b)
	{
		if(board[a][b] == botPlayer)
			return true;
		else 
			return false;
	}
	
	//checking adjacent stone's color is different (except empty space)
	private int diffColor(int a, int b)
	{
		if(board[a][b] != 0) {
		if(board[a][b] != botPlayer)
			return 1;
		else 
			return 2;
		}
		else return 3;
	}
	
	private boolean OutOfRange(int i) // check array's range  
	{
		if(i<0 || i>=boardSize) return false;
		else return true;
	}
}

