package com.ctb.prism.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ctb.prism.admin.service.IAdminService;
import com.ctb.prism.core.constant.IApplicationConstants;
import com.ctb.prism.core.logger.IAppLogger;
import com.ctb.prism.core.logger.LogFactory;
import com.ctb.prism.login.Service.ILoginService;
import com.ctb.prism.login.transferobject.UserTO;
import com.ctb.prism.parent.service.IParentService;
import com.ctb.prism.parent.transferobject.ParentTO;
import com.ctb.prism.parent.transferobject.QuestionTO;
import com.ctb.prism.report.service.IReportService;
import com.ctb.prism.report.transferobject.AssessmentTO;
import com.ctb.prism.report.transferobject.ReportTO;
import com.ctb.prism.web.manager.AuthenticatedUser;
import com.ctb.prism.web.util.JsonUtil;

 
@Controller
public class LoginController{
 
	private static final IAppLogger logger = LogFactory
			.getLoggerInstance(LoginController.class.getName());
	
	private List<String> prismUserHomePage;
	private List<ReportTO> homePageByRoleEntries;
	
	
	@Autowired
	private ILoginService loginService;
	
	@Autowired
	private IReportService reportService;
	
	@Autowired
	private IParentService parentService;
	
	@Autowired
	private IAdminService adminService;
	
	@RequestMapping(value = "/error", method = RequestMethod.POST)
	 public ModelAndView error(HttpServletRequest request,
		  HttpServletResponse response) throws ServletException, IOException {
		 
		  		 
		  ModelAndView modelAndView = new ModelAndView("error/error");
		 
		  logger.log(IAppLogger.INFO,
					"Exit: LoginController - loadErrorPage");
		  return modelAndView;
	  }
	
	/**
	 * This method is return display name for given username
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/getDisplayName", method = RequestMethod.GET)
	public @ResponseBody String getDisplayName(HttpServletRequest req,
			HttpServletResponse res) throws IOException {
		logger.log(IAppLogger.INFO, "Enter: ReportController - checkpagination");

		UserTO user = null;
		try {
			user = loginService.getUserDetails(req.getParameter("loginAsUsername"));
			
			res.setContentType("plain/text");
			res.getWriter().write( user.getDisplayName() );
		} catch (Exception exception) {
			logger.log(IAppLogger.ERROR, exception.getMessage(), exception);
		} finally {
			logger.log(IAppLogger.INFO, "Exit: ReportController - checkpagination");
		}
		return null;
	}
	
	/**
	 * This method is to display login page
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	 @RequestMapping(value = "/login", method = RequestMethod.GET)
	 public ModelAndView loadLoginPage(HttpServletRequest request,
		  HttpServletResponse response) throws ServletException, IOException {
		 
		  logger.log(IAppLogger.INFO, "Enter: LoginController - loadLoginPage");
		  String parent = request.getParameter(IApplicationConstants.PARENT_LOGIN);
		  String mess_login_error = (String)request.getParameter("login_error");
		  String message = null;
		  if("1".equalsIgnoreCase(mess_login_error)){
			  logger.log(IAppLogger.ERROR, "Invalid Login");
			  message = "error.login.invalidlogin";
		  } else if("2".equalsIgnoreCase(mess_login_error)){
			  logger.log(IAppLogger.ERROR, "Session Expired");
			  message = "error.login.sessionexpired";
		  } else {
			  //Fixed defect 73561
			  String currentUser = (String) request.getSession().getAttribute(IApplicationConstants.CURRUSER);;
			  if (currentUser == null) {
				  logger.log(IAppLogger.INFO, "Landing Page loaded");
				 
				  ModelAndView modelAndView = null;
				  modelAndView = new ModelAndView("user/login");
				  modelAndView.addObject("message", message);
				 
				  logger.log(IAppLogger.INFO,
							"Exit: LoginController - loadLoginPage");
				  return modelAndView;
				 // response.sendRedirect("userlogin.do");
			  }
			  //this is proper login
		  }
		  ModelAndView modelAndView = null;
		  if(IApplicationConstants.TRUE.equals(parent)) {
			  modelAndView = new ModelAndView("parent/login");
		  } else {
			  modelAndView = new ModelAndView("user/login");
		  }
		  modelAndView.addObject("message", message);
		 
		  logger.log(IAppLogger.INFO,
					"Exit: LoginController - loadLoginPage");
		  return modelAndView;
	  }
	 
	 /**
	 * This method is to display login page
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	 @RequestMapping(value = "/userlogin", method = RequestMethod.GET)
	 public ModelAndView userlogin(HttpServletRequest request,
			 HttpServletResponse response) throws ServletException, IOException {
		 
		  logger.log(IAppLogger.INFO, "Enter: LoginController - loadLoginPage");
		  String mess_login_error = (String)request.getParameter("login_error");
		  String message = null;
		  if("1".equalsIgnoreCase(mess_login_error)){
			  logger.log(IAppLogger.ERROR, "Invalid Login");
			  message = "error.login.invalidlogin";
		  } else if("2".equalsIgnoreCase(mess_login_error)){
			  logger.log(IAppLogger.ERROR, "Session Expired");
			  message = "error.login.sessionexpired";
		  } else {
			  //this is proper login
		  }
		  ModelAndView modelAndView = null;
		  modelAndView = new ModelAndView("user/userlogin");
		  modelAndView.addObject("message", message);
		 
		  logger.log(IAppLogger.INFO,
					"Exit: LoginController - loadLoginPage");
		  return modelAndView;
	  }
	 
	 /**
	  * Opens dashboards
	  * @param req
	  * @param res
	  * @return
	  * @throws IOException
	  */
	 @RequestMapping(value="/dashboards", method=RequestMethod.GET)
	  public ModelAndView dashboards(HttpServletRequest req, HttpServletResponse res) throws IOException{
		 return validateUser(req, res);
	 }
	 
