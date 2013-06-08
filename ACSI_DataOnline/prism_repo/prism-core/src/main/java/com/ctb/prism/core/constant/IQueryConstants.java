package com.ctb.prism.core.constant;

import com.ctb.prism.core.util.CustomStringUtil;

/**
 * This interface contains the constants used in the application
 * 
 * @author TATA CONSULTANCY SERVICES
 * @version 1.0
 */
public interface IQueryConstants {
	/** test */
	public static final String GET_USER_BY_EMAIL = "SELECT * FROM DPP_USR_USER USR" +
													" WHERE USR.USER_EMAIL = ?";
	
	// query to retrieve JRXML from database
	public static final String GET_JASPER_REPORT_OBJECT = CustomStringUtil.appendString(
			"SELECT FR.FILE_TYPE, FR.DATA DATA, re.label LABEL FROM JIRESOURCEFOLDER RF, JIRESOURCE RE, JIFILERESOURCE FR" ,
			" WHERE RF.URI LIKE ? AND RF.ID = RE.PARENT_FOLDER AND RE.ID = FR.ID and (FR.FILE_TYPE = 'jrxml' OR FR.FILE_TYPE = 'img') "); //  and (ROWNUM  <=1 or re.label = 'Main jrxml')
	
	// query to retrieve JRXML from database for given file name
	public static final String GET_JASPER_REPORT_OBJECT_FOR_NAME = CustomStringUtil.appendString(
			"SELECT FR.DATA DATA, re.label LABEL FROM JIRESOURCEFOLDER RF, JIRESOURCE RE, JIFILERESOURCE FR" ,
			" WHERE re.label = ? AND RF.ID = RE.PARENT_FOLDER AND RE.ID = FR.ID and FR.FILE_TYPE = 'jrxml' and ROWNUM  <=1 ");
	
	
	// query to retrieve input control details of a report
	public static final String GET_INPUT_CONTROL_DETAILS = CustomStringUtil.appendString(
			"SELECT RE.LABEL LBL, RUIC.INPUT_CONTROL_ID CONTROLID, " ,
			"RE.NAME LABELID, RUIC.CONTROL_INDEX SEQ, IC.TYPE TYPE, IC.DATA_TYPE DATATYPE, IC.READONLY READ_ONLY, IC.VISIBLE VISIBLE, " ,
			"IC.MANDATORY MANDATORY, IC.QUERY_VALUE_COLUMN QUERY_VALUE_COLUMN, (SELECT DATASOURCE FROM JIQUERY WHERE ID=IC.LIST_QUERY) DATASOURCE, " ,
			"(SELECT SQL_QUERY FROM JIQUERY WHERE ID=IC.LIST_QUERY) SQL_QUERY, F_GET_LIST_OF_VALUES(IC.LIST_OF_VALUES) LIST_OF_VALUES " ,
			"FROM JIREPORTUNITINPUTCONTROL RUIC, JIRESOURCE RE, JIINPUTCONTROL IC " ,
			"WHERE REPORT_UNIT_ID = (SELECT RU.ID FROM JIREPORTUNIT RU, JIRESOURCE R, JIRESOURCEFOLDER RF " ,
         	"WHERE RU.MAINREPORT = R.ID AND R.PARENT_FOLDER = RF.ID AND RF.URI = ? ) " ,
         	"AND RE.ID = RUIC.INPUT_CONTROL_ID AND RUIC.INPUT_CONTROL_ID = IC.ID ORDER BY SEQ");
	
	// query to retrieve tenant id for a particular username
	public static final String GET_TENANT_ID = "SELECT org_id FROM users WHERE upper(USERNAME) = upper(?) ";
	
	// query for getting logged-in user details
	public static final String GET_USER_DETAILS = CustomStringUtil.appendString(
			" SELECT users.IS_FIRSTTIME_LOGIN, users.user_id, users.org_id, users.display_username, users.activation_status ",
			" FROM users users ",
			" WHERE upper(users.username) = upper(?) ");
	
	// main query for manage reports screen. It returns all report details
	public static final String GET_ALL_REPORT_LIST = CustomStringUtil.appendString(
			"SELECT RE.DB_REPORT_ID ID, RE.REPORT_NAME, RE.REPORT_FOLDER_URI, RE.ACTIVATION_STATUS STATUS, " ,
			"SF_GET_LIST_OF_ROLES(RE.DB_REPORT_ID) ROLES, ASS.ASSESSMENT_NAME ASSESSMENT_NAME FROM " ,
			"DASHBOARD_REPORTS RE, assessment_dim ASS WHERE RE.ASSESSMENTID = ASS.ASSESSMENTID",
			" ORDER BY RE.REPORT_NAME");
	// retrieve a report based on the report id
	public static final String GET_DASHBOARD_DETAILS = CustomStringUtil.appendString("SELECT RE.DB_REPORT_ID ID,",
																				       "RE.REPORT_NAME,",
																				       "RE.REPORT_FOLDER_URI,",
																				       "RE.ACTIVATION_STATUS AS STATUS,",
																				       "SF_GET_LIST_OF_ROLES(RE.DB_REPORT_ID) ROLES,",
																				       "ASS.ASSESSMENT_NAME AS ASSESSMENT_NAME",
																				       " FROM DASHBOARD_REPORTS RE, ASSESSMENT_DIM ASS",
																				       " WHERE RE.ASSESSMENTID = ASS.ASSESSMENTID",
																				       " AND RE.DB_REPORT_ID=?");
	
	// query to update report table
	public static final String UPDATE_REPORT = CustomStringUtil.appendString(
			"UPDATE DASHBOARD_REPORTS SET REPORT_NAME = ?, REPORT_FOLDER_URI  = ?, " ,
			"ACTIVATION_STATUS = ? WHERE DB_REPORT_ID = ?");
	
	// query to delete from report and roles association table report_roles
	public static final String DELETE_REPORT_ROLE = "DELETE FROM DASHBOARD_REPORT_ACCESS WHERE DB_REPORT_ID = ?";
	
	// query to delete the report from the  reports table
	public static final String DELETE_REPORT = "DELETE  FROM DASHBOARD_REPORTS WHERE DB_REPORT_ID = ?";
	
	// query to insert report role relation in report_role table
	public static final String INSERT_REPORT_ROLE =  CustomStringUtil.appendString(
			" INSERT INTO DASHBOARD_REPORT_ACCESS ",
			" (DB_REPORT_ID, ",
			" ROLE_ID, ",
			" CREATED_DATE_TIME, ",
			" UPDATED_DATE_TIME) ",
			" VALUES ",
			" (? ,(SELECT ROLE_ID FROM ROLE WHERE ROLE_NAME = ? ), SYSDATE, SYSDATE) " );
	// query to add report
	public static final String INSERT_REPORT =CustomStringUtil.appendString(
			"INSERT INTO DASHBOARD_REPORTS",
			 "(db_report_id, report_name, report_desc, report_type, report_folder_uri, assessmentid, activation_status, created_date_time, updated_date_time, report_sequence)",
			"VALUES",
			"(?, ?, ?, ?, ?, ?, ?, SYSDATE, SYSDATE,REPORT_SEQUENCE_SEQ.NEXTVAL)" );
	
	// query to retrieve assessment details( including report details )
	public static final String GET_ALL_ASSESSMENT_LIST = CustomStringUtil.appendString(
			"SELECT ASS.ASSESSMENTID ASSESSMENT_ID, ASS.ASSESSMENT_NAME ASSESSMENT_NAME, " ,
			"RE.REPORT_SEQUENCE,RE.DB_REPORT_ID REPORT_ID, REPORT_NAME, REPORT_FOLDER_URI, RE.ACTIVATION_STATUS STATUS, SF_GET_LIST_OF_ROLES(RE.DB_REPORT_ID) ROLES " ,
			"FROM DASHBOARD_REPORTS RE, ASSESSMENT_DIM ASS WHERE ASS.ASSESSMENTID = RE.ASSESSMENTID AND RE.REPORT_TYPE=? AND RE.ACTIVATION_STATUS = '",
			IApplicationConstants.ACTIVE_FLAG,
			"' ORDER BY ASS.ASSESSMENTID,RE.REPORT_SEQUENCE, re.REPORT_NAME desc");
	
	// queru to fetch user roles for logged-in user
	public static final String SELECT_USER_AUTHORITIES = CustomStringUtil.appendString(
															"SELECT rle.role_name roleName, users.IS_FIRSTTIME_LOGIN, users.user_id, users.org_id, users.display_username, users.username, users.activation_status ",
															"FROM role rle, ",
															  "user_role urle, ",
															  "users users ",
															"WHERE rle.role_id       = urle.role_id ",
															"AND users.user_id = urle.user_id ",
															"AND upper(users.username) = upper(?) ");
	
	public static final String CHECK_LOGIN_TYPE = CustomStringUtil.appendString(
			"SELECT users.IS_FIRSTTIME_LOGIN ",
			"FROM users users ",
			"WHERE upper(users.username) = upper(?) ");
	
	public static final String GET_CURR_TENANT_DETAILS = CustomStringUtil
	.appendString(" SELECT ORG_ID as ID,",
			" ORG_NAME ,ORG_PARENT_ID as PARENTID,",
			" ORG_LEVEL FROM ORG_HIERARCHY WHERE ORG_ID = ? and (adminid = 0 or adminid = ?)",
			" ORDER BY ORG_NAME");

	public static final String GET_TENANT_DETAILS = CustomStringUtil
	.appendString(" SELECT ORG_ID as ID,",
			" ORG_NAME ,ORG_PARENT_ID as PARENTID,",
			" ORG_LEVEL FROM ORG_HIERARCHY WHERE ORG_PARENT_ID = ? and (adminid = 0 or adminid = ?)",
			" ORDER BY ORG_NAME");
	
	public static final String GET_TENANT_DETAILS_NON_ACSI = CustomStringUtil
	.appendString(" SELECT ORG_ID as ID,",
			" ORG_NAME ,ORG_PARENT_ID as PARENTID,",
			" ORG_LEVEL FROM ORG_HIERARCHY WHERE ORG_PARENT_ID = ?",
			" AND ORG_ID=? and (adminid = 0 or adminid = ?)",
			" ORDER BY ORG_NAME");
	public static final String GET_ORG_HIERARCHY_ON_REDIRECT = CustomStringUtil
	.appendString("SELECT TO_CHAR(O.ORG_ID) AS ORG_ID,O.ORG_NAME,O.ORG_PARENT_ID,O.ORG_LEVEL",
      			  " FROM ORG_HIERARCHY O",
                  " START WITH ORG_ID =?",
                  " CONNECT BY PRIOR ORG_PARENT_ID=ORG_ID",
                  " AND O.ORG_LEVEL >= (SELECT DISTINCT U.ORG_LEVEL FROM USERS U", 
                                        " WHERE U.ORG_ID=?",
                                        " AND U.USER_ID=?", 
                                        " AND  upper(U.USERNAME) = upper(?) )",
                  " ORDER BY  O.ORG_LEVEL ,O.ORG_PARENT_ID,O.ORG_NAME");
	
	

