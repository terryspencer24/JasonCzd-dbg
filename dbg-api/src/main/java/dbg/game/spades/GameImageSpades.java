package dbg.game.spades;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import dbg.card.Card;
import dbg.game.GameImage;
import dbg.security.User;

public class GameImageSpades extends GameImage<GameSpades> {

	@Override
	public BufferedImage drawBoard(GameSpades game, List<User> users) throws IOException {
		int[] cardx = { 595, 395, 595, 795 };
		int[] cardy = { 470, 270, 70, 270 };

		int[] wonx = { 635, 545, 635, 725 };
		int[] wony = { 455, 375, 310, 375 };

		BufferedImage board = getBoard();

		Graphics2D g = board.createGraphics();

		List<GameSeatSpades> seats = game.getSeats();
		logger.info("I have " + seats.size() + " seats");
		for (int i = 0; i < seats.size(); i++) {
			GameSeatSpades seat = seats.get(i);

			if (seat.trickWinner) {
				g.setColor(Color.YELLOW);
				g.drawRect(cardx[i] - 10, cardy[i] - 10, 150, 220);
				g.fillRect(cardx[i] - 10, cardy[i] - 10, 150, 220);
				g.setColor(Color.WHITE);
			}

			Card card = seat.cardPlayed(game.getRound(), game.getTrick());
			if (card != null) {
				g.drawImage(loadCard(card), cardx[i], cardy[i], null);
			}

			SpadesBid bid = seat.bid(game.getRound());
			if (bid != null) {
				writeText(bid.getWon() + " / " + bid.getBooks(), 28, board, g, wonx[i], wony[i]);
			}
		}

		if (game.getSeats().size() == 4) {
			int mod = 1;
			if (game.getSeats().get(0).isWinner) {
				g.setColor(Color.YELLOW);
				g.drawRect(40, 70, 300, 130);
				g.fillRect(40, 70, 300, 130);
				g.setColor(Color.WHITE);
				mod = 0;
			}
			writeText(users.get(0).username + " & " + users.get(2).username, board, g, 50, 110);
			writeText("" + game.getSeats().get(0).getScore(game.getRound() - mod), board, g, 50, 175);

			if (game.getSeats().get(1).isWinner) {
				g.setColor(Color.YELLOW);
				g.drawRect(40, 270, 300, 130);
				g.fillRect(40, 270, 300, 130);
				g.setColor(Color.WHITE);
				mod = 0;
			}
			writeText(users.get(1).username + " & " + users.get(3).username, board, g, 50, 310);
			writeText("" + game.getSeats().get(1).getScore(game.getRound() - mod), board, g, 50, 375);
		}

		return board;
	}

}
