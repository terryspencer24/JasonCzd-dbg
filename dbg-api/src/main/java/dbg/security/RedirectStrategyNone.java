package dbg.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.RedirectStrategy;

/**
 * As we’re securing a REST API, in case of authentication failure, the server
 * should not redirect to any error page. The server will simply return an HTTP
 * 401 (Unauthorized).
 */
class RedirectStrategyNone implements RedirectStrategy {

	@Override
	public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
	}

}
