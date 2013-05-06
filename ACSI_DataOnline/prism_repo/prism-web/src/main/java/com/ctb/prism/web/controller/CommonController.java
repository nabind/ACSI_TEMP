package com.ctb.prism.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ctb.prism.core.constant.IApplicationConstants;
import com.ctb.prism.core.dao.BaseDAO;
import com.ctb.prism.core.logger.IAppLogger;
import com.ctb.prism.core.logger.LogFactory;
import com.ctb.prism.core.util.CustomStringUtil;
import com.ctb.prism.core.util.Utils;
import com.ctb.prism.report.service.IReportService;
import com.ctb.prism.report.transferobject.ReportTO;
import com.googlecode.ehcache.annotations.TriggersRemove;

@Controller
public class CommonController extends BaseDAO {
	
	private static final IAppLogger logger = LogFactory.getLoggerInstance(CommonController.class.getName());

	@Autowired
	private Utils utils;
	
	@Autowired
	private IReportService reportService;
	
	/**
	 * Load error page
	 * @param jspPath
	 * @return
	 */
	@RequestMapping(value = "/showError", method = RequestMethod.GET)
	public ModelAndView loadErrorPage(HttpServletRequest req, HttpServletResponse res) {
		logger.log(IAppLogger.INFO, "Enter: UserController - loadJSPView");
		ModelAndView mv = new ModelAndView("error/error");
		logger.log(IAppLogger.INFO, "Exit: UserController - loadJSPView");
		return mv;
	}
	
	/**
	 * Load JSP view
	 * @param jspPath
	 * @return
	 */
	@RequestMapping(value = "/loadJSPView", method = RequestMethod.GET)
	public ModelAndView loadJSPView(@RequestParam("path") String jspPath) {
		logger.log(IAppLogger.INFO, "Enter: UserController - loadJSPView");
		ModelAndView mv = new ModelAndView(jspPath);
		logger.log(IAppLogger.INFO, "Exit: UserController - loadJSPView");
		return mv;
	}
	
	/**
	 * Load Report jsp
	 * @param jspPath
	 * @return
	 */
	@RequestMapping(value = "/loadReportView", method = RequestMethod.GET)
	public ModelAndView loadJSPView(HttpServletRequest req, HttpServletResponse res) {
		String assessmentId = req.getParameter("assessmentId");
		String studentBioId = req.getParameter("studentId");
		if(studentBioId != null && studentBioId.length() == 0) {
			studentBioId = null;
		}
		ReportTO report = new ReportTO();
		report.setReportUrl(req.getParameter("reportUrl"));
		report.setReportName(req.getParameter("reportName"));
		String reportId  = req.getParameter("reportId");
		report.setReportId( (reportId == null)? 0 : Long.parseLong(reportId));
		report.setTabCount(req.getParameter("tabCount"));
		report.setCurrentTabNumber(req.getParameter("currentTabNumber"));
		report.setAssessmentName(assessmentId);
		report.setStudentBioId(studentBioId);
		
		String productName = ("101".equals(assessmentId))? "TerraNova 3 : " : ("102".equals(assessmentId))? "TerraNova with PTCS : " : ("103".equals(assessmentId))? "TerraNova with InView : " : ("104".equals(assessmentId))? "Bible : " : ("105".equals(assessmentId))? "TerraNova Longitudinal : " : "";
		report.setProductName(productName);
		
		String jspPath = req.getParameter("path");
		ModelAndView mv = new ModelAndView(jspPath);
		mv.addObject("homeReport", report);
		return mv;
	}
	
