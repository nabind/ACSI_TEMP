CREATE OR REPLACE PACKAGE PKG_FILE_TRACKING AS

  TYPE GET_REFCURSOR IS REF CURSOR;

  PROCEDURE SP_GET_ER_DATA(P_DATE_FROM                  IN VARCHAR2,
                           P_DATE_TO                    IN VARCHAR2,
                           P_UUID                       IN VARCHAR2,
                           P_LAST_NAME                  IN VARCHAR2,
                           P_EX_CODE                    IN NUMBER,
                           P_RECORD_ID                  IN NUMBER,
                           P_PROCESS_ID                 IN NUMBER,
                           P_STATE_CODE                 IN VARCHAR2,
                           P_FORM                       IN VARCHAR2,
                           P_TEST_ELEMENT_ID            IN VARCHAR2,
                           P_TEST_BARCODE               IN VARCHAR2,
                           P_SEARCH_PARAM               IN VARCHAR2,
                           P_ORDERED_COLUMN             IN VARCHAR2,
                           P_ORDER                      IN VARCHAR2,
                           P_ROWNUM_FROM                IN NUMBER,
                           P_ROWNUM_TO                  IN NUMBER,
                           P_OUT_CUR_TOTAL_RECORD_COUNT OUT GET_REFCURSOR,
                           P_OUT_CUR_ER_DATA            OUT GET_REFCURSOR,
                           P_OUT_EXCEP_ERR_MSG          OUT VARCHAR2);