	 @RequestMapping(value="/childData", method=RequestMethod.GET)
	 public ModelAndView parentReports(HttpServletRequest req, HttpServletResponse res) throws IOException{
		 ReportTO homeReport = new ReportTO();
		 //homeReport.setReportUrl("/public/PN/Reports/Terranova3_Student_Report_Dashboard_files_files");
		 //homeReport.setReportName("TerraNova3 Student Report");
		 //homeReport.setAssessmentName("101");
		 homeReport.setStudentBioId(req.getParameter("childId"));
		 
		 req.getSession().setAttribute(IApplicationConstants.PARENT_REPORT, IApplicationConstants.TRUE);
		 req.getSession().setAttribute(IApplicationConstants.STUDENT_BIO_ID, req.getParameter("childId"));
		 List<AssessmentTO> assessmentList = reportService.getAssessments(true);
		 for(AssessmentTO assessment : assessmentList) {
			 if(assessment.getAssessmentId() == 101) {
				 for(ReportTO report : assessment.getReports()) {
					 homeReport.setReportUrl(report.getReportUrl());
					 homeReport.setReportName(report.getReportName());
					 homeReport.setAssessmentName(""+assessment.getAssessmentId());
					 homeReport.setReportId(report.getReportId());
					 break;
				 }
				 break;
			 }
		 }
		 
		 ModelAndView modelAndView = new ModelAndView("user/welcome");
		 modelAndView.addObject("assessmentList", assessmentList);
		 modelAndView.addObject("homeReport", homeReport);
		 return modelAndView;
	 }
	 
	 
	 /**
	  * This method is invoked after successful authentication
	  * @param req
	  * @param res
	  * @return
	  * @throws IOException
	  */
	  @RequestMapping(value="/validateUser", method=RequestMethod.GET)
	  public ModelAndView validateUser(HttpServletRequest req, HttpServletResponse res) throws IOException{
		 	logger.log(IAppLogger.INFO,
					"Enter: LoginController - validateUser");
		 	
		 	ModelAndView modelAndView = null;
		 	try {
				Authentication auth = SecurityContextHolder.getContext()
						.getAuthentication();
				if (!(auth instanceof AnonymousAuthenticationToken)) {
					ReportTO homeReport = null;
					String username = null;
					Collection<GrantedAuthority> authorities = null;
					UserTO user = null;
					if(auth.getPrincipal() instanceof LdapUserDetailsImpl) {
						LdapUserDetailsImpl userDetails = (LdapUserDetailsImpl) auth.getPrincipal();
						req.getSession().setAttribute(IApplicationConstants.CURRUSER, userDetails.getUsername());
						req.getSession().setAttribute(IApplicationConstants.AUTHORITIES, userDetails.getAuthorities());
						user = loginService.getUserDetails(userDetails.getUsername());
						req.getSession().setAttribute(IApplicationConstants.CURRORG, user.getOrgId());
						
						authorities = userDetails.getAuthorities();
						username = userDetails.getUsername();
					} else {
						// this section is only for DAO authentication - used during development
						AuthenticatedUser authenticatedUser = (AuthenticatedUser) auth.getPrincipal();
						
						UserTO loggedInUser = authenticatedUser.getUserTO();
						req.getSession().setAttribute(IApplicationConstants.CURRUSER, loggedInUser.getUserName());
						req.getSession().setAttribute(IApplicationConstants.AUTHORITIES, authenticatedUser.getAuthorities());
						user = loginService.getUserDetails(loggedInUser.getUserName());
						req.getSession().setAttribute(IApplicationConstants.CURRORG, user.getOrgId());
						
						authorities = authenticatedUser.getAuthorities();
						username = loggedInUser.getUserName();
					}
					req.getSession().setAttribute(IApplicationConstants.CURRUSERID, user.getUserId());
					req.getSession().setAttribute(IApplicationConstants.CURR_USER_DISPLAY, user.getDisplayName());
					String switchUser = (String) req.getSession().getAttribute(IApplicationConstants.PREV_ADMIN);
					boolean isSwitchUser = false;
					// check if user logs-in first time
					if(switchUser != null && switchUser.trim().length() > 0) {
						isSwitchUser = true;
					} else if(!IApplicationConstants.ACTIVE_FLAG.equals(user.getUserStatus())) {
						// user is disabled - redirect to access denied page
						req.getSession().invalidate();
						return new ModelAndView("redirect:denied.jsp");
					}
					if(!isSwitchUser && IApplicationConstants.FLAG_Y.equals(user.getFirstTimeLogin())) {
						req.getSession().setAttribute(IApplicationConstants.FIRST_TIME_LOGIN, IApplicationConstants.TRUE);
						//res.sendRedirect("changePassword.do");
						//logger.log(IAppLogger.DEBUG, "calling  changePassword.do...............................");
						return new ModelAndView("redirect:changePassword.do?username="+username);
					} else {
						req.getSession().setAttribute(IApplicationConstants.FIRST_TIME_LOGIN, IApplicationConstants.FALSE);
						homeReport = getBestHomeURLForUser(authorities);
					}
					// open home page based on user role
					if(homeReport.isRegularUser()) {
						homeReport.setProductName("TerraNova 3 : ");
						modelAndView = new ModelAndView("user/welcome");
						modelAndView.addObject("homeReport", homeReport);
					} else if(homeReport.isAccessDenied()) {
						modelAndView = new ModelAndView("error/accessDenied");
					} else {
						modelAndView = new ModelAndView(homeReport.getOtherUrl());
					}
				}
			}
			catch (Exception exception) {
				logger.log(IAppLogger.ERROR, exception.getMessage());
				return modelAndView.addObject("app_error", exception.getMessage());
			} finally {
				logger.log(IAppLogger.INFO, "Exit: LoginController - validateUser");
			}
			
			return modelAndView;
	  }
	  
