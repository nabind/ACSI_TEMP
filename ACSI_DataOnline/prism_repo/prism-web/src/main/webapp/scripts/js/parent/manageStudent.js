	/**
	 * This js file is to Find Students
	 * Author: Tata Consultancy Services Ltd.
	 * Version: 1
	 */
$(document).ready(function() {
		if($("#studentTable").length > 0) {
			$('#report-list').tablesorter({
				headers: {
					4: { sorter: false }
				},
				sortList: [[0,1]]
			});
			$("ul#shortcuts li").removeClass("current");
			$(".shortcut-agenda").parent().addClass("current");
			$('.clearfix').addClass('menu-hidden');
			$('.view-Assessment').live("click", function() {
				regenerateAC = false;
				openModalToViewAssessments($(this).attr("id"));		
			});	
			//$('input#lastStudentId').val("");//clearing the value on load.
			
			//========================SCROLL FOR THE STUDENT TABLE===============================	
			var tempScrollTop, currentScrollTop = 0
			//$("#studentTable").scroll(function() {
			$("#moreStudent").click(function(e) {
				e.stopPropagation();
				currentScrollTop = $("#studentTable").scrollTop();
				//if (tempScrollTop < currentScrollTop) {
				if(!$(this).hasClass("disabled")) {
					blockUI();
					var lastid ="scrollId=" + $('input#lastStudentId').val() + "&AdminYear=" + $("#AdminYear").val()+"&searchParam="+$("#searchStudent").val(); 
					//showLoader(currentScrollTop);//This method is defined in usermodule.js
					$.ajax({
						type : "GET",
						url : "getStudentDetailsOnScroll.do",
						data : lastid,
						dataType : 'json',
						cache:false,
						success : function(data) {
							if (data != null && data.length > 0){
								
								getStudentDetails(false,data);//This method is defined in manageParent.js
								retainUniqueValue();  //This method is defined in usermodule.js
								//hideLoader();//This method is defined in usermodule.js
								unblockUI();
								$("#studentTable").animate({scrollTop: currentScrollTop+600}, 500);
														
							} else {
								$("#moreStudent").addClass("disabled");
								if($.browser.msie) $("#moreStudent").addClass("disabled-ie");
								//$("#moreStudent").attr("disabled");
								unblockUI();
							}
							if (data != null && data.length < 14) {
								// check if this is the last set of result
								$("#moreStudent").addClass("disabled");
								if($.browser.msie) $("#moreStudent").addClass("disabled-ie");
							}
						},
						error : function(data) {
							$.modal.alert(strings['script.common.error1']);
							//hideLoader();//This method is defined in usermodule.js
							unblockUI();
							}
					});
		
				}
				tempScrollTop = currentScrollTop;
			});
			//========================SCROLL FOR THE STUDENT TABLE ENDS===============================
					
		}
	});
		
		
var regenerateAC=false;


function openModalToViewAssessments(studentBioId) {
	var nodeid = "studentBioId=" + studentBioId;	
	blockUI();
	$.ajax({
			type : "GET",
			url : "getStudentAssessmentList.do",
			data : nodeid,
			dataType : 'json',
			cache:false,
			success : function(data) {
				unblockUI();
				if(data.status != 'Blank') {
				//=========OPEN THE MODAL========
				$("#studentModal").modal({
					title: 'Student Assessment Details',
					//height: 320,
					//width: 350,
					resize:true,
					draggable: false,
					onOpen:buildAssessmentTableDom(data,"studentModal","studentModalContainer"),
					buttons: {
							'Close': {
								classes: 'glossy',						
								click: function(win) {
									win.closeModal();
								}
							},
							'Re-set Activation Code': {
								classes: 'orange-gradient glossy',						
								click: function(win) {
									confirmRecreationAC(1);
								}
							},
							'Create Letter':{
								classes: 'blue-gradient glossy createLetter',
								click: function(win) {
									window.open('download.do'+'?type=pdf'+'&token=0&reportUrl=/public/PN/Reports/Invitation_Pdf_files&drillDown=true&assessmentId=105_InvLetter&p_Student_Bio_Id='+studentBioId);
								}
							}
					}
				});
				} else {
					$.modal.alert(strings['script.student.blankAssessment']);
				}
			},
			error : function(data) {
				$.modal.alert(strings['script.common.error1']);
				unblockUI();
			}
		});
			
	}

	


