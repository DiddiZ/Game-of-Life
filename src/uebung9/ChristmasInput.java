package uebung9;
import java.util.Arrays;

public class ChristmasInput implements Input {

	// @formatter:off
	public static final byte[][] FLAKE = {
		{ 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0 },
		{ 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0 },
		{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		{ 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0 },
		{ 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0 },
		{ 0, 0, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 1, 0, 0 },
		{ 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 },
		{ 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 },
		{ 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1 },
		{ 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 },
		{ 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 },
	};
	// @formatter:on

	public static final int FLAKE_HEIGHT = FLAKE.length;
	public static final int FLAKE_WIDTH = FLAKE[0].length;
	public static final int SQUARE_SIZE = 55;

	@Override
	public byte[] get() {
		byte[] input = GameOfLife.createArray();
		Arrays.fill(input, (byte) 0);

		for (int squareRow = 1; squareRow < GameOfLife.HEIGHT - SQUARE_SIZE; squareRow += SQUARE_SIZE) {
			for (int squareColumn = 1; squareColumn < GameOfLife.WIDTH
					- SQUARE_SIZE; squareColumn += SQUARE_SIZE) {
				// place one flake...
				int row = squareRow + getRowOffset();
				int column = squareColumn + getColumnOffset();

				placeFlake(input, row, column);
			}
		}

		return input;
	}

	private static void placeFlake(byte[] input, int row, int column) {
		for (int flakeRow = 0; flakeRow < FLAKE_HEIGHT; flakeRow++) {
			int inputRow = row + flakeRow;
			for (int flakeColumn = 0; flakeColumn < FLAKE_WIDTH; flakeColumn++) {
				int inputColumn = column + flakeColumn;
				int inputIndex = GameOfLife.getIndex(inputRow, inputColumn);

				input[inputIndex] = FLAKE[flakeRow][flakeColumn];
			}
		}
	}

	private static int getRowOffset() {
		return getOffset(FLAKE_HEIGHT);
	}

	private static int getColumnOffset() {
		return getOffset(FLAKE_WIDTH);
	}

	/**
	 * @return random offset in [1; SQUARE_SIZE - flake - 2] to not disturb
	 *         other snowflakes
	 */
	private static int getOffset(int flake) {
		return 1 + (int) (Math.random() * (SQUARE_SIZE - flake - 2));
	}

}
