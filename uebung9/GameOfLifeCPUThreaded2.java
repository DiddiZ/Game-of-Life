import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GameOfLifeCPUThreaded2 extends GameOfLife
{
	protected byte[] temp;

	private static final int CHUNKS_X = 16, CHUNKS_Y = 16;
	private static final int CHUNK_WIDTH = WIDTH / CHUNKS_X, CHUNK_HEIGHT = HEIGHT / CHUNKS_Y;

	public GameOfLifeCPUThreaded2() {
		temp = createArray();
	}

	@Override
	public void iterate() {
		try {
			final ExecutorService executor = Executors.newWorkStealingPool();
			for (int x = 0; x < CHUNKS_X; x++)
				for (int y = 0; y < CHUNKS_Y; y++)
					executor.submit(new Worker(x * CHUNK_WIDTH, (x + 1) * CHUNK_WIDTH, y * CHUNK_HEIGHT, (y + 1) * CHUNK_HEIGHT));

			executor.shutdown();
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

			swapCurrentAndTemp();
		} catch (final InterruptedException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
			System.exit(1);
		}
	}

	protected int getNumberOfActiveNeighbors(int row, int column) {
		final int rowOffset = row * WIDTH;
		final int lowerRowOffset = row == 0 ? SIZE - WIDTH : rowOffset - WIDTH;
		final int upperRowOffset = row == HEIGHT - 1 ? 0 : rowOffset + WIDTH;
		final int lowerColumn = column == 0 ? WIDTH - 1 : column - 1;
		final int upperColumn = column == WIDTH - 1 ? 0 : column + 1;

		// Sum all neighbors to get the count of active neighbors
		return current[lowerRowOffset + lowerColumn] +
				current[lowerRowOffset + column] +
				current[lowerRowOffset + upperColumn] +
				current[rowOffset + lowerColumn] +
				current[rowOffset + upperColumn] +
				current[upperRowOffset + lowerColumn] +
				current[upperRowOffset + column] +
				current[upperRowOffset + upperColumn];
	}

	protected void swapCurrentAndTemp() {
		final byte[] newCurrent = temp;

		// re-use for next iteration
		temp = current;
		current = newCurrent;
	}

	private class Worker implements Runnable
	{
		private final int minX, maxX, minY, maxY;

		public Worker(int minX, int maxX, int minY, int maxY) {
			this.minX = minX;
			this.maxX = maxX;
			this.minY = minY;
			this.maxY = maxY;
		}

		@Override
		public void run() {
			for (int y = minY; y < maxY; y++)
				for (int x = minX; x < maxX; x++) {
					final int activeNeighbors = getNumberOfActiveNeighbors(y, x);
					// A Cell is active if it has three neighbors, or has two neighbors and is already active
					final boolean active = activeNeighbors == 3 || activeNeighbors == 2 && current[y * WIDTH + x] != 0;
					temp[y * WIDTH + x] = active ? (byte)1 : 0;
				}
		}
	}
}
