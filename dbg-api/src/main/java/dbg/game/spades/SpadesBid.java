package dbg.game.spades;

public class SpadesBid {

	private int books;

	private int won;

	// TODO add support for nil bids

	protected SpadesBid() {

	}

	protected SpadesBid(int books, int won) {
		setBooks(books);
		setWon(won);
	}

	public int getBooks() {
		return books;
	}

	public void setBooks(int books) {
		this.books = books;
	}

	public int getWon() {
		return won;
	}

	public void setWon(int won) {
		this.won = won;
	}

	public void incrementWon() {
		setWon(won + 1);
	}

}
