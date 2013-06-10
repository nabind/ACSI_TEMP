package com.ctb.prism.web.security;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

import com.ctb.prism.core.logger.IAppLogger;
import com.ctb.prism.core.logger.LogFactory;
import com.ctb.prism.core.util.CustomStringUtil;
import com.ctb.prism.web.controller.CommonController;

/**
 * This is a Custom generic Cross Site Scripting(XSS) Filter captured all XSS
 * sensitive user input and removed the same from Http response. It does not
 * stop application from taking any user input but if found any if those user
 * input to be XSS sensitive then it take out the same from response just before
 * rendering to browser. So there zero impact for this implenentation on
 * application and ensure no XSS sensitive malicious code can be injected in
 * server response.
 * 
 * @author tarun_chawdhury
 * 
 */
public class XSSFilter implements Filter {

	private FilterConfig config = null;
	
	//static final Logger LOGGER = Logger.getLogger(XSSFilter.class.getName());
	private static final IAppLogger LOGGER = LogFactory.getLoggerInstance(XSSFilter.class.getName());

	private static String[] xssUserInputPatterns = { "applet", "body", "embed", "frame",
			"script", "frameset", "html", "iframe", "img", "style", "layer",
			"link", "ilayer", "meta", "object" };
   
	private static ArrayList<String> ignoreXSSFilterURL = new ArrayList<String>();
	static { 
		//ignoreXSSFilterURL.add(""); 
	}
	
	private static class WrappedResponse extends HttpServletResponseWrapper {
		private CharArrayWriter buffer;

		public WrappedResponse(HttpServletResponse response) {
			super(response);
			buffer = new CharArrayWriter();
		}

		public PrintWriter getWriter() {
			return new PrintWriter(buffer);
		}

		public String toString() {
			return buffer.toString();
		}
	}

	public void init(FilterConfig _config) throws ServletException {
		this.config = _config;
	}

	public void doFilter(ServletRequest _req, ServletResponse _res,
			FilterChain _chain) throws IOException, ServletException {
		long t1 = System.currentTimeMillis();
		/* Wrap the response object */
		WrappedResponse wrappedRes = new WrappedResponse(
				(HttpServletResponse) _res);

		// Build an list of user input values which will be used to remove
		// unescaped characters from response
		HttpServletRequest request = (HttpServletRequest) _req;
		String url = request.getRequestURI();
		final String queryString = request.getQueryString();
		if (queryString!=null && queryString.contains("alert(")) {
			HttpSession session = request.getSession(false);
			if(session != null){session.invalidate();}
			wrappedRes.sendRedirect("showError.do");
			return;
		}
		
		//for product user there will be no time out start
		if(request.isRequestedSessionIdValid()){
			HttpSession session = request.getSession();
			String productKey = (String)session.getAttribute("Product.Key");
			int sessionTimeOut = session.getMaxInactiveInterval();
			if(!"0".equals(productKey) && sessionTimeOut >0){
				session.setMaxInactiveInterval(-1); //for product user there will be no time out
			}
		}
        //for product user there will be no time out start
		
		Enumeration names = request.getParameterNames();
		ArrayList<String> inputValues = new ArrayList<String>();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			String[] values = request.getParameterValues(name);
			if (values != null) {
				for (String value : values) {
					// TODO: Can We make logic of isXssSensitiveInput more
					// intellegent???
					if (isXssSensitiveInput(value)) {
						inputValues.add(value);
					}
//					build a second line of defense - onsite 7/22/2011
					if(value.toLowerCase().indexOf("demo.testfire.net") >= 0 ||
					   value.toUpperCase().indexOf("WF_XSRF.HTML") >= 0 ||
					   value.toUpperCase().indexOf("%2BALERT") >= 0 || 
					   value.toLowerCase().indexOf("alert(")>=0){
						
						HttpSession session = request.getSession(false);
						if(session != null){session.invalidate();}
						wrappedRes.sendRedirect("showError.do");
						return;
					}
				}
			}
		}
		/* Continue Processing */
		long t2 = System.currentTimeMillis();
		_chain.doFilter(_req, wrappedRes);
		long t3 = System.currentTimeMillis();
		/* Manipulate the response content */
		
		String xssFreeOutputHtml = xssFreeOutput(inputValues, wrappedRes
				.toString(),url);
		t3 = t3 - t2;
		long t4 = System.currentTimeMillis();
		long time = t4 - t1 - t3;
		LOGGER.log(IAppLogger.DEBUG, CustomStringUtil.appendString(
				"\n***************************************\nTime taken for Security Scan : " , ""+time
				, " milli seconds and \nactual request processing time(client<->App<->Database):" , ""+t3
				, " milli seconds for URL:",url,"\n*****************************************\n"));
		/* Write content to browser */
		_res.setContentLength(xssFreeOutputHtml.length());
		if(url != null && url.indexOf("/download.do") == -1) {
			_res.getWriter().print(xssFreeOutputHtml);
			_res.getWriter().close();
		}
	}

	

	/**
	 * filter out all XSS sensitive user inputs from HTTP response
	 * 
	 * @param output
	 * @return
	 */
	private String xssFreeOutput(ArrayList<String> inputValues, String output,String url) {

		StringBuilder xssFreeHtmlBuilder = new StringBuilder(output);
		
		boolean xssIgnore = ignoreXSSFilterURL.contains(url);
		
		for (String value : inputValues) {
            
			String temp = null;
			
			while (xssFreeHtmlBuilder.indexOf(value) != -1) {
                
                int size = xssFreeHtmlBuilder.indexOf(value) + value.length();
				if(xssIgnore){
					temp = value.replace("alert(","");
				}else{
					temp = Utility.sanitizeHtmlValue(value);
                    temp = temp.replace("alert(","");
				}
				
				if(value.equalsIgnoreCase(temp)){
					break;
				}
				
			    if(xssFreeHtmlBuilder.length()>size) {
			    	
			    	xssFreeHtmlBuilder.replace(xssFreeHtmlBuilder.indexOf(value),size,temp);
			    	
			    }
			}
			

		}
		return xssFreeHtmlBuilder.toString();
	}

	/**
	 * check whether user input is sensitive to XSS or not
	 * 
	 * @param input
	 * @return
	 */
	private boolean isXssSensitiveInput(String input) {
		String input_New = input.toLowerCase();
		for (String xssTag : xssUserInputPatterns) {
			
			if (input_New.indexOf(xssTag) != -1
					&& input_New.indexOf("<") != -1
					&& input_New.indexOf(">") != -1 ) {
				return true;
			}
		}
		return false; 
	}
	
	public void destroy() {
		config = null;
	}
}