	public static final String GET_USER_DETAILS_ON_FIRST_LOAD = CustomStringUtil
	.appendString(" SELECT ABC.USERROWID, ABC.USER_ID, ABC.USERNAME,",
			" ABC.FULLNAME, ABC.STATUS, ABC.ORG_NAME, ABC.ORG_ID, ABC.ORG_PARENT_ID, ABC.USER_TYPE" ,
			" FROM (SELECT ROWIDTOCHAR(USR.ROWID) AS USERROWID , ",
			" USR.USER_ID, USR.USERNAME, ",
			" USR.LAST_NAME || ' ' || USR.FIRST_NAME AS FULLNAME, ",
			" USR.ACTIVATION_STATUS AS STATUS, HIER.ORG_NAME, ",
			" HIER.ORG_ID,  HIER.ORG_PARENT_ID,",
			" DECODE(HIER.ORG_LEVEL, '1', 'ACSI USER', '3', 'SCHOOL USER', '4', 'TEACHER USER') AS USER_TYPE",
			" FROM USERS USR, (SELECT *  FROM ORG_HIERARCHY where (adminid = 0 or adminid = ?) ",
			" START WITH ORG_ID = ?  ",
			" CONNECT BY PRIOR ORG_ID = ORG_PARENT_ID ",
			" ) HIER ",
			" WHERE HIER.ORG_ID = USR.ORG_ID AND USR.ORG_LEVEL <> 0 ",
			" and (usr.adminid = ? or USR.ORG_LEVEL in (1,3) ) ",
			" ORDER BY UPPER(USR.USERNAME)) ABC",
			" WHERE ROWNUM <= 15",
			" ORDER BY UPPER(ABC.USERNAME)");	
	
	public static final String GET_USER_DETAILS_ON_SCROLL=	CustomStringUtil
	.appendString(" SELECT ABC.USERROWID, ABC.USER_ID, ABC.USERNAME,",
			" ABC.FULLNAME, ABC.STATUS, ABC.ORG_NAME, ABC.ORG_ID, ABC.ORG_PARENT_ID, ABC.USER_TYPE" ,
			" FROM (SELECT ROWIDTOCHAR(USR.ROWID) AS USERROWID , ",
			" USR.USER_ID, USR.USERNAME, ",
			" USR.LAST_NAME || ' ' || USR.FIRST_NAME AS FULLNAME, ",
			" USR.ACTIVATION_STATUS AS STATUS, HIER.ORG_NAME, ",
			" HIER.ORG_ID,  HIER.ORG_PARENT_ID,",
			" DECODE(HIER.ORG_LEVEL, '1', 'ACSI USER', '3', 'SCHOOL USER', '4', 'TEACHER USER') AS USER_TYPE",
			" FROM USERS USR, (SELECT *  FROM ORG_HIERARCHY where (adminid = 0 or adminid = ?) ",
			" START WITH ORG_ID = ?  ",
			" CONNECT BY PRIOR ORG_ID = ORG_PARENT_ID ",
			" ) HIER ",
			" WHERE HIER.ORG_ID = USR.ORG_ID AND USR.ORG_LEVEL <> 0 ",
			" AND UPPER(USR.USERNAME) > UPPER(?)", 
			" and (usr.adminid = ? or USR.ORG_LEVEL in (1,3) ) ",
			" ORDER BY UPPER(USR.USERNAME)) ABC",
			" WHERE ROWNUM <= 15",
			" ORDER BY UPPER(ABC.USERNAME)");
	
	public static final String GET_USER_DETAILS_ON_SCROLL_WITH_SRCH_PARAM=	CustomStringUtil
	.appendString(" SELECT ABC.USERROWID, ABC.USER_ID, ABC.USERNAME,",
			" ABC.FULLNAME, ABC.STATUS, ABC.ORG_NAME, ABC.ORG_ID, ABC.ORG_PARENT_ID, ABC.USER_TYPE" ,
			" FROM (SELECT ROWIDTOCHAR(USR.ROWID) AS USERROWID , ",
			" USR.USER_ID, USR.USERNAME, ",
			" USR.LAST_NAME || ' ' || USR.FIRST_NAME AS FULLNAME, ",
			" USR.ACTIVATION_STATUS AS STATUS, HIER.ORG_NAME, ",
			" HIER.ORG_ID,  HIER.ORG_PARENT_ID,",
			" DECODE(HIER.ORG_LEVEL, '1', 'ACSI USER', '3', 'SCHOOL USER', '4', 'TEACHER USER') AS USER_TYPE",
			" FROM USERS USR, (SELECT *  FROM ORG_HIERARCHY where (adminid = 0 or adminid = ?) ",
			" START WITH ORG_ID = ?  ",
			" CONNECT BY PRIOR ORG_ID = ORG_PARENT_ID ",
			" ) HIER ",
			" WHERE HIER.ORG_ID = USR.ORG_ID AND USR.ORG_LEVEL <> 0 ",
			" AND UPPER(USR.USERNAME) > UPPER(?)", 
			" and (usr.adminid = ? or USR.ORG_LEVEL in (1,3) ) ",
			" AND (UPPER(USR.USERNAME) LIKE UPPER(?) ",
			"   OR UPPER(USR.LAST_NAME)  LIKE UPPER(?) ",
			"	OR UPPER(USR.FIRST_NAME) LIKE UPPER(?)) ",
			" ORDER BY UPPER(USR.USERNAME)) ABC",
			" WHERE ROWNUM <= 15",
			" ORDER BY UPPER(ABC.USERNAME)");
	
	public static final String GET_USER_ROLE=CustomStringUtil.appendString( "SELECT RLE.ROLE_ID,RLE.ROLE_NAME",
																		     " FROM USER_ROLE URLE,",
																		          " ROLE RLE,",
																		          " USERS USR",
																		     " WHERE USR.USER_ID=?",
																		       " AND URLE.USER_ID=USR.USER_ID", 
																		       " AND RLE.ROLE_ID=URLE.ROLE_ID",
																		       " AND RLE.ROLE_NAME NOT IN (?)");
	
	public static final String GET_ROLE_ACSI = " SELECT ROLE_ID, ROLE_NAME FROM ROLE WHERE ROLE_NAME IN ( 'ROLE_ACSI', 'ROLE_ADMIN') ";
	
	public static final String GET_ROLE_SCHOOL = " SELECT ROLE_ID, ROLE_NAME FROM ROLE WHERE ROLE_NAME IN ('ROLE_SCHOOL', 'ROLE_ADMIN') ";
	
	public static final String GET_ROLE_TEACHER = " SELECT ROLE_ID, ROLE_NAME FROM ROLE WHERE ROLE_NAME IN ( 'ROLE_CLASS') ";
		
	public static final String GET_USER_DETAILS_ON_EDIT = CustomStringUtil
	.appendString(" SELECT USR.USER_ID AS ID, ",
			" USR.DISPLAY_USERNAME AS USERNAME, ",
			" USR.USERNAME AS USERID,  ",
			" NVL(USR.EMAIL_ADDRESS,'') AS EMAIL, ",
			" USR.ACTIVATION_STATUS AS STATUS  ", " FROM USERS USR  ",
			" WHERE USR.USER_ID = ?");
	
	public static final String GET_USER_ROLE_ON_EDIT = CustomStringUtil
	.appendString(" SELECT RLE.ROLE_ID AS ID, ",
			" RLE.ROLE_NAME AS ROLENAME FROM ROLE RLE,  ",
			" USER_ROLE USRL WHERE USRL.USER_ID = ? ",
			" AND RLE.ROLE_ID = USRL.ROLE_ID ");
	
	public static final String GET_USER_ORG_LEVEL = " SELECT ORG_LEVEL FROM USERS WHERE USER_ID = ? ";
		
	public static final String UPDATE_USER = CustomStringUtil.appendString(
			" UPDATE USERS  SET USERNAME     = ?, ",
			" DISPLAY_USERNAME     = ?,  EMAIL_ADDRESS = ?, ",
			" ACTIVATION_STATUS      = ?  WHERE USER_ID = ?");
	
	public static final String DELETE_USER = "DELETE FROM USERS WHERE USER_ID = ? ";
	
	public static final String DELETE_USER_ROLE = CustomStringUtil
			.appendString(" DELETE FROM USER_ROLE WHERE USER_ID = ? ");
	
	public static final String INSERT_USER_ROLE = CustomStringUtil
			.appendString("INSERT INTO USER_ROLE (ROLE_ID, USER_ID)",
					" VALUES ",
					" ((SELECT ROLE_ID FROM ROLE WHERE ROLE_NAME = ?), ?)");
	public static final String INSERT_USER = CustomStringUtil.appendString(
			" INSERT INTO USERS (USER_ID, USERNAME, ORG_ID,",
			" DISPLAY_USERNAME, EMAIL_ADDRESS, ACTIVATION_STATUS,",
			" ADMINID, ORG_LEVEL, IS_FIRSTTIME_LOGIN ) VALUES ",
			" (?, ?, ?, ?, ?, ?, ",
			" ?, ?, ? )");
	
	public static final String USER_SEQ_ID = "SELECT USER_ID_SEQ.NEXTVAL AS PARENT_SEQ_ID FROM DUAL";
	public static final String DB_REPORT_SEQ_ID = "SELECT DB_REPORT_ID_SEQ.NEXTVAL AS REPORT_SEQ_ID FROM DUAL";
	
	public static final String CHECK_EXISTING_USER = "SELECT USER_ID FROM USERS WHERE upper(USERNAME) = upper(?)";
	public static final String CHECK_EXISTING_REPORT = "SELECT DB_REPORT_ID FROM DASHBOARD_REPORTS  WHERE REPORT_NAME=?";
	
	// search users
	public static final String SEARCH_USER = CustomStringUtil
			.appendString( " SELECT ROWIDTOCHAR(USR.ROWID) AS USERROWID , ",
						   " USR.USER_ID,  USR.USERNAME, ",
						   " USR.LAST_NAME || ' ' || USR.FIRST_NAME AS FULLNAME, ",
						   " USR.LAST_NAME,USR.FIRST_NAME,",
						   " USR.ACTIVATION_STATUS AS STATUS, HIER.ORG_NAME, ",
						   " DECODE(HIER.ORG_LEVEL, '1', 'ACSI USER', '3', 'SCHOOL USER', '4', 'TEACHER USER') AS USER_TYPE,",
						   " HIER.ORG_ID,  HIER.ORG_PARENT_ID",
						   " FROM USERS USR,  (SELECT * ",
						   " FROM ORG_HIERARCHY WHERE (adminid = 0 or adminid = ?) ",
						   " START WITH ORG_ID = ? ",
						   " CONNECT BY PRIOR ORG_ID = ORG_PARENT_ID ",
						   " ORDER BY ORG_LEVEL)HIER",
				           " WHERE (UPPER(USR.USERNAME) LIKE UPPER(?)",
						   " OR UPPER(USR.LAST_NAME)  LIKE UPPER(?)",
						   " OR UPPER(USR.FIRST_NAME) LIKE UPPER(?))",
				           " AND USR.ORG_LEVEL <> 0",
				           " AND (usr.adminid = ? or USR.ORG_LEVEL in (1,3) ) ",
				           " AND USR.ORG_ID = HIER.ORG_ID and ROWNUM <= ? ");
	