	/**
	 * This method is for showing static PDF files from mount location
	 * 
	 * @param req
	 * @param res
	 * @return
	 * @throws ServletException
	 */
	@RequestMapping(value="/acsipdf", method=RequestMethod.GET)
	public ModelAndView acsiPdf(HttpServletRequest req, HttpServletResponse res)
			throws ServletException {
		OutputStream os = null;
		try {
			String fileName = utils.getAcsiPdfLocation() + req.getParameter("pdfFileName");
			String userType = req.getParameter("userType");
			logger.log(IAppLogger.INFO, CustomStringUtil.appendString("Downloading pdf, user type: "
					, userType, "file name : ", fileName));
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			LdapUserDetailsImpl userDetails = (LdapUserDetailsImpl) auth.getPrincipal();
			String currentOrg = (String) req.getSession().getAttribute(IApplicationConstants.CURRORG);
			
			//if(userType != null &&  userType.equals(currentOrg)) {
			if(userType != null && (userType.equals("00000") || userType.equals(currentOrg))) {
				File file = null;
				file = new File(fileName);
		
				byte[] pdf = null;
				if (file.isFile()) pdf = IOUtils.toByteArray(new FileInputStream(fileName)); //getFileData(file);
				
				if(pdf != null) {
					res.setContentType("application/pdf");
					res.setHeader("Content-Disposition",
							CustomStringUtil.appendString("attachment; filename=\"", file.getName(), "\""));
					res.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
					res.setHeader("Pragma", "public");
			
					os = res.getOutputStream();
					os.write(pdf);
					/*for (int i = 0; i < pdf.length; i++) {
						os.write(pdf[i]);
					}*/
					os.flush();
				} else {
					logger.log(IAppLogger.DEBUG, "PDF file not present is the specified location");
					return new ModelAndView("error/error");
				}
			} else {
				logger.log(IAppLogger.DEBUG, "User does not have acces to view this PDF");
				return new ModelAndView("error/error");
			}
		} catch (IOException e) {
			logger.log(IAppLogger.ERROR, "", e);
			return new ModelAndView("error/error");
		} catch (Exception e) {
			logger.log(IAppLogger.ERROR, "", e);
			return new ModelAndView("error/error");
		} finally {
			IOUtils.closeQuietly(os);
		}
		return null;
	}
	
	/**
	 * Utility method to convert file to byte[]
	 * @param file
	 * @return
	 */
	private byte[] getFileData(final File file) {
		byte[] fileData = null;
		java.io.InputStream inputStrem = null;
		int i = 0;
		if (file.isFile()) {
			java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
			try {
				inputStrem = file.toURL().openStream();
				while ((i = inputStrem.read()) != -1) {
					baos.write(i);
				}
				fileData = baos.toByteArray();
			} catch (java.net.MalformedURLException exMalformedURLException) {
				exMalformedURLException.printStackTrace();
			} catch (java.io.IOException exIOException) {
				exIOException.printStackTrace();
			} finally {
				try {
					if (inputStrem != null)
						inputStrem.close();
					baos.close();
				} catch (IOException exIOException) {
					exIOException.printStackTrace();
				}
			}
		}
		return fileData;
	}
	