	/**
	 * Opens home page after saving user profile for firsttime loggedin user
	 * 
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */

	@RequestMapping(value = "/changePassword", method = RequestMethod.GET)
	public ModelAndView changePassword(HttpServletRequest req,
			HttpServletResponse res) throws IOException {
		
		
		String username = (String) req.getParameter("username");
		// fetch security questions
		List questionList = parentService.getSecretQuestions();
		// logger.log(IAppLogger.DEBUG, questionList.size());
		//logger.log(IAppLogger.DEBUG, "into changePassword......................");
		//logger.log(IAppLogger.DEBUG, "username in  changePassword......................"+username);
		ParentTO parentDetails=	parentService.manageParentAccountDetails(username);
		List<QuestionTO> secretQuestionList = parentDetails.getQuestionToList();
		ModelAndView modelAndView = new ModelAndView("user/firstTimeUser");
		modelAndView.addObject("parentDetails", parentDetails);
		modelAndView.addObject("secretQuestionList", secretQuestionList);
		modelAndView.addObject("masterQuestionList", questionList);
		return modelAndView;
	}

	/**
	 * Opens home page after saving user profile for firsttime
	 * loggedin user
	 * 
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */

	@RequestMapping(value = "/welcome", method = RequestMethod.POST)
	public ModelAndView welcome(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		/*
		 * by deepak for fetching the user registration response from
		 * changePassword page
		 * 
		 * This code is similar to parent registration code so copied from
		 * Parent-controller object
		 */

		logger.log(IAppLogger.INFO, "Save user change Password screen");
		ParentTO parentTO = new ParentTO();
		QuestionTO questionTO = null;
		List<QuestionTO> questionToList = new ArrayList<QuestionTO>();
		// save user details
		String userName = (String) req.getSession().getAttribute(
				IApplicationConstants.CURRUSER);
		String password = (String) req.getParameter("password");
		String firstName = (String) req.getParameter("firstName");
		String lastName = (String) req.getParameter("lastName");
		String mailId = (String) req.getParameter("mail");
		String mobile = (String) req.getParameter("mobile");
		String street = (String) req.getParameter("street");
		String city = (String) req.getParameter("city");
		String state = (String) req.getParameter("state");
		String zip_code = (String) req.getParameter("zip_code");
		String country = (String) req.getParameter("userCountry");
		// checking whether the country has been selected form the drop down, if not then taking the default value of the dropdown
		if(country.trim().length()<=0){
			country = req.getParameter("country");
		}
			
		// String invitationCode = (String)req.getParameter("invitationCode");

		parentTO.setUserName(userName);
		parentTO.setPassword(password);
		parentTO.setFirstName(firstName);
		parentTO.setLastName(lastName);
		parentTO.setMail(mailId);
		parentTO.setMobile(mobile);
		parentTO.setStreet(street);
		parentTO.setCity(city);
		parentTO.setState(state);
		parentTO.setZipCode(zip_code);
		parentTO.setCountry(country);
		
		
		// parentTO.setInvitationCode(invitationCode);

		String qsn1 = (String) req.getParameter("qsn1");
		String ans1 = (String) req.getParameter("ans1");
		String ansId1 = (String) req.getParameter("ansId1");
		questionTO = new QuestionTO();
		questionTO.setQuestionId(Long.valueOf(qsn1));
		questionTO.setAnswer(ans1);
		questionTO.setAnswerId(Long.valueOf(ansId1));
		questionToList.add(questionTO);
		String qsn2 = (String) req.getParameter("qsn2");
		String ans2 = (String) req.getParameter("ans2");
		String ansId2 = (String) req.getParameter("ansId2");
		questionTO = new QuestionTO();
		questionTO.setQuestionId(Long.valueOf(qsn2));
		questionTO.setAnswer(ans2);
		questionTO.setAnswerId(Long.valueOf(ansId2));
		questionToList.add(questionTO);

		String qsn3 = (String) req.getParameter("qsn3");
		String ans3 = (String) req.getParameter("ans3");
		String ansId3 = (String) req.getParameter("ansId3");
		questionTO = new QuestionTO();
		questionTO.setQuestionId(Long.valueOf(qsn3));
		questionTO.setAnswer(ans3);
		questionTO.setAnswerId(Long.valueOf(ansId3));
		questionToList.add(questionTO);
		parentTO.setQuestionToList(questionToList);
		boolean success = false;
		try {
			success = parentService.firstTimeUserLogin(parentTO);
		} catch(Exception ex) {
			success = false;
		}
		ModelAndView mv = null;
		if (success) {
			return validateUser(req, res);
		} else {
			// fetch security questions
			List questionList = parentService.getSecretQuestions();
			// logger.log(IAppLogger.DEBUG, questionList.size());
			ModelAndView modelAndView = new ModelAndView("user/firstTimeUser");
			modelAndView.addObject("secretQuestionList", questionList);
			modelAndView.addObject("parentTO", parentTO);
			modelAndView.addObject("Error message", "System is experiencing some problem while saving. Please try later.");
			return modelAndView;
		}
	}
	  
