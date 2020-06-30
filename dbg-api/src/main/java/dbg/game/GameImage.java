package dbg.game;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import dbg.DbgObject;
import dbg.card.Card;
import dbg.card.Suit;
import dbg.card.Value;
import dbg.security.User;

@SuppressWarnings("rawtypes")
public abstract class GameImage<T extends Game> extends DbgObject {

	private static BufferedImage board;

	private static Map<String, BufferedImage> cards;
	
	public abstract BufferedImage drawBoard(T game, List<User> users) throws IOException;

	public static void initialize() {
		if (board == null) {
			try {
				InputStream bkgIS = GameImage.class.getResourceAsStream("/images/tables/wood.jpg");
				board = ImageIO.read(bkgIS);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (cards == null) {
			cards = new HashMap<>();
			Stream.of(Suit.values()).parallel().forEach(suit -> {
				Stream.of(Value.values()).parallel().forEach(value -> {
					try {
						Card card = new Card(suit, value);
						BufferedImage bi = loadCardImage(new Card(suit, value));
						cards.put(name(card), bi);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			});
		}
	}

	public GameImage() {
		initialize();
	}

	protected BufferedImage loadCard(Card card) throws IOException {
		if (cards.containsKey(name(card))) {
			return cards.get(name(card));
		}
		return loadCardImage(card);
	}

	private static BufferedImage loadCardImage(Card card) throws IOException {
		InputStream cardIS = GameImage.class.getResourceAsStream("/images/cards/" + name(card) + ".jpg");
		BufferedImage im2 = ImageIO.read(cardIS);
		BufferedImage resized = resizeCard(im2);
		return resized;
	}

	protected BufferedImage getBoard() {
		ColorModel cm = board.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = board.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	protected BufferedImage loadBack() throws IOException {
		BufferedImage back = ImageIO.read(GameImage.class.getResourceAsStream("/images/cards/back.jpg"));
		BufferedImage resizedBack = resizeCard(back);
		return resizedBack;
	}

	protected static BufferedImage resizeCard(BufferedImage img) {
		int newHeight = 200, newWidth = 130;
		BufferedImage resized = new BufferedImage(newWidth, newHeight, img.getType());
		Graphics2D g2 = resized.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(img, 0, 0, newWidth, newHeight, 0, 0, img.getWidth(), img.getHeight(), null);
		g2.dispose();
		return resized;
	}

	public static String name(Card card) {
		String name = "";
		switch (card.value) {
		case TWO:
			name += "2";
			break;
		case THREE:
			name += "3";
			break;
		case FOUR:
			name += "4";
			break;
		case FIVE:
			name += "5";
			break;
		case SIX:
			name += "6";
			break;
		case SEVEN:
			name += "7";
			break;
		case EIGHT:
			name += "8";
			break;
		case NINE:
			name += "9";
			break;
		case TEN:
			name += "10";
			break;
		case JACK:
			name += "J";
			break;
		case QUEEN:
			name += "Q";
			break;
		case KING:
			name += "K";
			break;
		case ACE:
			name += "A";
			break;
		}
		switch (card.suit) {
		case CLUBS:
			name += "C";
			break;
		case DIAMONDS:
			name += "D";
			break;
		case HEARTS:
			name += "H";
			break;
		case SPADES:
			name += "S";
			break;
		}
		return name;
	}

	protected void writeText(String text, BufferedImage image, Graphics2D g, int x, int y) {
		writeText(text, 40, image, g, x, y);
	}

	protected void writeText(String text, int size, BufferedImage image, Graphics2D g, int x, int y) {
		// FontMetrics fm = g.getFontMetrics();
		// int x = image.getWidth() - fm.stringWidth(text) - 5;
		// int y = fm.getHeight();
		g.setFont(new Font("Serif", Font.BOLD, size));
		g.drawString(text, x, y);
	}

}
