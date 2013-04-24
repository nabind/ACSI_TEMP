package com.ctb.prism.parent.dao;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.ctb.prism.core.constant.IApplicationConstants;
import com.ctb.prism.core.constant.IQueryConstants;
import com.ctb.prism.core.dao.BaseDAO;
import com.ctb.prism.core.exception.BusinessException;
import com.ctb.prism.core.logger.IAppLogger;
import com.ctb.prism.core.logger.LogFactory;
import com.ctb.prism.core.util.CustomStringUtil;
import com.ctb.prism.core.util.LdapManager;
import com.ctb.prism.login.transferobject.UserTO;
import com.ctb.prism.parent.transferobject.ParentTO;
import com.ctb.prism.parent.transferobject.QuestionTO;
import com.ctb.prism.parent.transferobject.StudentTO;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.TriggersRemove;

@Repository("parentDAO")
public class ParentDAOImpl extends BaseDAO implements IParentDAO {

	@Autowired
	private LdapManager ldapManager;
	
	private static final IAppLogger logger = LogFactory.getLoggerInstance(ParentDAOImpl.class.getName());

	/**
	 * Fetch all questions
	 * 
	 * @return Map
	 */
	@Cacheable(cacheName = "securityQuestions")
	public List getSecretQuestions() {
		List<Map<String, Object>> lstData = getJdbcTemplatePrism()
				.queryForList(IQueryConstants.GET_SECRET_QUESTION);
		List questionList = new ArrayList();
		QuestionTO quesTo = null;
		for (Map<String, Object> questiondetails : lstData) {
			quesTo = new QuestionTO();
			quesTo.setQuestionId(((BigDecimal) questiondetails
					.get("QUESTION_ID")).longValue());
			quesTo.setQuestion((String) (questiondetails.get("QUESTION")));
			quesTo.setSno(((BigDecimal) questiondetails.get("SNO")).longValue());
			questionList.add(quesTo);
		}

		return questionList;
	}