	public static final String SEARCH_USER_EXACT = CustomStringUtil
	.appendString( " SELECT ROWIDTOCHAR(USR.ROWID) AS USERROWID , ",
				   " USR.USER_ID,  USR.USERNAME, ",
				   " USR.LAST_NAME || ' ' || USR.FIRST_NAME AS FULLNAME, ",
				   " USR.LAST_NAME,USR.FIRST_NAME,",
				   " USR.ACTIVATION_STATUS AS STATUS, HIER.ORG_NAME, ",
				   " DECODE(HIER.ORG_LEVEL, '1', 'ACSI USER', '3', 'SCHOOL USER', '4', 'TEACHER USER') AS USER_TYPE,",
				   " HIER.ORG_ID,  HIER.ORG_PARENT_ID",
				   " FROM USERS USR,  (SELECT * ",
				   " FROM ORG_HIERARCHY WHERE (adminid = 0 or adminid = ?) ",
				   " START WITH ORG_ID = ? ",
				   " CONNECT BY PRIOR ORG_ID = ORG_PARENT_ID ",
				   " ORDER BY ORG_LEVEL)HIER",
		           " WHERE UPPER(USR.USERNAME) LIKE UPPER(?)",
				   " AND USR.ORG_LEVEL <> 0",
		           " AND (usr.adminid = ? or USR.ORG_LEVEL in (1,3) ) ",
		           " AND USR.ORG_ID = HIER.ORG_ID and ROWNUM <= ? ");
		
/*	// search user auto complete
	public static final String SEARCH_USER_AUTO_COMPLETE= CustomStringUtil
			.appendString("SELECT USR.USERNAME AS USERNAME FROM USERS USR",
							" WHERE UPPER(USR.USERNAME) LIKE  UPPER(?)",
							" AND (USR.ORG_ID=? OR USR.PARENT_ORG_ID=?)");	*/
	
	// get all organization list based on tenantId
	public static final String GET_ORGANIZATION_LIST = CustomStringUtil.appendString("SELECT ORG_ID,ORG_NAME,O.ORG_PARENT_ID,to_char(?) AS SELECTED_ORG_ID,O.ORG_LEVEL, ",
		       " (SELECT NVL(COUNT(1),0) FROM ORG_HIERARCHY WHERE ORG_PARENT_ID = ? and (adminid = 0 OR adminid = ?) ) CHILD_ORG_NO,",
		       " (SELECT NVL(SUM(COUNT(1)),0 ) FROM USERS U WHERE ORG_ID IN (SELECT ORG_ID",
		       " FROM (SELECT DISTINCT LEVEL1_JASPER_ORGID AS ORG_ID,LEVEL1_ELEMENT_NAME AS ORG_NAME,0 PARENT_ID",
               " FROM ORG_NODE_DIM L1 UNION",
               " SELECT DISTINCT LEVEL2_JASPER_ORGID AS ORG_ID,LEVEL2_ELEMENT_NAME AS ORG_NAME,LEVEL1_JASPER_ORGID PARENT_ID",
               " FROM ORG_NODE_DIM L2 UNION",
               " SELECT DISTINCT LEVEL3_JASPER_ORGID AS ORG_ID,LEVEL3_ELEMENT_NAME AS ORG_NAME,LEVEL2_JASPER_ORGID PARENT_ID",
               " FROM ORG_NODE_DIM L3 UNION",
               " SELECT DISTINCT LEVEL4_JASPER_ORGID AS ORG_ID,LEVEL4_ELEMENT_NAME AS ORG_NAME,LEVEL3_JASPER_ORGID PARENT_ID",
               " FROM ORG_NODE_DIM L4 where adminid = ? ) CONNECT BY NOCYCLE PRIOR ORG_ID = PARENT_ID START WITH ORG_ID = ?)",
               " and u.org_level NOT IN 0 GROUP BY ORG_ID) USER_NO",
		       " FROM ORG_HIERARCHY O",
		       " WHERE O.ORG_ID = ?");
	
	// retrieves the organization list children for the org tree based on the org parent id
	public static final String GET_ORG_CHILDREN_LIST = CustomStringUtil.appendString("SELECT ORG_ID,ORG_NAME,O.ORG_PARENT_ID,O.ORG_LEVEL,",
		       " (SELECT NVL(COUNT(1),0) FROM ORG_HIERARCHY WHERE ORG_PARENT_ID = O.ORG_ID AND (o.adminid = 0 OR o.adminid = ?) ) CHILD_ORG_NO,",
		       " (SELECT NVL(SUM(COUNT(1)),0 ) FROM USERS U WHERE ORG_ID IN (SELECT ORG_ID",
		       " FROM (SELECT DISTINCT LEVEL1_JASPER_ORGID AS ORG_ID,LEVEL1_ELEMENT_NAME AS ORG_NAME,0 PARENT_ID",
               " FROM ORG_NODE_DIM L1 UNION",
               " SELECT DISTINCT LEVEL2_JASPER_ORGID AS ORG_ID,LEVEL2_ELEMENT_NAME AS ORG_NAME,LEVEL1_JASPER_ORGID PARENT_ID",
               " FROM ORG_NODE_DIM L2 UNION",
               " SELECT DISTINCT LEVEL3_JASPER_ORGID AS ORG_ID,LEVEL3_ELEMENT_NAME AS ORG_NAME,LEVEL2_JASPER_ORGID PARENT_ID",
               " FROM ORG_NODE_DIM L3 UNION",
               " SELECT DISTINCT LEVEL4_JASPER_ORGID AS ORG_ID,LEVEL4_ELEMENT_NAME AS ORG_NAME,LEVEL3_JASPER_ORGID PARENT_ID",
               " FROM ORG_NODE_DIM L4 where adminid = ?) CONNECT BY NOCYCLE PRIOR ORG_ID = PARENT_ID START WITH ORG_ID = O.ORG_ID)",
               " and u.org_level NOT IN 0 GROUP BY ORG_ID) USER_NO",
		       " FROM ORG_HIERARCHY O",
		       " WHERE O.ORG_PARENT_ID = ?",
		       " AND (o.adminid = 0 OR o.adminid = ?) ",
		       " ORDER BY  ORG_NAME ");
	
	// get all organization list based on tenantId on first load
	/*public static final String GET_ORGANIZATION_CHILDREN_LIST = CustomStringUtil.appendString("SELECT ORG_ID,ORG_NAME,O.ORG_PARENT_ID,",
		       " (SELECT NVL(COUNT(1),0) FROM ORG_HIERARCHY WHERE ORG_PARENT_ID = O.ORG_ID) CHILD_ORG_NO,",
		       " (SELECT NVL(SUM(COUNT(1)),0 ) FROM USERS U WHERE ORG_ID IN (SELECT ORG_ID",
		       " FROM (SELECT DISTINCT LEVEL1_JASPER_ORGID AS ORG_ID,LEVEL1_ELEMENT_NAME AS ORG_NAME,0 PARENT_ID",
               " FROM ORG_NODE_DIM L1 UNION",
               " SELECT DISTINCT LEVEL2_JASPER_ORGID AS ORG_ID,LEVEL2_ELEMENT_NAME AS ORG_NAME,LEVEL1_JASPER_ORGID PARENT_ID",
               " FROM ORG_NODE_DIM L2 UNION",
               " SELECT DISTINCT LEVEL3_JASPER_ORGID AS ORG_ID,LEVEL3_ELEMENT_NAME AS ORG_NAME,LEVEL2_JASPER_ORGID PARENT_ID",
               " FROM ORG_NODE_DIM L3 UNION",
               " SELECT DISTINCT LEVEL4_JASPER_ORGID AS ORG_ID,LEVEL4_ELEMENT_NAME AS ORG_NAME,LEVEL3_JASPER_ORGID PARENT_ID",
               " FROM ORG_NODE_DIM L4) CONNECT BY NOCYCLE PRIOR ORG_ID = PARENT_ID START WITH ORG_ID = O.ORG_ID)",
               " and u.org_level NOT IN 0 GROUP BY ORG_ID) USER_NO", 
		       " FROM ORG_HIERARCHY O",
		       " WHERE O.ORG_PARENT_ID = ?",
		       " AND ROWNUM <= 15",
		       " ORDER BY ORG_NAME");*///===== this query is taking too much time
	
	public static final String GET_ORGANIZATION_CHILDREN_LIST = CustomStringUtil.appendString("SELECT a.* FROM", 
			" (SELECT O.ORG_ID,O.ORG_NAME,O.ORG_PARENT_ID,to_char(?) AS SELECTED_ORG_ID,",
			       " (SELECT NVL(COUNT(1),0) FROM ORG_HIERARCHY WHERE ORG_PARENT_ID = O.ORG_ID and (adminid = 0 or adminid = ?)) CHILD_ORG_NO",
			       " FROM ORG_HIERARCHY O",
			       " WHERE  O.ORG_ID IN  (SELECT ORG_ID",
	                  		       " FROM (SELECT DISTINCT LEVEL1_JASPER_ORGID AS ORG_ID,LEVEL1_ELEMENT_NAME AS ORG_NAME,0 PARENT_ID",
	                                "  FROM ORG_NODE_DIM L1 UNION",
	                                "  SELECT DISTINCT LEVEL2_JASPER_ORGID AS ORG_ID,LEVEL2_ELEMENT_NAME AS ORG_NAME,LEVEL1_JASPER_ORGID PARENT_ID",
	                                "  FROM ORG_NODE_DIM L2 UNION",
	                                "  SELECT DISTINCT LEVEL3_JASPER_ORGID AS ORG_ID,LEVEL3_ELEMENT_NAME AS ORG_NAME,LEVEL2_JASPER_ORGID PARENT_ID",
	                                "  FROM ORG_NODE_DIM L3 UNION",
	                                "  SELECT DISTINCT LEVEL4_JASPER_ORGID AS ORG_ID,LEVEL4_ELEMENT_NAME AS ORG_NAME,LEVEL3_JASPER_ORGID PARENT_ID",
	                                "  FROM ORG_NODE_DIM L4 WHERE ADMINID=?) CONNECT BY NOCYCLE PRIOR ORG_ID = PARENT_ID START WITH ORG_ID = ?)",
	             " AND  O.ORG_ID <>?",
                 " AND (o.adminid = 0 or o.adminid = ?) ",
	             "GROUP BY O.ORG_ID ,",
	                     " O.ORG_NAME ,",
	                     " O.ORG_PARENT_ID", 
	             " ORDER BY O.ORG_ID) a",
			     "  WHERE ROWNUM <= 15");
		       
    // get all organization list based on tenantId on scrolling
   	/*public static final String GET_ORGANIZATION_CHILDREN_LIST_ON_SCROLL = CustomStringUtil.appendString("SELECT ORG_ID,ORG_NAME,O.ORG_PARENT_ID,",
				" (SELECT NVL(COUNT(1),0) FROM ORG_HIERARCHY WHERE ORG_PARENT_ID = O.ORG_ID) CHILD_ORG_NO,",
				" (SELECT NVL(SUM(COUNT(1)),0 ) FROM USERS U WHERE ORG_ID IN (SELECT ORG_ID",
				" FROM (SELECT DISTINCT LEVEL1_JASPER_ORGID AS ORG_ID,LEVEL1_ELEMENT_NAME AS ORG_NAME,0 PARENT_ID",
				" FROM ORG_NODE_DIM L1 UNION",
				" SELECT DISTINCT LEVEL2_JASPER_ORGID AS ORG_ID,LEVEL2_ELEMENT_NAME AS ORG_NAME,LEVEL1_JASPER_ORGID PARENT_ID",
				" FROM ORG_NODE_DIM L2 UNION",
				" SELECT DISTINCT LEVEL3_JASPER_ORGID AS ORG_ID,LEVEL3_ELEMENT_NAME AS ORG_NAME,LEVEL2_JASPER_ORGID PARENT_ID",
				" FROM ORG_NODE_DIM L3 UNION",
				" SELECT DISTINCT LEVEL4_JASPER_ORGID AS ORG_ID,LEVEL4_ELEMENT_NAME AS ORG_NAME,LEVEL3_JASPER_ORGID PARENT_ID",
				" FROM ORG_NODE_DIM L4) CONNECT BY NOCYCLE PRIOR ORG_ID = PARENT_ID START WITH ORG_ID = O.ORG_ID)",
				" and u.org_level NOT IN 0 GROUP BY ORG_ID) USER_NO",
				" FROM ORG_HIERARCHY O",
				" WHERE O.ORG_PARENT_ID = ?",
				" AND O.ORG_ID > ?",
				" AND ROWNUM <= 15");*/ //commnetd for tunning
	
