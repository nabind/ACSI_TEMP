/**
 * This js file is to manage parent module
 * Author: Tata Consultancy Services Ltd.
 * Version: 1
 */

var isInvCoveValid = false;

//Added by Ravi for Claim New Invitation
var firstSubmit = true;
//End

$(document).ready(function() {
	//associateCustomFunction(step);
	$("#manageProfile").validationEngine();	
	$("#registrationForm").validationEngine();
	$("#changePasswordFrom").validationEngine();
	$("#mandatoryValidation").hide(500);
	
	// load children list at parent home page, after the page loading
	if ($(".children-list").size()>0){
		refreshChildrenList();
		$("ul#shortcuts li").removeClass("current");
		$(".shortcut-dashboard").parent().addClass("current");
	}
	
	$("#myaccount").live("click", function() {
		location = "myAccount.do";
	});
	
	// select manage shortcut in menu for my account
	if ($(".manageAccount").size()>0){
		if($.browser.msie) {
			$('.label').css('width', '200px');
		}
		refreshChildrenList();
		$("ul#shortcuts li").removeClass("current");
		$(".shortcut-stats").parent().addClass("current");
	}
	
	//=================== SAVE MY PROFILE =======================
	$('.save-Profile').live("click", function() {
		if($("#manageProfile").validationEngine('validate')) {
			blockUI();
			
			if( $("#firstName").val() != "" && $("#lastName").val() != "" && $("#verify_mail").val() != ""
				&& $("#ans1").val() != "" && $("#ans2").val() != "" && $("#ans3").val() != "" ) {
				$('#manageProfile').validationEngine('hide');
				$("#mandatoryValidation").hide(500);
				
				if ( ($("span.qsn1").children(".select-value").text()==$("span.qsn2").children(".select-value").text())
					||($("span.qsn1").children(".select-value").text()==$("span.qsn3").children(".select-value").text())||($("span.qsn2").children(".select-value").text()==$("span.qsn3").children(".select-value").text())
					){
					unblockUI();
					$.modal.alert(strings['script.parent.question']);
				}
				else{
					saveParentProfileInfo($(".manage-Profile-form"));
				}	
			}
			else{
				$("#mandatoryValidation").show();
				unblockUI();
			}
		} else {
			$('#manageProfile').validationEngine('show');
		}
	});	
	
	// -------------------------- reset invitation code -------------------------
	$("#invitationCode").keypress(function() {
		isInvCoveValid = false;
		$("#invalidICMsg").hide(500);
	});
	
	// -------------------------- code to align wizard -------------------------
	$("#completed").live('click', function() {
		$("#command").css('margin-top', '0px');
	});
	$("#wizard-previous").live('click', function() {
		$("#command").css('margin-top', '0px');
	});
	$("#wizard-next").live('click', function() {
		$("#command").css('margin-top', '0px');
	});
	
	//Added by Ravi for Claim New Invitation
	/*$('.claim-Invitation').live("click", function() {
		$("input#invitationCode").val("");
		$("#invalidICMsg").hide();
		firstSubmit=true;
		openNewInvitationClaimModal();	
	});*/
	$('.claim-Invitation').click(function() {
		$("input#invitationCode").val("");
		$("#invalidICMsg").hide();
		firstSubmit=true;
		openNewInvitationClaimModal();	
	});
	//End
	
	//============================= OPEN clildren home page report on-clicking menu =============================
	$(".child_report_menu").live("click", function() {
		var loc = $(this).attr("href");
		location.href = loc;
	});
});
	// *********** END DOCUMENT.READY ************

	//============================= Function to fetch ChildrenList =============================
	function refreshChildrenList() {
		$(".children-list").addClass("loader big");
		$.ajax({
			type:"GET",
			url:"getChildrenList.do",
			dataType:'json',
			cache:false,
			success:function(data){
				if(data != null) {
					var userContent = '';
					var menuContent = '';
					$.each(data, function () { 
						userContent += '<a href="childData.do?childId='+this.studentBioId+'" style="color: #fff; font-weight: bold">'
									+ this.studentName + '</a><br/>'
									+ this.administration + ', Grade: ' +this.grade + '<br/><br/>';
						
						menuContent += '<li class="mid-margin-left font-12 small-line-height"><a class="child_report_menu" href="childData.do?childId='+this.studentBioId+'"> '+this.studentName+' (Grade: '+this.grade+')</a></li>';
					});
					$(".children-list").html(userContent);
					$(".children-list").removeClass("loader big");
					
					// update menu
					$("#child_list").html(menuContent);
				}else{
					var userContent = '<div>No child is associated to your account.</div>';
					$(".children-list").html(userContent);
					$(".children-list").removeClass("loader big");
				}
			},
			error:function(data){
				$.modal.alert(strings['script.parent.getChildrenError']);
				$(".children-list").removeClass("loader big");
			}
			
		});
	}

	//============================= Registration next button click =============================
	function associateCustomFunction(step) {
		if(step == 0) {//1
			// * Moved to wizard.register.js
			//removePreviousChild();
			//getChildList();
		} 
		else if(step == 1) {//2
			CheckUserNameAvailability("input#username");
			//$("#registrationForm").validationEngine('hide');
			//removePromptOnBack();
		}
		else if(step == 2) {//3
			if($("input#.ABC").length>0) {
				$.modal.alert(strings['script.user.duplicateUserName'])
				stepBack(1);
			}
			
			//$("#registrationForm").validationEngine('hide');
			//validatePwd($("#registrationForm input#password"),$("#registrationForm input#username"),$("#registrationForm"));
			//removePromptOnBack();
		}
		else if(step == 3) {//4
			//$("#registrationForm").validationEngine('hide');
			//removePromptOnBack();
			
		}
		else if(step == 4) {
			//$("#registrationForm").validationEngine('hide');
			//removePromptOnBack();
		}
		
		$("#command").css('margin-top', '0px');
	}
	//============================= Registration First Next button click =============================
	function removePreviousChild() {
		$("#ic_student_list").find("p").remove();
	}
	//============================= Get Child List for Invitation Code ================================
	function getChildList() {
		var ic = "invitationCode=" + $("input#invitationCode").val();
		var student_details = "";
		blockUI();
		$.ajax({
			type : "GET",
			url : "regn/validateCode.do",
			data : ic,
			dataType : 'json',
			cache:false,
			success : function(data) {
				unblockUI();
				if(data[0].errorMsg == "NA" ) {		
					$("#invalidICMsg").hide();
					isInvCoveValid = true;
					var studentList = data[0].studentToList;
					$.each(studentList, function(index, value) { 
						student_details += '<p class="margin-bottom" id="student_info">'
											+'<strong>'+ studentList[index].studentName+'</strong>'
											+'<br>'+studentList[index].grade
											+'<br>'+studentList[index].administration
											+'</p>'; 
					});
					$("#ic_student_list").html(student_details);
					$("#displayChild").show();
					$("#displayInvitation").hide();
					firstSubmit=false;
					$.each( $('.wizard-next'), function(index, value) { 
						if(index == 0) $(this).click();
					});
				} else if (data[0].errorMsg == "IC_INVALID" ) {
					isInvCoveValid = false;
					$("#invalidICMsg").show(500);
					$("#invalidICMsg").text("The code you have entered is not valid. Make sure you have entered exactly same code as you received in letter.");
					$("#displayChild").hide();
					$("#displayInvitation").show();
				} else if (data[0].errorMsg == "IC_NOTAVAILABLE" ) {
					isInvCoveValid = false;
					$("#invalidICMsg").show(500);
					$("#invalidICMsg").text("The code you have entered has reached its maximum number of claims.");
					$("#displayChild").hide();
					$("#displayInvitation").show();
				} else if (data[0].errorMsg == "IC_EXPIRED" ) {
					isInvCoveValid = false;
					$("#invalidICMsg").show(500);
					$("#invalidICMsg").text("The code you have entered is expired.");
					$("#displayChild").hide();
					$("#displayInvitation").show();
				} else {
					isInvCoveValid = false;
					$("#invalidICMsg").show(500);
					$("#invalidICMsg").text("The code you have entered is not valid. Make sure you have entered exactly same code as you received in letter.");
					$("#displayChild").hide();
					$("#displayInvitation").show();
				}
			},
			error : function(data) {
				unblockUI();
				isInvCoveValid = false;
				$("#displayChild").hide();
				$("#displayInvitation").show();
			}
		});
	}
	
	//============================= Return If Code validation is success ================================	
	function isInvCoveValid() {
		return isInvCoveValid;
	}
	
	//============================= Check Availability of the username on blur ================================	
	function CheckUserNameAvailability(fieldId) {
		$("#imgHolder").find("span").remove();
		$(fieldId).blur(function(e) {
			e.stopImmediatePropagation();
			var pattern = /^[0-9a-zA-Z]+$/g;
		    var username = "username=" + $(fieldId).val();
			if($(fieldId).val().replace(/ /g,'').length > 0 && pattern.test($(fieldId).val())) {
				 showLoadingImage();
				  $.ajax({
						type : "GET",
						url : "regn/checkusername.do",
						data : username,
						dataType : 'json',
						cache:false,
						success : function(data) {
							unblockUI();
							//alert(data.available);
							if (data.available == "true") {
								removeLoadingImage();
								showAvailability();
								return true;
							}
							else if (data.available == "false") {
								removeLoadingImage();
								showUnAvailability();
								return false;
							}
						},
						error : function(data) {						
							unblockUI();
							showUnAvailability();
							return false;
						}
					});
				}/*
				else {
					$("span#imgHolder").html('<!--<img src="themes/acsi/img/standard/loaders/loading16.gif">-->');
					$("span#imgHolder").html('<span class="icon-user icon-size2 icon-red" style="color: red;">Please provide valid username</span>');
				}*/
		});	
		
	}
	//Show Loading image
	function showLoadingImage(){
	   $("span#imgHolder").html('<img src="themes/acsi/img/standard/loaders/loading16.gif">');	
	}
	//Remove Loading image	
	function removeLoadingImage(){
	   $("span#imgHolder").html('');
	}
	//Show if userName is available	
	function showAvailability(){
		$("span#imgHolder").html('<span id="validated" class="validated" style="color: green;">Username available.</span>');	
		//$("button.wizard-next").removeAttr("disabled"); 
		$("input#username").removeClass("ABC");
	}
	//Show if userName is not available	
	function showUnAvailability(){
		$("span#imgHolder").html('<span class="" style="color: red;">Username already present. Please choose other.</span>');
		//$("button.wizard-next").attr("disabled", "disabled");
		$("input#username").addClass("ABC");
	}
	

	
	//Remove the alert prompt of User Profile
	function removePromptOnBack()
	{
			$("button.wizard-previous").click(function(){
			$(".formErrorContent").remove();
			$(".formErrorArrow").remove();
			
		});
	}
	
	//Added by Ravi for Manage Profile
	function saveParentProfileInfo(form) {
		//alert(form.serialize());
		$.ajax({
			type : "POST",
			url : 'updateProfile.do',
			data : form.serialize(),
			dataType : 'html',
			cache:false,
			success : function(data){
				unblockUI();
				var obj = jQuery.parseJSON(data);
				if(obj.status == 'equalsUserName'){
					$.modal.alert(strings['script.user.passwordLikeUsername']);	
				}
				else if(obj.status == 'invalidPwd') {
					$.modal.alert(strings['script.user.passwordPolicy']);
				}
				else if(obj.status == 'LDAP_ERROR') {
					$.modal.alert(obj.message);
				}
				else if(obj.status == 'Success') {
					$.modal.alert(strings['script.student.updateSuccess']);
				}
				else{
					$.modal.alert(strings['script.user.saveError']);
				}
				
			},
			error : function(data){
				unblockUI();
				$.modal.alert(strings['script.user.saveError']);
			}
		});
	}
	
	//Added by Ravi for Claim New Invitation
	function openNewInvitationClaimModal(){
	$("#newICModal").modal({
		title: 'Enter Activation Code',
		height: false,
		width: 670,
		resizable: false,
		draggable: false,
		buttons: {
			'Cancel': {
				classes: 'glossy',
				click: function(win) {
					win.closeModal(); 
				}
			},
			'Submit': {
				classes: 'blue-gradient glossy',
				click: function(win) {
					// Submit clicked for the first time for validating IC
					if(firstSubmit){
						getChildList();
					}
					// Submit clicked for the second time for verify child info and insert an entry in invitation_code_claim table
					else{
						var invCode = $("input#invitationCode").val();
						verifyChildInfoAndSaveIC(invCode, win);
					}

				}
			}
		}
	});		
	$("#displayChild").hide();
	$("#displayInvitation").show();
	}
	//End
	
	//Added by Ravi for Claim New Invitation
	function verifyChildInfoAndSaveIC(invCode, win) {
		blockUI();
		//alert(form.serialize());
		var dataURL = "invitationCode="+invCode;
		$.ajax({
			type : "GET",
			url : 'claimInvitation.do',
			data : dataURL,
			dataType: 'html',
			cache:false,
			success : function(data) {
				unblockUI();
				var obj = jQuery.parseJSON(data);
				win.closeModal(); 
				if(obj.status == 'Success') {
					$.modal.alert(strings['script.parent.invitationCode']);
					
					//calling ajax to refresh children list
					refreshChildrenList();
					
				} else {
					$.modal.alert(strings['script.parent.alreadyClaimed']);
				}
			},
			error : function(data) {
				unblockUI();
				$.modal.alert(strings['script.common.error1']);
			}
		});
	}
	//End
	
	

	//========================CHANGE PASSWORD FORM SUBMISSION=====================	
	$("#saveChangedPassword").click(function(e){
		e.stopImmediatePropagation();
		$("#loggedInUserName").val($("#changePasswordFrom #currUser").text());
		//alert($("#changePasswordFrom #currUser").text());
		validatePwd($("#changePasswordFrom input#password"),$("#changePasswordFrom input#loggedInUserName"),$("#changePasswordFrom"), '0');
		//$("#changePasswordFrom").submit();
	});	
