$(document).ready(function(){
	
	/*$('#coCheckDateFrom').datepicker({maxDate: '0',onSelect: function(date) {
		date = $(this).datepicker('getDate');
        var maxDate = new Date(date.getTime());
        maxDate.setDate(maxDate.getDate() + 365);
        $('#coCheckDateTo').datepicker('option', {minDate: date, maxDate: maxDate});
    }});*/
	$('#dateFrom').datepicker();
	$('#dateTo').datepicker();
	
	$('#searchScoreReview').on("click", function(){
		validateForm();
	});
	
	$("#coCheckErrorDialog").dialog({
		bgiframe: true, 
		autoOpen: false, 
		modal: true, 
		height: 100, 
		minWidth: 450, 
		closeOnEscape: true, 
		resizable: true
	});
	
	$.fn.dataTable.ext.errMode = 'none';
	
	$("#scoreResultTable").dataTable( {
		"bJQueryUI": true,
		"sPaginationType": "full_numbers",
	});
	
	
	
});

function getMoreInfoWin(studentBioId, subtestId) {
	jQuery("#searchScoreReview").dialog({
		title: 'Review pending scores: ',
		width: 900,
		height: 500,
		draggable: false
	});
	var dataString = "studentBioId="+studentBioId+"subtestId="+subtestId;
	$.ajax({
	      type: "POST",
	      url: "getReviewResult.htm",
	      data: dataString,
	      success: function(data) {
	    	  if(data == "Error") {
	    		  clearStudentDetailsTableWhenError('Failed to get review records');
	    	  } else {
	    		  $("#_stu_log").html( '' );
		    	  var obj = JSON.parse(data);
		    	  
	    	  }
	      },
		  error: function(data) {
			  clearStudentDetailsTableWhenError('Failed to get Student Details');
		  }
	});
}

function dataTableCallBack(){
	update_rows();
	$('.moreInfoWin').on("click", function(){
		getMoreInfoWin($(this));
	});
}

function update_rows(){
    $(".process_details tr:even").css("background-color", "#fff");
    $(".process_details tr:odd").css("background-color", "#eee");
}

function validateForm(){
	//TODO validation if needed
	$('#scoreReviewForm').submit();
}

function clearMoreInfoTableRows() {
	$("#studentTestId").html('');
	$("#formName").html('');
	$("#newNC").html('');
	$("#newSS").html('');
	$("#newHSE").html('');
	$("#processedDate").html('');
	
}

function studentDetails(studentTestEventId,subTestName){
	clearMoreInfoTableRows();
	var dataString = "studentTestEventId="+studentTestEventId+"&subTestName="+subTestName;
	$("#review").html( '<img src="css/ajax-loader.gif"></img>' );
	jQuery("#reviewDialog").dialog({
		title: 'Student Score Info: ',
		width: 675,
		height: 410,
		draggable: false
	});
	$.ajax({
	      type: "POST",
	      url: "getStudentScoreInfo.htm",
	      cache: false,
	      data: dataString,
	      success: function(data) {
	    	  if(data == "Error") {
	    		  clearMoreInfoTableWhenError('Failed to get Data');
	    	  } else if(data.length == 2) {
	    		  clearMoreInfoTableWhenError('Data Not Found');
	          } else {
	    		  $("#review").html('');
		    	  var obj = JSON.parse(data);
		    	  $("#studentTestId").html( obj.TEST_ELEMENT_ID );
		    	  $("#formName").html( obj.FORM_NAME );
		    	  $("#newNC").html( obj.NCE );
		    	  $("#newSS").html( obj.SS );
		    	  $("#newHSE").html( obj.HSE );
		    	  $("#processedDate").html(obj.UPDATED_DATE_TIME);
		     }
	      },
		  error: function(data) {
			  clearMoreInfoTableWhenError('Failed to get Data');
		  }
    });

}

function rejectOther(id){
	var elem = document.getElementsByName("isApprove");
	for(var i=1;i<=elem.length;i++){
	if(i==id){
		document.getElementById(i).disabled = false;
    }
    else 
    {	
    	document.getElementById(i).disabled = true;
    }
    }
}