	 // get all organization list based on tenantId on scrolling(on "more" button click)
	public static final String GET_ORGANIZATION_CHILDREN_LIST_ON_SCROLL = CustomStringUtil.appendString("SELECT a.* FROM", 
			" (SELECT O.ORG_ID,O.ORG_NAME,O.ORG_PARENT_ID,to_char(?) AS SELECTED_ORG_ID,",
					" (SELECT NVL(COUNT(1),0) FROM ORG_HIERARCHY WHERE ORG_PARENT_ID = O.ORG_ID and (adminid = 0 or adminid = ?)) CHILD_ORG_NO",
					" FROM ORG_HIERARCHY O",
					" WHERE O.ORG_ID IN (SELECT ORG_ID",
	                    				" FROM (SELECT DISTINCT LEVEL1_JASPER_ORGID AS ORG_ID,LEVEL1_ELEMENT_NAME AS ORG_NAME,0 PARENT_ID",
	                    				" FROM ORG_NODE_DIM L1 UNION",
	                    				" SELECT DISTINCT LEVEL2_JASPER_ORGID AS ORG_ID,LEVEL2_ELEMENT_NAME AS ORG_NAME,LEVEL1_JASPER_ORGID PARENT_ID",
	                    				" FROM ORG_NODE_DIM L2 UNION",
	                    				" SELECT DISTINCT LEVEL3_JASPER_ORGID AS ORG_ID,LEVEL3_ELEMENT_NAME AS ORG_NAME,LEVEL2_JASPER_ORGID PARENT_ID",
	                    				" FROM ORG_NODE_DIM L3 UNION",
	                    				" SELECT DISTINCT LEVEL4_JASPER_ORGID AS ORG_ID,LEVEL4_ELEMENT_NAME AS ORG_NAME,LEVEL3_JASPER_ORGID PARENT_ID",
	                    				" FROM ORG_NODE_DIM L4 WHERE ADMINID=?) CONNECT BY NOCYCLE PRIOR ORG_ID = PARENT_ID START WITH ORG_ID = ?)",
			        " AND O.ORG_ID <>?", 
					" AND O.ORG_ID > ?",
					" and (o.adminid = 0 or o.adminid = ?) ",
					" GROUP BY O.ORG_ID ,",
			                     " O.ORG_NAME ,",
			                     " O.ORG_PARENT_ID", 
			             " ORDER BY O.ORG_ID ) a",
							" WHERE  ROWNUM <= 15"); 
	
	public static final String GET_ORGANIZATION_CHILDREN_LIST_ON_SCROLL_WITH_SRCH_PARAM = CustomStringUtil.appendString("SELECT a.* FROM", 
			" (SELECT O.ORG_ID,O.ORG_NAME,O.ORG_PARENT_ID,to_char(?) AS SELECTED_ORG_ID,",
					" (SELECT NVL(COUNT(1),0) FROM ORG_HIERARCHY WHERE ORG_PARENT_ID = O.ORG_ID and (adminid = 0 or adminid = ?)) CHILD_ORG_NO",
					" FROM ORG_HIERARCHY O",
					" WHERE O.ORG_ID IN (SELECT ORG_ID",
	                    				" FROM (SELECT DISTINCT LEVEL1_JASPER_ORGID AS ORG_ID,LEVEL1_ELEMENT_NAME AS ORG_NAME,0 PARENT_ID",
	                    				" FROM ORG_NODE_DIM L1 UNION",
	                    				" SELECT DISTINCT LEVEL2_JASPER_ORGID AS ORG_ID,LEVEL2_ELEMENT_NAME AS ORG_NAME,LEVEL1_JASPER_ORGID PARENT_ID",
	                    				" FROM ORG_NODE_DIM L2 UNION",
	                    				" SELECT DISTINCT LEVEL3_JASPER_ORGID AS ORG_ID,LEVEL3_ELEMENT_NAME AS ORG_NAME,LEVEL2_JASPER_ORGID PARENT_ID",
	                    				" FROM ORG_NODE_DIM L3 UNION",
	                    				" SELECT DISTINCT LEVEL4_JASPER_ORGID AS ORG_ID,LEVEL4_ELEMENT_NAME AS ORG_NAME,LEVEL3_JASPER_ORGID PARENT_ID",
	                    				" FROM ORG_NODE_DIM L4 WHERE ADMINID=?) CONNECT BY NOCYCLE PRIOR ORG_ID = PARENT_ID START WITH ORG_ID = ?)",
			        " AND O.ORG_ID <>?", 
					" AND O.ORG_ID > ?",
					" and (o.adminid = 0 or o.adminid = ?) ",
					" AND UPPER(O.ORG_NAME) LIKE UPPER(?)",
					" GROUP BY O.ORG_ID ,",
			                     " O.ORG_NAME ,",
			                     " O.ORG_PARENT_ID", 
			             " ORDER BY O.ORG_ID ) a",
							" WHERE  ROWNUM <= 15"); 
	
	public static final String GET_USER_COUNT = CustomStringUtil.appendString("SELECT COUNT.USER_NO, adm.admin_name FROM",
			" (SELECT NVL(SUM(COUNT(1)),0) USER_NO,1 AS MATCH FROM USERS U WHERE ORG_ID IN (SELECT ORG_ID",
			 " FROM (SELECT DISTINCT LEVEL1_JASPER_ORGID AS ORG_ID,LEVEL1_ELEMENT_NAME AS ORG_NAME,0 PARENT_ID",
					" FROM ORG_NODE_DIM L1 UNION",
					" SELECT DISTINCT LEVEL2_JASPER_ORGID AS ORG_ID,LEVEL2_ELEMENT_NAME AS ORG_NAME,LEVEL1_JASPER_ORGID PARENT_ID",
					" FROM ORG_NODE_DIM L2 UNION",
					" SELECT DISTINCT LEVEL3_JASPER_ORGID AS ORG_ID,LEVEL3_ELEMENT_NAME AS ORG_NAME,LEVEL2_JASPER_ORGID PARENT_ID",
					" FROM ORG_NODE_DIM L3 UNION",
					" SELECT DISTINCT LEVEL4_JASPER_ORGID AS ORG_ID,LEVEL4_ELEMENT_NAME AS ORG_NAME,LEVEL3_JASPER_ORGID PARENT_ID",
					" FROM ORG_NODE_DIM L4 where adminid = ?) CONNECT BY NOCYCLE PRIOR ORG_ID = PARENT_ID START WITH ORG_ID = ?)",
					" AND (u.adminid = ? or U.ORG_LEVEL in (1,3) ) and u.org_level NOT IN 0 ",
					" GROUP BY ORG_ID)COUNT,",
					" (SELECT 1 AS MATCH,adm.admin_name FROM  admin_dim adm WHERE adm.adminid =?) adm",
					"  WHERE COUNT.MATCH=adm.MATCH"); 
       
	// search organization
	/*public static final String SEARCH_ORGANNIZATION = CustomStringUtil.appendString("SELECT ORG_ID,",
								       " ORG_NAME,",
								       "(SELECT NVL(COUNT(1), 0)",
								          " FROM ORG_HIERARCHY",
								         " WHERE ORG_PARENT_ID = O.ORG_ID) CHILD_ORG_NO,",
								       " (SELECT NVL(SUM(COUNT(1)), 0)",
								          " FROM USERS U",
								        " WHERE ORG_ID IN (O.ORG_ID)",
								          " AND U.ORG_LEVEL NOT IN 0",
								        " GROUP BY ORG_ID) USER_NO",
								 " FROM ORG_HIERARCHY O",
								 " WHERE UPPER(O.ORG_NAME) LIKE UPPER(?)",
								 " AND (o.adminid = 0 or o.adminid = ?) ",
								 " AND O.ORG_LEVEL >=",
								     " (SELECT distinct ORG_LEVEL FROM ORG_HIERARCHY WHERE ORG_ID =?)",
								 " AND O.ORG_ID IN (SELECT ORG_ID",
								                           " FROM (SELECT DISTINCT LEVEL1_JASPER_ORGID AS ORG_ID,",
								                                                " LEVEL1_ELEMENT_NAME AS ORG_NAME,",
								                                                " 0                   PARENT_ID",
								                                   " FROM ORG_NODE_DIM L1",
								                                 " UNION",
								                                 " SELECT DISTINCT LEVEL2_JASPER_ORGID AS ORG_ID,",
								                                                 " LEVEL2_ELEMENT_NAME AS ORG_NAME,",
								                                                 " LEVEL1_JASPER_ORGID PARENT_ID",
								                                   " FROM ORG_NODE_DIM L2",
								                                 " UNION",
								                                 " SELECT DISTINCT LEVEL3_JASPER_ORGID AS ORG_ID,",
								                                                 " LEVEL3_ELEMENT_NAME AS ORG_NAME,",
								                                                 " LEVEL2_JASPER_ORGID PARENT_ID",
								                                   " FROM ORG_NODE_DIM L3",
								                                 " UNION",
								                                 " SELECT DISTINCT LEVEL4_JASPER_ORGID AS ORG_ID,",
								                                                 " LEVEL4_ELEMENT_NAME AS ORG_NAME,",
								                                                 " LEVEL3_JASPER_ORGID PARENT_ID",
								                                   " FROM ORG_NODE_DIM L4)",
								                         " CONNECT BY NOCYCLE PRIOR ORG_ID = PARENT_ID",
								                          " START WITH ORG_ID =?)" );*/
	public static final String SEARCH_ORGANNIZATION = CustomStringUtil.appendString("SELECT ORG_ID,",
								       " ORG_NAME,",
								       " (SELECT NVL(COUNT(1), 0)",
								         "  FROM ORG_HIERARCHY",
								         " WHERE ORG_PARENT_ID = O.ORG_ID",
                                         "  and (adminid = 0 or adminid = ?)) CHILD_ORG_NO",
								  " FROM ORG_HIERARCHY O",
								  " WHERE UPPER(O.ORG_NAME) LIKE UPPER(?)",
								  " AND (o.adminid = 0 or o.adminid = ?)", 
								  " AND O.ORG_LEVEL >=",
								     " (SELECT distinct ORG_LEVEL FROM ORG_HIERARCHY WHERE ORG_ID =?)",
								  " AND O.ORG_ID IN (SELECT ORG_ID",
	                  		       " FROM (SELECT DISTINCT LEVEL1_JASPER_ORGID AS ORG_ID,LEVEL1_ELEMENT_NAME AS ORG_NAME,0 PARENT_ID",
	                               " FROM ORG_NODE_DIM L1 UNION",
	                               " SELECT DISTINCT LEVEL2_JASPER_ORGID AS ORG_ID,LEVEL2_ELEMENT_NAME AS ORG_NAME,LEVEL1_JASPER_ORGID PARENT_ID",
	                               " FROM ORG_NODE_DIM L2 UNION",
	                               " SELECT DISTINCT LEVEL3_JASPER_ORGID AS ORG_ID,LEVEL3_ELEMENT_NAME AS ORG_NAME,LEVEL2_JASPER_ORGID PARENT_ID",
	                               " FROM ORG_NODE_DIM L3 UNION",
	                               " SELECT DISTINCT LEVEL4_JASPER_ORGID AS ORG_ID,LEVEL4_ELEMENT_NAME AS ORG_NAME,LEVEL3_JASPER_ORGID PARENT_ID",
	                               " FROM ORG_NODE_DIM L4 WHERE ADMINID=?) CONNECT BY NOCYCLE PRIOR ORG_ID = PARENT_ID START WITH ORG_ID = ?)");
	// search organization auto complete
	public static final String SEARCH_ORG_AUTO_COMPLETE = CustomStringUtil.appendString("SELECT ABC.ORG_NAME",
																				         " FROM ",
																				             " (SELECT O.ORG_NAME,O.ORG_LEVEL",
																				      		    " FROM ORG_HIERARCHY O",
																				      		    " WHERE (o.adminid = 0 or o.adminid = ?) ",
																				                " START WITH ORG_ID =?",
																				                " CONNECT BY PRIOR ORG_ID = ORG_PARENT_ID",
																				                " ORDER BY  O.ORG_NAME ) ABC",
																				          " WHERE UPPER(ABC.ORG_NAME) LIKE UPPER(?)",
																				          " AND ROWNUM <=100");
	/*public static final String SEARCH_ORG_AUTO_COMPLETE = CustomStringUtil.appendString("SELECT ORG_NAME",
				" FROM ORG_HIERARCHY o",
				" WHERE UPPER(O.ORG_NAME) LIKE UPPER( ? )",
				" AND O.ORG_LEVEL >= (SELECT ORG_LEVEL FROM ORG_HIERARCHY WHERE ORG_ID = ? )");*/
	
	
	// START---- Queries related to Role Management
	public static final String GET_ROLE_DETAILS = "SELECT ROLE_ID, ROLE_NAME, DESCRIPTION FROM ROLE ORDER BY ROLE_NAME";
	
