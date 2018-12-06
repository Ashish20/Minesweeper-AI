

/*

AUTHOR:      John Lu

DESCRIPTION: This file contains your agent class, which you will
             implement.

NOTES:       - If you are having trouble understanding how the shell
               works, look at the other parts of the code, as well as
               the documentation.

             - You are only allowed to make changes to this portion of
               the code. Any changes to other portions of the code will
               be lost when the tournament runs your code.
*/

package src;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import src.World;

//import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry.Entry;

import src.Action.ACTION;

public class MyAI extends AI {
	int x,y;
	int count =0;
	Point point;
	Queue<Point> safe;
	Queue<Point> notSafe;
	boolean firstVisit;
	boolean visited[][];
	boolean mineAlreadyInQueue[][];
	int numRows,numCols;
	LinkedHashMap<Point,Integer> mines;
	int noOfSteps;
	World world;
	int flag_x;
	int flag_y;
	boolean flagSet;
	int number;
	int m[][];
	int m1[][];
	int prev_x;
	int prev_y;
	int prev_x1;
	int prev_y1;
	int noOfFlags;
	boolean looping;
	int totalMines;
	int alreadyAffectedDueToThisPoint[][];
	// ########################## INSTRUCTIONS ##########################
	// 1) The Minesweeper Shell will pass in the board size, number of mines
	// 	  and first move coordinates to your agent. Create any instance variables
	//    necessary to store these variables.
	//
	// 2) You MUST implement the getAction() method which has a single parameter,
	// 	  number. If your most recent move is an Action.UNCOVER action, this value will
	//	  be the number of the tile just uncovered. If your most recent move is
	//    not Action.UNCOVER, then the value will be -1.
	// 
	// 3) Feel free to implement any helper functions.
	//
	// ###################### END OF INSTURCTIONS #######################
	
	// This line is to remove compiler warnings related to using Java generics
	// if you decide to do so in your implementation.
	@SuppressWarnings("unchecked")


	public MyAI(int rowDimension, int colDimension, int totalMines, int startX, int startY) {
		this.safe=new LinkedList();
		this.notSafe=new LinkedList();
		System.out.println(startX+"  " + startY);
		this.x=startX-1;
		this.y=startY-1;
		this.point=new Point(x,y);
		this.firstVisit=true;
		this.numRows=rowDimension;
		this.numCols=colDimension;
		visited=new boolean[numCols][numRows];
		alreadyAffectedDueToThisPoint=new int[numCols][numRows];
		noOfSteps=0;
		m=new int[numCols][numRows];
		m1=new int[numCols][numRows];
		this.flagSet=false;
		noOfFlags=0;
		this.totalMines=totalMines;
		//world=new World();
		for(int i=0;i<numCols;i++)
		{
			Arrays.fill(visited[i], false);
		}
		mineAlreadyInQueue=new boolean[numCols][numRows];
		for(int i=0;i<numCols;i++)
		{
			Arrays.fill(mineAlreadyInQueue[i], false);
		}
		for(int i=0;i<numCols;i++)
		{
			Arrays.fill(alreadyAffectedDueToThisPoint[i], 0);
		}
		for(int i=0;i<numCols;i++)
		{
			Arrays.fill(m[i], -1);
		}
		for(int i=0;i<numCols;i++)
		{
			Arrays.fill(m1[i], -1);
		}
		mines=new LinkedHashMap();
		this.prev_x1=0;
		this.prev_y1=0;
		this.looping=false;
		
		// ################### Implement Constructor (required) ####################	
	}
	
