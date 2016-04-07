package de.diddiz.gol;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import uebung9.GameOfLife;

/**
 * Basic multithreaded implementation.
 *
 * @author Robin Kupper
 */
public class GameOfLifeCPUThreaded extends GameOfLife
{
	protected byte[] temp;

	private static final int CHUNKS_X = 8, CHUNKS_Y = 8;

	private static final int CHUNK_WIDTH = WIDTH / CHUNKS_X, CHUNK_HEIGHT = HEIGHT / CHUNKS_Y;

	private static int CORES = Runtime.getRuntime().availableProcessors();

	public GameOfLifeCPUThreaded() {
		temp = createArray();
	}

	@Override
	public void iterate() {
		try {
			final ExecutorService executor = Executors.newFixedThreadPool(CORES);
			for (int x = 0; x < CHUNKS_X; x++)
				for (int y = 0; y < CHUNKS_Y; y++)
					executor.submit(new Worker(x * CHUNK_WIDTH, (x + 1) * CHUNK_WIDTH, y * CHUNK_HEIGHT, (y + 1) * CHUNK_HEIGHT));

			executor.shutdown();
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

			swapCurrentAndTemp();
		} catch (final InterruptedException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	protected int getNumberOfActiveNeighbors(int row, int column) { // TODO Optimize
		final int[] rows = new int[]{row == 0 ? HEIGHT - 1 : row - 1, row,
				row == HEIGHT - 1 ? 0 : row + 1};
		final int[] columns = new int[]{column == 0 ? WIDTH - 1 : column - 1,
				column, column == WIDTH - 1 ? 0 : column + 1};

		int alive = 0;
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++) {
				if (i == 1 && j == 1)
					continue;

				if (isActive(rows[i], columns[j]))
					alive++;
			}

		return alive;
	}

	protected void swapCurrentAndTemp() {
		final byte[] newCurrent = temp;

		// re-use for next iteration
		temp = current;
		current = newCurrent;
	}

	private void setActive(int row, int column, boolean active) {
		temp[getIndex(row, column)] = (byte)(active ? 1 : 0);
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
			for (int x = minX; x < maxX; x++)
				for (int y = minY; y < maxY; y++) {
					final int activeNeighbors = getNumberOfActiveNeighbors(y, x);
					// Cell is only active in next frame it it has three neighbors, or has two neighbors and is active
					setActive(y, x, activeNeighbors == 3 || activeNeighbors == 2 && isActive(y, x));
				}
		}

	}
}
