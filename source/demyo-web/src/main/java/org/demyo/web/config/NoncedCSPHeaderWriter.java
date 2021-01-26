package org.demyo.web.config;

import java.security.SecureRandom;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.header.HeaderWriter;

/**
 * Handles the Content-Security-Policy header by providing a dynamic nonce for scripts.
 */
public class NoncedCSPHeaderWriter implements HeaderWriter {
	public static final String BEAN_NAME = "noncedCSPHeaderWriter";

	private static final int NONCE_SIZE = 16;

	private SecureRandom rand = new SecureRandom();

	@Override
	public void writeHeaders(HttpServletRequest request, HttpServletResponse response) {
		byte[] nonceArray = new byte[NONCE_SIZE];
		rand.nextBytes(nonceArray);
		String scriptNonce = Base64.getEncoder().encodeToString(nonceArray);
		rand.nextBytes(nonceArray);
		String styleNonce = Base64.getEncoder().encodeToString(nonceArray);

		// Note: this CSP may yield an unsafe-eval from Webpack (in global.js) and FilePond, but it's perfectly fine
		// because there's a fallback
		String csp = "default-src 'none'; connect-src 'self'; font-src 'self'; manifest-src 'self'; "
				// blob: and data: are used by filepond. Perhaps we could avoid this with strict-dynamic?
				+ "img-src 'self' data: blob:; "
				// We allow only nonced scripts and self
				// 'strict-dynamic' allows Filepond to use a blob: URL for an image. Seems like a quirk in Firefox
				// as Chrome properly allows it
				+ "script-src 'nonce-" + scriptNonce + "' 'strict-dynamic' 'self'; "
				// Until https://github.com/vuejs/vue-style-loader/issues/33 is resolved, unsafe-inline will be needed
				// Since a nonce overrides unsafe-inline, we will only be able to move to nonces once the whole stack
				// supports it
				+ "style-src 'self' 'unsafe-inline';";

		response.addHeader("Content-Security-Policy", csp);
		request.setAttribute("cspScriptNonce", scriptNonce);
		request.setAttribute("cspStyleNonce", styleNonce);
	}

}
