package environment;

import game.Game;

public class Coordinate {
	public final int x;
	public final int y;

	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	@Override
	public boolean equals(Object obj) {
		Coordinate other = (Coordinate) obj;
		return other.x==x && other.y == y;
	}
	
	public double distanceTo(Coordinate other) {
		double dx = y - other.y;
		double dy = x - other.x;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public static Coordinate getRandomCoordinate(){
		return new Coordinate((int)(Math.random()* Game.DIMX),(int)(Math.random()*Game.DIMY));
	}

	public static boolean isValid(Coordinate coordinate){
		return coordinate.x >= 0 && coordinate.y >= 0 && coordinate.x < Game.DIMX && coordinate.y < Game.DIMY;
	}

	public Coordinate translate(Coordinate vector) {
		return new Coordinate(x+vector.x, y+vector.y);
	}
}