	public static final String GET_ROLE_DETAILS_BY_ID = "SELECT ROLE_ID, ROLE_NAME, DESCRIPTION  FROM ROLE WHERE ROLE_ID = ?";
	
	public static final String GET_USERS_FOR_SELECTED_ROLE = "select ou.user_id, ou.username, ou.org_level from users ou, user_role ur where ou.user_id = ur.user_id and ur.role_id = ?";
	
	public static final String DELETE_ROLE_FROM_ROLES_TABLE ="DELETE FROM ROLE WHERE ROLE_ID = ?";
	
	public static final String DELETE_ROLE_FROM_USER_ROLE_TABLE ="DELETE FROM USER_ROLE WHERE ROLE_ID = ?";
	
	public static final String INSERT_INTO_USER_ROLE ="INSERT INTO USER_ROLE (USER_ID, ROLE_ID) VALUES((SELECT USER_ID FROM USERS WHERE upper(USERNAME) = upper(?) ), ?)";
	public static final String IS_ROLE_TAGGED= CustomStringUtil.appendString("SELECT URL.USER_ID FROM USER_ROLE URL",
																			 " WHERE URL.ROLE_ID=? ",
																			 " AND URL.USER_ID=(SELECT USER_ID FROM USERS WHERE UPPER(USERNAME)= UPPER(?) AND ROWNUM=1)");
	
	public static final String UPDATE_ROLE = "UPDATE ROLE SET ROLE_NAME = ?, DESCRIPTION  = ? WHERE ROLE_ID = ?";
	
	//dissociate user from role
	public static final String DELETE_USER_FROM_USER_ROLE_TABLE ="DELETE FROM USER_ROLE WHERE ROLE_ID = ? AND USER_ID = ?";
	
	//END ---- Queries related to Role Management
	
	public static final String VALIDATE_INVITATION_CODE = CustomStringUtil
	.appendString(
			" SELECT INV.INVITATION_CODE_ID," ,
			" INV.TOTAL_AVAILABLE_IC_CLAIM," , 
			" INV.TOTAL_ATTEMPT_IC_CLAIM," ,
			" DECODE(SIGN(INV.INVITATION_EXPIRATION_DATE - SYSDATE),-1,'IN','AC') AS EXPIRATION_STATUS," ,
			" INV.ACTIVATION_STATUS AS ACTIVATION_STATUS",
			" FROM INVITATION_CODE INV" ,
			" WHERE INV.INVITATION_CODE = ?" ,
			" AND INV.ACTIVATION_STATUS = 'AC'" ,
			" AND rownum = 1");
			

	public static final String GET_STUDENT_FOR_INVITATION_CODE = CustomStringUtil.appendString(
			" SELECT STD.FIRST_NAME || ' ' || STD.MIDDLE_NAME || ' ' || STD.LAST_NAME AS STUDENT_NAME," ,
			     " ADM.ADMIN_NAME AS ADMINISTRATION, " ,
			     " 'Grade ' || grd.grade_name AS GRADE " ,
			     " FROM INVITATION_CODE IC, STUDENT_BIO_DIM STD, ADMIN_DIM ADM, GRADE_DIM GRD " ,
			     " WHERE IC.STUDENT_STRUC_ELEMENT = STD.STRUCTURE_ELEMENT" ,
			     " AND STD.ADMINID = IC.ADMINID " ,
			     " AND ADM.ADMINID = STD.ADMINID " ,
			     " and ic.org_nodeid = std.org_nodeid ",
			     " AND GRD.GRADEID = STD.GRADEID AND IC.ACTIVATION_STATUS = 'AC' " ,
			     " AND IC.INVITATION_CODE = ? ");
	
	public static final String VALIDATE_USER_NAME = "SELECT USR.USERNAME AS USERNAME FROM USERS USR WHERE upper(USR.USERNAME) = upper(?) ";
	
	public static final String VALIDATE_ACTIVE_USER_NAME = "SELECT USR.USERNAME AS USERNAME FROM USERS USR WHERE upper(USR.USERNAME) = upper(?) AND USR.ACTIVATION_STATUS='AC'";
	
	public static final String GET_SECRET_QUESTION = CustomStringUtil.appendString(
			" SELECT PASSWORD_HINT_QUESTION_ID AS QUESTION_ID," ,
			" PASSWORD_HINT_QUESTION AS QUESTION, " ,
			" SNO AS SNO " ,
			" FROM PASSWORD_HINT_QUESTIONS" ,
			" WHERE ACTIVATION_STATUS = 'AC'");
	
	public static final String PARENT_USER_SEQ_ID = "SELECT USER_ID_SEQ.NEXTVAL AS PARENT_SEQ_ID FROM DUAL";
	
	//Query to insert newly registered user
	public static final String INSERT_USER_DATA = CustomStringUtil.appendString(
			" INSERT INTO USERS  " ,
			" (USER_ID,   USERNAME,   DISPLAY_USERNAME, " ,
			"  LAST_NAME,   FIRST_NAME,   MIDDLE_NAME, " ,
			"  EMAIL_ADDRESS,   PHONE_NO,   COUNTRY, " ,
			"  ZIPCODE,  STREET, CITY, STATE, ORG_ID, " ,
			"  ORG_LEVEL,   IS_FIRSTTIME_LOGIN,  ADMINID, " ,
			"  ACTIVATION_STATUS,   CREATED_DATE_TIME)" ,
			"  VALUES  (?, ?, ?, ?, ?, '', ?, ?, ?, ?, ?, ?,?," ,
			"  (select DISTINCT level3_jasper_orgid from org_node_dim org, invitation_code inv where inv.org_nodeid = org.org_nodeid and INV.ACTIVATION_STATUS = 'AC' AND inv.invitation_code = ?), " ,
			"  0, ?,  (SELECT adminid FROM ADMIN_DIM ADM WHERE ADM.CURRENT_ADMIN = 'Y'), " ,
			"  'AC',   SYSDATE)");
	
	//Query to assign user role to the new registered user
	public static final String ADD_ROLE_TO_REGISTERED_USER = CustomStringUtil.appendString(
			"INSERT INTO USER_ROLE VALUES ((SELECT USER_ID FROM USERS WHERE upper(USERNAME) LIKE upper(?)),(SELECT ROLE_ID FROM ROLE WHERE ROLE_NAME LIKE ?),SYSDATE,SYSDATE)");
		
	
	public static final String INSERT_INVITATION_CODE_CLAIM_DATA =  CustomStringUtil.appendString(
			" INSERT INTO INVITATION_CODE_CLAIM  " ,
			" (INVITATION_CODE_CLAIM_ID,   USER_ID,   INVITATION_CODE_ID, " ,
			" ACTIVATION_STATUS,   CREATED_DATE_TIME)" ,
			" VALUES  " ,
			" (INVITATION_CODE_CLAIM_ID_SEQ.NEXTVAL,   ?,   (SELECT INVITATION_CODE_ID FROM INVITATION_CODE INV where INV.ACTIVATION_STATUS = 'AC' and INV.INVITATION_CODE = ? and rownum = 1), " ,
			" 'AC',  SYSDATE)");
	
	public static final String UPDATE_INVITATION_CODE_CLAIM_COUNT =CustomStringUtil.appendString(
			" UPDATE INVITATION_CODE " ,
			" SET TOTAL_ATTEMPT_IC_CLAIM = (SELECT TOTAL_ATTEMPT_IC_CLAIM " ,
			" FROM INVITATION_CODE " ,
			" WHERE INVITATION_CODE = ? AND ACTIVATION_STATUS = 'AC')+1 " ,
			" WHERE INVITATION_CODE = ?	");
	public static final String UPDATE_AVAILABLE_INVITATION_CODE_CLAIM_COUNT =CustomStringUtil.appendString(
			" UPDATE INVITATION_CODE " ,
			" SET TOTAL_AVAILABLE_IC_CLAIM = (SELECT TOTAL_AVAILABLE_IC_CLAIM " ,
			" FROM INVITATION_CODE " ,
			" WHERE INVITATION_CODE = ? AND ACTIVATION_STATUS = 'AC')-1 " ,
			" WHERE INVITATION_CODE = ?	");
	public static final String CHECK_AVAILABLE_INVITATION_CODE_CLAIM_COUNT =CustomStringUtil.appendString(
			//" SELECT (IC.TOTAL_AVAILABLE_IC_CLAIM - TOTAL_ATTEMPT_IC_CLAIM) AS CLAIM_AVAILABILITY", //Fixed 74114
			" SELECT IC.TOTAL_AVAILABLE_IC_CLAIM  AS CLAIM_AVAILABILITY",
			" FROM INVITATION_CODE IC", 
			" WHERE UPPER(IC.INVITATION_CODE) = UPPER(?) AND IC.ACTIVATION_STATUS = 'AC' ");
	
	public static final String CHECK_INVITATION_CODE_CLAIM =CustomStringUtil.appendString(
			" SELECT INVITATION_CODE_CLAIM_ID" ,
			" FROM INVITATION_CODE_CLAIM ICC, INVITATION_CODE IC, USERS USR" ,
			" WHERE ICC.USER_ID = USR.USER_ID AND upper(USR.USERNAME) = upper(?) AND" ,
			" IC.INVITATION_CODE_ID = ICC.INVITATION_CODE_ID AND" ,
			" IC.INVITATION_CODE = ? AND IC.ACTIVATION_STATUS = 'AC' ");
	    		   
	public static final String ADD_INVITATION_CODE_TO_ACCOUNT =  CustomStringUtil.appendString(
			" INSERT INTO INVITATION_CODE_CLAIM  " ,
			" (INVITATION_CODE_CLAIM_ID,   USER_ID,   INVITATION_CODE_ID, " ,
			" ACTIVATION_STATUS,   CREATED_DATE_TIME)" ,
			" VALUES  " ,
			" (INVITATION_CODE_CLAIM_ID_SEQ.NEXTVAL,   (SELECT USER_ID FROM USERS WHERE upper(USERNAME) = upper(?) ),   (SELECT INVITATION_CODE_ID FROM INVITATION_CODE IC WHERE IC.INVITATION_CODE = ? AND IC.ACTIVATION_STATUS = 'AC'), " ,
			" 'AC',  SYSDATE)");
	