	// ################## Implement getAction(), (required) #####################
	public Action getAction(int number) {
		//printWorld();
		this.number=number;
		if(noOfFlags==totalMines)
		{
			unCoverEverything();
		}
		count++;
		if(count ==800)
			return new Action(ACTION.LEAVE);
		if(firstVisit)
		{
			prev_x=(int)point.getX();
			prev_y=(int)point.getY();
			this.firstVisit=false;
			System.out.println(this.x+"  " + this.y);
			System.out.println((int)point.getX()+"  " + (int)point.getY());
			visited[(int)point.getX()][(int)point.getY()]=true;
			
			return new Action(ACTION.UNCOVER,this.x+1,this.y+1);
		}
		if(number!=-1)
		{
		m[prev_x][prev_y]=number;
		m1[prev_x][prev_y]=number;
		updateM1AfterUncovering(prev_x, prev_y);
		}
		if(number==0)
		{
			if(fillSafeQueue(point))
			{
				//System.out.println("successfull filled");
			}
		}
		if(!safe.isEmpty())
		{
			point=pointToUncover();
			visited[(int)point.getX()][(int)point.getY()]=true;
			prev_x=(int)point.getX();
			prev_y=(int)point.getY();
			removeMineIfExists(prev_x, prev_y);
			return new Action(ACTION.UNCOVER,(int)point.getX()+1,(int)point.getY()+1);
		}
		if(!notSafe.isEmpty())
		{
			point=pointToFlag();
			m[(int)point.getX()][(int)point.getY()]=-2;
			m1[(int)point.getX()][(int)point.getY()]=-2;
			boolean b=removeMineIfExists((int)point.getX(),(int)point.getY());
			updateM1((int)point.getX(), (int)point.getY());
			noOfFlags++;
			return new Action(ACTION.FLAG,(int)point.getX()+1,(int)point.getY()+1);
		}
		/*else if(number>0)
		{
			//System.out.println("saw a mine around");
			fillMines(point);
		}*/
		if(safe.isEmpty())
		{
			
			for(int i=prev_x1;i<numCols;i++)
			{
				
				for(int j=prev_y1;j<numRows;j++)
				{
					if(m[i][j]==-2 || m[i][j]==-1 || m[i][j]==0)
						continue;
					String str=checkSurroundingMines(i,j);
					if(str.equals("UNCOVER"))
					{
						//System.out.println("inside uncover 2");
						looping=true;
						point=pointToUncover();
						visited[(int)point.getX()][(int)point.getY()]=true;
						prev_x1=0;
						prev_y1=0;
						prev_x=(int)point.getX();
						prev_y=(int)point.getY();
						removeMineIfExists(prev_x, prev_y);
						return new Action(ACTION.UNCOVER,(int)point.getX()+1,(int)point.getY()+1);
						
					}
					else if(str.equals("FLAG"))
					{//
						looping= true;
						point=pointToFlag();
						prev_x1=0;
						prev_y1=0;
						prev_x=(int)point.getX();
						prev_y=(int)point.getY();
						m[(int)point.getX()][(int)point.getY()]=-2;
						m1[(int)point.getX()][(int)point.getY()]=-2;
						boolean b=removeMineIfExists((int)point.getX(),(int)point.getY());
						updateM1((int)point.getX(), (int)point.getY());
						noOfFlags++;
						return new Action(ACTION.FLAG,(int)point.getX()+1,(int)point.getY()+1);
					}
					else
					{
						continue;
					}
				}
				if(i==numCols-1)
				{
					if(!looping)
					{
						System.out.println("using probabilty");
						Point p=checkFor121Pattern();
						Point p1=checkFor1221Pattern();
						Point p2=checkFor12Pattern();
						if(p!=null || p1!=null || p2!=null)
						{
							prev_x1=0;
							prev_y1=0;
							System.out.println(mines);
							System.out.println("got 12 pattern");
							if(p!=null)
							{
								m[(int)p.getX()][(int)p.getY()]=-2;
								m1[(int)p.getX()][(int)p.getY()]=-2;
								boolean b=removeMineIfExists((int)p.getX(),(int)p.getY());
								updateM1((int)p.getX(), (int)p.getY());
								noOfFlags++;
								return new Action(ACTION.FLAG,(int)p.getX()+1,(int)p.getY()+1);
							}
							else if(p1!=null)
							{
								m[(int)p1.getX()][(int)p1.getY()]=-2;
								m1[(int)p1.getX()][(int)p1.getY()]=-2;
								boolean b=removeMineIfExists((int)p1.getX(),(int)p1.getY());
								updateM1((int)p1.getX(), (int)p1.getY());
								noOfFlags++;
								return new Action(ACTION.FLAG,(int)p1.getX()+1,(int)p1.getY()+1);
								
							}
							else
							{
								m[(int)p2.getX()][(int)p2.getY()]=-2;
								m1[(int)p2.getX()][(int)p2.getY()]=-2;
								boolean b=removeMineIfExists((int)p2.getX(),(int)p2.getY());
								updateM1((int)p2.getX(), (int)p2.getY());
								noOfFlags++;
								return new Action(ACTION.FLAG,(int)p2.getX()+1,(int)p2.getY()+1);
							}
							
						}
						Point p3=checkFor11Pattern();
						if(p3!=null)
						{
							visited[(int)p3.getX()][(int)p3.getY()]=true;
							prev_x1=0;
							prev_y1=0;
							prev_x=(int)p3.getX();
							prev_y=(int)p3.getY();
							removeMineIfExists(prev_x, prev_y);
							return new Action(ACTION.UNCOVER,(int)p3.getX()+1,(int)p3.getY()+1);
						}
						
						Point p4=findTheMax();
						m[(int)p4.getX()][(int)p4.getY()]=-2;
						m1[(int)p4.getX()][(int)p4.getY()]=-2;
						prev_x1=0;
						prev_y1=0;
						boolean b=removeMineIfExists((int)p4.getX(),(int)p4.getY());
						updateM1((int)p4.getX(), (int)p4.getY());
						noOfFlags++;
						return new Action(ACTION.FLAG,(int)p4.getX()+1,(int)p4.getY()+1);
						
					}
					else
					{
						i=-1;
						looping = false;
					}
				}
					
				prev_y1=0;
			}
		}
		prev_x=(int)point.getX();
		prev_y=(int)point.getY();
		return new Action(ACTION.UNCOVER,(int)point.getX()+1,(int)point.getY()+1);
	}
	public boolean fillSafeQueue(Point point)
	{
		int centerX=(int)point.getX();
		int centerY=(int)point.getY();
		if(centerX-1>=0)
		{
			if(!visited[centerX-1][centerY] && !mineAlreadyInQueue[centerX-1][centerY] && removeMineIfExists(centerX-1,centerY))
			{
				safe.add(new Point(centerX-1,centerY));
				mineAlreadyInQueue[centerX-1][centerY]=true;
			}
			if(centerY-1>=0 && !visited[centerX-1][centerY-1] && !mineAlreadyInQueue[centerX-1][centerY-1] && removeMineIfExists(centerX-1,centerY-1))
			{
				safe.add(new Point(centerX-1,centerY-1));
				mineAlreadyInQueue[centerX-1][centerY-1]=true;
			}
			if(centerY+1<numRows && !visited[centerX-1][centerY+1] && !mineAlreadyInQueue[centerX-1][centerY+1] && removeMineIfExists(centerX-1,centerY+1))
			{
				safe.add(new Point(centerX-1,centerY+1));
				mineAlreadyInQueue[centerX-1][centerY+1]=true;
			}
			
		}
		if(centerY-1>=0 && !visited[centerX][centerY-1] && !mineAlreadyInQueue[centerX][centerY-1] && removeMineIfExists(centerX,centerY-1))
		{
			safe.add(new Point(centerX,centerY-1));
			mineAlreadyInQueue[centerX][centerY-1]=true;
		}
		if(centerY+1<numRows && !visited[centerX][centerY+1] && !mineAlreadyInQueue[centerX][centerY+1] && removeMineIfExists(centerX,centerY+1))
		{
			safe.add(new Point(centerX,centerY+1));
			mineAlreadyInQueue[centerX][centerY+1]=true;
		}
		if(centerX+1<numCols)
		{
			if(!visited[centerX+1][centerY] && !mineAlreadyInQueue[centerX+1][centerY] && removeMineIfExists(centerX+1,centerY))
			{
				safe.add(new Point(centerX+1,centerY));
				mineAlreadyInQueue[centerX+1][centerY]=true;
			}
			if(centerY-1>=0 && !visited[centerX+1][centerY-1] && !mineAlreadyInQueue[centerX+1][centerY-1] && removeMineIfExists(centerX+1,centerY-1))
			{
				safe.add(new Point(centerX+1,centerY-1));
				mineAlreadyInQueue[centerX+1][centerY-1]=true;
			}
			if(centerY+1<numRows && !visited[centerX+1][centerY+1] && !mineAlreadyInQueue[centerX+1][centerY+1] && removeMineIfExists(centerX+1,centerY+1))
			{
				safe.add(new Point(centerX+1,centerY+1));
				mineAlreadyInQueue[centerX+1][centerY+1]=true;
			}
			
		}
		return true;
		
	}
	public Point pointToUncover()
	{
		return safe.poll();
	}
	public Point pointToFlag()
	{
		return notSafe.poll();
	}
	/*public void fillMines(Point point)
	{
		int centerX=(int)point.getX();
		int centerY=(int)point.getY();
	//	System.out.println("fill mine from centerx and center y" + centerX+ "  " + centerY);
		if(centerX-1>=0)
		{
			if(!mineAlreadyInQueue[centerX-1][centerY]&& !visited[centerX-1][centerY] && !checkIfMineExits(centerX-1,centerY))
				mines.put(new Point(centerX-1,centerY), number);
			if(centerY-1>=0 && !visited[centerX-1][centerY-1]&&  !mineAlreadyInQueue[centerX-1][centerY-1] && !checkIfMineExits(centerX-1,centerY-1))
			{
				//if(!checkIfMineExits(point))
					mines.put(new Point(centerX-1,centerY-1),number);
			}
			if(centerY+1<numCols && !visited[centerX-1][centerY+1]&& !mineAlreadyInQueue[centerX-1][centerY+1] && !checkIfMineExits(centerX-1,centerY+1))
			{
				//if(!checkIfMineExits(point))
					mines.put(new Point(centerX-1,centerY+1),number);
			}
			
		}
		if(centerY-1>=0 && !visited[centerX][centerY-1]&& !mineAlreadyInQueue[centerX][centerY-1] && !checkIfMineExits(centerX,centerY-1))
		{	
			if(!checkIfMineExits(centerX,centerY-1))
				mines.put(new Point(centerX,centerY-1),number);
		}
		if(centerY+1<numCols && !visited[centerX][centerY+1] && !mineAlreadyInQueue[centerX][centerY+1] && !checkIfMineExits(centerX,centerY+1))
		{
			if(!checkIfMineExits(centerX,centerY+1))
				mines.put(new Point(centerX,centerY+1),number);
		}
		if(centerX+1<numRows && !visited[centerX+1][centerY])
		{
			if(!visited[centerX+1][centerY] && !mineAlreadyInQueue[centerX+1][centerY] && !checkIfMineExits(centerX+1,centerY))
				mines.put(new Point(centerX+1,centerY),number);
			
			if(centerY-1>=0 && !visited[centerX+1][centerY-1] && !mineAlreadyInQueue[centerX+1][centerY-1] && !checkIfMineExits(centerX+1,centerY-1))
			{
				if(!checkIfMineExits(centerX+1,centerY-1))
					mines.put(new Point(centerX+1,centerY-1),number);
			}
			if(centerY+1<numCols && !visited[centerX+1][centerY+1] && !mineAlreadyInQueue[centerX+1][centerY+1] && !checkIfMineExits(centerX+1,centerY+1))
			{
				if(!checkIfMineExits(centerX+1,centerY+1))
					mines.put(new Point(centerX+1,centerY+1),number);
			}
			
		}
		
	}*/
	public boolean checkIfMineExits(int x1,int y1,int a)
	{
		//System.out.println("already exists");
		for (java.util.Map.Entry<Point, Integer> entry : mines.entrySet())
		{
			if((int)entry.getKey().getX()==x1 && (int)entry.getKey().getY()==y1)
			{
				//System.out.println("already exists");
				mines.put(entry.getKey(), entry.getValue()+a);
				
				return true;
			}
		}
		return false;
	
		
	}
	public void unCoverEverything()
	{
		for(int i=0;i<numCols;i++)
		{
			for(int j=0;j<numRows;j++)
			{
				if(m[i][j]==-1)
				{
					safe.add(new Point(i,j));
				}
			}
		}
		
	}
	public boolean removeMineIfExists(int x1,int y1)
	{
		//System.out.println("already exists");
		Point toRemove=null;
		for (java.util.Map.Entry<Point, Integer> entry : mines.entrySet())
		{
			if((int)entry.getKey().getX()==x1 && (int)entry.getKey().getY()==y1)
			{
				//System.out.println("already exists");
				//mines.put(entry.getKey(), entry.getValue()+1);
				toRemove=entry.getKey();
				mines.remove(toRemove);
				break;
			}
		}
		return true;
	}
	public void printWorld()
	{
		
		for(int i=0;i<numCols;i++)
		{
			for(int j=0;j<numRows;j++)
			{
				System.out.print(m1[i][j]+"\t ");
			}
			System.out.println();
		}
		return;
	}
	public Point findTheMax()
	{
		int max=Integer.MIN_VALUE;
		Point toRemove=null;
		for (java.util.Map.Entry<Point, Integer> entry : mines.entrySet())
		{
			if(entry.getValue()>=max)
			{
				max=entry.getValue();
				flag_x=(int)entry.getKey().getX();
				flag_y=(int)entry.getKey().getY();
				toRemove=entry.getKey();
				
			}
		}
		mines.remove(toRemove);
		
		return new Point(flag_x,flag_y);
	}
	/*public Point uncoverRemainingMines()
	{
		Point temp;
		for (java.util.Map.Entry<Point, Integer> entry : mines.entrySet())
		{
			temp=entry.getKey();
			mines.remove(entry.getKey());
			return temp;
		}
		return null;
		
		
	}
	public Point checkForUnvisitedNodes()
	{
		for(int i=0;i<numRows;i++)
		{
			for(int j=0;j<numCols;j++)
			{
				if(!visited[i][j])
					return new Point(i,j);
			}
		}
		return null;
	}*/
	
