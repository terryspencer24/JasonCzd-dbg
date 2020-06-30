package dbg.security;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.removeStart;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Responsible for extracting the authentication token from the request headers.
 * It takes the Authorization header value and attempts to extract the token
 * from it.
 */
public class TokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
	private static final String BEARER = "Bearer";

	TokenAuthenticationFilter(RequestMatcher requiresAuth) {
		super(requiresAuth);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		String token = extractAuthToken(request);
		return attemptAuthentication(token);
	}

	public Authentication attemptAuthentication(String token) {
		Authentication auth = new UsernamePasswordAuthenticationToken(token, token);
		return getAuthenticationManager().authenticate(auth);
	}

	public static String extractAuthToken(HttpServletRequest request) {
		String param = ofNullable(request.getHeader("Authorization")).orElse(request.getParameter("t"));
		return extractAuthToken(param);
	}
	
	public static String extractAuthToken(String param) {
		String token = ofNullable(param).map(value -> removeStart(value, BEARER)).map(String::trim)
				.orElseThrow(() -> new BadCredentialsException("Missing Authentication Token"));
		return token;
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		super.successfulAuthentication(request, response, chain, authResult);
		chain.doFilter(request, response);
	}
}