	public static final String INSERT_ANSWER_DATA = CustomStringUtil.appendString(
			" INSERT INTO PASSWORD_HINT_ANSWERS  " ,
			" (PASSWORD_HINT_ANSWER_ID,   USER_ID,   " ,
			" PASSWORD_HINT_QUESTION_ID,   PASSWORD_HINT_ANSWER,   " ,
			" CREATED_DATE_TIME) VALUES  " ,
			" (PASSWORD_HINT_ANSWER_SEQ.NEXTVAL,   ?,  " ,
			" ?,   ?,  " ,
			" SYSDATE)");
	
	public static final String GET_PARENT_DETAILS_ON_FIRST_LOAD = CustomStringUtil
	.appendString(  " SELECT ABC.USER_ID, ABC.USERNAME,",
					" ABC.FULLNAME, ABC.STATUS, " ,
					" TO_CHAR(ABC.LAST_LOGIN_ATTEMPT,'MM/DD/YY') AS LAST_LOGIN_ATTEMPT," ,
					" ABC.ORG_NAME, ABC.ORG_ID " ,
					" FROM (SELECT USR.USER_ID, USR.USERNAME, ",
					" USR.LAST_NAME || ' ' || USR.FIRST_NAME AS FULLNAME, ",
					" USR.ACTIVATION_STATUS AS STATUS, USR.LAST_LOGIN_ATTEMPT, ",
					" HIER.ORG_NAME, HIER.ORG_ID ",
					" FROM USERS USR, (SELECT *  FROM ORG_HIERARCHY ",
					" START WITH ORG_ID = ?  ",
					" CONNECT BY PRIOR ORG_ID = ORG_PARENT_ID ",
					" ) HIER ",
					" WHERE HIER.ORG_ID = USR.ORG_ID AND USR.ORG_LEVEL = 0 ",
					" ORDER BY UPPER(USR.USERNAME)) ABC",
					" WHERE ROWNUM <= 15",
					" ORDER BY UPPER(ABC.USERNAME)");
	
	//Added by Ravi for Manage Profile
	public static final String GET_PARENT_DETAILS_BY_USERNAME = CustomStringUtil
	.appendString(  " SELECT USER_ID, USERNAME, DISPLAY_USERNAME, LAST_NAME, FIRST_NAME, MIDDLE_NAME,",
					" EMAIL_ADDRESS, PHONE_NO, COUNTRY, ZIPCODE, STATE, STREET,CITY FROM USERS USR WHERE upper(USR.USERNAME)=upper(?)");
	
	//Added by Ravi for Manage Profile
	public static final String GET_PARENT_SECURITY_QUESTION = CustomStringUtil
	.appendString( " SELECT Q.PASSWORD_HINT_QUESTION_ID AS QUESTION_ID, Q.PASSWORD_HINT_QUESTION AS QUESTION, Q.SNO AS SNO,", 
	" A.PASSWORD_HINT_ANSWER_ID AS ANSWER_ID, A.PASSWORD_HINT_ANSWER AS ANSWER FROM PASSWORD_HINT_QUESTIONS Q,",
	" PASSWORD_HINT_ANSWERS A, USERS U WHERE U.USER_ID=A.USER_ID AND", 
	" Q.PASSWORD_HINT_QUESTION_ID=A.PASSWORD_HINT_QUESTION_ID AND upper(U.USERNAME)=upper(?)",
	" ORDER BY A.PASSWORD_HINT_ANSWER_ID");//ORDER BY Q.SNO
	
	//Added by Ravi for Manage Profile
	public static final String DELETE_ANSWER_DATA = CustomStringUtil.appendString("DELETE FROM PASSWORD_HINT_ANSWERS WHERE USER_ID = ?");
	//Added by Ravi for Manage Profile
	public static final String UPDATE_USER_DATA = CustomStringUtil.appendString("UPDATE USERS SET LAST_NAME = ?, FIRST_NAME = ?, EMAIL_ADDRESS = ?, PHONE_NO = ?, COUNTRY  = ?, ZIPCODE = ?, STATE=? , STREET=?, CITY=?, DISPLAY_USERNAME=?  WHERE USER_ID = ?");
	

	public static final String GET_PARENT_DETAILS_ON_SCROLL = CustomStringUtil
	.appendString(  " SELECT ABC.USER_ID, ABC.USERNAME,",
					" ABC.FULLNAME, ABC.STATUS, " ,
					" TO_CHAR(ABC.LAST_LOGIN_ATTEMPT,'MM/DD/YY') AS LAST_LOGIN_ATTEMPT," ,
					" ABC.ORG_NAME, ABC.ORG_ID " ,
					" FROM (SELECT USR.USER_ID, USR.USERNAME, ",
					" USR.LAST_NAME || ' ' || USR.FIRST_NAME AS FULLNAME, ",
					" USR.ACTIVATION_STATUS AS STATUS, USR.LAST_LOGIN_ATTEMPT, ",
					" HIER.ORG_NAME, HIER.ORG_ID ",
					" FROM USERS USR, (SELECT *  FROM ORG_HIERARCHY ",
					" START WITH ORG_ID = ?  ",
					" CONNECT BY PRIOR ORG_ID = ORG_PARENT_ID ",
					" ) HIER ",
					" WHERE HIER.ORG_ID = USR.ORG_ID AND USR.ORG_LEVEL = 0 ",
					" AND UPPER(USR.USERNAME) > UPPER(?) ", 
					" ORDER BY UPPER(USR.USERNAME)) ABC",
					" WHERE ROWNUM <= 15",
					" ORDER BY UPPER(ABC.USERNAME)");
	
	public static final String GET_PARENT_DETAILS_ON_SCROLL_WITH_SRCH_PARAM = CustomStringUtil
	.appendString(  " SELECT ABC.USER_ID, ABC.USERNAME,",
					" ABC.FULLNAME, ABC.STATUS, " ,
					" TO_CHAR(ABC.LAST_LOGIN_ATTEMPT,'MM/DD/YY') AS LAST_LOGIN_ATTEMPT," ,
					" ABC.ORG_NAME, ABC.ORG_ID " ,
					" FROM (SELECT USR.USER_ID, USR.USERNAME, ",
					" USR.LAST_NAME || ' ' || USR.FIRST_NAME AS FULLNAME, ",
					" USR.ACTIVATION_STATUS AS STATUS, USR.LAST_LOGIN_ATTEMPT, ",
					" HIER.ORG_NAME, HIER.ORG_ID ",
					" FROM USERS USR, (SELECT *  FROM ORG_HIERARCHY ",
					" START WITH ORG_ID = ?  ",
					" CONNECT BY PRIOR ORG_ID = ORG_PARENT_ID ",
					" ) HIER ",
					" WHERE HIER.ORG_ID = USR.ORG_ID AND USR.ORG_LEVEL = 0 ",
					" AND UPPER(USR.USERNAME) > UPPER(?) ", 
					" AND (UPPER(USR.USERNAME) LIKE UPPER(?) ",
					" OR UPPER(USR.LAST_NAME) LIKE UPPER(?)",
					" OR UPPER(USR.FIRST_NAME) LIKE UPPER(?))", 
					" ORDER BY UPPER(USR.USERNAME)) ABC",
					" WHERE ROWNUM <= 15",
					" ORDER BY UPPER(ABC.USERNAME)");

	// Query to retrieve list of children of the logged in parent.
	public static final String SEARCH_CHILDREN = CustomStringUtil.appendString(
		"SELECT ST.LAST_NAME || ',' || ST.FIRST_NAME || ' ' || ST.MIDDLE_NAME AS STUDENT_NAME,",
           " ST.STUDENT_BIO_ID,",
           " ST.STRUCTURE_ELEMENT,",
           " GRD.GRADE_NAME AS STUDENTGRADE,",
           " U.ORG_ID,",
           " AD.ADMIN_SEASON || ' ' || AD.ADMIN_YEAR AS ADMIN_SEASON_YEAR,",
           " AD.ADMIN_SEQ,",
           " AD.ADMINID",
        " FROM STUDENT_BIO_DIM  ST,",
           " ADMIN_DIM AD,",
           " GRADE_DIM GRD,",
           " INVITATION_CODE IC,",
           " INVITATION_CODE_CLAIM ICC,",
           " USERS U",              
	    " WHERE ST.ADMINID = AD.ADMINID",
	       " AND ST.ADMINID = IC.ADMINID",
	       " AND ST.GRADEID=GRD.GRADEID",
	       " AND ST.STRUCTURE_ELEMENT = IC.STUDENT_STRUC_ELEMENT",
	       " AND IC.INVITATION_CODE_ID = ICC.INVITATION_CODE_ID",
	       " AND ICC.USER_ID = U.USER_ID",
	       " and ic.org_nodeid = st.org_nodeid ",
	       " and icc.activation_status = 'AC' ",
	       " AND IC.activation_status = 'AC'",
	       " AND upper(U.USERNAME) = upper(?) ",
	       " ORDER BY AD.ADMIN_SEQ DESC");
	// Query to search parent.
	public static final String SEARCH_PARENT = CustomStringUtil.appendString(
													  	" SELECT ABC.USER_ID, ABC.USERNAME,",
														" ABC.FULLNAME, ABC.STATUS,ABC.LAST_NAME,ABC.FIRST_NAME, " ,
														" TO_CHAR(ABC.LAST_LOGIN_ATTEMPT,'MM/DD/YY') AS LAST_LOGIN_ATTEMPT," ,
														" ABC.ORG_NAME, ABC.ORG_ID " ,
														" FROM (SELECT USR.USER_ID, USR.USERNAME, ",
														" USR.LAST_NAME || ' ' || USR.FIRST_NAME AS FULLNAME, USR.LAST_NAME,USR.FIRST_NAME, ",
														" USR.ACTIVATION_STATUS AS STATUS, USR.LAST_LOGIN_ATTEMPT, ",
														" HIER.ORG_NAME, HIER.ORG_ID ",
														" FROM USERS USR, (SELECT *  FROM ORG_HIERARCHY ",
														" START WITH ORG_ID = ?  ",
														" CONNECT BY PRIOR ORG_ID = ORG_PARENT_ID ",
														" ) HIER ",
														" WHERE HIER.ORG_ID = USR.ORG_ID AND USR.ORG_LEVEL = 0 ",
														" AND (UPPER(USR.USERNAME) LIKE UPPER(?) ",
														" OR UPPER(USR.LAST_NAME) LIKE UPPER(?)",
														" OR UPPER(USR.FIRST_NAME) LIKE UPPER(?))", 
														" ORDER BY UPPER(USR.USERNAME)) ABC",
														" WHERE ROWNUM<=?");
	
