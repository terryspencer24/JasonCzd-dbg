package dbg.game;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import dbg.DbgObject;
import dbg.ticket.GameTicket;
import dbg.ticket.GameTicketService;

@RestController
public class GameImageController extends DbgObject {

	@Autowired
	GameTicketService svc;

	@RequestMapping(value = "/papi/gameimage/{id}", method = RequestMethod.GET)
	public ResponseEntity<byte[]> gameimage(@PathVariable("id") long id) throws IOException {
		logger.info("Generating image for " + id);

		GameTicket ticket = svc.loadFromId(id);
		Game<?, ?> g = svc.loadGameFromTicket(ticket);
		BufferedImage bi = g.getGameImage().drawBoard(g, ticket.invites);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bi, "jpg", baos);
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();

		final HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl(CacheControl.noCache().getHeaderValue());
		headers.setContentType(MediaType.IMAGE_JPEG);
		ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(imageInByte, headers, HttpStatus.OK);
		return responseEntity;
	}

}