END PKG_FILE_TRACKING;
/
CREATE OR REPLACE PACKAGE BODY PKG_FILE_TRACKING AS

  PROCEDURE SP_GET_ER_DATA(P_DATE_FROM                  IN VARCHAR2,
                           P_DATE_TO                    IN VARCHAR2,
                           P_UUID                       IN VARCHAR2,
                           P_LAST_NAME                  IN VARCHAR2,
                           P_EX_CODE                    IN NUMBER,
                           P_RECORD_ID                  IN NUMBER,
                           P_PROCESS_ID                 IN NUMBER,
                           P_STATE_CODE                 IN VARCHAR2,
                           P_FORM                       IN VARCHAR2,
                           P_TEST_ELEMENT_ID            IN VARCHAR2,
                           P_TEST_BARCODE               IN VARCHAR2,
                           P_SEARCH_PARAM               IN VARCHAR2,
                           P_ORDERED_COLUMN             IN VARCHAR2,
                           P_ORDER                      IN VARCHAR2,
                           P_ROWNUM_FROM                IN NUMBER,
                           P_ROWNUM_TO                  IN NUMBER,
                           P_OUT_CUR_TOTAL_RECORD_COUNT OUT GET_REFCURSOR,
                           P_OUT_CUR_ER_DATA            OUT GET_REFCURSOR,
                           P_OUT_EXCEP_ERR_MSG          OUT VARCHAR2) IS
  
    V_QUERY_PAGING             CLOB := '';
    V_QUERY_ACTUAL             CLOB := '';
    V_QUERY_TOTAL_RECORD_COUNT CLOB := '';
    V_EXCEPTION_STATUS         VARCHAR2(10) := '';
  
  BEGIN
  
    V_QUERY_ACTUAL := V_QUERY_ACTUAL || ' SELECT DISTINCT ESSH.LASTNAME || '', '' || ESSH.FIRSTNAME || '' '' ||
                    ESSH.MIDDLENAME STUDENTNAME,
                    ESSH.UUID UUID,
                    TO_CHAR(NVL(EED.TEST_ELEMENT_ID, ''NA'')) TEST_ELEMENT_ID,
                    NVL(TO_CHAR(EED.PROCESS_ID), ''NA'') PROCESS_ID,
                    TO_CHAR(NVL(EED.EXCEPTION_CODE, ''NA'')) EXCEPTION_CODE,
                    NVL(EED.SOURCE_SYSTEM, ''ERESOURCE'') SOURCE_SYSTEM,
                    NVL(EED.EXCEPTION_STATUS, ''CO'') EXCEPTION_STATUS,
                    ESSH.ER_SS_HISTID ER_SS_HISTID,
                    ESSH.BARCODE BARCODE,
                    ESSH.DATE_SCHEDULED DATE_SCHEDULED,
                    ESSH.STATE_CODE STATE_CODE,
                    ESSH.FORM FORM,
                    ESSH.DATETIMESTAMP,
                    NVL(EED.ER_EXCDID, 0) ER_EXCDID,
                    (SELECT SUBTEST_NAME
                       FROM SUBTEST_DIM
                      WHERE SUBTEST_CODE = ESSH.CONTENT_AREA_CODE) SUBTEST,
                    TO_CHAR(ESSH.DATETIMESTAMP, ''MM/DD/YYYY'') PROCESSED_DATE
      FROM ER_STUDENT_SCHED_HISTORY ESSH
      LEFT OUTER JOIN ER_EXCEPTION_DATA EED
        ON ESSH.ER_SS_HISTID = EED.ER_SS_HISTID
     WHERE 1 = 1';
  
    IF P_DATE_FROM <> '-1' THEN
      V_QUERY_ACTUAL := V_QUERY_ACTUAL ||
                        ' AND TRUNC(ESSH.DATETIMESTAMP) >= TO_DATE(''' ||
                        P_DATE_FROM || ''', ''MM/DD/YYYY'')';
    END IF;
  
    IF P_DATE_TO <> '-1' THEN
      V_QUERY_ACTUAL := V_QUERY_ACTUAL ||
                        ' AND TRUNC(ESSH.DATETIMESTAMP) <= TO_DATE(''' ||
                        P_DATE_TO || ''', ''MM/DD/YYYY'')';
    END IF;
  
    IF P_UUID <> '-1' THEN
      V_QUERY_ACTUAL := V_QUERY_ACTUAL || ' AND ESSH.UUID LIKE ''%' ||
                        P_UUID || '%''';
    END IF;
  
    IF P_LAST_NAME <> '-1' THEN
      V_QUERY_ACTUAL := V_QUERY_ACTUAL ||
                        ' AND UPPER(ESSH.LASTNAME) LIKE UPPER(''%' ||
                        P_LAST_NAME || '%'')';
    END IF;
  
    IF P_EX_CODE <> -1 THEN
      V_QUERY_ACTUAL := V_QUERY_ACTUAL || ' AND EED.EXCEPTION_CODE = ' ||
                        P_EX_CODE;
    END IF;
  
    IF P_RECORD_ID <> -1 THEN
      V_QUERY_ACTUAL := V_QUERY_ACTUAL || ' AND ESSH.ER_SS_HISTID = ' ||
                        P_RECORD_ID;
    END IF;
  
    IF P_PROCESS_ID <> -1 THEN
      V_QUERY_ACTUAL := V_QUERY_ACTUAL || ' AND EED.PROCESS_ID = ' ||
                        P_PROCESS_ID;
    END IF;
  
    IF P_STATE_CODE <> '-1' THEN
      V_QUERY_ACTUAL := V_QUERY_ACTUAL || ' AND ESSH.STATE_CODE =  ''' ||
                        P_STATE_CODE || '''';
    END IF;
  
    IF P_FORM <> '-1' THEN
      V_QUERY_ACTUAL := V_QUERY_ACTUAL || ' AND ESSH.FORM =  ''' || P_FORM || '''';
    END IF;
  
    IF P_TEST_ELEMENT_ID <> '-1' THEN
      V_QUERY_ACTUAL := V_QUERY_ACTUAL || ' AND ESSH.TEST_ELEMENT_ID =  ''' ||
                        P_TEST_ELEMENT_ID || '''';
    END IF;
  
    IF P_TEST_BARCODE <> '-1' THEN
      V_QUERY_ACTUAL := V_QUERY_ACTUAL || ' AND ESSH.BARCODE =  ''' ||
                        P_TEST_BARCODE || '''';
    END IF;
  
    IF P_SEARCH_PARAM <> '-1' THEN
      V_QUERY_ACTUAL := 'SELECT * FROM (' || V_QUERY_ACTUAL ||
                        ') TAB_SEARCH WHERE STUDENTNAME LIKE ''%' ||
                        P_SEARCH_PARAM || '%'' OR UUID LIKE ''%' ||
                        P_SEARCH_PARAM || '%'' OR TEST_ELEMENT_ID LIKE ''%' ||
                        P_SEARCH_PARAM || '%'' OR PROCESS_ID LIKE ''%' ||
                        P_SEARCH_PARAM || '%'' OR EXCEPTION_CODE LIKE ''%' ||
                        P_SEARCH_PARAM || '%'' OR ER_SS_HISTID LIKE ''%' ||
                        P_SEARCH_PARAM || '%'' OR BARCODE LIKE ''%' ||
                        P_SEARCH_PARAM || '%'' OR DATE_SCHEDULED LIKE ''%' ||
                        P_SEARCH_PARAM || '%'' OR STATE_CODE LIKE ''%' ||
                        P_SEARCH_PARAM || '%'' OR FORM LIKE ''%' ||
                        P_SEARCH_PARAM || '%'' OR ER_EXCDID LIKE ''%' ||
                        P_SEARCH_PARAM || '%'' OR SUBTEST LIKE ''%' ||
                        P_SEARCH_PARAM || '%''';
    
      IF P_SEARCH_PARAM = 'Error' THEN
        V_EXCEPTION_STATUS := 'ER';
      ELSIF P_SEARCH_PARAM = 'Completed' THEN
        V_EXCEPTION_STATUS := 'CO';
      ELSIF P_SEARCH_PARAM = 'Invalidated' THEN
        V_EXCEPTION_STATUS := 'IN';
      END IF;
    
      IF V_EXCEPTION_STATUS <> '' THEN
        V_QUERY_ACTUAL := V_QUERY_ACTUAL || 'OR EXCEPTION_STATUS LIKE ''%' ||
                          V_EXCEPTION_STATUS || '%''';
      END IF;
    
    END IF;
  
    V_QUERY_TOTAL_RECORD_COUNT := V_QUERY_TOTAL_RECORD_COUNT ||
                                  'SELECT COUNT(1) TOTAL_RECORD_COUNT FROM (' ||
                                  V_QUERY_ACTUAL || ') TAB';
  
    DBMS_OUTPUT.PUT_LINE('V_QUERY_TOTAL_RECORD_COUNT: ' ||
                         V_QUERY_TOTAL_RECORD_COUNT);
  
    OPEN P_OUT_CUR_TOTAL_RECORD_COUNT FOR V_QUERY_TOTAL_RECORD_COUNT;
  
    V_QUERY_ACTUAL := V_QUERY_ACTUAL || ' ORDER BY ';
    IF P_ORDERED_COLUMN = '1' THEN
      V_QUERY_ACTUAL := V_QUERY_ACTUAL || ' ER_SS_HISTID ';
    ELSIF P_ORDERED_COLUMN = '2' THEN
      V_QUERY_ACTUAL := V_QUERY_ACTUAL || ' STUDENTNAME ';
    ELSIF P_ORDERED_COLUMN = '3' THEN
      V_QUERY_ACTUAL := V_QUERY_ACTUAL || ' UUID ';
    ELSIF P_ORDERED_COLUMN = '4' THEN
      V_QUERY_ACTUAL := V_QUERY_ACTUAL || ' TEST_ELEMENT_ID ';
    ELSIF P_ORDERED_COLUMN = '5' THEN
      V_QUERY_ACTUAL := V_QUERY_ACTUAL || ' PROCESS_ID ';
    ELSIF P_ORDERED_COLUMN = '6' THEN
      V_QUERY_ACTUAL := V_QUERY_ACTUAL || ' EXCEPTION_CODE ';
    ELSIF P_ORDERED_COLUMN = '7' THEN
      V_QUERY_ACTUAL := V_QUERY_ACTUAL || ' EXCEPTION_STATUS ';
    ELSIF P_ORDERED_COLUMN = '8' THEN
      V_QUERY_ACTUAL := V_QUERY_ACTUAL || ' BARCODE ';
    ELSIF P_ORDERED_COLUMN = '9' THEN
      V_QUERY_ACTUAL := V_QUERY_ACTUAL || ' DATE_SCHEDULED ';
    ELSIF P_ORDERED_COLUMN = '10' THEN
      V_QUERY_ACTUAL := V_QUERY_ACTUAL || ' STATE_CODE ';
    ELSIF P_ORDERED_COLUMN = '11' THEN
      V_QUERY_ACTUAL := V_QUERY_ACTUAL || ' FORM ';
    ELSIF P_ORDERED_COLUMN = '12' THEN
      V_QUERY_ACTUAL := V_QUERY_ACTUAL || ' SUBTEST ';
    END IF;
  
    IF P_ORDER = 'asc' THEN
      V_QUERY_ACTUAL := V_QUERY_ACTUAL || ' ASC ';
    ELSIF P_ORDER = 'desc' THEN
      V_QUERY_ACTUAL := V_QUERY_ACTUAL || ' DESC ';
    END IF;
  
    DBMS_OUTPUT.PUT_LINE('V_QUERY_ACTUAL: ' || V_QUERY_ACTUAL);
  
    V_QUERY_PAGING := V_QUERY_PAGING ||
                      'SELECT *
        FROM (SELECT ROWNUM RNUM, A.* FROM (' ||
                      V_QUERY_ACTUAL || ') A WHERE ROWNUM <= ' ||
                      P_ROWNUM_TO || ')
       WHERE RNUM >= ' || P_ROWNUM_FROM;
  
    DBMS_OUTPUT.PUT_LINE('V_QUERY_PAGING: ' || V_QUERY_PAGING);
  
    OPEN P_OUT_CUR_ER_DATA FOR V_QUERY_PAGING;
  
  EXCEPTION
    WHEN OTHERS THEN
      P_OUT_EXCEP_ERR_MSG := UPPER(SUBSTR(SQLERRM, 12, 255));
  END SP_GET_ER_DATA;

END PKG_FILE_TRACKING; --END OF PACKAGE
/
