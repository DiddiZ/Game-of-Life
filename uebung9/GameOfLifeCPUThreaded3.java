import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Robin Kupper
 */
public class GameOfLifeCPUThreaded3 extends GameOfLifeCPU
{
	private byte[] open, tempOpen;

	/**
	 * Tick counter to keep track of currently open cells. Overflows, it's cared for.
	 */
	private byte tick;

	private static final int CHUNKS_X = 16, CHUNKS_Y = 16;
	private static final int CHUNK_WIDTH = WIDTH / CHUNKS_X, CHUNK_HEIGHT = HEIGHT / CHUNKS_Y;

	public GameOfLifeCPUThreaded3() {
		temp = createArray();
		open = createArray();
		tempOpen = createArray();
	}

	@Override
	public void iterate() {
		try {
			// Spawn a lot of threads
			final ExecutorService executor = Executors.newWorkStealingPool();
			for (int x = 0; x < CHUNKS_X; x++)
				for (int y = 0; y < CHUNKS_Y; y++)
					executor.submit(new Worker(x * CHUNK_WIDTH, (x + 1) * CHUNK_WIDTH, y * CHUNK_HEIGHT, (y + 1) * CHUNK_HEIGHT));

			// Wait for spawned threads to finish
			executor.shutdown();
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

			// Prepare next iteration
			swapCurrentAndTemp();
		} catch (final InterruptedException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void setStart(byte[] start) {
		super.setStart(start);

		// Prepare open cache
		for (int y = 0; y < HEIGHT; y++)
			for (int x = 0; x < WIDTH; x++)
				if (isActive(y, x))
					addToOpen(y, x);
		swapOpen();
	}

	@Override
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

	@Override
	protected void swapCurrentAndTemp() {
		final byte[] newCurrent = temp;

		// re-use for next iteration
		temp = current;
		current = newCurrent;

		swapOpen();
	}

	/**
	 * Adds a cell and it's neighbors to temp open cache
	 */
	private void addToOpen(int row, int column) {
		final int rowOffset = row * WIDTH;
		final int lowerRowOffset = row == 0 ? SIZE - WIDTH : rowOffset - WIDTH;
		final int upperRowOffset = row == HEIGHT - 1 ? 0 : rowOffset + WIDTH;
		final int lowerColumn = column == 0 ? WIDTH - 1 : column - 1;
		final int upperColumn = column == WIDTH - 1 ? 0 : column + 1;

		final byte nextTick = (byte)(tick + 1);
		tempOpen[lowerRowOffset + lowerColumn] = nextTick;
		tempOpen[lowerRowOffset + column] = nextTick;
		tempOpen[lowerRowOffset + upperColumn] = nextTick;
		tempOpen[rowOffset + lowerColumn] = nextTick;
		tempOpen[rowOffset + column] = nextTick;
		tempOpen[rowOffset + upperColumn] = nextTick;
		tempOpen[upperRowOffset + lowerColumn] = nextTick;
		tempOpen[upperRowOffset + column] = nextTick;
		tempOpen[upperRowOffset + upperColumn] = nextTick;
	}

	private void swapOpen() {
		final byte[] newOpen = tempOpen;
		tempOpen = open;
		open = newOpen;
		tick++;

		// Skip zero and clear open buffers
		if (tick == (byte)-1) {
			tick = 1;
			for (int i = 0; i < SIZE; i++) {
				open[i] = open[i] == -1 ? (byte)1 : 0;
				tempOpen[i] = 0;
			}
		}
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
				for (int x = minX; x < maxX; x++)
					if (open[y * WIDTH + x] == tick) { // Cell changed in last step
						final int activeNeighbors = getNumberOfActiveNeighbors(y, x);
						final boolean isActive = current[y * WIDTH + x] != 0;

						// A Cell is active if it has three neighbors, or has two neighbors and is already active
						final boolean active = activeNeighbors == 3 || activeNeighbors == 2 && isActive;
						temp[y * WIDTH + x] = active ? (byte)1 : 0;

						if (active != isActive)
							addToOpen(y, x);
					}
		}
	}
}