	/**
	 * Method to keep alive users
	 * @param req
	 * @param res
	 * @return
	 * @throws ServletException
	 */
	@RequestMapping(value="/acsiAlert", method=RequestMethod.GET)
	public ModelAndView acsiAlert(HttpServletRequest req, HttpServletResponse res) throws ServletException {
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			LdapUserDetailsImpl userDetails = (LdapUserDetailsImpl) auth.getPrincipal();
			logger.log(IAppLogger.INFO, CustomStringUtil.appendString("-----KEEP ALIVE USER ------- "
					, userDetails.getUsername()));
			
			res.setContentType("plain/text");
			//res.setCharacterEncoding("UTF-8");
			res.getWriter().write("Success");
			
		} catch (IOException e) {
			logger.log(IAppLogger.ERROR, "", e);
		} catch (Exception e) {
			logger.log(IAppLogger.ERROR, "", e);
		} 
		return null;
	}
	
	/**
	 * Method to return images to report
	 * @param req
	 * @param res
	 * @return
	 * @throws ServletException
	 */
	@RequestMapping(value="/image", method=RequestMethod.GET)
	public ModelAndView image(HttpServletRequest req, HttpServletResponse res) throws ServletException {
		OutputStream os = null;
		try {
			logger.log(IAppLogger.INFO, "fetching image for report");
			String image = req.getParameter("image");
			String assessmentId = req.getParameter("assessmentId");
			if(assessmentId == null) assessmentId = (String) req.getSession().getAttribute(IApplicationConstants.CURR_ASSESSMENT);
			InputStream inputStream = null;/*(InputStream) req.getSession().getAttribute(
					CustomStringUtil.appendString(IApplicationConstants.REPORT_IMAGE, assessmentId));*/
			if(inputStream == null) {
				logger.log(IAppLogger.INFO, "Image stream not found in session");
				String logoFile = "";
				if("101".equals(assessmentId)) logoFile = "TerranovaLogo.jpg";
				if("102".equals(assessmentId)) logoFile = "PTCSLogo.jpg";
				if("103".equals(assessmentId)) logoFile = "InViewLogo.jpg";
				if("104".equals(assessmentId)) logoFile = "BibleLogo.jpg";
				if("105".equals(assessmentId)) logoFile = "TerranovaLogo.jpg";
				
				URL settingsUrl = Thread.currentThread().getContextClassLoader().getResource(logoFile);
				if(settingsUrl != null) inputStream = settingsUrl.openStream();
			}
			if(inputStream != null) {
				byte[] img = Utils.getFileDataFromInputStream(inputStream);
				res.setContentType("image/jpeg");
				os = res.getOutputStream();
				for (int i = 0; i < img.length; i++) {
					os.write(img[i]);
				}
			}
			
		} catch (IOException e) {
			logger.log(IAppLogger.ERROR, "", e);
		} catch (Exception e) {
			logger.log(IAppLogger.ERROR, "", e);
		} 
		return null;
	}
	
	/**
	 * Method to clear all EH-Cache
	 * @param req
	 * @param res
	 * @return
	 * @throws ServletException
	 * @throws IOException 
	 */
	@Secured({"ROLE_CTB"})
	@RequestMapping(value="/clearCache", method=RequestMethod.GET)
	public ModelAndView clearCache(HttpServletRequest req, HttpServletResponse res) 
		throws ServletException, IOException {
		ModelAndView mv = new ModelAndView("common/success");
		try {
			reportService.removeCache();
			logger.log(IAppLogger.INFO, "Cache cleared ....");
			
			mv.addObject("message", "Cache cleared !!!");
		} catch (Exception e) {
			logger.log(IAppLogger.ERROR, "", e);
			mv.addObject("message", e.getMessage());
		} 
		return mv;
	}
	
	@Secured({"ROLE_CTB"})
	@RequestMapping(value="/clearReportCache", method=RequestMethod.GET)
	public ModelAndView clearReportCache(HttpServletRequest req, HttpServletResponse res) 
		throws ServletException, IOException {
		ModelAndView mv = new ModelAndView("common/success");
		try {
			reportService.removeReportCache();
			logger.log(IAppLogger.INFO, "Cache cleared ....");
			
			mv.addObject("message", "Cache cleared !!!");
		} catch (Exception e) {
			logger.log(IAppLogger.ERROR, "", e);
			mv.addObject("message", e.getMessage());
		} 
		return mv;
	}
	
	@RequestMapping(value="/clearAllCache", method=RequestMethod.GET)
	public ModelAndView clearAllCache(HttpServletRequest req, HttpServletResponse res) 
		throws ServletException, IOException {
		ModelAndView mv = new ModelAndView("common/success");
		System.out.println("Inside clearAllCache");
		
		try {
			reportService.removeCache();
			logger.log(IAppLogger.INFO, "All Cache cleared ....");
			
			mv.addObject("message", "All Cache cleared !!!");
		} catch (Exception e) {
			logger.log(IAppLogger.ERROR, "", e);
			mv.addObject("message", e.getMessage());
		} 
		return mv;
	}
	

}
