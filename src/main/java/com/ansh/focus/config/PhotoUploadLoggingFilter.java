package com.ansh.focus.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class PhotoUploadLoggingFilter extends OncePerRequestFilter {

	private static final Logger log = LoggerFactory.getLogger(PhotoUploadLoggingFilter.class);

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return !"POST".equalsIgnoreCase(request.getMethod())
				|| !request.getRequestURI().matches(".*/groups/[^/]+/photos/?");
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
	) throws ServletException, IOException {
		log.info(
				"Incoming photo upload: uri={}, contentType={}, contentLength={}, remoteAddr={}",
				request.getRequestURI(),
				request.getContentType(),
				request.getContentLengthLong(),
				request.getRemoteAddr()
		);
		filterChain.doFilter(request, response);
		log.info(
				"Photo upload response: uri={}, status={}",
				request.getRequestURI(),
				response.getStatus()
		);
	}
}
