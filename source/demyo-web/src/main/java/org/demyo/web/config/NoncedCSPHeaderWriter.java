package org.demyo.web.config;

import java.security.SecureRandom;
import java.util.Base64;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.header.HeaderWriter;

/**
 * Handles the Content-Security-Policy header by providing a dynamic nonce for scripts.
 */
public class NoncedCSPHeaderWriter implements HeaderWriter {
	private static final String REQ_ATTRIB_SCRIPT_NONCE = "cspScriptNonce";

	public static final String BEAN_NAME = "noncedCSPHeaderWriter";

	private static final Logger LOGGER = LoggerFactory.getLogger(NoncedCSPHeaderWriter.class);
	private static final int NONCE_SIZE = 16;

	private SecureRandom rand = new SecureRandom();

	@Override
	public void writeHeaders(HttpServletRequest request, HttpServletResponse response) {
		if (request.getAttribute(REQ_ATTRIB_SCRIPT_NONCE) != null) {
			LOGGER.trace("The request already has a nonce, not generating a second one");
			return;
		}

		byte[] nonceArray = new byte[NONCE_SIZE];
		rand.nextBytes(nonceArray);
		String scriptNonce = Base64.getEncoder().encodeToString(nonceArray);
		rand.nextBytes(nonceArray);
		String styleNonce = Base64.getEncoder().encodeToString(nonceArray);

		// Note: this CSP may yield an unsafe-eval from Webpack (in global.js) and FilePond, but it's perfectly fine
		// because there's a fallback
		String csp = "default-src 'none'; connect-src 'self'; font-src 'self'; manifest-src 'self'; "
				// Allow the Google Maps iframe
				+ "frame-src https://maps.google.com/ https://www.google.com/maps/; "
				// blob: and data: are used by filepond. Perhaps we could avoid this with strict-dynamic?
				+ "img-src 'self' data: blob:; "
				// We allow only nonced scripts and self
				// 'strict-dynamic' allows Filepond to use a blob: URL for an image. Seems like a quirk in Firefox
				// as Chrome properly allows it
				+ "script-src 'strict-dynamic' 'nonce-" + scriptNonce + "'; "
				// Until https://github.com/vuejs/vue-style-loader/issues/33 is resolved, unsafe-inline will be needed
				// Since a nonce overrides unsafe-inline, we will only be able to move to nonces once the whole stack
				// supports it
				+ "style-src 'self' 'unsafe-inline';";

		response.addHeader("Content-Security-Policy", csp);
		request.setAttribute(REQ_ATTRIB_SCRIPT_NONCE, scriptNonce);
		request.setAttribute("cspStyleNonce", styleNonce);
	}

}
