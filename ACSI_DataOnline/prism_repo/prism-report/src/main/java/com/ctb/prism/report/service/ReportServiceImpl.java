package com.ctb.prism.report.service;

import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ctb.prism.core.exception.SystemException;
import com.ctb.prism.report.business.IReportBusiness;
import com.ctb.prism.report.transferobject.AssessmentTO;
import com.ctb.prism.report.transferobject.InputControlTO;
import com.ctb.prism.report.transferobject.ObjectValueTO;
import com.ctb.prism.report.transferobject.ReportFilterTO;
import com.ctb.prism.report.transferobject.ReportTO;

@Service("reportService")
public class ReportServiceImpl implements IReportService {

	@Autowired
	private IReportBusiness reportBusiness;
	
	public JasperPrint getFilledReport(JasperReport jasperReport, Map<String, Object> parameters) throws Exception {
		return reportBusiness.getFilledReport(jasperReport, parameters);
	}
	
	public void removeReportCache() {
		reportBusiness.removeReportCache();
	}
	public void removeCache() {
		reportBusiness.removeCache();
	}
	public JasperReport getReportJasperObject(String reportPath) {
		return reportBusiness.getReportJasperObject(reportPath);
	}
	public JasperReport getReportJasperObjectForName(String reportname) {
		return reportBusiness.getReportJasperObjectForName(reportname);
	}
	public List<ReportTO> getReportJasperObjectList(final String reportPath) throws JRException {
		return reportBusiness.getReportJasperObjectList(reportPath);
	}

	public List<InputControlTO> getInputControlDetails(String reportPath) {
		return reportBusiness.getInputControlDetails(reportPath);
	}
	
	public ReportFilterTO getDefaultFilter(List<InputControlTO> tos, String userName, String assessmentId, String combAssessmentId,
			String reportUrl ) {
		return reportBusiness.getDefaultFilter(tos, userName, assessmentId, combAssessmentId, reportUrl );
	}
	
	public List<ObjectValueTO> getValuesOfSingleInput(String query) {
		return reportBusiness.getValuesOfSingleInput(query);
	}
	
	public List<ObjectValueTO> getValuesOfSingleInput(String query, String userName, String changedObject, 
			String changedValue, Map<String, String> replacableParams, ReportFilterTO reportFilterTO) throws SystemException {
		return reportBusiness.getValuesOfSingleInput(query, userName, changedObject, changedValue, replacableParams, reportFilterTO);
	}
	public List<ReportTO> getAllReportList() {
		return reportBusiness.getAllReportList();
	}

	@Transactional (propagation = Propagation.REQUIRES_NEW)
	public boolean updateReport(String reportId, String reportName,
			String reportUrl, String isEnabled, String[] roles) {
		return reportBusiness.updateReport(reportId, reportName, reportUrl, isEnabled, roles);
	}
	
	@Transactional (propagation = Propagation.REQUIRES_NEW)
	public boolean deleteReport(String reportId) throws SystemException {
		return reportBusiness.deleteReport(reportId);
	}
	
	public List<AssessmentTO> getAssessments(boolean parentReports)
	{
		return reportBusiness.getAssessments(parentReports);
	}
	public ReportTO addNewDashboard(String reportName, String reportDescription, String reportType,
			String reportUri, String assessmentType, String reportStatus, String[] userRoles)throws Exception{
		return reportBusiness.addNewDashboard(reportName,reportDescription,reportType,reportUri,assessmentType,reportStatus,userRoles);
	}
}
