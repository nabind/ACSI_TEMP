package com.ctb.prism.web.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.ldap.userdetails.LdapUserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.switchuser.AuthenticationSwitchUserEvent;
import org.springframework.security.web.authentication.switchuser.SwitchUserAuthorityChanger;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import com.ctb.prism.core.constant.IApplicationConstants;

public class FirstTimeUserFilter extends GenericFilterBean {
	
	private String exitUserUrl = "/j_spring_security_exit_user";
	private String switchUserUrl = "/j_spring_security_switch_user";
	private String changePasswordUrl = "/changePassword.do";
	
	/**
	 * This filter check if user has already changed password
	 * 
	 * FUTURE - this will check if user password got expired
	 */
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		
		if(requiresSwitchUser(request)) chain.doFilter(request, response);
		else if(requiresExitUser(request)) chain.doFilter(request, response);
		else if (!requiresChangepassword(request)) {
			if(IApplicationConstants.TRUE.equals(request.getSession().getAttribute(IApplicationConstants.FIRST_TIME_LOGIN))) {
				// user need to change password 
				logger.debug("user need to change password");
				response.sendRedirect("changePassword.do");
			} else if(IApplicationConstants.FALSE.equals(request.getSession().getAttribute(IApplicationConstants.FIRST_TIME_LOGIN))) {
				chain.doFilter(request, response);
			}
		} else {
			chain.doFilter(request, response);
		}

		
	}
	
	/**
	 * Checks the request URI for the presence of <tt>exitUserUrl</tt>.
	 * @param request The http servlet request
	 * @return <code>true</code> if the request requires a exit user,
	 *         <code>false</code> otherwise.
	 * @see SwitchUserFilter#exitUserUrl
	 */
	protected boolean requiresExitUser(HttpServletRequest request) {
		String uri = stripUri(request);

		return uri.endsWith(request.getContextPath() + exitUserUrl);
	}

	/**
	 * Checks the request URI for the presence of <tt>switchUserUrl</tt>.
	 * @param request The http servlet request
	 * @return <code>true</code> if the request requires a switch,
	 *         <code>false</code> otherwise.
	 * @see SwitchUserFilter#switchUserUrl
	 */
	protected boolean requiresSwitchUser(HttpServletRequest request) {
		String uri = stripUri(request);

		return uri.endsWith(request.getContextPath() + switchUserUrl);
	}
	
	/**
	 * Checks the request URI for the presence of <tt>changePasswordUrl</tt>.
	 * @param request The http servlet request
	 * @return <code>true</code> if the request requires a switch,
	 *         <code>false</code> otherwise.
	 * @see SwitchUserFilter#switchUserUrl
	 */
	protected boolean requiresChangepassword(HttpServletRequest request) {
		String uri = stripUri(request);

		return uri.endsWith(request.getContextPath() + changePasswordUrl);
	}
	
	/**
	 * Strips any content after the ';' in the request URI
	 * @param request The http request
	 * @return The stripped uri
	 */
	private String stripUri(HttpServletRequest request) {
		String uri = request.getRequestURI();
		int idx = uri.indexOf(';');

		if (idx > 0) {
			uri = uri.substring(0, idx);
		}

		return uri;
	}

}
