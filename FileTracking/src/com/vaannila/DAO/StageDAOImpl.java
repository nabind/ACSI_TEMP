package com.vaannila.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.vaannila.TO.OrgProcess;
import com.vaannila.TO.OrgTO;
import com.vaannila.TO.SearchProcess;
import com.vaannila.util.JDCConnectionDriver;


public class StageDAOImpl {
	static JDCConnectionDriver driver = null;
	static String DATA_SOURCE = "jdbc:jdc:acsi";
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<OrgProcess> getProcessDetails(String adminid) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<OrgProcess> processList = new ArrayList<OrgProcess>(); 
		OrgProcess process = null;
		try {
			driver = StageConnectionProvider.getDriver();
			conn = driver.connect(DATA_SOURCE, null);
			
			String query = "SELECT DISTINCT ORG.PROCESSID, ORG.STRUC_ELEMENT, ORG.ORG_LEVEL, ORG.CASE_COUNT, " +
						       "ORG.STAGE_DATA_STATUS, ORG.STAGE_PDF_STATUS, ORG.TARGET_STATUS, " +
						       "ORG.TARGET_EMAIL_STATUS, ORG.PROCESS_STATUS, ORG.PROCESS_LOG, ORG.SOURCE_FILENAME, " +
						       "ORG.UPDATED_DATE_TIME, to_char(ORG.UPDATED_DATE_TIME, 'MM/DD/YYYY HH24:MI:SS'), " +
						       "node.level3_element_name, node.level3_customer_codes, ORG.LOGIN_PDF_LOC, ORG.LETTER_PDF_LOC " +
						  "FROM ORG_PROCESS_STATUS ORG, " +
						       "stg_org_node node " +
						  "WHERE node.processid (+) = org.processid and org.adminid = ? " +       
						 "ORDER BY ORG.PROCESSID DESC";
			
			pstmt = conn.prepareCall(query);
			pstmt.setString(1, adminid);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				process = new OrgProcess(); 
				process.setProcessid(rs.getLong(1));
				process.setStructElement(rs.getString(2));
				process.setOrgLevel(rs.getString(3));
				process.setCaseCount(rs.getString(4));
				process.setDataStatus(rs.getString(5));
				process.setPdfStatus(rs.getString(6));
				process.setTargetStatus(rs.getString(7));
				process.setTargerEmailStatus(rs.getString(8));
				process.setProcessStatus(rs.getString(9));
				process.setProcessLog(rs.getString(10));
				process.setSourceFileName(rs.getString(11));
				process.setCreatedDate(rs.getString(12));
				process.setUpdatedDate(rs.getString(13));
				
				process.setElementName(rs.getString(14));
				process.setCustomarCode(rs.getString(15));
				process.setLoginPDFLoc(rs.getString(16));
				process.setLetterPDFLoc(rs.getString(17));
				
				System.out.println(process);
				processList.add(process);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			try {rs.close();} catch (Exception e2) {}
			try {pstmt.close();} catch (Exception e2) {}
			try {conn.close();} catch (Exception e2) {}
		}
		return processList;
	}
	
	public List<OrgProcess> searchProcess(SearchProcess orgProcess, String adminid) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<OrgProcess> processList = new ArrayList<OrgProcess>(); 
		OrgProcess process = null;
		try {
			driver = StageConnectionProvider.getDriver();
			conn = driver.connect(DATA_SOURCE, null);
			StringBuffer queryBuff = new StringBuffer();
			String query = "SELECT DISTINCT ORG.PROCESSID, ORG.STRUC_ELEMENT, ORG.ORG_LEVEL, ORG.CASE_COUNT, " +
						       "ORG.STAGE_DATA_STATUS, ORG.STAGE_PDF_STATUS, ORG.TARGET_STATUS, " +
						       "ORG.TARGET_EMAIL_STATUS, ORG.PROCESS_STATUS, ORG.PROCESS_LOG, ORG.SOURCE_FILENAME, " +
						       "ORG.UPDATED_DATE_TIME, to_char(ORG.UPDATED_DATE_TIME, 'MM/DD/YYYY HH24:MI:SS'), " +
						       "node.level3_element_name, node.level3_customer_codes, ORG.LOGIN_PDF_LOC, ORG.LETTER_PDF_LOC " +
						  "FROM ORG_PROCESS_STATUS ORG, " +
						       "stg_org_node node " +
						  "WHERE node.processid (+) = org.processid and org.adminid = ? ";
			
			queryBuff.append(query);
			
			if(orgProcess.getProcessid() > 0) 
				queryBuff.append("AND org.processid = ? ");
			if(orgProcess.getStructElement() != null & orgProcess.getStructElement().trim().length() > 0) 
				queryBuff.append("AND org.struc_element LIKE ? ");
			if(orgProcess.getUpdatedDate() != null & orgProcess.getUpdatedDate().trim().length() > 0) 
				queryBuff.append("AND to_char(org.updated_date_time, 'MM/DD/YYYY') = ? ");
			
			if(orgProcess.getDataStatus() != null & orgProcess.getDataStatus().trim().length() > 0) {
				if("BL".equals(orgProcess.getDataStatus())) {
					queryBuff.append("AND nvl(org.stage_data_status, '-') = '-' ");
				} else {
					queryBuff.append("AND org.stage_data_status = ? ");
				}
			}
			if(orgProcess.getPdfStatus() != null & orgProcess.getPdfStatus().trim().length() > 0) {
				if("BL".equals(orgProcess.getDataStatus())) {
					queryBuff.append("AND nvl(org.stage_pdf_status, '-') = '-' ");
				} else {
					queryBuff.append("AND org.stage_pdf_status = ? ");
				}
			}
			if(orgProcess.getTargetStatus() != null & orgProcess.getTargetStatus().trim().length() > 0) {
				if("BL".equals(orgProcess.getDataStatus())) {
					queryBuff.append("AND nvl(org.target_status, '-') = '-' ");
				} else {
					queryBuff.append("AND org.target_status = ? ");
				}
			}
			if(orgProcess.getTargerEmailStatus() != null & orgProcess.getTargerEmailStatus().trim().length() > 0) {
				if("BL".equals(orgProcess.getDataStatus())) {
					queryBuff.append("AND nvl(org.target_email_status, '-') = '-' ");
				} else {
					queryBuff.append("AND org.target_email_status = ? ");
				}
			}
			if(orgProcess.getProcessStatus() != null & orgProcess.getProcessStatus().trim().length() > 0) { 
				if("BL".equals(orgProcess.getDataStatus())) {
					queryBuff.append("AND nvl(org.process_status, '-') = '-' ");
				} else {
					queryBuff.append("AND org.process_status = ? ");
				}
			}
			
			
			queryBuff.append("ORDER BY ORG.PROCESSID DESC");
			
			pstmt = conn.prepareCall(queryBuff.toString());
			
			int count = 1;
			pstmt.setString(count++, adminid);
			if(orgProcess.getProcessid() > 0) 
				pstmt.setLong(count++, orgProcess.getProcessid());
			
			if(orgProcess.getStructElement() != null & orgProcess.getStructElement().trim().length() > 0) 
				pstmt.setString(count++, orgProcess.getStructElement());
			if(orgProcess.getUpdatedDate() != null & orgProcess.getUpdatedDate().trim().length() > 0) 
				pstmt.setString(count++, orgProcess.getUpdatedDate());
			
			if(orgProcess.getDataStatus() != null & orgProcess.getDataStatus().trim().length() > 0) 
				if(!"BL".equals(orgProcess.getDataStatus())) pstmt.setString(count++, orgProcess.getDataStatus());
			if(orgProcess.getPdfStatus() != null & orgProcess.getPdfStatus().trim().length() > 0) 
				if(!"BL".equals(orgProcess.getPdfStatus())) pstmt.setString(count++, orgProcess.getPdfStatus());
			if(orgProcess.getTargetStatus() != null & orgProcess.getTargetStatus().trim().length() > 0) 
				if(!"BL".equals(orgProcess.getTargetStatus())) pstmt.setString(count++, orgProcess.getTargetStatus());
			if(orgProcess.getTargerEmailStatus() != null & orgProcess.getTargerEmailStatus().trim().length() > 0) 
				if(!"BL".equals(orgProcess.getTargerEmailStatus())) pstmt.setString(count++, orgProcess.getTargerEmailStatus());
			if(orgProcess.getProcessStatus() != null & orgProcess.getProcessStatus().trim().length() > 0) 
				if(!"BL".equals(orgProcess.getProcessStatus())) pstmt.setString(count++, orgProcess.getProcessStatus());
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				process = new OrgProcess(); 
				process.setProcessid(rs.getLong(1));
				process.setStructElement(rs.getString(2));
				process.setOrgLevel(rs.getString(3));
				process.setCaseCount(rs.getString(4));
				process.setDataStatus(rs.getString(5));
				process.setPdfStatus(rs.getString(6));
				process.setTargetStatus(rs.getString(7));
				process.setTargerEmailStatus(rs.getString(8));
				process.setProcessStatus(rs.getString(9));
				process.setProcessLog(rs.getString(10));
				process.setSourceFileName(rs.getString(11));
				process.setCreatedDate(rs.getString(12));
				process.setUpdatedDate(rs.getString(13));
				
				process.setElementName(rs.getString(14));
				process.setCustomarCode(rs.getString(15));
				process.setLoginPDFLoc(rs.getString(16));
				process.setLetterPDFLoc(rs.getString(17));
				
				System.out.println(process);
				processList.add(process);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			try {rs.close();} catch (Exception e2) {}
			try {pstmt.close();} catch (Exception e2) {}
			try {conn.close();} catch (Exception e2) {}
		}
		return processList;
	}
	
	/**
	 * This method is to update email id of a school
	 * @param email
	 * @param structureElement
	 * @return
	 * @throws Exception
	 */
	public long updateEmail(String email, String structureElement, String adminid) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		long updateCount = 0;
		try {
			driver = StageConnectionProvider.getDriver();
			conn = driver.connect(DATA_SOURCE, null);

			String query = "update stg_org_node set level3_email = ? ," +
					" updated_date_time = SYSDATE where level3_structure_element = ? and adminid = ? "; 
			
			pstmt = conn.prepareCall(query);
			pstmt.setString(1, email);
			pstmt.setString(2, structureElement);
			pstmt.setString(3, adminid);
			updateCount = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			try {pstmt.close();} catch (Exception e2) {}
			try {conn.close();} catch (Exception e2) {}
		}
		
		return updateCount;
	}
	
	/**
	 * Method to update the process log
	 * @param processId
	 * @param log
	 * @return
	 * @throws Exception 
	 */
	public int updateProcessLog(long processId, String log) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int updateCount = 0;
		String processLog = getProcessLog(processId);
		try {
			driver = StageConnectionProvider.getDriver();
			conn = driver.connect(DATA_SOURCE, null);
			
			processLog = (processLog == null) ? log : processLog + log;
			
			String query = "UPDATE ORG_PROCESS_STATUS SET process_log = ?  " +
							",updated_date_time = SYSDATE WHERE processid = ?";
			pstmt = conn.prepareCall(query);
			pstmt.setString(1, processLog);
			pstmt.setLong(2, processId);
			updateCount = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			try {pstmt.close();} catch (Exception e2) {}
			try {conn.close();} catch (Exception e2) {}
		}
		return updateCount;
	}
	
	/**
	 * Method to update the process log
	 * @param processId
	 * @param log
	 * @return
	 * @throws Exception 
	 */
	public String getProcessLog(long processId) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String processLog = "";
		try {
			driver = StageConnectionProvider.getDriver();
			conn = driver.connect(DATA_SOURCE, null);
			
			String query = "SELECT u.process_log FROM ORG_PROCESS_STATUS u " +
						"WHERE u.processid = ?";
			pstmt = conn.prepareCall(query);
			pstmt.setLong(1, processId);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				processLog = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			try {rs.close();} catch (Exception e2) {}
			try {pstmt.close();} catch (Exception e2) {}
			try {conn.close();} catch (Exception e2) {}
		}
		return processLog;
	}
	
	
}
