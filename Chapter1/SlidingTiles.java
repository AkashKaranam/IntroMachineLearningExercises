import java.io.*;
import java.util.*;

public class SlidingTiles {
    public static void main(String[] args) {
        int[][] initialGrid = {
            {-1,2,1}, 
            {6,7,4},
            {3,8,5}
        };

        int[][] finalGrid = {
            {1,2,3},
            {8,-1,4},
            {7,6,5}
        };

        State initialState = new State(initialGrid);
        State finalState = new State(finalGrid);

        search(initialState, finalState);



    }

    public static void search(State initialState, State finalState)
    {
        HashSet<State> seen = new HashSet<State>();
        Deque<State> queue = new LinkedList<State>();
        ArrayList<State> children = new ArrayList<State>();
        queue.addLast(initialState);
        seen.add(initialState);
        int iterations = 0;
        while(!queue.isEmpty())
        {
            State current = queue.pollFirst();
            iterations++;
            System.out.println(current.toString());

            System.out.println("");
            System.out.println("-----------------------");
            System.out.println("");
    
            if(current.equals(finalState))
            {
                System.out.println("Reached final state in " + iterations + " iterations");
                return;
            }
                

            int[] neighbors = new int[]{1,0,-1,0,0,1,0,-1};
            int blankRow = current.blankLocation()[0];
            int blankCol = current.blankLocation()[1];
            children.clear();
            for(int i=0;i<neighbors.length;i+=2)
            {
                
                int nextRow = blankRow + neighbors[i];
                int nextCol = blankCol + neighbors[i+1];
                if(nextRow < 0 || nextRow >= initialState.getGrid().length)
                    continue;
                else if(nextCol < 0 || nextCol >= initialState.getGrid()[0].length)
                    continue;
                else
                {
                    State newState = transition(current, blankRow, blankCol, nextRow, nextCol);
                    children.add(newState);
                }
                    
            }

            if(children.size() == 0)
                System.out.println("Reached a dead end");
            else
            {
                State bestChild = null;
                int minDist = Integer.MAX_VALUE;
                for(State child: children)
                {
                    if(child.computeManhattanDist(finalState) < minDist && !seen.contains(child))
                    {
                        bestChild = child;
                        minDist = child.computeManhattanDist(finalState);
                    }
                }

                seen.add(bestChild);
                queue.addLast(bestChild);
            }



            
        }


    }

    public static State transition(State current, int blankRow, int blankCol, int nextRow, int nextCol)
    {
        int[][] currentGrid = current.getGrid();
        int[][] newGrid = new int[currentGrid.length][currentGrid[0].length];
        for(int i=0;i<currentGrid.length;i++)
        {
            for(int j=0;j<currentGrid[i].length;j++)
            {
                newGrid[i][j] = currentGrid[i][j];
                
            }
        }

        int temp = newGrid[blankRow][blankCol];
        newGrid[blankRow][blankCol] = newGrid[nextRow][nextCol];
        newGrid[nextRow][nextCol] = temp;

        return new State(newGrid);
    }

}

class State
{
    public int[][] grid;
    
    public State(int[][] grid)
    {
        this.grid = grid;
    }

    public int[][] getGrid()
    {
        return this.grid;
    }

    @Override
    public boolean equals(Object o)
    {
        if(!(o instanceof State))
            return false;

        State oState = (State) o;
        if(this.grid.length != oState.grid.length)
            return false;
        if(this.grid[0].length != oState.grid[0].length)
            return false;
        for(int i=0;i<this.grid.length;i++)
        {
            for(int j=0;j<this.grid[i].length;j++)
            {
                if(this.grid[i][j] != oState.grid[i][j])
                    return false;
            }
        }

        return true;
        
    }

    public int cellNumber(int row, int col)
    {
        return row * this.grid[0].length + col + 1;
    }

    @Override
    public int hashCode()
    {
        int result = 0;
        for(int i=0;i<this.grid.length;i++)
        {
            for(int j=0;j<this.grid[i].length;j++)
            {
                result+= (this.grid[i][j] * cellNumber(i,j));
            }
        }

        return result;
    }

    @Override
    public String toString()
    {
        String res = "";
        for(int i=0;i<this.grid.length;i++)
        {
            for(int j=0;j<this.grid[i].length;j++)
            {
                res += this.grid[i][j] + " ";
            }
            res += "\n";
        }

        return res;
    }

    public int[] blankLocation()
    {
        for(int i=0;i<this.grid.length;i++)
        {
            for(int j=0;j<this.grid[i].length;j++)
            {
                if(this.grid[i][j] == -1)
                    return new int[]{i,j};
            }
        }
        // should never be reached
        return new int[]{-1,-1};
    }

    public int computeManhattanDist(State finalState)
    {
        int dist = 0;
        HashMap<Integer, Pair> currentLoc = new HashMap<Integer, Pair>();
        HashMap<Integer, Pair> finalLoc = new HashMap<Integer, Pair>();

        for(int i=0;i<this.grid.length;i++)
        {
            for(int j=0;j<this.grid[i].length;j++)
            {
                currentLoc.put(this.grid[i][j], new Pair(i,j));
            }
        }

        for(int i=0;i<finalState.grid.length;i++)
        {
            for(int j=0;j<finalState.grid[i].length;j++)
            {
                finalLoc.put(finalState.grid[i][j], new Pair(i,j));
            }
        }

        int[] values = new int[]{1,2,3,4,5,6,7,8};
        for(Integer val: values)
        {
            Pair finalValLoc = finalLoc.get(val);
            Pair currentValLoc = currentLoc.get(val);

            dist += Math.abs(finalValLoc.getFirst() - currentValLoc.getFirst()) + Math.abs(finalValLoc.getSecond() - currentValLoc.getSecond());
        }

        return dist;

    }

    public int computeDiff(State finalState)
    {
        int dist = 0;

        HashMap<Integer, Pair> currentLoc = new HashMap<Integer, Pair>();
        HashMap<Integer, Pair> finalLoc = new HashMap<Integer, Pair>();

        for(int i=0;i<this.grid.length;i++)
        {
            for(int j=0;j<this.grid[i].length;j++)
            {
                currentLoc.put(this.grid[i][j], new Pair(i,j));
            }
        }

        for(int i=0;i<finalState.grid.length;i++)
        {
            for(int j=0;j<finalState.grid[i].length;j++)
            {
                finalLoc.put(finalState.grid[i][j], new Pair(i,j));
            }
        }

        int[] values = new int[]{1,2,3,4,5,6,7,8};
        for(Integer val: values)
        {
            if(!currentLoc.get(val).equals(finalLoc.get(val)))
                dist+=1;
        }

        return dist;

    }

}

class Pair
{
    int first;
    int second;

    public Pair(int first, int second)
    {
        this.first = first;
        this.second = second;
    }

    public int getFirst()
    {
        return this.first;
    }

    public int getSecond()
    {
        return this.second;
    }

    public String toString()
    {
        return this.first + " " + this.second;
    }

    public boolean equals(Object o)
    {
        if(!(o instanceof Pair))
            return false;
        
        Pair oPair = (Pair) o;

        if(this.getFirst() != oPair.getFirst())
            return false;
        
        if(this.getSecond() != oPair.getSecond())
            return false;
        
        return true;
    }
}