//========================REGISTRATION FORM SUBMISSION=====================	
	$("#regSubmit").click(function(e){
		e.stopImmediatePropagation();
		validatePwd($("#registrationForm input#password"),$("#registrationForm input#username"),$("#registrationForm"));
		//$("#registrationForm").submit();
	});
//=============================PREVENTING REGISTRATION FORM SUBMIT ON ENTER===========================	
	$('.noEnterSubmit').keypress(function(e){
        if ( e.which == 13 ) e.preventDefault();
		});
		
//=============================AJAX CALL TO VALIDATE PASSWORD===========================			
	function validatePwd(pwdObj,useNameObj,formObj, pwdPage)
		{
			if(formObj.validationEngine('validate')){
			 blockUI();
			}
			var patt=/^(\d+.*|-\d+.*)/g;
			var password=pwdObj.val();
			var username=useNameObj.val();
			if(password.indexOf(username) != -1) {
				unblockUI();
				$.modal.alert(strings['script.user.passwordPartUsername']);
			} else if (patt.test(username)) {
				unblockUI();
				$.modal.alert(strings['script.user.useridStartNumber']);
			}else {
				$.ajax({
					type : "GET",
					url : 'validatePwd.do',
					data : 'password='+password+'&username='+username,
					contentType: "application/json",
					dataType : 'html',
					cache:false,
					success : function(data) {
						
						var obj = jQuery.parseJSON(data);
					
						if (obj.status == 'equalsUserName')	{
							$.modal.alert(strings['script.user.passwordLikeUsername']);
							stepBack(2);
							if(pwdPage != null) stepBack(2);
							unblockUI();
						}
						else if(obj.status == 'invalidPwd'){
							$.modal.alert(strings['script.user.passwordPolicy']);
							stepBack(2);
							if(pwdPage != null) stepBack(2);
							unblockUI();
						}
						else if(obj.status == 'LDAP_ERROR'){
							$.modal.alert(obj.message);
							stepBack(2);
							if(pwdPage != null) stepBack(2);
							unblockUI();
						}
						else if(obj.status == 'Success') {
						   	if (validateSecurityQuestion()){
								formObj.submit();
							}else{
								$.modal.alert(strings['script.parent.question']);
								unblockUI();
							}
								
						} else {
							$.modal.alert(strings['script.user.saveError']);
							stepBack(2);
							if(pwdPage != null) stepBack(2);
							unblockUI();
						}
					},
					error : function(data){
						unblockUI();
						$.modal.alert(strings['script.common.error1']);
						
					}
				});
			}		
   }
	
	function stepBack(step) {
		$.each( $('.wizard-previous'), function(index, value) { 
		if(index < step) {
			//alert("index="+index+",step="+step);
			$(this).click();			
		}
		});
		
	}
	//=======================DISABLES THE ERROR PROMTS WHILE CLICKING THE BACK BUTTON==============
	$("button.wizard-previous").live("click",function(){
		$("#registrationForm").validationEngine('hide');
		$("#changePasswordFrom").validationEngine('hide');
		//$("button.wizard-next").removeAttr("disabled"); 
	});
	

	
	//=======================DISABLES THE ERROR PROMTS WHILE TRAVERSING BACK CLICKING THE WIZARD TABS==============
	$("ul.wizard-steps li.completed").live("click",function(e){
			//e.stopImmediatePropagation();
		$("#registrationForm").validationEngine('hide');
		$("#changePasswordFrom").validationEngine('hide');
	});
	//================================VALIDATES WHETHER ALL THE THREE DIFFERENT QUESTIONS ARE DIFFERENT============
	function validateSecurityQuestion(){
		if ( ($("span.qsn1").children(".select-value").text()==$("span.qsn2").children(".select-value").text())
				||($("span.qsn1").children(".select-value").text()==$("span.qsn3").children(".select-value").text())||($("span.qsn2").children(".select-value").text()==$("span.qsn3").children(".select-value").text())
				){ 
				 return false;
				 }
				 else {
				 return true;
				 }
	}