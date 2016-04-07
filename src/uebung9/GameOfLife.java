package uebung9;
import java.util.Arrays;

public abstract class GameOfLife {

	// 4K is the future!
	public static final int WIDTH = 3840;
	public static final int HEIGHT = 2160;

	public static final int SIZE = WIDTH * HEIGHT;

	protected byte[] current = null;

	public void setStart(byte[] start) {
		current = Arrays.copyOf(start, SIZE);
	}

	public boolean isActive(int row, int column) {
		return current[getIndex(row, column)] != 0;
	}

	public abstract void iterate();

	public static int getIndex(int row, int column) {
		return row * WIDTH + column;
	}

	public static byte[] createArray() {
		return new byte[SIZE];
	}

}