	public String checkSurroundingMines(int x,int y)
	{
		int centerNumber=m[x][y];
		int noOfMines=0;
		List<Point> notDisclosed=new ArrayList();
		
		if(x-1>=0)
		{
			if(y-1>=0)
			{
				noOfMines=common(notDisclosed,noOfMines,x-1,y-1);
				//noOfMines=common(notDiscolsed,noOfMines,x-1,y-1);
			}
			if(y+1<numRows)
			{
				noOfMines=common(notDisclosed,noOfMines,x-1,y+1);
			}
			noOfMines=common(notDisclosed,noOfMines,x-1,y);
		}
		if(x+1<numCols)
		{
			if(y-1>=0)
			{
				noOfMines=common(notDisclosed,noOfMines,x+1,y-1);
			}
			if(y+1<numRows)
			{
				noOfMines=common(notDisclosed,noOfMines,x+1,y+1);
			}
			noOfMines=common(notDisclosed,noOfMines,x+1,y);
		}
		if(y-1>=0)
		{
			noOfMines=common(notDisclosed,noOfMines,x,y-1);
		}
		if(y+1<numRows)
		{
			noOfMines=common(notDisclosed,noOfMines,x,y+1);
		}
		int finalDec=centerNumber-noOfMines;
		if(noOfMines>0 && (finalDec)==0)
		{
			//System.out.println("inside uncover");
			if(notDisclosed.size()==0)
				return "NOTHING";
			//System.out.println(notDisclosed);
			for(int i=0;i<notDisclosed.size();i++)
			{
				
				safe.add(notDisclosed.get(i));
				//System.out.println(safe);
			}
			notDisclosed.clear();
			
			return "UNCOVER";
			
			//add all not disclosed to safe
		}
		else if(finalDec == notDisclosed.size())
		{
			//System.out.println("inside flag");
			
			for(int i=0;i<notDisclosed.size();i++)
			{
				notSafe.add(notDisclosed.get(i));
			}
			notDisclosed.clear();
			
			return "FLAG";
			//flag those for sure;
		}
		else
		{
			if(alreadyAffectedDueToThisPoint[x][y]==0 || alreadyAffectedDueToThisPoint[x][y]!=finalDec)
			{
				finalDec=finalDec-alreadyAffectedDueToThisPoint[x][y];
			for(int i=0;i<notDisclosed.size();i++)
			{
				if(checkIfMineExits((int)notDisclosed.get(i).getX(), (int)notDisclosed.get(i).getY(),finalDec))
				{
					
				}
				else
				{
					mines.put(notDisclosed.get(i),finalDec);
					
				}
				
			}
			alreadyAffectedDueToThisPoint[x][y]=alreadyAffectedDueToThisPoint[x][y]+finalDec;
		}
		}
		//System.out.println(mines);
		notDisclosed.clear();
		//System.out.println(centerNumber + " heloooo " + noOfMines);
		return "NOTHING";
		
	}
	public int common(List<Point> notDisclosed,int noOfMines,int x,int y)
	{
		if(m[x][y]==-2)
		{
			noOfMines++;
		}
		else if(m[x][y]==-1)
		{
			notDisclosed.add(new Point(x,y));
		}
		return noOfMines;
	}
	public void updateM1(int x,int y)
	{
		
		if(x-1>=0)
		{
			if(y-1>=0)
			{
				if(m1[x-1][y-1]>0)
				{
					m1[x-1][y-1]-=1;
				}
				//noOfMines=common(notDiscolsed,noOfMines,x-1,y-1);
			}
			if(y+1<numRows)
			{
				if(m1[x-1][y+1]>0)
				{
					m1[x-1][y+1]-=1;
				}			}
			if(m1[x-1][y]>0)
			{
				m1[x-1][y]-=1;
			}
		}
		if(x+1<numCols)
		{
			if(y-1>=0)
			{
				if(m1[x+1][y-1]>0)
				{
					m1[x+1][y-1]-=1;
				}
			}
			if(y+1<numRows)
			{
				if(m1[x+1][y+1]>0)
				{
					m1[x+1][y+1]-=1;
				}
			}
			if(m1[x+1][y]>0)
			{
				m1[x+1][y]-=1;
			}
		}
		if(y-1>=0)
		{
			if(m1[x][y-1]>0)
			{
				m1[x][y-1]-=1;
			}
		}
		if(y+1<numRows)
		{
			if(m1[x][y+1]>0)
			{
				m1[x][y+1]-=1;
			}
		}
		
		
	}
	public void updateM1AfterUncovering(int x,int y)
	{
		
		if(x-1>=0)
		{
			if(y-1>=0)
			{
				if(m1[x-1][y-1]==-2)
				{
					m1[x][y]-=1;
				}
				//noOfMines=common(notDiscolsed,noOfMines,x-1,y-1);
			}
			if(y+1<numRows)
			{
				if(m1[x-1][y+1]==-2)
				{
					m1[x][y]-=1;
				}			}
			if(m1[x-1][y]==-2)
			{
				m1[x][y]=-1;
			}
		}
		if(x+1<numCols)
		{
			if(y-1>=0)
			{
				if(m1[x+1][y-1]==-2)
				{
					m1[x][y]-=1;
				}
			}
			if(y+1<numRows)
			{
				if(m1[x+1][y+1]==-2)
				{
					m1[x][y]-=1;
				}
			}
			if(m1[x+1][y]==-2)
			{
				m1[x][y]-=1;
			}
		}
		if(y-1>=0)
		{
			if(m1[x][y-1]==-2)
			{
				m1[x][y]-=1;
			}
		}
		if(y+1<numRows)
		{
			if(m1[x][y+1]==-2)
			{
				m1[x][y]-=1;
			}
		}
		
		
	}
	public Point checkFor12Pattern()
	{
		System.out.println("inside 12");
		for(int i=0;i<numCols;i++)
		{
			for(int j=0;j<numRows;j++) {
				
				if(m1[i][j]==1)
				{
					System.out.println(i+"    "+j);
					if(i-1>=0 && m1[i-1][j]==2)
					{
						
						System.out.println("found 21---"+m1[i-1][j]);
						//printWorld();
						boolean success;
						if(i-2>=0 && j-1>=0 && i+1<numCols && m1[i-2][j-1]==-1 && m1[i-1][j-1]==-1 && m1[i][j-1]==-1 && m1[i+1][j-1]==-1 && m1[i+1][j]>-1 && m1[i-2][j]>-1 )
						{
							System.out.println("-");
							return new Point(i-2,j-1);
							//success=false;
						}
						//if(!success)
						//{
						if(i-2>=0 && j+1<numRows && m1[i-2][j+1]==-1 && m1[i-1][j+1]==-1 && m1[i][j+1]==-1 && i+1<numCols && m1[i+1][j+1]==-1 && m1[i+1][j]>-1 && m1[i-2][j]>-1 )
						{
							System.out.println("--");
							return new Point(i-2,j+1);
						}
						//checkForWall(i,j,i-1,j);
					}
					if(i+1<numCols && m1[i+1][j]==2)
					{
						//boolean success=true;
						System.out.println("found 22");
						if(i+2<numCols && j-1>=0 && m1[i+2][j-1]==-1 && m1[i+1][j-1]==-1 && m1[i][j-1]==-1 && i-1>=0 && m1[i-1][j-1]==-1 && m1[i+2][j]>-1 && m1[i-1][j]>-1)
						{
							return new Point(i+2,j-1);
						}
						//if(!success)
						//{
						if(i+2<numCols && j+1<numRows && m1[i+2][j+1]==-1 && m1[i+1][j+1]==-1 && m1[i][j+1]==-1 && i-1>=0 && m1[i-1][j+1]==-1 && m1[i+2][j]>-1 && m1[i-1][j]>-1)
				
						{
							return new Point(i+2,j+1);
						}
			
					}
					if(j-1>=0 && m1[i][j-1]==2)
					{
						System.out.println("found 23");
						if(i-1>=0 && j-2>=0 && m1[i-1][j-2]==-1 && m1[i-1][j-1]==-1 && m1[i-1][j]==-1 && j+1<numRows && m1[i-1][j+1]==-1 && m1[i][j+1]>-1 && m1[i][j-2]>-1)
						{
							return new Point(i-1,j-2);
						}
						//if(!success)
						//{
						if(i+1<numCols && j-2>=0 && m1[i+1][j-2]==-1 && m1[i+1][j-1]==-1 && m1[i+1][j]==-1 && j+1<numRows && m1[i+1][j+1]==-1 && m1[i][j+1]>-1 && m1[i][j-2]>-1)
						
						{
							return new Point(i+1,j-2);
						}
					}
					if(j+1<numRows && m1[i][j+1]==2)
					{
						System.out.println("found 24");
						//boolean success=true;
						if(i-1>=0 && j-1>=0 && m1[i-1][j-1]==-1 && m1[i-1][j]==-1 && m1[i-1][j+1]==-1 && j+2<numRows && m1[i-1][j+2]==-1 && m1[i][j+2]>-1 && m1[i][j-1]>-1)
						{
							return new Point(i-1,j+2);
						}
						//if(!success)
						//{
						if(i+1<numCols && j-1>=0 && m1[i+1][j-1]==-1 && m1[i+1][j]==-1 && m1[i+1][j+1]==-1 && j+2<numRows && m1[i+1][j+2]==-1 && m1[i][j+2]>-1 && m1[i][j-1]>-1)
						
						{
							return new Point(i+1,j+2);
						}
		
					}
				}
				
			}
		}
		return null;
	}
	public Point checkFor121Pattern()
	{
		System.out.println("inside 121");
		for(int i=0;i<numCols;i++)
		{
			for(int j=0;j<numRows;j++) {
				
				if(m1[i][j]==1)
				{
					System.out.println(i+"    "+j);
					if(i-2>=0 && m1[i-1][j]==2 && m1[i-2][j]==1)
					{
						
						System.out.println("found 21---"+m1[i-1][j]);
						//printWorld();
						//boolean success;
						if(i-2>=0 && j-1>=0 && i+1<numCols && m1[i-2][j-1]==-1 && m1[i-1][j-1]==-1 && m1[i][j-1]==-1)
						{
							System.out.println("-");
							notSafe.add(new Point(i,j-1));
							return new Point(i-2,j-1);
							//success=false;
						}
						//if(!success)
						//{
						if(i-2>=0 && j+1<numRows && m1[i-2][j+1]==-1 && m1[i-1][j+1]==-1 && m1[i][j+1]==-1)
						{
							System.out.println("--");
							notSafe.add(new Point(i,j+1));
							return new Point(i-2,j+1);
						}
						//checkForWall(i,j,i-1,j);
					}
					/*if(i+<numCols && m1[i+1][j]==2)
					{
						//boolean success=true;
						System.out.println("found 22");
						if(i+2<numCols && j-1>=0 && m1[i+2][j-1]==-1 && m1[i+1][j-1]==-1 && m1[i][j-1]==-1 && i-1>=0 && m1[i-1][j-1]==-1)
						{
							return new Point(i+2,j-1);
						}
						//if(!success)
						//{
						if(i+2<numCols && j+1<numRows && m1[i+2][j+1]==-1 && m1[i+1][j+1]==-1 && m1[i][j+1]==-1 && i-1>=0 && m1[i-1][j-1]==-1)
				
						{
							return new Point(i+2,j+1);
						}
			
					}*/
					if(j-2>=0 && m1[i][j-1]==2 && m1[i][j-2]==1)
					{
						System.out.println("found 23");
						if(i-1>=0 && j-2>=0 && m1[i-1][j-2]==-1 && m1[i-1][j-1]==-1 && m1[i-1][j]==-1)
						{
							notSafe.add(new Point(i-1,j));
							return new Point(i-1,j-2);
						}
						//if(!success)
						//{
						if(i+1<numCols && j-2>=0 && m1[i+1][j-2]==-1 && m1[i+1][j-1]==-1 && m1[i+1][j]==-1 )
						
						{
							notSafe.add(new Point(i+1,j));
							return new Point(i+1,j-2);
						}
					}
					/*if(j+1<numRows && m1[i][j+1]==2)
					{
						System.out.println("found 24");
						//boolean success=true;
						if(i-1>=0 && j-1>=0 && m1[i-1][j-1]==-1 && m1[i-1][j]==-1 && m1[i-1][j+1]==-1 && j+2<numRows && m1[i-1][j+2]==-1)
						{
							return new Point(i-1,j+2);
						}
						//if(!success)
						//{
						if(i+1<numCols && j-1>=0 && m1[i+1][j-1]==-1 && m1[i+1][j]==-1 && m1[i+1][j+1]==-1 && j+2<numRows && m1[i+1][j+2]==-1)
						
						{
							return new Point(i+1,j+2);
						}
		
					}*/
				}
				
			}
		}
		return null;
	}
	public Point checkFor1221Pattern()
	{
		System.out.println("inside 1221");
		for(int i=0;i<numCols;i++)
		{
			for(int j=0;j<numRows;j++) {
				
				if(m1[i][j]==1)
				{
					System.out.println(i+"    "+j);
					if(i-3>=0 && m1[i-1][j]==2 && m1[i-2][j]==2 && m1[i-2][j]==1)
					{
						
						System.out.println("found 21---"+m1[i-1][j]);
						//printWorld();
						//boolean success;
						if(i-3>=0 && j-1>=0 && m1[i-3][j-1]==-1 && m1[i-2][j-1]==-1 && m1[i-1][j-1]==-1 && m1[i][j-1]==-1)
						{
							System.out.println("-");
							notSafe.add(new Point(i-1,j-1));
							return new Point(i-2,j-1);
							//success=false;
						}
						//if(!success)
						//{
						if(i-3>=0 && j+1<numRows && m1[i-3][j+1]==-1 && m1[i-2][j+1]==-1 && m1[i-1][j+1]==-1 && m1[i][j+1]==-1)
						{
							System.out.println("--");
							notSafe.add(new Point(i-3,j+1));
							return new Point(i-2,j+1);
						}
						//checkForWall(i,j,i-1,j);
					}
					/*if(i+<numCols && m1[i+1][j]==2)
					{
						//boolean success=true;
						System.out.println("found 22");
						if(i+2<numCols && j-1>=0 && m1[i+2][j-1]==-1 && m1[i+1][j-1]==-1 && m1[i][j-1]==-1 && i-1>=0 && m1[i-1][j-1]==-1)
						{
							return new Point(i+2,j-1);
						}
						//if(!success)
						//{
						if(i+2<numCols && j+1<numRows && m1[i+2][j+1]==-1 && m1[i+1][j+1]==-1 && m1[i][j+1]==-1 && i-1>=0 && m1[i-1][j-1]==-1)
				
						{
							return new Point(i+2,j+1);
						}
			
					}*/
					if(j-3>=0 && m1[i][j-1]==2 && m1[i][j-2]==2 && m1[i][j-2]==1)
					{
						System.out.println("found 23");
						if(i-1>=0 && j-3>=0 && m1[i-1][j-2]==-1 && m1[i-1][j-1]==-1 && m1[i-1][j]==-1 && m1[i-1][j-3]==-1)
						{
							notSafe.add(new Point(i-1,j-1));
							return new Point(i-1,j-2);
						}
						//if(!success)
						//{
						if(i+1<numCols && j-3>=0 && m1[i+1][j-2]==-1 && m1[i+1][j-1]==-1 && m1[i+1][j]==-1 && m1[i+1][j-3]==-1)
						
						{
							notSafe.add(new Point(i+1,j-1));
							return new Point(i+1,j-2);
						}
					}
					/*if(j+1<numRows && m1[i][j+1]==2)
					{
						System.out.println("found 24");
						//boolean success=true;
						if(i-1>=0 && j-1>=0 && m1[i-1][j-1]==-1 && m1[i-1][j]==-1 && m1[i-1][j+1]==-1 && j+2<numRows && m1[i-1][j+2]==-1)
						{
							return new Point(i-1,j+2);
						}
						//if(!success)
						//{
						if(i+1<numCols && j-1>=0 && m1[i+1][j-1]==-1 && m1[i+1][j]==-1 && m1[i+1][j+1]==-1 && j+2<numRows && m1[i+1][j+2]==-1)
						
						{
							return new Point(i+1,j+2);
						}
		
					}*/
				}
				
			}
		}
		return null;
	}
	public Point checkFor11Pattern()
	{
		System.out.println("entered 11");
		for(int i=0;i<numCols;i++)
		{
			
			for(int j=0;j<numRows;j++)
			{
				
				if((i==0 || j==0 || i==numCols-1 || j==numRows-1) && m1[i][j]==1)
				{
					
					if(i==0 && m1[1][j]==1)
					{
						if(j+1<numRows && m1[i][j+1]==-1 && m1[i+1][j+1]==-1 && m1[i+2][j+1]==-1 && unClosed(i,j)==2 && unClosed(i+1,j)==3)
						{
							return new Point(i+2,j+1);
							
						}
						if(j-1>=0 && m1[i][j-1]==-1 && m1[i+1][j-1]==-1 && m1[i+2][j-1]==-1 && unClosed(i,j)==2 && unClosed(i+1,j)==3)
						{
							return new Point(i+2,j-1);
							
						}
						
						
					}
					if(j==0 && m1[i][1]==1)
					{
						if(i+1<numCols && m1[i+1][j]==-1 && m1[i+1][j+1]==-1 && m1[i+1][j+2]==-1 && unClosed(i,j)==2 && unClosed(i,j+1)==3)
						{
							return new Point(i+1,j+2);
							
						}
						if(i-1>=0 && m1[i-1][j]==-1 && m1[i-1][j+1]==-1 && m1[i-1][j+2]==-1 && unClosed(i,j)==2 && unClosed(i,j+1)==3)
						{
							return new Point(i-1,j+2);
							
						}
						
						
					}
					if(i==numCols-1 && m1[numCols-2][j]==1)
					{
						if(j+1<numRows && m1[i][j+1]==-1 && m1[i-1][j+1]==-1 && m1[i-2][j+1]==-1 && unClosed(i,j)==2 && unClosed(i-1,j)==3)
						{
							return new Point(i-2,j+1);
							
						}
						if(j-1>=0 && m1[i][j-1]==-1 && m1[i-1][j-1]==-1 && m1[i-2][j-1]==-1 && unClosed(i,j)==2 && unClosed(i-1,j)==3)
						{
							return new Point(i-2,j-1);
							
						}
						
						
					}
					if(j==numRows-1 && m1[i][numRows-2]==1)
					{
						if(i+1<numCols && m1[i+1][j]==-1 && m1[i+1][j-1]==-1 && m1[i+1][j-2]==-1 && unClosed(i,j)==2 && unClosed(i,j-1)==3)
						{
							return new Point(i+1,j-2);
							
						}
						if(i-1>=0 && m1[i-1][j]==-1 && m1[i-1][j-1]==-1 && m1[i-1][j-2]==-1 && unClosed(i,j)==2 && unClosed(i,j-1)==3)
						{
							return new Point(i-1,j-2);
							
						}
						
						
					}
					
					
				}
				
			}
		}
		return null;
		
		
	}
	public int unClosed(int x,int y)
	{
		int count=0;
			
			if(x-1>=0)
			{
				if(y-1>=0)
				{
					if(m1[x-1][y-1]==-1)
					{
						count++;
					}
					//noOfMines=common(notDiscolsed,noOfMines,x-1,y-1);
				}
				if(y+1<numRows)
				{
					if(m1[x-1][y+1]==-1)
					{
						count++;
					}			}
				if(m1[x-1][y]==-1)
				{
					count++;
				}
			}
			if(x+1<numCols)
			{
				if(y-1>=0)
				{
					if(m1[x+1][y-1]==-1)
					{
						count++;
					}
				}
				if(y+1<numRows)
				{
					if(m1[x+1][y+1]==-1)
					{
						count++;
					}
				}
				if(m1[x+1][y]==-1)
				{
					count++;
				}
			}
			if(y-1>=0)
			{
				if(m1[x][y-1]==-1)
				{
					count++;
				}
			}
			if(y+1<numRows)
			{
				if(m1[x][y+1]==-1)
				{
					count++;
				}
			}
			
			
		return count;
		
	}
//	public boolean checkForWall(int a1,int b1,int a2,int b2)
//	{
		
//	}
	

	// ################### Helper Functions Go Here (optional) ##################
	// ...
}