//==========================CREATES THE TABULAR STRUCTURE FOR ASSESSMENT VIEW=======================
function buildAssessmentTableDom(jsonData,modalId,modalContainerDivId)
{	
	var rowCounter=1;
	$("#"+modalId +" > "+"#"+modalContainerDivId + ">" +"p.message").remove();
	$("#"+modalId +" > "+"#"+modalContainerDivId ).find("table").remove();
	var makeViewAssessmentTableDom = '<table id="assessmentTable" class="table " style="width:940px">'
									+'<thead class ="table-header glossy ">'
									+'<tr >'
									+'<th scope="col" class="blue-gradient glossy"><span class="white">Available Assessments</span></th>'
									+'<th scope="col" class="blue-gradient glossy"><span class="white">Activation Code</span></th>'
									//+'<th scope="col" class="blue-gradient glossy"><span class="white">Status</span></th>'
									+'<th scope="col" class="blue-gradient glossy"><span class="white">Available AC Claims</span></th>'
									+'<th scope="col" class="blue-gradient glossy"><span class="white">Expiration Date</span></th>'
									+'<th scope="col" class="blue-gradient glossy"><span class="white">Action</span></th>'
									+'</tr>'
									+'</thead>'
									+'<tbody>';
	$.each(jsonData, function () { 
	
		makeViewAssessmentTableDom += '<tr>'
												+'<td name="administration'+rowCounter+'" id="administration'+rowCounter+'" >'+this.administration +'</td>'
												+'<td name="invitationcode'+rowCounter+'" id="invitationcode'+rowCounter+'">'+this.invitationcode +'</td>'
												//+'<td name="icExpirationStatus'+rowCounter+'"  id="icExpirationStatus'+rowCounter+'">'+this.icExpirationStatus +'</td>'
												+'<td><span class="number input margin-right">'
												+'<button type="button" class="button number-down">-</button>'
												+'<input  type="text" size="8" name="totalAvailableClaim'+rowCounter+'" id="totalAvailableClaim'+rowCounter+'"  class="input-unstyled" value='+this.totalAvailableClaim +' data-number-options="{&quot;min&quot;:0,&quot;increment&quot;:1,&quot;shiftIncrement&quot;:1,&quot;precision&quot;:1}" >'
												+'<button type="button" class="button number-up">+</button>'
												+'</span></td>'
												+'<td><span class="input"><span class="icon-calendar"></span>'
												+'<input  type="text" name="expirationDate'+rowCounter+'"  id="expirationDate'+rowCounter+'" style="width:200px" class="input-unstyled datepicker" value="'+ new Date(this.expirationDate).toLocaleDateString()+'">'
												+'<input type="hidden" name="studentBioIdAss'+rowCounter+'"  id="studentBioIdAss'+rowCounter+'" value="'+this.studentBioId+'" />'
												+'</span></td>'
												+' <td><button class="button blue-gradient glossy mid-margin-left btnSaveViewAssessment" type="button" rowCounter="'+rowCounter+'">Save</button></td>'
												+'</tr>';
		
		     rowCounter	++;			
				
						});
	
	makeViewAssessmentTableDom += '</tbody></table>';
	$("#"+modalId+ " > "+"#"+modalContainerDivId ).append(makeViewAssessmentTableDom);	
		
	if(regenerateAC) {
		$("#invitationcode"+globalcounter).addClass("orange-bg");
		$("#invitationcode"+globalcounter).css('box-shadow', '0 0 30px orange');
		$(".createLetter").css('box-shadow', '0 0 15px blue');
		notify('Activation Code Refreshed', 'The old activation code will no longer be linked to that student\'s results. Parents and family will no longer be able to view results, although the account is still active. <br/><br/>Please refresh \'Manage Student\' screen to view updated \'Parent User ID\' column.', {
			autoClose: true,
			closeDelay:10000,
			delay: 100,
			showCloseOnHover:false,
			textOneSimilar:'Activation Code Regenated!',
			textSeveralSimilars:'Activation Code Regenated!',
			icon: 'themes/acsi/img/demo/icon.png',
			onDisplay: function() {
				$(".createLetter").css('box-shadow', '0 0 15px blue');
			}
		});
	}	

}

//======================================= REGERATE Activation Code =========================
var globalcounter = 0;
function confirmRecreationAC(rowcounter)
{
	globalcounter = rowcounter;
	$.modal.confirm('Re-setting the activation code should be used cautiously. Once the code is re-set, the old activation code will no longer be linked to that student\'s results. Parents and family will no longer be able to view results, although the account is still active. Do you want to continue?', function()
	{
		blockUI();
		var studentBioId = $("#studentBioIdAss"+globalcounter).val();
		var adminYear = $("#administration"+globalcounter).text();
		var invitationCode = $("#invitationcode"+globalcounter).text();
		var data = "studentBioId="+studentBioId+"&adminYear="+adminYear+"&invitationCode="+invitationCode;
		$.ajax({
			type : "GET",
			url : "regenerateActivationCode.do",
			data : data,
			dataType : 'json',
			cache:false,
			success : function(data) {
				$("#studentModal").closeModal();
				openModalToViewAssessments(studentBioId);
				regenerateAC=true;
				unblockUI();
			},
			error : function(data) {
				$.modal.alert(strings['script.common.error1']);
				unblockUI();
			}
		});

	}, function()
	{
		// do nothing
	});
};

