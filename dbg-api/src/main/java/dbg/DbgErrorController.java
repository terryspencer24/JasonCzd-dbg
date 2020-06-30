package dbg;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class prevents out of box HTML error page from displaying
 */
@RestController
public class DbgErrorController implements ErrorController {

	private static final String PATH = "/error";

	@RequestMapping(value = PATH)
	public String error() {
		return "";
	}

	@Override
	public String getErrorPath() {
		return PATH;
	}

}