	public static final String SEARCH_PARENT_EXACT = CustomStringUtil.appendString(
								  	" SELECT ABC.USER_ID, ABC.USERNAME,",
									" ABC.FULLNAME, ABC.STATUS,ABC.LAST_NAME,ABC.FIRST_NAME, " ,
									" TO_CHAR(ABC.LAST_LOGIN_ATTEMPT,'MM/DD/YY') AS LAST_LOGIN_ATTEMPT," ,
									" ABC.ORG_NAME, ABC.ORG_ID " ,
									" FROM (SELECT USR.USER_ID, USR.USERNAME, ",
									" USR.LAST_NAME || ' ' || USR.FIRST_NAME AS FULLNAME, USR.LAST_NAME,USR.FIRST_NAME, ",
									" USR.ACTIVATION_STATUS AS STATUS, USR.LAST_LOGIN_ATTEMPT, ",
									" HIER.ORG_NAME, HIER.ORG_ID ",
									" FROM USERS USR, (SELECT *  FROM ORG_HIERARCHY ",
									" START WITH ORG_ID = ?  ",
									" CONNECT BY PRIOR ORG_ID = ORG_PARENT_ID ",
									" ) HIER ",
									" WHERE HIER.ORG_ID = USR.ORG_ID AND USR.ORG_LEVEL = 0 ",
									" AND UPPER(USR.USERNAME) LIKE UPPER(?) ",
									" ORDER BY UPPER(USR.USERNAME)) ABC",
									" WHERE ROWNUM<=?");
	
/*	public static final String GET_STUDENT_DETAILS_ON_FIRST_LOAD = CustomStringUtil
											.appendString( "SELECT STU.ROWIDENTIFIER,STU.STUDENTNAME, STU.STUDENT_BIO_ID , STU.STRUCTURE_ELEMENT,STU.STUDENTGRADE, STU.SCHOOL ",
															" FROM", 
															" (SELECT ST.ROWIDENTIFIER,ST.STUDENTNAME, ST.STUDENT_BIO_ID , ST.STRUCTURE_ELEMENT, GRD.GRADE_NAME AS STUDENTGRADE, ORG.level3_element_name AS SCHOOL ",
															" FROM (SELECT STD.LAST_NAME || ',' || STD.FIRST_NAME || ' ' || STD.MIDDLE_NAME AS STUDENTNAME,",
											                " STD.LAST_NAME||STD.FIRST_NAME||STD.MIDDLE_NAME||'_'||to_char(STD.STUDENT_BIO_ID)AS ROWIDENTIFIER,",
											                " STD.STUDENT_BIO_ID AS STUDENT_BIO_ID,",
											                " STD.STRUCTURE_ELEMENT AS STRUCTURE_ELEMENT,",
											                " STD.GRADEID,",
											                " STD.ORG_NODEID",
												            " FROM STUDENT_BIO_DIM STD ) ST,", 
												            " GRADE_DIM GRD,",
												            " ORG_NODE_DIM ORG",
												            " WHERE ORG.ORG_NODEID = ST.ORG_NODEID", 
														    " AND ST.GRADEID = GRD.GRADEID",
														    " and ORG.adminid = ? ",
														    " AND (ORG.LEVEL1_JASPER_ORGID = ?", 
													        " OR ORG.LEVEL2_JASPER_ORGID = ?",
													        " OR ORG.LEVEL3_JASPER_ORGID =?",
													        " OR ORG.LEVEL4_JASPER_ORGID = ?)",
													        " ORDER BY ST.ROWIDENTIFIER )STU",
															" WHERE  ROWNUM  <=15");*/

	public static final String GET_STUDENT_DETAILS_ON_FIRST_LOAD = CustomStringUtil
											.appendString(  "Select * From ( ",
															" Select Std.Last_Name || Std.First_Name || Std.Middle_Name || '_' ||",
															" To_Char(Std.Student_Bio_Id) As Rowidentifier, ",
															" Std.Last_Name || ', ' || Std.First_Name || ' ' ||",
															" Std.Middle_Name As Studentname,",
															" Std.Student_Bio_Id,",
															" Std.Structure_Element,",
															" Grd.Grade_Name As Studentgrade,",
															" Org.Level3_Element_Name As School",
															" From Student_Bio_Dim Std, Grade_Dim Grd,Org_Node_Dim Org",
															" Where Org.Org_Nodeid = Std.Org_Nodeid",
															" And Std.Gradeid = Grd.Gradeid",
															" And std.Adminid= ?",
															" And Org.Adminid = ?",
															" And (Org.Level1_Jasper_Orgid = ? Or Org.Level2_Jasper_Orgid = ? Or ",
															" Org.Level3_Jasper_Orgid = ? Or Org.Level4_Jasper_Orgid = ?)",
															" Order By Rowidentifier)  Where Rownum <= 15");
													


	public static final String GET_STUDENT_DETAILS_ON_SCROLL = CustomStringUtil
																.appendString( "SELECT STU.ROWIDENTIFIER,STU.STUDENTNAME, STU.STUDENT_BIO_ID , STU.STRUCTURE_ELEMENT,STU.STUDENTGRADE, STU.SCHOOL ",
																				" FROM", 
																				" (SELECT ST.ROWIDENTIFIER,ST.STUDENTNAME, ST.STUDENT_BIO_ID , ST.STRUCTURE_ELEMENT,GRD.GRADE_NAME AS STUDENTGRADE, ORG.level3_element_name AS SCHOOL ",
																				" FROM (SELECT STD.LAST_NAME || ',' || STD.FIRST_NAME || ' ' || STD.MIDDLE_NAME AS STUDENTNAME,",
																	            " STD.LAST_NAME||STD.FIRST_NAME||STD.MIDDLE_NAME||'_'||to_char(STD.STUDENT_BIO_ID)AS ROWIDENTIFIER,",
																	            " STD.STUDENT_BIO_ID AS STUDENT_BIO_ID,",
																	            " STD.STRUCTURE_ELEMENT AS STRUCTURE_ELEMENT,",
																	            " STD.GRADEID,",
																	            " STD.ORG_NODEID",
																	            " FROM STUDENT_BIO_DIM STD " ,
																	            " WHERE STD.adminid = ? " ,
																	            " ) ST, ",
																	            " GRADE_DIM GRD,",
																	            " ORG_NODE_DIM ORG",
																			    " WHERE ORG.ORG_NODEID = ST.ORG_NODEID",
																			    " AND ST.GRADEID = GRD.GRADEID",
																			    " and ORG.adminid = ? ",
																			    " AND (ORG.LEVEL1_JASPER_ORGID = ?", 
																		        " OR ORG.LEVEL2_JASPER_ORGID = ?",
																		        " OR ORG.LEVEL3_JASPER_ORGID =?",
																		        " OR ORG.LEVEL4_JASPER_ORGID = ?)",
																		        " AND ST.ROWIDENTIFIER  > ?",
																		        " ORDER BY ST.ROWIDENTIFIER )STU",
																				" WHERE  ROWNUM  <=15");
	
	public static final String GET_STUDENT_DETAILS_ON_SCROLL_WITH_SRCH_PARAM = CustomStringUtil
	.appendString( "SELECT STU.ROWIDENTIFIER,STU.STUDENTNAME, STU.STUDENT_BIO_ID , STU.STRUCTURE_ELEMENT,STU.STUDENTGRADE, STU.SCHOOL ",
					" FROM", 
					" (SELECT ST.ROWIDENTIFIER,ST.STUDENTNAME, ST.STUDENT_BIO_ID , ST.STRUCTURE_ELEMENT,GRD.GRADE_NAME AS STUDENTGRADE, ORG.level3_element_name AS SCHOOL ",
					" FROM (SELECT STD.LAST_NAME || ',' || STD.FIRST_NAME || ' ' || STD.MIDDLE_NAME AS STUDENTNAME,",
		            " STD.LAST_NAME||STD.FIRST_NAME||STD.MIDDLE_NAME||'_'||to_char(STD.STUDENT_BIO_ID)AS ROWIDENTIFIER,",
		            " STD.STUDENT_BIO_ID AS STUDENT_BIO_ID,",
		            " STD.STRUCTURE_ELEMENT AS STRUCTURE_ELEMENT,",
		            " STD.GRADEID,",
		            " STD.ORG_NODEID",
		            " FROM STUDENT_BIO_DIM STD " ,
		            " WHERE STD.adminid = ? " ,
		            " ) ST, ",
		            " GRADE_DIM GRD,",
		            " ORG_NODE_DIM ORG",
				    " WHERE ORG.ORG_NODEID = ST.ORG_NODEID",
				    " AND ST.GRADEID = GRD.GRADEID",
				    " and ORG.adminid = ? ",
				    " AND (ORG.LEVEL1_JASPER_ORGID = ?", 
			        " OR ORG.LEVEL2_JASPER_ORGID = ?",
			        " OR ORG.LEVEL3_JASPER_ORGID =?",
			        " OR ORG.LEVEL4_JASPER_ORGID = ?)",
			        " AND ST.ROWIDENTIFIER  > ?",
			        " AND UPPER(ST.STUDENTNAME)LIKE UPPER(?)",
			        " ORDER BY ST.ROWIDENTIFIER )STU",
					" WHERE  ROWNUM  <=15");

	public static final String GET_PARENT_DETAILS_FOR_CHILDREN = CustomStringUtil.appendString(
															 " SELECT USR.USERNAME,",
															 " IC.INVITATION_CODE,",
															 " IC.ACTIVATION_STATUS ", 
															 " FROM INVITATION_CODE_CLAIM ICC,INVITATION_CODE IC,",
															 " USERS USR, STUDENT_BIO_DIM STD",
															 " WHERE STD.STUDENT_BIO_ID = ?",
															 " AND STD.STRUCTURE_ELEMENT = IC.STUDENT_STRUC_ELEMENT",
															 " AND IC.INVITATION_CODE_ID= ICC.INVITATION_CODE_ID",
															 " AND IC.ADMINID=STD.ADMINID AND IC.ACTIVATION_STATUS = 'AC'",
															 " AND ICC.USER_ID = USR.USER_ID");
	