//=======================================ENABLING THE SAVE ACTION IN THE VIEW ASSESSMENT RFOW WISE=========================
	$(".btnSaveViewAssessment").live("click",function(event){
		event.stopImmediatePropagation();
		var rowCounter=$(this).attr("rowCounter");
		var date = new Date ($("#expirationDate"+rowCounter).val());
		var today = new Date();
		if(validateDate(date) != "NotValid")
		{
			 var mmddyyyy= validateDate(date);
			 saveAssessmentDetails($("#studentBioIdAss"+rowCounter).val(),$("#administration"+rowCounter).text(),$("#invitationcode"+rowCounter).text(),$("#icExpirationStatus"+rowCounter).text(),$("#totalAvailableClaim"+rowCounter).val(),mmddyyyy);
		}
		else
		{
			$.modal.alert(strings['script.student.expirationDate']);
		}
		
	});

//======================================================DATE VALIDATION FOR ASSESSMENT===================================
	function validateDate(dateObj)
	{	
		var today = new Date();
		var mmddyyyy= "";
		if(dateObj >= today)
		{
			 if( ((dateObj.getMonth()+1)%10 ) == (dateObj.getMonth()+1))
			 {
				 mmddyyyy += '0'+(dateObj.getMonth()+1)+'/';
			 }
			 else 
			 {
				 mmddyyyy += (dateObj.getMonth()+1)+'/';
			 }
			
			if( (dateObj.getDate() %10 )== dateObj.getDate())
				{
					mmddyyyy += '0'+ dateObj.getDate()+'/';
				}
			else
				{
					mmddyyyy += dateObj.getDate()+'/';
				}
				mmddyyyy += dateObj.getFullYear();
			return 	mmddyyyy;	
		}
		
		else
		{
			return "NotValid";
		}
	}
//============================================ ENABLING DATE PICKER ================================================
		
		$('.datepicker').live("focus",function ()	{
			showSelectedDate('.datepicker',$(this).attr("id"),$(this).val());
			});
			
			
			
			
			function showSelectedDate(classSelector,id,dateObjString)
			{
					$("#"+id).glDatePicker({ 
					zIndex: 100,
					startDate:new Date(dateObjString),
					selectedDate:dateObjString,
					onChange:function()
								{	
														
									$("#"+id).val(new Date($("#"+id).val()).toLocaleDateString());
									
								}				
					});	
			}
			
			
			/*	$('.gldp-days').live("click",function ()	{
			datePick(".datepicker",$('.datepicker').val());
				});
		
			function datePick(classSelector,dateObjString)
			{
				alert("hii");
				$(classSelector).glDatePicker({ 
					startDate:new Date(),
					selectedDate:dateObjString,
					allowOld:false
					});
			}*/
			
//======================================================UPDATES THE ASSESSMENT DETAILS IN THE DATABASE==========================
			
			function saveAssessmentDetails(studentBioId,administration,invitationcode,icExpirationStatus,totalAvailableClaim,expirationDate)
			{
							
					$.ajax({
							type : "GET",
							url : 'updateAssessmentDetails.do',
							data : "&studentBioId="+studentBioId+"&administration="+administration+"&invitationcode="+invitationcode+"&icExpirationStatus="+icExpirationStatus+"&totalAvailableClaim="+totalAvailableClaim+"&expirationDate="+expirationDate,
							dataType: 'html',
							cache:false,
							success : function(data) {
								var obj = jQuery.parseJSON(data);
								if(obj.status == 'Success') {
									$.modal.alert(strings['script.student.updateSuccess']);				
									
								} else {
									$.modal.alert(strings['script.user.saveError']);
								}
							},
							error : function(data) {
								$.modal.alert(strings['script.user.saveError']);
							}
						});

			}
			
			//====================================AJAX CALL TO FETCH ALL THE STUDENTS FOR THE SELECTED ORG====================
			function fetchAllStudents(id,studentBioId,isRedirectedTree,url)
			{
				blockUI();
				var nodeid = "scrollId="+id+"&studentBioId="+studentBioId+"&isRedirectedTree="+isRedirectedTree+ "&AdminYear=" + $("#AdminYear").val();
				$("tbody#student_details").find("tr").remove();
				$("#report-list").trigger("update");
				$("tbody#student_details").addClass("loader big");
				$.ajax({
					type : "GET",
					url : url,
					data : nodeid,
					dataType : 'json',
					cache:false,
					success : function(data) {
						if (data != null && data.length > 14){
							$(".pagination").show(200);
							$("#moreStudent").removeClass("disabled");
						} else {
							$(".pagination").hide(200);
						}
						$("span#showOrgNameStudent").text('Students of '+$("a.jstree-clicked").text())
						getStudentDetails(true, data);
						enableSorting(true);
						$("#isRedirectedTree").val("No");
						$("tbody#student_details").removeClass("loader big");				
						if (data != null && data.length > 14){
							$("#moreParent").removeClass("disabled");
							if($.browser.msie) $("#moreParent").removeClass("disabled-ie");
						} else {
							$("#moreParent").addClass("disabled");
							if($.browser.msie) $("#moreParent").addClass("disabled-ie");
						}
						unblockUI();
					},
					error : function(data) {
						unblockUI();
						$("tbody#student_details").removeClass();	
						$.modal.alert(strings['script.org.error']);
					}
				});
			}