	/**
	 * Given a user, find their home page based on their role
	 * 
	 * @param User user
	 * @return Home page URL for user
	 */
	private ReportTO getBestHomeURLForUser(Collection<GrantedAuthority> u) {
		List<ReportTO> roleEntries = getHomePageByRoleEntries();
		if(roleEntries != null) {
			Iterator<ReportTO> it = roleEntries.iterator();
			while (it.hasNext()) {
				ReportTO entry = it.next();
				if (hasRole(u, entry.getAllRoles())) {
					return entry;
				}
			}
		}
		return null;
	}
	
	/**
	 * Does this user have a role of this name?
	 * 
	 * @param User u
	 * @param String roleName
	 * @return true if user has a role with the given name
	 */
	private boolean hasRole(Collection<GrantedAuthority> u, String roleName) {
		for(GrantedAuthority auth : u) {
			if (auth.getAuthority().equalsIgnoreCase(roleName)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return Returns the homePageByRole.
	 */
	public List getPrismUserHomePage() {
		return prismUserHomePage;
	}
	  
	/**
	 * Converts basic Spring list to Map.Entry
	 * 
	 * @param homePageByRole The homePageByRole to set.
	 */
	public void setPrismUserHomePage(List<String> homePageByRole) {
		this.prismUserHomePage = homePageByRole;
		if (homePageByRole == null) {
			setHomePageByRoleEntries(null);
			return;
		}
		List<ReportTO> entriesList = new ArrayList<ReportTO>(homePageByRole.size());
		Iterator<String> it = homePageByRole.iterator();

		while (it.hasNext()) {
			String str = it.next();
			int pos = str.indexOf('|');
			if (pos == -1) {
				throw new RuntimeException("Invalid home page entry (needs | to separate role and URL:"+ str);
			}
			String[] urls = str.split("|");
			if(urls.length == 5) {
				ReportTO entry = new ReportTO();
				entry.setAllRoles(urls[0]);
				entry.setReportUrl(urls[1]);
				entry.setReportName(urls[2].replace("_", " "));
				entry.setReportId(Integer.parseInt(urls[3]));
				entry.setAssessmentName(urls[4]);
				entriesList.add(entry);
			}
		}
		setHomePageByRoleEntries(entriesList);
	}
	
	/**
	 * Get url entries
	 * @param homePageByRole
	 */
	public void populateRoleEntries() {
		List<String> homePageByRole = this.prismUserHomePage;
		if (homePageByRole == null) {
			setHomePageByRoleEntries(null);
			return;
		}
		List<ReportTO> entriesList = new ArrayList<ReportTO>(homePageByRole.size());
		Iterator<String> it = homePageByRole.iterator();

		while (it.hasNext()) {
			String str = it.next();
			int pos = str.indexOf('|');
			if (pos == -1) {
				throw new RuntimeException("Invalid home page entry (needs | to separate role and URL:"+ str);
			}
			String[] urls = str.split("\\|");
			if(urls.length == 5) {
				ReportTO entry = new ReportTO();
				entry.setRegularUser(true);
				entry.setAllRoles(urls[0]);
				entry.setReportUrl(urls[1]);
				entry.setReportName(urls[2].replace("_", " "));
				entry.setReportId(Integer.parseInt(urls[3]));
				entry.setAssessmentName(urls[4]);
				entriesList.add(entry);
			} else if(urls.length == 2) {
				// might be parent/ctb support login
				ReportTO entry = new ReportTO();
				entry.setAllRoles(urls[0]);
				entry.setOtherUrl(urls[1]);
				entriesList.add(entry);
			} else {
				// invalid settings
				ReportTO entry = new ReportTO();
				entry.setAccessDenied(true);
				entriesList.add(entry);
			}
		}
		setHomePageByRoleEntries(entriesList);
	}
	
	/**
	 * @return Returns the homePageByRoleEntries.
	 */
	public List<ReportTO> getHomePageByRoleEntries() {
		populateRoleEntries();
		return homePageByRoleEntries;
	}

	/**
	 * @param homePageByRoleEntries The homePageByRoleEntries to set.
	 */
	public void setHomePageByRoleEntries(List<ReportTO> homePageByRoleEntries) {
		this.homePageByRoleEntries = homePageByRoleEntries;
	}
	
	
	/**
	 * Check if the entered Invitation code is valid. For valid IC returns student details
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/regn/securityQuestionForUser", method = RequestMethod.GET)
	public @ResponseBody String securityQuestionForUser(HttpServletRequest req,
			HttpServletResponse res) throws IOException {
		logger.log(IAppLogger.INFO, "Fectching the User Security Questions");
		try {
			List<QuestionTO> questionToList = new ArrayList<QuestionTO>();
			String username = req.getParameter("username");
			questionToList = parentService.getSecurityQuestionForUser(username);
			if(questionToList != null) {
				String jsonString = JsonUtil.convertToJsonAdmin(questionToList);
				res.setContentType("application/json");
				res.getWriter().write(jsonString);
				//res.getWriter().write( "{\"status\":\"Success\"}" );
				logger.log(IAppLogger.DEBUG,jsonString);
			}
			
		} catch (Exception exception) {
			logger.log(IAppLogger.ERROR, exception.getMessage(), exception);
		} finally {
			logger.log(IAppLogger.INFO, "Fectching the User Security Questions");
		}
		return null;
	}
	
	/**
	 * This function validate the user secret answers
	 * @param req
	 * @param res
	 * @return
	 */

	@RequestMapping(value="/regn/checkAnswers", method=RequestMethod.GET )
	public ModelAndView checkAnswers(HttpServletRequest req, HttpServletResponse res)throws IOException  {
		try {
			logger.log(IAppLogger.INFO, "Enter: LoginController - checkAnswers");
			String userName = (String)req.getParameter("username");
			String ans1 = (String)req.getParameter("ans1");	
			String ans2 = (String)req.getParameter("ans2");	
			String ans3 = (String)req.getParameter("ans3");	
			String questionId1 = (String)req.getParameter("questionId1");
			String questionId2 = (String)req.getParameter("questionId2");	
			String questionId3 = (String)req.getParameter("questionId3");	
			
			
			boolean isValid = parentService.validateAnswers(userName,ans1, ans2,ans3,questionId1,questionId2,questionId3);
			res.setContentType("text/plain");
			String status = "Fail";
			if(isValid) {
				status = "Success";
			}
			res.getWriter().write( "{\"status\":\""+status+"\"}" );

			logger.log(IAppLogger.INFO, "Exit: LoginController - checkAnswers");

		} catch (Exception e) {
			logger.log(IAppLogger.ERROR, "Error validating answers", e);
		}
		return null;
	}
	
	/**
	 * Method performs reset password operation. This method is called through AJAX
	 * @param req
	 * @param res
	 * @return Returns nothing, instead writes password in response as JSON
	 */
	@RequestMapping(value="/regn/generateTempPwd", method=RequestMethod.GET)
	public String generateTempPwd(HttpServletRequest req, HttpServletResponse res ) {
		logger.log(IAppLogger.INFO, "Enter: LoginController - generateTempPwd");
		try {
			String userName= (String) req.getParameter("username");
			if ( userName != null ) {
				String newPassword = adminService.resetPassword(userName);
				if (newPassword != null) {
					res.getWriter().write("{\"password\":\"" + newPassword + "\"}");
				}
			}
			
		} catch (Exception e) {
			logger.log(IAppLogger.ERROR, e.getMessage(), e);
		} finally {
			logger.log(IAppLogger.INFO, "Exit: LoginController - generateTempPwd");
		}
		return null;
			
	}
	
	/**
	 *  retrieve users
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/regn/getUserNames", method = RequestMethod.GET)
	public @ResponseBody String getUserNames(HttpServletRequest req,
			HttpServletResponse res) throws IOException {
		logger.log(IAppLogger.INFO, "Fectching the User Names");
		try {
			List<UserTO> userToList = new ArrayList<UserTO>();
			String emailId = req.getParameter("emailId");
			userToList = parentService.getUserNamesByEmail(emailId);
			if(userToList != null) {
				String jsonString = JsonUtil.convertToJsonAdmin(userToList);
				res.setContentType("application/json");
				res.getWriter().write(jsonString);
				//res.getWriter().write( "{\"status\":\"Success\"}" );
				logger.log(IAppLogger.DEBUG,jsonString);
			}
			
		} catch (Exception exception) {
			logger.log(IAppLogger.ERROR, exception.getMessage(), exception);
		} finally {
			logger.log(IAppLogger.INFO, "Fectching the User Names");
		}
		return null;
	}
	
	/**
	 * Check if entered username is available and the user is enabled 
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	
	@RequestMapping(value = "/regn/checkActiveUser", method = RequestMethod.GET)
	public @ResponseBody String checkActiveUserAvailability(HttpServletRequest req,
			HttpServletResponse res) throws IOException {
		logger.log(IAppLogger.INFO, "Validating Username");

		try {
			String username = req.getParameter("username");
			// check username is available and the user is enabled
			if(parentService.checkActiveUserAvailability(username)) {
				res.setContentType("application/json");
				res.getWriter().write( "{\"status\":\"Success\", \"available\":\""+"true"+"\"}" );				
			}
			else {
				res.setContentType("application/json");
				res.getWriter().write( "{\"status\":\"Failure\", \"available\":\""+"false"+"\"}" );					
			}
	
		} catch (Exception exception) {
			logger.log(IAppLogger.ERROR, exception.getMessage(), exception);
		} finally {
			logger.log(IAppLogger.INFO, "Validating Username");
		}
		return null;
	}
	
	
	
} 