	/**
	 * check provided user availability
	 * 
	 * @return boolean
	 */
	public boolean checkUserAvailability(String username) {
		List<Map<String, Object>> lstData = getJdbcTemplatePrism()
				.queryForList(IQueryConstants.VALIDATE_USER_NAME, username);
		if (lstData == null || lstData.isEmpty()) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	/**
	 * check provided user and activation status for forgot password
	 * 
	 * @return boolean
	 */
	public boolean checkActiveUserAvailability(String username) {
		List<Map<String, Object>> lstData = getJdbcTemplatePrism()
				.queryForList(IQueryConstants.VALIDATE_ACTIVE_USER_NAME, username);
		if (lstData == null || lstData.isEmpty()) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	/**
	 * check check whether role is already tagged to this user
	 * 
	 * @return boolean
	 */
	public boolean isRoleAlreadyTagged(String roleId, String userName){
		List<Map<String, Object>> lstData = getJdbcTemplatePrism()
		.queryForList(IQueryConstants.IS_ROLE_TAGGED, roleId,userName);
					if (lstData == null || lstData.isEmpty()) {
						return Boolean.FALSE;
					}
		return Boolean.TRUE;
	}

	/**
	 * check provided invitation code availability
	 * 
	 * @return boolean
	 */
	public ParentTO validateIC(String invitationCode) {

		List<Map<String, Object>> lstData = getJdbcTemplatePrism()
				.queryForList(IQueryConstants.VALIDATE_INVITATION_CODE,
						invitationCode);
		ParentTO parentTO = null;
		if (lstData.size() > 0) {
			parentTO = new ParentTO();
			for (Map<String, Object> fieldDetails : lstData) {
				parentTO.setTotalAttemptedCalim(((BigDecimal) fieldDetails.get("TOTAL_ATTEMPT_IC_CLAIM")).longValue());
				parentTO.setTotalAvailableCalim(((BigDecimal) fieldDetails.get("TOTAL_AVAILABLE_IC_CLAIM")).longValue());
				parentTO.setIcExpirationStatus((String) (fieldDetails.get("EXPIRATION_STATUS")));
				parentTO.setIcActivationStatus((String) (fieldDetails.get("ACTIVATION_STATUS")));
			}
		}
		return parentTO;
	}

	/**
	 * Fetch all Students for invitation code
	 * 
	 * @return ParentTO
	 */
	public ParentTO getStudentForIC(String invitationCode) {

		StudentTO studentTO = null;
		ParentTO parentTO = new ParentTO();
		List<StudentTO> studentToList = new ArrayList<StudentTO>();
		List<Map<String, Object>> lstData = getJdbcTemplatePrism()
				.queryForList(IQueryConstants.GET_STUDENT_FOR_INVITATION_CODE,
						invitationCode);
			for (Map<String, Object> fieldDetails : lstData) {
				studentTO = new StudentTO();
				studentTO.setStudentName((String) (fieldDetails
						.get("STUDENT_NAME")));
				studentTO
						.setGrade((String) (fieldDetails.get("ADMINISTRATION")));
				studentTO
						.setAdministration((String) (fieldDetails.get("GRADE")));
				studentToList.add(studentTO);
			}
			parentTO.setStudentToList(studentToList);

		return parentTO;
	}

	/**
	 * Save parent user information
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	public boolean registerUser(ParentTO parentTO) throws BusinessException {
		try {
			// incase the user exists into LDAP
			ldapManager.deleteUser(parentTO.getUserName(), parentTO.getUserName(),
					parentTO.getUserName());
		} catch (Exception ex){}
		/*String displayName=(CustomStringUtil.appendString(parentTO.getLastName(),  parentTO.getFirstName())).trim();
			if (displayName.length()>10) displayName=displayName.substring(0, 10);*/
		if( ldapManager.addUser(parentTO.getUserName(), parentTO.getUserName(),
								parentTO.getUserName(), parentTO.getPassword()) ) {
			long user_seq_id = getJdbcTemplatePrism().queryForLong(
					IQueryConstants.USER_SEQ_ID);
			int count = getJdbcTemplatePrism().update(
					IQueryConstants.INSERT_USER_DATA, user_seq_id,
					parentTO.getUserName(),parentTO.getDisplayName(),
					parentTO.getLastName(), parentTO.getFirstName(),
					parentTO.getMail(), parentTO.getMobile(),
					parentTO.getCountry(), parentTO.getZipCode(), parentTO.getStreet(),parentTO.getCity(), parentTO.getState(), 
					parentTO.getInvitationCode(), parentTO.isFirstTimeUser() ? IApplicationConstants.FLAG_Y : IApplicationConstants.FLAG_N);
			logger.log(IAppLogger.DEBUG, "INSERT_USER_DATA DONE");
			
			
	
			if (count > 0) {
					
					getJdbcTemplatePrism().update(IQueryConstants.ADD_ROLE_TO_REGISTERED_USER,parentTO.getUserName(),"ROLE_USER");
					getJdbcTemplatePrism().update(IQueryConstants.ADD_ROLE_TO_REGISTERED_USER,parentTO.getUserName(),"ROLE_PARENT");
					logger.log(IAppLogger.DEBUG, "ADD_ROLE_TO_REGISTERED_USER DONE");
					
				boolean isSavedInvitationCodeClaim = saveInvitationCodeClaim(
						user_seq_id, parentTO);
					
				if (isSavedInvitationCodeClaim) {
					boolean isUpdatedInvitationCodeClaimCount = updateInvitationCodeClaimCount(parentTO.getInvitationCode());
					if (isUpdatedInvitationCodeClaimCount) {
						boolean isSavedPasswordHistAnswer = savePasswordHistAnswer(
								user_seq_id, parentTO.getQuestionToList());
		
						if (isSavedPasswordHistAnswer) {
							/*ldapManager.addUser(parentTO.getUserName(), parentTO.getUserName(),
									parentTO.getUserName(), parentTO.getPassword());*/
							return Boolean.TRUE;
						}
					}
				}
			}
			return Boolean.FALSE;
		} else {
			return Boolean.FALSE;
		}
	}

	/**
	 * Save Invitation code claim
	 * 
	 * @return boolean
	 */
	public boolean saveInvitationCodeClaim(long userid, ParentTO parentTO) {

		int count = getJdbcTemplatePrism().update(
				IQueryConstants.INSERT_INVITATION_CODE_CLAIM_DATA, userid,
				parentTO.getInvitationCode());
		logger.log(IAppLogger.DEBUG, "INSERT_INVITATION_CODE_CLAIM_DATA Done");
		if (count > 0) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * Update Invitation code claim count
	 * 
	 * @return boolean
	 */
	public boolean updateInvitationCodeClaimCount(String invitationCode) {
		int availableCliamCount= getJdbcTemplatePrism().queryForInt(IQueryConstants.CHECK_AVAILABLE_INVITATION_CODE_CLAIM_COUNT,invitationCode);
		if (availableCliamCount >0){
			int count1 = getJdbcTemplatePrism().update(
					IQueryConstants.UPDATE_INVITATION_CODE_CLAIM_COUNT, invitationCode, invitationCode);
			int count2 = getJdbcTemplatePrism().update(
					IQueryConstants.UPDATE_AVAILABLE_INVITATION_CODE_CLAIM_COUNT, invitationCode, invitationCode);
			logger.log(IAppLogger.DEBUG, "UPDATE_INVITATION_CODE_CLAIM_COUNT Done");
			if ((count1 > 0) && (count2 > 0)) {
				return Boolean.TRUE;
			}
		}
		
		return Boolean.FALSE;
	}
	

	/**
	 * Save Password hint answer
	 * 
	 * @return boolean
	 */
	public boolean savePasswordHistAnswer(long userid,
			List<QuestionTO> questionToList) {
		int count = 0;
		long answerCount= getJdbcTemplatePrism().queryForLong(IQueryConstants.CHECK_FOR_EXISTING_ANSWER,userid);	
		logger.log(IAppLogger.DEBUG, "answerCount................"+answerCount);
		if(answerCount==0){
			//Inserting answers if there no existing answers
			for (QuestionTO questionTo : questionToList) {
				getJdbcTemplatePrism().update(IQueryConstants.INSERT_ANSWER_DATA,
						userid, questionTo.getQuestionId(), questionTo.getAnswer());
				logger.log(IAppLogger.DEBUG, "INSERT_ANSWER_DATA Done");
				count++;
			}
		}else{	
				//Updating answer if there is existing answer and updating the uadated_date_time
					for (QuestionTO questionTo : questionToList) {
						getJdbcTemplatePrism().update(IQueryConstants.UPDATE_ANSWER_DATA,
								questionTo.getQuestionId(), questionTo.getAnswer(),userid,questionTo.getAnswerId() );
						logger.log(IAppLogger.DEBUG, "UPDATED EXISTING USER ANSWERS");
						count++;
				}
			}
		
		if (count > 0) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	
	/**
	 * Retrieves the list of the children of the logged in parent.
	 * This information is displayed in the home page of the parent login.
	 * @param userName User Name of the logged in parent 
	 */
	public List<StudentTO> getChildrenList( String userName,String clickedTreeNode, String adminYear ) {
		List<StudentTO> children = null;
		List<Map<String,Object>> list = getJdbcTemplatePrism().queryForList(IQueryConstants.SEARCH_CHILDREN, userName);
		logger.log(IAppLogger.DEBUG, "outside...... list");
		if ( list != null && list.size() > 0 )
		{
			logger.log(IAppLogger.DEBUG, "Inside...... list");
			children = new ArrayList<StudentTO>();
			for (Map<String, Object> data : list) {
				StudentTO studentTO = new StudentTO();
				studentTO.setStudentName((String) data.get("STUDENT_NAME"));
				studentTO.setStudentBioId(((BigDecimal) data.get("STUDENT_BIO_ID"))
						.longValue());
				studentTO.setStructureElement(((BigDecimal) data.get("STRUCTURE_ELEMENT")).longValue());
				studentTO.setAdministration((String) data.get("ADMIN_SEASON_YEAR"));
				studentTO.setOrgId(((BigDecimal) data.get("ORG_ID")).longValue());
				studentTO.setGrade((String) data.get("STUDENTGRADE"));
				//studentTO.setClikedOrgId(((BigDecimal) data.get("CLICKED_ORG")).longValue());
				try{studentTO.setClikedOrgId(Long.parseLong(clickedTreeNode));} catch(Exception ex){}
				studentTO.setAdminid(((BigDecimal) data.get("ADMINID")).toString());
				children.add(studentTO);
			}
		}
		return children;
	}
	
	/**
	 * Returns the parentList on load.
	 * 
	 * @param orgId
	 * @return
	 */
	public ArrayList<ParentTO> getParentList(String orgId, String adminYear, String searchParam) {

		ArrayList<ParentTO> parentTOs = new ArrayList<ParentTO>();
		String userName = "";
		String tenantId = "";
		List<Map<String, Object>> lstData = null;
		if (orgId.indexOf("_") > 0) {
			userName = orgId.substring((orgId.indexOf("_") + 1),orgId.length());
			tenantId = orgId.substring(0, orgId.indexOf("_"));
			if(searchParam != null && searchParam.trim().length() > 0) {
				searchParam = CustomStringUtil.appendString("%", searchParam, "%");
				lstData = getJdbcTemplatePrism().queryForList(
						IQueryConstants.GET_PARENT_DETAILS_ON_SCROLL_WITH_SRCH_PARAM, tenantId, userName, 
						searchParam, searchParam, searchParam);
			} else {
				lstData = getJdbcTemplatePrism().queryForList(
						IQueryConstants.GET_PARENT_DETAILS_ON_SCROLL, tenantId, userName);
			}
		} else {
			tenantId = orgId;
			lstData = getJdbcTemplatePrism().queryForList(
					IQueryConstants.GET_PARENT_DETAILS_ON_FIRST_LOAD, tenantId);
			logger.log(IAppLogger.DEBUG, lstData.size()+"");
		}

		if (lstData.size() > 0) {
			parentTOs = new ArrayList<ParentTO>();
			for (Map<String, Object> fieldDetails : lstData) {
				ParentTO to = new ParentTO();
				to.setUserId(((BigDecimal) fieldDetails.get("USER_ID"))
						.longValue());
				to.setUserName((String) (fieldDetails.get("USERNAME")));
				to.setDisplayName((String) (fieldDetails.get("FULLNAME")));
				to.setStatus((String) (fieldDetails.get("STATUS")));
				to.setOrgId(((BigDecimal) fieldDetails.get("ORG_ID")).longValue());
				//to.setClikedOrgId(((BigDecimal) fieldDetails.get("CLICKED_ORG")).longValue());
				try{to.setClikedOrgId(Long.parseLong(tenantId));} catch(Exception ex){}
				to.setLastLoginAttempt((String) (fieldDetails.get("LAST_LOGIN_ATTEMPT")));
				to.setOrgName((String) (fieldDetails.get("ORG_NAME")));
				parentTOs.add(to);
			}
		}

		return parentTOs;
	}	
	
	/**
	 * Searches and returns the parent names(use like operator) as a JSON
	 * string. Performs case insensitive searching. This method is used to
	 * perform auto complete in search box.
	 * 
	 * @param parentName
	 *            Search String treated as parent name
	 * @param tenantId
	 *            parentId of the logged in user
	 */
	public ArrayList <ParentTO> searchParent(String parentName, String tenantId, String adminYear,String isExactSeacrh){
		
		ArrayList<ParentTO> parentTOs = new ArrayList<ParentTO>();
		List<Map<String, Object>> parentlist = null;
		
		if (IApplicationConstants.FLAG_N.equalsIgnoreCase(isExactSeacrh)){
			parentName = CustomStringUtil.appendString("%", parentName, "%");
			parentlist = getJdbcTemplatePrism().queryForList(
					IQueryConstants.SEARCH_PARENT, tenantId, parentName,parentName,parentName, "15");
		}	else{
			parentlist = getJdbcTemplatePrism().queryForList(
					IQueryConstants.SEARCH_PARENT_EXACT, tenantId, parentName, "15");
		}
		
		 
		
		if (parentlist.size() > 0) {
			parentTOs = new ArrayList<ParentTO>();
			for (Map<String, Object> fieldDetails : parentlist) {
				ParentTO to = new ParentTO();
				to.setUserId(((BigDecimal) fieldDetails.get("USER_ID"))
						.longValue());
				to.setUserName((String) (fieldDetails.get("USERNAME")));
				to.setDisplayName((String) (fieldDetails.get("FULLNAME")));
				to.setStatus((String) (fieldDetails.get("STATUS")));
				to.setOrgId(((BigDecimal) fieldDetails.get("ORG_ID")).longValue());
				//to.setClikedOrgId(((BigDecimal) fieldDetails.get("CLICKED_ORG")).longValue());
				try{to.setClikedOrgId(Long.parseLong(tenantId));} catch(Exception ex){}
				to.setLastLoginAttempt((String) (fieldDetails.get("LAST_LOGIN_ATTEMPT")));
				to.setOrgName((String) (fieldDetails.get("ORG_NAME")));
				parentTOs.add(to);
			}
		}
		return parentTOs;
	}
	
	/**
	 * Searches and returns the parent(s) with given name (like operator).
	 * Performs case insensitive searching.
	 * 
	 * @param parentName
	 *            Search String treated as parent name
	 * @param tenantId
	 *            parentId of the logged in user
	 * @return
	 */
	public String searchParentAutoComplete( String parentName, String tenantId, String adminYear ) {
		parentName = CustomStringUtil.appendString("%", parentName, "%");
		String parentListJsonString = null;
		List<Map<String, Object>> listOfParents = getJdbcTemplatePrism().queryForList(
				IQueryConstants.SEARCH_PARENT, tenantId, parentName,parentName,parentName, "100");
		if (listOfParents != null && listOfParents.size() > 0) {
			parentListJsonString = "[";
			for (Map<String, Object> data : listOfParents) {
				//String parentNameStr = (String) data.get("USERNAME");
				parentListJsonString = CustomStringUtil.appendString(
						parentListJsonString, "\"", (String) data.get("USERNAME"), "<br/>"
						, (String) data.get("FULLNAME"), "\",");
			}
			parentListJsonString = CustomStringUtil.appendString(parentListJsonString
					.substring(0, parentListJsonString.length() - 1), "]");
		}
		logger.log(IAppLogger.DEBUG, parentListJsonString);
		return parentListJsonString;
	}
	
	/**
	 * 
	 */
	public ArrayList<StudentTO> getStudentList(String orgId, String adminYear, String searchParam) {

		ArrayList<StudentTO> studentTOs = new ArrayList<StudentTO>();
		String studentNameAndId = "";
		String tenantId = "";
		List<Map<String, Object>> lstData = null;
		if (orgId.lastIndexOf("|") > 0) {
			tenantId = orgId.substring((orgId.lastIndexOf("|") + 1),orgId.length());
			studentNameAndId = orgId.substring(0, orgId.lastIndexOf("|"));
			if(searchParam != null && searchParam.trim().length() > 0) {
				searchParam = CustomStringUtil.appendString("%", searchParam, "%");
				lstData = getJdbcTemplatePrism().queryForList(
						IQueryConstants.GET_STUDENT_DETAILS_ON_SCROLL_WITH_SRCH_PARAM,adminYear,adminYear,tenantId,tenantId,tenantId,
						tenantId,studentNameAndId, searchParam);
			} else {
				lstData = getJdbcTemplatePrism().queryForList(
						IQueryConstants.GET_STUDENT_DETAILS_ON_SCROLL,adminYear,adminYear,tenantId,tenantId,tenantId,tenantId,studentNameAndId);
			}
			
		} else {
			tenantId = orgId;
			lstData = getJdbcTemplatePrism().queryForList(
					IQueryConstants.GET_STUDENT_DETAILS_ON_FIRST_LOAD,adminYear,adminYear,tenantId,tenantId,tenantId,tenantId);
			logger.log(IAppLogger.DEBUG, lstData.size()+"");
		}

		if (lstData.size() > 0) {
			studentTOs = new ArrayList<StudentTO>();
			for (Map<String, Object> fieldDetails : lstData) {
				StudentTO to = new StudentTO();
				long studentBioId = ((BigDecimal) fieldDetails.get("STUDENT_BIO_ID")).longValue();
				to.setStudentBioId(studentBioId);
				if(getParentAccountDetails(String.valueOf(studentBioId))!=null)
				{
					to.setParentAccount(getParentAccountDetails(String.valueOf(studentBioId)));
				}
				else
				{
					to.setParentAccount(Collections.<ParentTO>emptyList());
				}
				to.setStructureElement(((BigDecimal) fieldDetails.get("STRUCTURE_ELEMENT")).longValue());
				to.setStudentName((String) (fieldDetails.get("STUDENTNAME")));
				to.setGrade((String) (fieldDetails.get("STUDENTGRADE")));
				to.setRowIndentifier((String) (fieldDetails.get("ROWIDENTIFIER")));
				to.setOrgName((String) (fieldDetails.get("SCHOOL")));
				tenantId = (tenantId == null)? tenantId = "0" : tenantId;
				to.setClikedOrgId(Long.parseLong(tenantId));
				studentTOs.add(to);
			}
		}

		return studentTOs;
	}
	
	/**
	 * 
	 * @param studentBioId
	 * @return
	 */	
	private ArrayList<ParentTO> getParentAccountDetails(String studentBioId) {
		
		ArrayList<ParentTO> parentTOs = null;
		List<Map<String, Object>> parentAccountData = null;
		if (studentBioId!=null)
		{
			parentAccountData = getJdbcTemplatePrism().queryForList(
				IQueryConstants.GET_PARENT_DETAILS_FOR_CHILDREN, studentBioId);
		
			if (parentAccountData.size() > 0) {
				parentTOs = new ArrayList<ParentTO>();
				for (Map<String, Object> fieldDetails : parentAccountData) {
					ParentTO to = new ParentTO();
					to.setUserName((String) (fieldDetails.get("USERNAME")));
					to.setStatus((String) (fieldDetails.get("STATUS")));
					parentTOs.add(to);
				}
			}
		}
		return parentTOs;
	}
	
	
	
	
	
	/**
	 * Searches and returns the students(s) with given name (like operator).
	 * Performs case insensitive searching.
	 * 
	 * @param parentName
	 *            Search String treated as student name
	 * @param tenantId
	 *            parentId of the logged in user
	 * @return
	 */
	public String searchStudentAutoComplete( String studentName, String tenantId, String adminyear ) {
		studentName = CustomStringUtil.appendString("%", studentName, "%");
		String studentListJsonString = null;
		List<Map<String, Object>> listOfStudents = getJdbcTemplatePrism().queryForList(
				IQueryConstants.SEARCH_STUDENT,adminyear,adminyear, tenantId,tenantId,tenantId,tenantId,studentName,"100");
		if (listOfStudents != null && listOfStudents.size() > 0) {
			studentListJsonString = "[";
			for (Map<String, Object> data : listOfStudents) {
				String studentNameStr = (String) data.get("STUDENTNAME");
				studentListJsonString = CustomStringUtil.appendString(
						studentListJsonString, "\"", studentNameStr, "\",");
			}
			studentListJsonString = CustomStringUtil.appendString(studentListJsonString
					.substring(0, studentListJsonString.length() - 1), "]");
		}
		logger.log(IAppLogger.DEBUG, studentListJsonString);
		return studentListJsonString;
	}
	
	/**
	 * Searches and returns the student names(use like operator) as a JSON
	 * string. Performs case insensitive searching. This method is used to
	 * perform auto complete in search box.
	 * 
	 * @param studentName
	 *            Search String treated as student name
	 * @param tenantId
	 *            parentId of the logged in user
	 */
	public ArrayList <StudentTO> searchStudent(String studentName, String tenantId, String adminyear){
		
		ArrayList<StudentTO> studentTOs = new ArrayList<StudentTO>();
		List<Map<String, Object>> studentlist = null;
		
		studentName = CustomStringUtil.appendString("%", studentName, "%");
		
		studentlist = getJdbcTemplatePrism().queryForList(
				IQueryConstants.SEARCH_STUDENT, adminyear, adminyear, tenantId, tenantId, tenantId, tenantId, studentName, "15");
		
			if (studentlist != null && studentlist.size() > 0) {
				studentTOs = new ArrayList<StudentTO>();
				for (Map<String, Object> fieldDetails : studentlist) {
					StudentTO to = new StudentTO();
					long studentBioId = ((BigDecimal) fieldDetails.get("STUDENT_BIO_ID")).longValue();
					to.setStudentBioId(studentBioId);
					if(getParentAccountDetails(String.valueOf(studentBioId))!=null)
					{
						to.setParentAccount(getParentAccountDetails(String.valueOf(studentBioId)));
					}
					else
					{
						to.setParentAccount(Collections.<ParentTO>emptyList());
					}
					to.setStructureElement(((BigDecimal) fieldDetails.get("STRUCTURE_ELEMENT")).longValue());
					to.setStudentName((String) (fieldDetails.get("STUDENTNAME")));
					to.setRowIndentifier((String) (fieldDetails.get("ROWIDENTIFIER")));
					to.setGrade((String) (fieldDetails.get("STUDENTGRADE")));
					tenantId = (tenantId == null)? tenantId = "0" : tenantId;
					to.setClikedOrgId(Long.parseLong(tenantId));
					//to.setInvitationcode((String) (fieldDetails.get("INVITATIONCODE")));
					//to.setOrgId(((BigDecimal) fieldDetails.get("ORG_ID")).longValue());
					//to. setActivationStatus((String) (fieldDetails.get("ACTIVATIONSTATUS")));
					to.setOrgName((String) (fieldDetails.get("SCHOOL")));
					studentTOs.add(to);
				}
			}



		return studentTOs;
	}
	
	
	public ArrayList <StudentTO> searchStudentOnRedirect(String studentBioId, String tenantId)
	{
		ArrayList<StudentTO> studentTOs = new ArrayList<StudentTO>();
		List<Map<String, Object>> studentlist = null;
				
		studentlist = getJdbcTemplatePrism().queryForList(
				IQueryConstants.SEARCH_STUDENT_ON_REDIRECT, tenantId, studentBioId);
		
			if (studentlist != null && studentlist.size() > 0) {
				studentTOs = new ArrayList<StudentTO>();
				for (Map<String, Object> fieldDetails : studentlist) {
					StudentTO to = new StudentTO();
					to.setStudentBioId(((BigDecimal) fieldDetails.get("STUDENT_BIO_ID"))
							.longValue());
					to.setParentAccount(getParentAccountDetails(((Long)(to.getStudentBioId())).toString()));
					to.setStructureElement(((BigDecimal) fieldDetails.get("STUDENT_STRUC_ELEMENT")).longValue());
					to.setStudentName((String) (fieldDetails.get("STUDENTNAME")));
					to.setRowIndentifier((String) (fieldDetails.get("ROWIDENTIFIER")));
					to.setGrade((String) (fieldDetails.get("STUDENTGRADE")));
					to.setInvitationcode((String) (fieldDetails.get("INVITATIONCODE")));
					to.setOrgId(((BigDecimal) fieldDetails.get("ORG_ID")).longValue());
					//to.setClikedOrgId(((BigDecimal) fieldDetails.get("CLICKED_ORG")).longValue());
					try{to.setClikedOrgId(Long.parseLong(tenantId));} catch(Exception ex){}
					to. setActivationStatus((String) (fieldDetails.get("ACTIVATIONSTATUS")));
					to.setOrgName((String) (fieldDetails.get("ORG_NAME")));
					studentTOs.add(to);
				}
			}



		return studentTOs;
	}
	
	/**
	 * Retrieves the list of the children of the logged in parent.
	 * This information is displayed in the home page of the parent login.
	 * @param userName User Name of the logged in parent 
	 */
	public List<StudentTO> getAssessmentList( String studentBioId ) {
		List<StudentTO> assessmentList = null;
		List<Map<String,Object>> list = getJdbcTemplatePrism().queryForList(IQueryConstants.GET_ASSESSMENT_LIST, studentBioId);
		if ( list != null && list.size() > 0 )
		{
			assessmentList = new ArrayList<StudentTO>();
			for (Map<String, Object> fieldDetails : list) {
				StudentTO studentTO = new StudentTO();
				studentTO.setStudentBioId(((BigDecimal) fieldDetails.get("STUDENT_BIO_ID")).longValue());
				studentTO.setAdministration((String) fieldDetails.get("ASSESSMENT_YEAR"));
				studentTO.setInvitationcode((String) fieldDetails.get("INVITATION_CODE"));
				studentTO.setExpirationDate((String) fieldDetails.get("EXPIRATION_DATE"));
				studentTO.setIcExpirationStatus((String) fieldDetails.get("EXPIRATION_STATUS"));
				studentTO.setTotalAvailableClaim(((BigDecimal) fieldDetails.get("TOTAL_AVAILABLE_IC_CLAIM")).longValue());
				assessmentList.add(studentTO);
			}
		}
		return assessmentList;
	}
	
	public boolean updateAssessmentDetails(String studentBioId, String administration, String invitationcode,
			String icExpirationStatus, String totalAvailableClaim,String expirationDate) throws Exception {

		logger.log(IAppLogger.INFO, "Enter: ParentDAOImpl - updateUser");
		try {		
				// update invitation_code table
				getJdbcTemplatePrism().update(IQueryConstants.UPDATE_ASSESSMENT,
						totalAvailableClaim, expirationDate, invitationcode, studentBioId);
		
		} catch (Exception e) {
			logger.log(IAppLogger.ERROR, "Error occurred while updating assessment details.", e);
			return false;
		}
		logger.log(IAppLogger.INFO, "Exit: ParentDAOImpl - updateUser");
		return true;
	}
	
	/**
	 * by Deepak Save First Time user profile &(ChangePAssword)
	 * 
	 * @return boolean
	 * @throws BusinessException 
	 * @throws Exception
	 */

	public boolean firstTimeUserLogin(ParentTO parentTO) throws BusinessException {
		Boolean status = false;
		try {
			Long userID = getJdbcTemplatePrism()
					.queryForLong(IQueryConstants.CHECK_EXISTING_USER,
							parentTO.getUserName());
			
			Boolean ldapStatus = ldapManager.updateUser(
					parentTO.getUserName(), parentTO.getUserName(), parentTO.getUserName(),
					parentTO.getPassword());
			if (ldapStatus) {
				int count = getJdbcTemplatePrism().update(
						IQueryConstants.UPDATE_FIRSTTIMEUSERLOGIN_DATA,
						parentTO.getLastName(), parentTO.getFirstName(),
						parentTO.getMail(), parentTO.getMobile(),
						parentTO.getCountry(), parentTO.getZipCode(),parentTO.getState(),parentTO.getStreet(), parentTO.getCity(),userID);
				
				logger.log(IAppLogger.DEBUG, "INSERT_USER_DATA Done");
				if (count > 0) {
					boolean isSavedPasswordHistAnswer = savePasswordHistAnswer(
							userID, parentTO.getQuestionToList());
					if (isSavedPasswordHistAnswer) {
						int count1 = getJdbcTemplatePrism().update(
								IQueryConstants.UPDATE_FIRSTTIMEUSERFLAG_DATA,
								IApplicationConstants.FLAG_N,parentTO.getUserName());
						// logger.log(IAppLogger.DEBUG, "INSERT_USER_DATA Done");
						if (count1 > 0) {
							status = true;
						}
					}
				}
			}
		} catch (BusinessException bex) {
			throw new BusinessException(bex.getCustomExceptionMessage());
		} catch (Exception e) {
			logger.log(IAppLogger.ERROR, 
					"Error occurred while updating frist time user details.", e);
			return false;
		}
		return status;
	}
	
	
	/**
	 * Get parent account details details for manage
	 * @param username
	 * @return ParentTO
	 */
	//Added by Ravi for Manage Profile
	public ParentTO manageParentAccountDetails(String username) {

		ParentTO parentTO = new ParentTO();
		List<Map<String, Object>> lstData = null;
		try {
		//populate parent account details
		lstData = getJdbcTemplatePrism().queryForList(
				IQueryConstants.GET_PARENT_DETAILS_BY_USERNAME, username);
		
		if (lstData.size() > 0) {
			for (Map<String, Object> fieldDetails : lstData) {
				parentTO.setUserId(((BigDecimal) fieldDetails.get("USER_ID")).longValue());
				parentTO.setUserName((String) (fieldDetails.get("USERNAME")));
				parentTO.setDisplayName((String) (fieldDetails.get("DISPLAY_USERNAME")));
				parentTO.setLastName((String) (fieldDetails.get("LAST_NAME")));
				parentTO.setFirstName((String) (fieldDetails.get("FIRST_NAME")));
				//parentTO.setMiddleName((String) (fieldDetails.get("MIDDLE_NAME")));
				parentTO.setMail((String) (fieldDetails.get("EMAIL_ADDRESS")));
				parentTO.setMobile((String) (fieldDetails.get("PHONE_NO")));
				parentTO.setCountry((String) (fieldDetails.get("COUNTRY")));
				parentTO.setZipCode((String) (fieldDetails.get("ZIPCODE")));
				parentTO.setCity((String) (fieldDetails.get("CITY")));
				parentTO.setState((String) (fieldDetails.get("STATE")));
				parentTO.setStreet((String) (fieldDetails.get("STREET")));
			}
		}
		//setting secret question list by calling method getParentSecretQuestionDetails()
		parentTO.setQuestionToList(getParentSecretQuestionDetails(username));
		} catch (Exception e) {
			logger.log(IAppLogger.ERROR, "Error occurred while retrieving parent details for manage.", e);
		}
		return parentTO;
	}
	
	/**
	 * Get parent secret questions details for manage
	 * @param username
	 * @return QuestionTOs
	 */
	//Added by Ravi for Manage Profile
	public ArrayList<QuestionTO> getParentSecretQuestionDetails(String username) {
		
		List<Map<String, Object>> lstData = null;
		ArrayList<QuestionTO> questionTOs = new ArrayList<QuestionTO>();
		
		// populate parent security question and answer detail
		lstData = getJdbcTemplatePrism().queryForList(
				IQueryConstants.GET_PARENT_SECURITY_QUESTION, username);
		
		if (lstData.size() > 0) {

			for (Map<String, Object> fieldDetails : lstData) {
				QuestionTO questionTo = new QuestionTO();
				questionTo.setSno(((BigDecimal) fieldDetails.get("SNO")).longValue());
				questionTo.setQuestionId(((BigDecimal) fieldDetails.get("QUESTION_ID")).longValue());
				questionTo.setQuestion((String)fieldDetails.get("QUESTION"));
				questionTo.setAnswerId(((BigDecimal) fieldDetails.get("ANSWER_ID")).longValue());
				questionTo.setAnswer((String)fieldDetails.get("ANSWER"));
				
				questionTOs.add(questionTo);
			}
		}
		return questionTOs;
	}
	
	
	
	/**
	 * Get parent secret questions details for Forget password
	 * @param username
	 * @return QuestionTOs
	 */
	
	public ArrayList<QuestionTO> getSecurityQuestionForUser(String username) {
		
		List<Map<String, Object>> lstData = null;
		ArrayList<QuestionTO> questionTOs = new ArrayList<QuestionTO>();
		
		// populate parent security question and answer detail
		lstData = getJdbcTemplatePrism().queryForList(
				IQueryConstants.GET_PARENT_SECURITY_QUESTION, username);
		
		if (lstData.size() > 0) {

			for (Map<String, Object> fieldDetails : lstData) {
				QuestionTO questionTo = new QuestionTO();
				questionTo.setSno(((BigDecimal) fieldDetails.get("SNO")).longValue());
				questionTo.setQuestionId(((BigDecimal) fieldDetails.get("QUESTION_ID")).longValue());
				questionTo.setQuestion((String)fieldDetails.get("QUESTION"));
				//questionTo.setAnswerId(((BigDecimal) fieldDetails.get("ANSWER_ID")).longValue());
				//questionTo.setAnswer((String)fieldDetails.get("ANSWER"));
				
				questionTOs.add(questionTo);
			}
		}
		return questionTOs;
	}
	
	/**
	 * This function validate the user secret answers
	 * @param username,ans1, ans2,ans3,questionId1,questionId2,questionId3
	 * @return boolean flag
	 */
	public boolean validateAnswers(String userName,String ans1, String ans2,String ans3,String questionId1,String questionId2,String questionId3){
		long validUser=0;
		try {
			validUser= getJdbcTemplatePrism().queryForLong(IQueryConstants.VALIDATE_SECURITY_ANSWERS, userName,questionId1,ans1,userName,questionId2,ans2,questionId3,ans3);
		if (validUser != 0 && validUser != -1 && validUser==1){
			return true;
		}
		} catch (Exception e) {
			logger.log(IAppLogger.ERROR, "Error occurred while validating answers.", e);
			return false;
		}
		return false;
	}
	
	/**
	 * This function retrieves the user names for a particular email id
	 * @param emailId
	 * @return UsersList
	 */
	
	public List<UserTO> getUserNamesByEmail(String emailId)
	{	
		ArrayList<UserTO> UserTOs = new ArrayList<UserTO>();
		List<Map<String, Object>> userslist = null;
		userslist = getJdbcTemplatePrism().queryForList(
				IQueryConstants.GET_ALL_USERS_BY_EMAIL, emailId);
		if (userslist.size() > 0) {
					
			for (Map<String, Object> fieldDetails : userslist) {
				UserTO to = new UserTO();
				to.setUserName((String) (fieldDetails.get("USERNAME")));
				to.setFirstName((String) (fieldDetails.get("FIRST_NAME")));
				to.setLastName((String) (fieldDetails.get("LAST_NAME")));
				UserTOs.add(to);
				
			}
			
		}
		
		return UserTOs;
	}
	
	
	/**
	 * Update user profile information on save
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	//Added by Ravi for Manage Profile
	@TriggersRemove(cacheName="orgUsers", removeAll=true)
	public boolean updateUserProfile(ParentTO parentTO) throws BusinessException {

		long user_id = parentTO.getUserId();
		String password = parentTO.getPassword();
		try{
		boolean ldapFlag = true;
		// calling ldapManager for updating password for a username in LDAP
		if (password != null && !"".equals(password)) {
			ldapFlag = 	ldapManager.updateUser(parentTO.getUserName(), parentTO.getUserName(), parentTO.getUserName(), password);		
		}
		if(ldapFlag){
			// updating user details in users table
			int count = getJdbcTemplatePrism().update(
					IQueryConstants.UPDATE_USER_DATA, parentTO.getLastName(), parentTO.getFirstName(), parentTO.getMail(), 
					parentTO.getMobile(), parentTO.getCountry(), parentTO.getZipCode(), parentTO.getState(), 
					parentTO.getStreet(),parentTO.getCity(), parentTO.getDisplayName(), user_id);
			
			if (count > 0) {
				// delete security answers for that user and then insert as fresh
				//boolean isDeleted= deletePasswordHistAnswer(user_id);
				//if(isDeleted){
					savePasswordHistAnswer(user_id, parentTO.getQuestionToList());	
				//}
			}
		}
		} catch (BusinessException bex) {
			throw new BusinessException(bex.getCustomExceptionMessage());
		} catch (Exception e) {
			logger.log(IAppLogger.ERROR, "Error occurred while updating user profile details.", e);
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
	
	/**
	 * Delete Password hint answers for a userid
	 * 
	 * @return boolean
	 */
	//Added by Ravi for Manage Profile
	public boolean deletePasswordHistAnswer(long userid) {
		int count = getJdbcTemplatePrism().update(IQueryConstants.DELETE_ANSWER_DATA, userid);
		return Boolean.TRUE;
	}
	
	/**
	 * check invitation code claim to existing parent account
	 * 
	 * @return boolean
	 */
	public boolean checkInvitationCodeClaim(String userName, String invitationCode) {
		List<Map<String, Object>> lstData = getJdbcTemplatePrism().queryForList(
				IQueryConstants.CHECK_INVITATION_CODE_CLAIM, userName,
				invitationCode);
		if (lstData == null || lstData.isEmpty()) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
	/**
	 * Add invitation code to existing parent account
	 * 
	 * @return boolean
	 */
	public boolean addInvitationToAccount(String userName, String invitationCode) {
		int count = getJdbcTemplatePrism().update(
				IQueryConstants.ADD_INVITATION_CODE_TO_ACCOUNT, userName,
				invitationCode);
		if (count > 0) {
			boolean isUpdatedInvitationCodeClaimCount=updateInvitationCodeClaimCount(invitationCode);
			if (isUpdatedInvitationCodeClaimCount) {
				return updateOrgIdForParent(userName, invitationCode);
			}
		}
		return Boolean.FALSE;
	}
	
	/**
	 * Update Invitation code claim count
	 * 
	 * @return boolean
	 */
	public boolean updateOrgIdForParent(String userName, String invitationCode) {
	
		int count = getJdbcTemplatePrism().update(
				IQueryConstants.UPDATE_PARENT_USER_ORG, invitationCode, userName);
		
		logger.log(IAppLogger.DEBUG, "UPDATE ORG ID FOR PARENT Done");
		if (count > 0) {
			return Boolean.TRUE;
		}
	
		
		return Boolean.FALSE;
	}
	
	/**
	 * Retrieves and returns school id (leve3_jasper_orgid) for given student bio id
	 * @param studentBioId
	 * @return leve3_jasper_orgid
	 */
	public String getSchoolOrgId( String studentBioId ) {
		return getJdbcTemplatePrism().queryForObject(IQueryConstants.FETCH_SCHOOLID_FOR_STUDENT, new Object[]{ studentBioId }, new RowMapper<String>() {
			public String mapRow(ResultSet rs, int col) throws SQLException {
				return ((BigDecimal) rs.getObject(1)).toString();
			}
		});
	}
	
	/**
	 * Insert new Invitation code
	 * 
	 * @return boolean
	 */
	public boolean generateActivationCode(StudentTO student) {
		int count = getJdbcTemplatePrism().update(
				IQueryConstants.ADD_NEW_INVITATION_CODE, student.getInvitationcode(), student.getStudentBioId());
		if (count > 0) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	/**
	 * disable Invitation code
	 * 
	 * @return boolean
	 */
	public boolean disableActivationCode(StudentTO student) {
		int count = getJdbcTemplatePrism().update(
				IQueryConstants.UPDATE_ACTIVATION_CODE, student.getInvitationcode(), student.getStudentBioId());
		if (count > 0) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
}
