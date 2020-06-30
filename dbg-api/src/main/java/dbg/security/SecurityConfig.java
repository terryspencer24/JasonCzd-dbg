package dbg.security;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * URLs starting with /api/** are protected, all others are not secured. The
 * TokenAuthenticationFilter is registered within the Spring Security Filter
 * Chain very early. We want it to catch any authentication token passing by.
 * Most other login methods like formLogin or httpBasic have been disabled as
 * we’re not willing to use them here (we want to use our own system). Some
 * boiler-plate code to disable automatic filter registration, related to Spring
 * Boot.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static final RequestMatcher PROTECTED_URLS = new OrRequestMatcher(new AntPathRequestMatcher("/api/**"));
	private static final RequestMatcher PUBLIC_URLS = new NegatedRequestMatcher(PROTECTED_URLS);

	TokenAuthenticationProvider provider;

	SecurityConfig(TokenAuthenticationProvider provider) {
		super();
		this.provider = requireNonNull(provider);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) {
		auth.authenticationProvider(provider);
	}

	@Override
	public void configure(WebSecurity web) {
		web.ignoring().requestMatchers(PUBLIC_URLS).and().ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.sessionManagement().sessionCreationPolicy(STATELESS).and().exceptionHandling()
				// this entry point handles when you request a protected page and you are not
				// yet
				// authenticated
				.defaultAuthenticationEntryPointFor(forbiddenEntryPoint(), PROTECTED_URLS).and()
				.authenticationProvider(provider)
				.addFilterBefore(restAuthenticationFilter(), AnonymousAuthenticationFilter.class).authorizeRequests()
				.requestMatchers(PROTECTED_URLS).authenticated().and().csrf().disable().formLogin().disable()
				.httpBasic().disable().logout().disable();
	}

	@Bean
	TokenAuthenticationFilter restAuthenticationFilter() throws Exception {
		TokenAuthenticationFilter filter = new TokenAuthenticationFilter(PROTECTED_URLS);
		filter.setAuthenticationManager(authenticationManager());
		filter.setAuthenticationSuccessHandler(successHandler());
		return filter;
	}

	@Bean
	SimpleUrlAuthenticationSuccessHandler successHandler() {
		SimpleUrlAuthenticationSuccessHandler successHandler = new SimpleUrlAuthenticationSuccessHandler();
		successHandler.setRedirectStrategy(new RedirectStrategyNone());
		return successHandler;
	}

	/**
	 * Disable Spring boot automatic filter registration.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	FilterRegistrationBean disableAutoRegistration(TokenAuthenticationFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean(filter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	AuthenticationEntryPoint forbiddenEntryPoint() {
		return new HttpStatusEntryPoint(FORBIDDEN);
	}

}