package com.example.eventos.seguridad;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTAuthorizationFilter extends OncePerRequestFilter {
    private final String HEADER = "Authorization";
	private final String PREFIX = "Bearer ";

    private String jwtSecret;
	
	public JWTAuthorizationFilter(String jwtSecret) {
		this.jwtSecret = jwtSecret;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
		try {
			String token = getJWTToken(request);
			if (token != null) {
				Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
				JWTVerifier verifier = JWT.require(algorithm)
					.withIssuer("arturober")
					.build(); //Reusable verifier instance
				DecodedJWT jwt = verifier.verify(token);
				setUpSpringAuthentication(jwt);
			}
			chain.doFilter(request, response);
		} catch (JWTVerificationException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
		}
	}	

	/**
	 * Metodo para autenticarnos dentro del flujo de Spring
	 * 
	 * @param claims
	 */
	private void setUpSpringAuthentication(DecodedJWT jwt) {
		Authentication auth = new UsernamePasswordAuthenticationToken(jwt.getSubject(), jwt.getClaim("id").asInt(), null);
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

	private String getJWTToken(HttpServletRequest request) {
		String authenticationHeader = request.getHeader(HEADER);
		if (authenticationHeader == null || !authenticationHeader.startsWith(PREFIX))
			return null;
		return authenticationHeader.replace(PREFIX, "");
	}
}