	public static final String GET_ASSESSMENT_LIST = CustomStringUtil.appendString(
															 " SELECT STD.STUDENT_BIO_ID, ",
															 " IC.INVITATION_CODE,TO_CHAR(IC.INVITATION_EXPIRATION_DATE,'mm/dd/yyyy') AS EXPIRATION_DATE, ",
															 " IC.TOTAL_AVAILABLE_IC_CLAIM,",
															 " DECODE(SIGN(IC.INVITATION_EXPIRATION_DATE - SYSDATE),-1,'Expired','Active') AS EXPIRATION_STATUS, ",
															 " ADM.ADMIN_SEASON || ' ' || ADM.ADMIN_YEAR AS ASSESSMENT_YEAR ",
															 " FROM INVITATION_CODE IC, STUDENT_BIO_DIM STD, ADMIN_DIM ADM",
															 " WHERE IC.STUDENT_STRUC_ELEMENT = STD.STRUCTURE_ELEMENT  ",
															 " AND IC.ACTIVATION_STATUS = 'AC' ",
															 " AND IC.ADMINID=STD.ADMINID",
															 " AND ADM.ADMINID=IC.ADMINID",
															 " AND STD.STUDENT_BIO_ID = ?");
	
	
	/*public static final String SEARCH_STUDENT = CustomStringUtil
													.appendString( "SELECT DISTINCT (ABC.STUDENT_BIO_ID) AS STUDENT_BIO_ID,",
												            " ABC.STUDENT_STRUC_ELEMENT,",
												            " ABC.STUDENTNAME,",
												            " ABC.ORG_NAME,",
												            " ABC.ORG_ID",
												            " FROM (SELECT STD.LAST_NAME||','||STD.FIRST_NAME  || ' ' || STD.MIDDLE_NAME",
												            " AS STUDENTNAME,",
												            " STD.STUDENT_BIO_ID AS STUDENT_BIO_ID,",
												            " IC.STUDENT_STRUC_ELEMENT AS STUDENT_STRUC_ELEMENT,",
												            " USR.ACTIVATION_STATUS AS STATUS,",
												            " IC.INVITATION_CODE_ID AS INVITATIONCODE,",
												            " IC.ACTIVATION_STATUS AS ACTIVATIONSTATUS,",
												            " HIER.ORG_NAME,",
												            " HIER.ORG_ID",
												            " FROM USERS USR,",
												            " STUDENT_BIO_DIM STD,",
												            " INVITATION_CODE IC,",
												            " INVITATION_CODE_CLAIM ICC,",
												            " (SELECT *",
												            " FROM ORG_HIERARCHY",
												            " START WITH ORG_ID = ?",
												            " CONNECT BY PRIOR ORG_ID = ORG_PARENT_ID",
												            " ORDER BY ORG_LEVEL) HIER",
												            " WHERE HIER.ORG_ID = USR.ORG_ID",
												            " AND USR.ORG_LEVEL = 0",
												            " AND USR.USER_ID = ICC.USER_ID",
												            " AND ICC.INVITATION_CODE_ID = IC.INVITATION_CODE_ID",
												            " AND IC.STUDENT_STRUC_ELEMENT = STD.STRUCTURE_ELEMENT) ABC",
												            " WHERE UPPER(ABC.STUDENTNAME) LIKE UPPER(?)", 
												            " ORDER BY UPPER(ABC.STUDENTNAME)");*/
	public static final String SEARCH_STUDENT = CustomStringUtil
														.appendString( "SELECT STU.ROWIDENTIFIER,STU.STUDENTNAME, STU.STUDENT_BIO_ID , STU.STRUCTURE_ELEMENT,STU.GRADE_NAME AS STUDENTGRADE, STU.SCHOOL as SCHOOL ",
																" FROM", 
																" (SELECT ST.ROWIDENTIFIER,ST.STUDENTNAME, ST.STUDENT_BIO_ID , ST.STRUCTURE_ELEMENT,GRD.GRADE_NAME, ORG.level3_element_name AS SCHOOL ",
																" FROM (SELECT STD.LAST_NAME || ',' || STD.FIRST_NAME || ' ' || STD.MIDDLE_NAME AS STUDENTNAME,",
													            " STD.LAST_NAME||STD.FIRST_NAME||STD.MIDDLE_NAME||'_'||to_char(STD.STUDENT_BIO_ID)AS ROWIDENTIFIER,",
													            " STD.STUDENT_BIO_ID AS STUDENT_BIO_ID,",
													            " STD.STRUCTURE_ELEMENT AS STRUCTURE_ELEMENT,",
													            " STD.GRADEID,",
													            " STD.ORG_NODEID",
													            " FROM STUDENT_BIO_DIM STD " ,
													            " WHERE STD.adminid = ? " ,
													            " ) ST, ",
													            " GRADE_DIM GRD,",
													            " ORG_NODE_DIM ORG",
															    " WHERE ORG.ORG_NODEID = ST.ORG_NODEID", 
															    " AND ST.GRADEID = GRD.GRADEID",
															    " AND ORG.adminid=? ",
															    " AND (ORG.LEVEL1_JASPER_ORGID = ?", 
														        " OR ORG.LEVEL2_JASPER_ORGID = ?",
														        " OR ORG.LEVEL3_JASPER_ORGID =?",
														        " OR ORG.LEVEL4_JASPER_ORGID = ?)",
														        " AND UPPER(ST.STUDENTNAME)LIKE UPPER(?)",
														        " ORDER BY ST.ROWIDENTIFIER )STU",
														        " WHERE ROWNUM <= ? ");
																
	public static final String SEARCH_STUDENT_ON_REDIRECT = CustomStringUtil
													.appendString( "SELECT DISTINCT (ABC.STUDENT_BIO_ID) AS STUDENT_BIO_ID,",
												            " ABC.STUDENT_STRUC_ELEMENT,",
												            " ABC.STUDENTNAME,",
												            " ABC.ROWIDENTIFIER,",
												            " ABC.ORG_NAME,",
												            " ABC.STUDENTGRADE,",
												            " ABC.ORG_ID",
												            " FROM (SELECT STD.LAST_NAME||','||STD.FIRST_NAME  || ' ' || STD.MIDDLE_NAME",
												            " AS STUDENTNAME,",
												            " STD.LAST_NAME||STD.FIRST_NAME||STD.MIDDLE_NAME||'_'||to_char(STD.STUDENT_BIO_ID)AS ROWIDENTIFIER,",
												            " STD.STUDENT_BIO_ID AS STUDENT_BIO_ID,",
												            " GRD.GRADE_NAME AS STUDENTGRADE,",
												            " IC.STUDENT_STRUC_ELEMENT AS STUDENT_STRUC_ELEMENT,",
												            " USR.ACTIVATION_STATUS AS STATUS,",
												            " IC.INVITATION_CODE_ID AS INVITATIONCODE,",
												            " IC.ACTIVATION_STATUS AS ACTIVATIONSTATUS,",
												            " HIER.ORG_NAME,",
												            " HIER.ORG_ID",
												            " FROM USERS USR,",
												            " STUDENT_BIO_DIM STD,",
												            " GRADE_DIM GRD,",
												            " INVITATION_CODE IC,",
												            " INVITATION_CODE_CLAIM ICC,",
												            " (SELECT *",
												            " FROM ORG_HIERARCHY",
												            " START WITH ORG_ID = ?",
												            " CONNECT BY PRIOR ORG_ID = ORG_PARENT_ID",
												            " ) HIER",
												            " WHERE HIER.ORG_ID = USR.ORG_ID",
												            " AND USR.ORG_LEVEL = 0",
												            " AND USR.USER_ID = ICC.USER_ID AND IC.ACTIVATION_STATUS = 'AC' ",
												            " AND ICC.INVITATION_CODE_ID = IC.INVITATION_CODE_ID",
												            " AND IC.STUDENT_STRUC_ELEMENT = STD.STRUCTURE_ELEMENT",
												            " AND STD.GRADEID=GRD.GRADEID) ABC",
												            " WHERE ABC.STUDENT_BIO_ID =(?)", 
												            " ORDER BY UPPER(ABC.STUDENTNAME)");
	public static final String UPDATE_ASSESSMENT = CustomStringUtil.appendString(
			"UPDATE INVITATION_CODE IC SET  IC.TOTAL_AVAILABLE_IC_CLAIM = ?, IC.INVITATION_EXPIRATION_DATE= TO_DATE(?,'mm/dd/yyyy')",
            " WHERE IC.INVITATION_CODE = ? AND IC.ACTIVATION_STATUS = 'AC' ",
            " AND IC.STUDENT_STRUC_ELEMENT = (SELECT STD.STRUCTURE_ELEMENT  FROM STUDENT_BIO_DIM STD WHERE STD.STUDENT_BIO_ID = ?)");
	
	//by Deepak update user profile when they login for the first time
    public static final String UPDATE_FIRSTTIMEUSERLOGIN_DATA = CustomStringUtil.appendString(
                " UPDATE USERS  SET LAST_NAME = ?, ",
                " FIRST_NAME =?,  "  ,
                "  EMAIL_ADDRESS = ?  ,   PHONE_NO =?   ,   COUNTRY = ? , " ,
                "  ZIPCODE=? , STATE=? , STREET=? ,CITY=?" ,
                "  WHERE USER_ID = ? ");

    //by Deepak update user profile when they login for the first time
    public static final String UPDATE_FIRSTTIMEUSERFLAG_DATA = " UPDATE USERS  SET IS_FIRSTTIME_LOGIN = ? WHERE upper(USERNAME) =upper(?)";

    public static final String FETCH_SCHOOLID_FOR_STUDENT = CustomStringUtil.appendString(
    		" select ORG.level3_jasper_orgid from student_bio_dim std, ORG_NODE_DIM org ",
    		" where ORG.ORG_NODEID = std.ORG_NODEID and std.student_bio_id = ? " );

    public static final String CHECK_FOR_EXISTING_ANSWER = CustomStringUtil.appendString("SELECT COUNT(*) AS NO_OF_ANSWERS FROM  PASSWORD_HINT_ANSWERS P WHERE P.USER_ID=?");
    public static final String UPDATE_ANSWER_DATA = CustomStringUtil.appendString("UPDATE PASSWORD_HINT_ANSWERS PANS SET PANS.PASSWORD_HINT_QUESTION_ID=?,PANS.PASSWORD_HINT_ANSWER=?,",
																		    	  " PANS.UPDATED_DATE_TIME=SYSDATE",
																		          " WHERE PANS.USER_ID=?",
																		          " AND PANS.PASSWORD_HINT_ANSWER_ID=?");
    
    public static final String ADMIN_YEAR_LIST = "select adminid, admin_name, current_admin from admin_dim order by admin_seq desc";
    public static final String VALIDATE_SECURITY_ANSWERS= CustomStringUtil.appendString(
    		" SELECT (NVL((SUM (COUNT (u.username))),-1)) AS usercount",
		    " FROM password_hint_answers pwd ,users u ,",
		        "  (SELECT u.User_Id FROM password_hint_answers pwd ,users u",  
		           "  WHERE u.User_Id = (SELECT user_id FROM users WHERE upper(username)=upper(?) AND ROWNUM=1)",
		            " AND pwd.user_id=u.User_Id",
		            " AND pwd.password_hint_question_id = ?", 
		            " AND upper(pwd.password_hint_answer)=upper(?))val1,",
		            " (SELECT u.User_Id FROM password_hint_answers pwd ,users u",  
		            "  WHERE u.User_Id = (SELECT user_id FROM users WHERE upper(username)=upper(?) AND ROWNUM=1)",
		            " AND pwd.user_id=u.User_Id",
		            " AND pwd.password_hint_question_id = ?", 
		            " AND upper(pwd.password_hint_answer)=upper(?))val2",
		    " WHERE  pwd.user_id=val1.User_Id",
		           " AND val1.User_Id=val2.User_Id",
		           " AND u.user_id=pwd.user_id",
		           " AND pwd.password_hint_question_id = ?",
		           " AND upper(pwd.password_hint_answer)=upper(?)",
		           " AND ROWNUM=1",
		           " GROUP BY u.username ");
    
    public static final String GET_ALL_USERS_BY_EMAIL = "SELECT USERNAME,FIRST_NAME,LAST_NAME FROM users WHERE EMAIL_ADDRESS=? and ACTIVATION_STATUS = 'AC'";
    
    public static final String UPDATE_PARENT_USER_ORG = "Update Users Set Org_Id = (Select o.level3_jasper_orgid From Invitation_Code i,org_node_dim o  Where i.Activation_Status = 'AC' And i.Invitation_Code = ? And i.ORG_NODEID =o.org_nodeid And Rownum = 1) Where Upper(Username) = Upper(?)";

    public static final String UPDATE_ACTIVATION_CODE = CustomStringUtil.appendString(
			"UPDATE INVITATION_CODE IC SET  IC.ACTIVATION_STATUS = 'IN', IC.UPDATED_DATE_TIME = sysdate",
            " WHERE IC.INVITATION_CODE = ? AND IC.ACTIVATION_STATUS = 'AC' ",
            " AND IC.STUDENT_STRUC_ELEMENT = (SELECT STD.STRUCTURE_ELEMENT  FROM STUDENT_BIO_DIM STD WHERE STD.STUDENT_BIO_ID = ?)");
    
    public static final String ADD_NEW_INVITATION_CODE =  CustomStringUtil.appendString(
			"insert into INVITATION_CODE",
				  " select NVITATION_CODE_ID_SEQ.Nextval, (select sf_gen_invite_code from DUAL), student_struc_element, (select parameter_value from PARAMETER_MASTER_LOOKUP where parameter_name = 'TOTAL_AVAILABLE_IC_CLAIM' and rownum = 1), '0', org_nodeid, adminid, invitation_expiration_date, 'AC', created_by_id, sysdate, sysdate, 'N'", 
				  " from INVITATION_CODE",
				  " where invitation_code = ? and activation_status = 'AC' AND STUDENT_STRUC_ELEMENT = (SELECT STD.STRUCTURE_ELEMENT  FROM STUDENT_BIO_DIM STD WHERE STD.STUDENT_BIO_ID = ?) ");
}

