public class GameOfLifeCPU extends GameOfLife {

	protected byte[] temp;

	public GameOfLifeCPU() {
		temp = createArray();
	}

	private void setActive(int row, int column, boolean active) {
		temp[getIndex(row, column)] = (byte) (active ? 1 : 0);
	}

	protected int getNumberOfActiveNeighbors(int row, int column) {
		int[] rows = new int[] { (row == 0 ? HEIGHT - 1 : row - 1), row,
				(row == HEIGHT - 1 ? 0 : row + 1) };
		int[] columns = new int[] { (column == 0 ? WIDTH - 1 : column - 1),
				column, (column == WIDTH - 1 ? 0 : column + 1) };

		int alive = 0;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (i == 1 && j == 1) {
					continue;
				}

				if (isActive(rows[i], columns[j])) {
					alive++;
				}
			}
		}

		return alive;
	}

	protected void swapCurrentAndTemp() {
		byte[] newCurrent = temp;

		// re-use for next iteration
		temp = current;
		current = newCurrent;
	}

	@Override
	public void iterate() {
		// TODO a)
	}

}
