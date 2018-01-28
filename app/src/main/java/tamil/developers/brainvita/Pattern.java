package tamil.developers.brainvita;

class Pattern {
	int[][] Patterns = {
		{0,0,1,1,1,0,0,
		0,0,1,1,1,0,0,
		1,1,1,1,1,1,1,
		1,1,1,2,1,1,1,
		1,1,1,1,1,1,1,
		0,0,1,1,1,0,0,
		0,0,1,1,1,0,0}, 
				{0,0,1,1,1,0,0,
				0,1,1,1,1,1,0,
				1,1,1,1,1,1,1,
				1,1,1,2,1,1,1,
				1,1,1,1,1,1,1,
				0,1,1,1,1,1,0,
				0,0,1,1,1,0,0}, 
		{0,0,1,0,1,0,0,
		0,1,1,1,1,1,0,
		1,1,1,1,1,1,1,
		1,1,1,2,1,1,1,
		1,1,1,1,1,1,1,
		0,1,1,1,1,1,0,
		0,0,1,0,1,0,0},
				{0,0,1,0,1,0,0,
				0,1,1,1,1,1,0,
				1,1,1,1,1,1,1,
				0,1,1,2,1,1,0,
				1,1,1,1,1,1,1,
				0,1,1,1,1,1,0,
				0,0,1,0,1,0,0},
		{0,0,0,1,0,0,0,
		0,0,1,1,1,0,0,
		1,1,1,1,1,1,1,
		1,1,1,2,1,1,1,
		1,1,1,1,1,1,1,
		0,0,1,1,1,0,0,
		0,0,0,1,0,0,0},
				{0,0,0,1,0,0,0,
				0,0,1,1,1,0,0,
				1,1,1,1,1,1,1,
				0,1,1,2,1,1,0,
				1,1,1,1,1,1,1,
				0,0,1,1,1,0,0,
				0,0,0,1,0,0,0},
		{0,1,0,1,0,
		0,1,1,1,0,
		1,1,1,1,1,
		1,1,2,1,1,
		1,1,1,1,1,
		0,1,1,1,0,
		0,1,0,1,0},
				{0,1,1,1,0,
				0,1,1,1,0,
				1,1,1,1,1,
				1,1,2,1,1,
				1,1,1,1,1,
				0,1,1,1,0,
				0,1,1,1,0},
		{0,0,1,0,0,
		1,1,1,1,1,
		0,1,2,1,0,
		1,1,1,1,1,
		0,0,1,0,0},/*
				{1,1,1,1,1,
				1,1,1,1,1,
				1,1,1,1,1,
				1,1,2,1,1,
				1,1,1,1,1},*/
		{0,0,0,1,1,1,0,0,0,
		0,0,0,1,1,1,0,0,0,
		0,0,0,1,1,1,0,0,0,
		1,1,1,1,1,1,1,1,1,
		1,1,1,1,2,1,1,1,1,
		1,1,1,1,1,1,1,1,1,
		0,0,0,1,1,1,0,0,0,
		0,0,0,1,1,1,0,0,0,
		0,0,0,1,1,1,0,0,0} 
	};
	int[][] rowCol = {{7,7},{7,7},{7,7},{7,7},{7,7},{7,7},{7,5},{7,5},{5,5},{9,9}};
	int coinCount(int game){
		int count = 0;
		for (int i=0; i<Patterns[game].length;i++){
			if (Patterns[game][i] > 0) count++;
		}
		return count;
	}
	String movement(int game){
		String moving = "";
		String moving1 = "";
		int[] pattern = Patterns[game];
		int r = rowCol[game][0];
		int c = rowCol[game][1];
		for (int i=0; i<pattern.length; i++){
			if (pattern[i] > 0){
				int x = i % c; 
				int y = i / c;
				if (y-1 > 0){
					if (pattern[i-(2*c)] > 0 && pattern[i-c] > 0){
						if (moving1.equals("")) moving1 = "" + seq(game,i-(2*c));
						else moving1 = moving1 + ":" + seq(game,i-(2*c));
					}
				}
				if (x-1 > 0){
					if (pattern[i-2] > 0 && pattern[i-1] > 0){
						if (moving1.equals("")) moving1 = "" + seq(game,i-2);
						else moving1 = moving1 + ":" + seq(game,i-2);
					}
				}
				if (x+2 < c){
					if (pattern[i+2] > 0 && pattern[i+1] > 0){
						if (moving1.equals("")) moving1 = "" + seq(game,i+2);
						else moving1 = moving1 + ":" + seq(game,i+2);
					}
				}
				if (y+2 < r){
					if (pattern[i+(2*c)] > 0 && pattern[i+c] > 0){
						if (moving1.equals("")) moving1 = "" + seq(game,i+(2*c));
						else moving1 = moving1 + ":" + seq(game,i+(2*c));
					}
				}
				moving = moving + moving1 + "=";
				moving1 = "";
			}
		}
		moving = moving.substring(0, moving.length()-1);
		return moving;
	}
	private int seq(int game, int to){
		int count = 0;
		for (int i=0; i<=to;i++){
			if (Patterns[game][i] >0) count++;
		}
		return count;
	}
	
}
