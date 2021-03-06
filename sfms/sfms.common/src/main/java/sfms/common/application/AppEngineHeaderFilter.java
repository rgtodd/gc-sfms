package sfms.common.application;

import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;

import sfms.common.Constants;
import sfms.common.PropertyFile;
import sfms.common.Secret;
import sfms.rest.api.RestHeaders;

/**
 * Filter used by REST controllers to ensure the required HTTP header is
 * present. This ensures that REST controllers only process requests submitted
 * by the web service.
 * 
 * @see Secret#getRestAuthorizationToken()
 * @see Constants#REST_AUTHORIZATION_TOKEN_HEADER_KEY
 *
 */
public class AppEngineHeaderFilter implements Filter {

	private final Logger logger = Logger.getLogger(AppEngineHeaderFilter.class.getName());

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// No action required.
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		if (PropertyFile.INSTANCE.isProduction()) {
			String expectedAuthorizationToken = Secret.getRestAuthorizationToken();
			if (!StringUtils.isEmpty(expectedAuthorizationToken)) {
				HttpServletRequest httpRequest = (HttpServletRequest) request;
				String actualAuthorizationToken = httpRequest.getHeader(RestHeaders.REST_AUTHORIZATION_TOKEN);
				if (actualAuthorizationToken == null || !actualAuthorizationToken.equals(expectedAuthorizationToken)) {

					String message = "Invalid authorization token";

					String prefix = ": ";
					Enumeration<String> headerNames = httpRequest.getHeaderNames();
					while (headerNames.hasMoreElements()) {
						message += prefix;
						prefix = ", ";

						String headerName = headerNames.nextElement();
						message += headerName;
					}
					message += ".";

					logger.log(Level.INFO, message);

					HttpServletResponse httpResponse = (HttpServletResponse) response;
					httpResponse.setContentType("text/plain");
					httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, message);

					return;
				}
			}
		}

		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// No action required.
	}

}
