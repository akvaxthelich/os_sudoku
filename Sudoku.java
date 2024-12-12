import java.util.HashSet;

public class Sudoku {
	int[] mat[];
	int N; // number of columns/rows.
	int SRN; // square root of N
	int K; // No. Of missing digits
	int R; // No. Of remaining missing digits
	HashSet<String> missingLoc; // keeping track of missing locations
	
	// Constructor
	public Sudoku() {
		this.N = 9;
		this.K = 25;
		this.R = 25;

		// Compute square root of N
		Double SRNd = Math.sqrt(N);
		SRN = SRNd.intValue();

		mat = new int[N][N];
		
		missingLoc = new HashSet<String>();
	}

	// Sudoku Generator
	public void fillValues() {
		// Fill the diagonal of SRN x SRN matrices
		fillDiagonal();

		// Fill remaining blocks
		fillRemaining(0, SRN);

		// Remove Randomly K digits to make game
		removeKDigits();
	}

	// Fill the diagonal SRN number of SRN x SRN matrices
	private void fillDiagonal() {
		for (int i = 0; i < N; i = i + SRN)
			// for diagonal box, start coordinates->i==j
			fillBox(i, i);
	}

	// Returns false if given 3 x 3 block contains num.
	private boolean unUsedInBox(int rowStart, int colStart, int num) {
		for (int i = 0; i < SRN; i++)
			for (int j = 0; j < SRN; j++)
				if (mat[rowStart + i][colStart + j] == num){
					return false;
				}


		return true;
	}

	// Fill a 3 x 3 matrix.
	private void fillBox(int row, int col) {
		int num;
		for (int i = 0; i < SRN; i++) {
			for (int j = 0; j < SRN; j++) {
				do {
					num = randomGenerator(N);
				} while (!unUsedInBox(row, col, num));

				mat[row + i][col + j] = num;
			}
		}
	}

	// Random generator
	private int randomGenerator(int num) {
		return (int) Math.floor((Math.random() * num + 1));
	}

	// Check if safe to put in cell
	private boolean CheckIfSafe(int i, int j, int num) {
		return (unUsedInRow(i, num) && unUsedInCol(j, num) && unUsedInBox(i - i % SRN, j - j % SRN, num));
	}

	// check in the row for existence
	private boolean unUsedInRow(int i, int num) {
		for (int j = 0; j < N; j++)
			if (mat[i][j] == num){
				return false;
			}
		return true;
	}

	// check in the row for existence
	private boolean unUsedInCol(int j, int num) {
		for (int i = 0; i < N; i++)
			if (mat[i][j] == num){
				return false;
			}
		return true;
	}

	// A recursive function to fill remaining
	// matrix
	private boolean fillRemaining(int i, int j) {
		if (j >= N && i < N - 1) {
			i = i + 1;
			j = 0;
		}
		if (i >= N && j >= N)
			return true;

		if (i < SRN) {
			if (j < SRN)
				j = SRN;
		} else if (i < N - SRN) {
			if (j == (int) (i / SRN) * SRN)
				j = j + SRN;
		} else {
			if (j == N - SRN) {
				i = i + 1;
				j = 0;
				if (i >= N)
					return true;
			}
		}

		for (int num = 1; num <= N; num++) {
			if (CheckIfSafe(i, j, num)) {
				mat[i][j] = num;
				if (fillRemaining(i, j + 1))
					return true;

				mat[i][j] = 0;
			}
		}
		return false;
	}

	// Remove the K no. of digits to
	// complete game
	private void removeKDigits() {
		int count = K;
		while (count != 0) {
			int cellId = randomGenerator(N * N) - 1;

			// extract coordinates i and j
			int i = (cellId / N);
			int j = cellId % N;
			if (j != 0)
				j = j - 1;

			if (mat[i][j] != 0) {
				count--;
				mat[i][j] = 0;
				missingLoc.add(i + "-" + j);
			}
		}
	}

	// Return Sudoku board as a string
	public String getSudokuString() {
		StringBuffer sb = new StringBuffer();
		sb.append("     ");
		for (int j = 0; j < N; j++) {
			sb.append("[" + j + "] ");
		}
		sb.append(System.lineSeparator());
		for (int i = 0; i < N; i++) {
			sb.append("[" + i + "]   ");
			for (int j = 0; j < N; j++)
				sb.append(mat[i][j] + "   ");
			sb.append(System.lineSeparator());
		}
		sb.append(System.lineSeparator());
		return sb.toString();
	}
	
	// check if the location is updatable by a user
	private boolean isLocationUpdatable(int i, int j) {
		return missingLoc.contains(i + "-" + j);
	}
	
	// check if the Sudoku board is full
	public boolean isBoardFull() {
		return this.R == 0;
	}
	
	// Fill in sudoku by user

	//enterNumber(2,3,4)
	// colStart = Math.floor(2/9); = 0
	public boolean enterNumber(int i, int j, int num) {
		int rowStart = SRN * (int) Math.floor(i / SRN);
		int colStart = SRN * (int) Math.floor(j / SRN);
		System.out.println("["+i+","+j+"]:"+rowStart + "," +colStart);
		if(isLocationUpdatable(i, j) && unUsedInBox(rowStart, colStart, num)){
			if (unUsedInRow(i, num) && unUsedInCol(j, num) && (num >= 0) && (num <= 9)){ 
				//checks row, col, box to see if num is there plus checks to see if int is between 0 to 9
				mat[i][j] = num; //if it is put it in
				missingLoc.remove(i+"-"+j);
				this.R--;
				return true;
			}
			else { //if not, you can't enter number
				return false;
			}
			//start looking for rowstart and colstart
			//we know that dividing i by N and flooring returns the index [i] of the leftmost position
			//of the matrix
			// the same is true for j.

		}
		return false; // CHANGE RETURN VALUE
	}

	// Driver code
	public static void main(String[] args) {
		Sudoku sudoku = new Sudoku();
		sudoku.fillValues();
		System.out.println(sudoku.getSudokuString());
		for(int i = 0;i<9;i++){
			for(int j=0;j<9;j++){
				sudoku.enterNumber(i, j, 3);
			}
		}
	}
}