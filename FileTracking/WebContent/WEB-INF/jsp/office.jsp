<%@page import="javax.servlet.http.HttpServletRequest" %>
<%@page import="com.vaannila.TO.OrgProcess" %>
<%@page import="com.vaannila.TO.AdminTO" %>
<%@page import="java.util.ArrayList" %>
<%@page import="com.vaannila.web.UserController" %>
<%@page import="java.util.Properties" %>
<%@page import="com.vaannila.util.PropertyFile" %>

<script src="css/jquery.validate.js"></script>
	
	<script type="text/javascript" charset="utf-8">
	

	
	function update_rows()
	{
	    $(".process_details tr:even").css("background-color", "#fff");
	    $(".process_details tr:odd").css("background-color", "#eee");
	}
	
		$(document).ready(function() {
			 update_rows();
			 
			oTable = $('#process').dataTable({
				"bJQueryUI": true,
				"sPaginationType": "full_numbers",
				//"aaSorting": [[ 1, "desc" ]],
				"aoColumnDefs": [ 
								  { "bSortable": false, "aTargets": [ 0,3,4,12 ] },
								  { "bVisible": false, "aTargets": [ 0 ] }
								]
			});
			
			//$("a[rel^='prettyPhoto']").prettyPhoto();
			
			$("#dialog_details").dialog({
				bgiframe: true, 
				autoOpen: false, 
				modal: false, 
				minHeight: 50, 
				minWidth: 500, 
				closeOnEscape: true, 
				resizable: false
			});
			
			$("#updateEmailBox").dialog({
				bgiframe: true, 
				autoOpen: false, 
				modal: false, 
				minHeight: 50, 
				minWidth: 400, 
				modal: true, 
				resizable: false
			});
			
			$("#processLogDialog").dialog({
				bgiframe: true, 
				autoOpen: false, 
				modal: true, 
				height: 500, 
				minWidth: 450, 
				closeOnEscape: true, 
				resizable: true
			});
			
			
			
		} );
		
		
		
		function getProcessDetails(id, name, pdfPresent, letterPresent) {
			var str = name;
			var substr = str.split('_');
			var pdfStatus = substr[0];
			var processId = substr[1];
			resetProcessDetails(processId);
			jQuery("#dialog_details").dialog("open");
			var dataString = "structElem="+id;
			
			$.ajax({
			      type: "POST",
			      url: "processDetails.htm",
			      data: dataString,
			      success: function(data) {
			        //alert('session refreshed !! '+data.process.elementName);
			        updateProcessDetails(data, processId, pdfStatus, pdfPresent, letterPresent);
			      },
				  error: function(data) {
					 alert('Error');
				  }
		    });
		    return false;
		}
		
		function openEmailEdit(processId) {
			clearError();
			$("#school_process_id").val(processId);
			jQuery('#updateEmailBox').dialog('open'); 
		}
		
		
		function updateProcessDetails(data, processId, pdfStatus, pdfPresent, letterPresent) {
			//alert(data.process.processNotExist == true);
			if(data.process.processNotExist == true) {
				$("#_error_message").css('display', 'block');
				$("#_error_message").text( 'No data is available at this point for process # '+processId+'. Stage data load is in-progress. Please try again after operational data load completes. ' );
				resetProcessDetailsToBlank();
			} else {
				$("#_school_name").text( data.process.elementName );
				$("#email_school_name").text( data.process.elementName );
				
				$("#_region_name").text( data.process.regionName );
				
				$("#sender_email_address").val( data.process.email );
				
				<% if(UserController.checkAdmin(request)) { %>
				$("#_school_email").html( data.process.email + "&nbsp;&nbsp;<a href='#note' class='updateEmail buttonLink' onClick='openEmailEdit("+processId+");'>Edit</a>" );
				<% } else { %>
				$("#_school_email").html( data.process.email );
				<% } %>
				$("#old_school_email").text( data.process.email );
				
				
				$("#_customer_code").text( data.process.customerCode );
				$("#_structure_element").text( data.process.structureElement );
				$("#_class_count").text( data.process.classCount );
				$("#_stud_count").text( data.process.studCount );
				$("#_user_count").text( data.process.userCount );
				if(pdfStatus == 'Success' && pdfPresent) {
				$("#_pdf_name").html( '<a href="viewPdf.htm?type=L&processId='+processId+'" target="_blank" name="processId" class="regularLink">View/Download</a>' );
				} else {
				$("#_pdf_name").html( 'No PDF is generated for this process.' );
				}
				
				if(letterPresent) {
					$("#_letter_name").html( '<a href="viewPdf.htm?type=I&processId='+processId+'" target="_blank" name="processId" class="regularLink">View/Download</a>' );
					} else {
					$("#_leter_name").html( 'No Invitation Letter is generated for this process.' );
				}
				
				
				<% if(UserController.checkAdmin(request)) { %>
				if(pdfStatus == 'Success' && pdfPresent) {
					$("#_resend_pdf").html( '<a class="_resend_pdf buttonLink" href="#" onClick="sendEmail(this.id, \'L\');" name="processId" id="'+processId+'">Resend login email</a> to [<span class="_to_email">'+data.process.email+'</span>]' );
				}
				if(letterPresent) {
					$("#_resend_letter").html( '<a class="_resend_letter buttonLink" href="#" onClick="sendEmail(this.id, \'I\');" name="processId" id="'+processId+'">Resend invitation letter</a> to [<span class="_to_email">'+data.process.email+'</span>]' );
				}
				if(pdfStatus == 'Success' && pdfPresent && letterPresent) {
					$("#_resend_both").html( '<a class="_resend_both buttonLinkGreen" href="#" onClick="sendEmail(this.id, \'A\');" name="processId" id="'+processId+'">Resend Both PDFs</a> to [<span class="_to_email">'+data.process.email+'</span>]' );
				}
				<% } %>
			}
		}
		
		function resetProcessDetails(processId) {
			//alert('session refreshed !! '+data.process.elementName);
			$("#_error_message").css('display', 'none');
			$("#ui-dialog-title-dialog_details").html('Process Id : '+processId);
			$("#sender_email_address").val( '' );
			$("#_school_name").html( '<img src="css/ajax-loader.gif"></img>' );
			$("#_region_name").html( '<img src="css/ajax-loader.gif"></img>' );
			$("#_school_email").html( '<img src="css/ajax-loader.gif"></img>' );
			$("#_customer_code").html( '<img src="css/ajax-loader.gif"></img>' );
			$("#_structure_element").html( '<img src="css/ajax-loader.gif"></img>' );
			$("#_class_count").html( '<img src="css/ajax-loader.gif"></img>' );
			$("#_stud_count").html( '<img src="css/ajax-loader.gif"></img>' );
			$("#_user_count").html( '<img src="css/ajax-loader.gif"></img>' );
			
			$("#_pdf_name").html( '<img src="css/ajax-loader.gif"></img>' );
			$("#_letter_name").html( '<img src="css/ajax-loader.gif"></img>' );
			$("#_resend_pdf").html( '' );
			$("#_resend_letter").html( '' );
			$("#_resend_both").html( '' );
			$("#_resend_pdf_progress").html( '' );
		}
		function resetProcessDetailsToBlank() {
			//alert('session refreshed !! '+data.process.elementName);
			$("#sender_email_address").val( '' );
			$("#_school_name").html( '' );
			$("#_region_name").html( '' );
			$("#_school_email").html( '' );
			$("#_customer_code").html( '' );
			$("#_structure_element").html( '' );
			$("#_class_count").html( '' );
			$("#_stud_count").html( '' );
			$("#_user_count").html( '' );
			
			$("#_pdf_name").html( '' );
			$("#_letter_name").html( '' );
			$("#_resend_pdf").html( '' );
			$("#_resend_letter").html( '' );
			$("#_resend_both").html( '' );
			$("#_resend_pdf_progress").html( '' );
		}
		
		function sendEmail(id, type) {
			var processId = id;
			var dataString = "processId="+id+"&schoolEmail="+$("#sender_email_address").val()+"&type="+type;
			var message = '';
			if(type == 'L') message = 'with Login PDF?';
			else if(type == 'I') message = 'with Invitation Letter?';
			else if(type == 'A') message = 'with both PDFs?';
			if(confirm("Do you want to resend email "+message)) {
				$("#_resend_pdf_progress").html( '&nbsp;<img src="css/ajax-loader.gif"></img>' );
				$.ajax({
				      type: "POST",
				      url: "sendMail.htm",
				      data: dataString,
				      success: function(data) {
				    	  if(data == 'Success') {
				    	  	$("#_resend_pdf_progress").css('color', 'green');
				    		$("#_resend_pdf_progress").html( ' Mail sent successfully' );
				    	  } else {
				    		  $("#_resend_pdf_progress").css('color', 'red');
							  $("#_resend_pdf_progress").html( ' Failed to send mail' );
				    	  }
				      },
					  error: function(data) {
						  	$("#_resend_pdf_progress").css('color', 'red');
							$("#_resend_pdf_progress").html( ' Failed to send mail' );
					  }
			    });
			}
		    return false;
		}
		
		function getProcessLog(processId) {
			var dataString = "processId="+processId;
			
			$("#_process_log").html( '<img src="css/ajax-loader.gif"></img>' );
			$("#ui-dialog-title-processLogDialog").html('Log for Process Id : '+processId);
			jQuery("#processLogDialog").dialog("open");
			
			
			$.ajax({
			      type: "POST",
			      url: "getProcessLog.htm",
			      data: dataString,
			      success: function(data) {
			    	  $("#_process_log").html( data );
			      },
				  error: function(data) {
					  $("#_process_log").html( ' Failed to get Process Log' );
				  }
		    });
		    return false;
		}
		
		function clearError() {
			$("#save_email_progress").html( ' ' );
		}
		function isValidEmailAddress(emailAddress) {
		    var pattern = new RegExp(/^(("[\w-+\s]+")|([\w-+]+(?:\.[\w-+]+)*)|("[\w-+\s]+")([\w-+]+(?:\.[\w-+]+)*))(@((?:[\w-+]+\.)*\w[\w-+]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$)|(@\[?((25[0-5]\.|2[0-4][\d]\.|1[\d]{2}\.|[\d]{1,2}\.))((25[0-5]|2[0-4][\d]|1[\d]{2}|[\d]{1,2})\.){2}(25[0-5]|2[0-4][\d]|1[\d]{2}|[\d]{1,2})\]?$)/i);
		    return pattern.test(emailAddress);
		}
		
		function updateEmail() {
			var processId = $("#school_process_id").val();
			if(!isValidEmailAddress($("#school_email").val())) { 
				$("#save_email_progress").css('color', 'red');
				$("#save_email_progress").html( ' Please provide a valid email address' );
				return false;
			}
			
			var dataString = "structureElement="+$("#_structure_element").text()+"&newMail="+$("#school_email").val()+"&processId="+processId+"&oldEmail="+$("#old_school_email").text();
			if(confirm("Do you want to update email?")) {
				$("#save_email_progress").html( '&nbsp;<img src="css/ajax-loader.gif"></img>' );
				$.ajax({
				      type: "POST",
				      url: "updateEmail.htm",
				      data: dataString,
				      success: function(data) {
				    	  if(data == 'Updated') {
				    	  	$("#save_email_progress").css('color', 'green');
				    		$("#save_email_progress").html( ' Mail updated successfully' );
				    		
				    		<% if(UserController.checkAdmin(request)) { %>
				    		$("#_school_email").html( $("#school_email").val()  + "&nbsp;&nbsp;<a href='#note' class='updateEmail buttonLink' onClick='openEmailEdit("+processId+");'>Edit</a>" );
				    		<% } else { %>
				    		$("#_school_email").html( $("#school_email").val() );
				    		<% } %>
				    		
				    		$("#old_school_email").text( $("#school_email").val() );
				    		$("._to_email").text( $("#school_email").val() );
				    		
				    		alert('Email address is updated in PRISM Database. Please update the same in EISS using ESOP to keep EISS in sync with PRISM.');
				    		jQuery('#updateEmailBox').dialog('close');
				    		
				    		$("#sender_email_address").val( $("#school_email").val() );
				    	  } else {
				    		  $("#save_email_progress").css('color', 'red');
							  $("#save_email_progress").html( ' Failed to update mail' );
				    	  }
				      },
					  error: function(data) {
						  	$("#save_email_progress").css('color', 'red');
							$("#save_email_progress").html( ' Failed to update mail' );
					  }
			    });
			}
		    return false;
		}
		
		$(document).ready(function() {
		var bEnabled = document.getElementById("hEnabled").value;
		if (bEnabled == 'F'){
			document.getElementById("theSelect").setAttribute("disabled", "disabled");
			//$("#theSelect option:selected").attr('disabled', 'disabled');
		}
	    });
		
	</script>
	
<div id="heromaskarticle">
	<div id="articlecontent">
	 <%   Properties prop = PropertyFile.loadProperties("acsi.properties");
			  String isEnabled = prop.getProperty("admin.year.enable");
	 %>
		<input type="hidden" value="<%=isEnabled%>" id="hEnabled">	  
		<h1>All Processes for Administration 
		<select onChange="refreshList(this.value, 'process.htm')" id="theSelect">
			<%
			java.util.List<AdminTO> adminList = (ArrayList) request.getSession().getAttribute("adminList");
			String adminyear = (String) request.getSession().getAttribute("adminid");
			for(AdminTO admin : adminList) { 
				if(adminyear != null && adminyear.equals(admin.getAdminId())) {
				%>
				<option value="<%=admin.getAdminId() %>" selected="true" ><%=admin.getAdminName() %></option>
				<%} else {%>
				<option value="<%=admin.getAdminId() %>" ><%=admin.getAdminName() %></option>
				<%} %>
			<%} %>
		</select></h1>
		<div id="accordion">
				<!-- panel -->
				
				
				<table id="process" width="100%">
				<thead>
					<tr>
						<th>&nbsp;</th>
						<th>Process Id</th>
						
						<th>School Name</th>
						<th>Region Code</th>
						<th>School Code</th>
						
						<th>School Stru-Elm</th>
						
						<th>Stage Load</th>
						<th>Stage PDF</th>
						<th>Target Load</th>
						<th>Email Status</th>
						<th>Process Status</th>
						
						<th>Processed Date</th>
						<th>Source File Name</th>
					</tr>
					</thead>
					<tbody>
				<% 
				java.util.List<OrgProcess> allProcess = (ArrayList) request.getSession().getAttribute("allProcess");
				int count=0;
				for(OrgProcess process : allProcess) {
					count++;
				%>
				
				
					<tr>
						<td>&nbsp;</td>
						<%
							/* String log = process.getProcessLog();
							boolean pdfPresent = true;
							if(log != null && log.indexOf("No New School and Teacher user found. Exiting the process.") != -1) {
								pdfPresent = false;
							} */
							boolean pdfPresent = true;
							String pdfFileLoc = process.getLoginPDFLoc();
							if(pdfFileLoc != null && pdfFileLoc.trim().length() == 0) {
								pdfPresent = false;
							}
							
							boolean letterPresent = true;
							String letterFileLoc = process.getLetterPDFLoc();
							if(letterFileLoc != null && letterFileLoc.trim().length() == 0) {
								letterPresent = false;
							}
						
						%>
						<td>
							<a href='#note' class='processDetails' style='color:#00329B; font-weight:bold;text-decoration:underline' onClick="getProcessDetails(this.id, this.name, <%=pdfPresent%>, <%=letterPresent%>);" 
								name="<%=process.getTargerEmailStatus() %>_<%=process.getProcessid() %>" 
								id="<%=process.getStructElement() %>">
									<%=process.getProcessid() %>
							</a>
						</td>
						
						<td><%=process.getElementName() %></td>
						<td><%=(process.getCustomarCode() == null) ? "" : process.getCustomarCode().substring(0,2) %></td>
						<td><%=(process.getCustomarCode() == null) ? "" : process.getCustomarCode().substring(2,9) %></td>
						
						<td><%=process.getStructElement() %></td>
						
						<td><%=process.getDataStatus() %></td>
						<td><%=process.getPdfStatus() %></td>
						<td><%=process.getTargetStatus() %></td>
						<td><%=process.getTargerEmailStatus() %></td>
						<td><a href='#note' class='noteLink' style='color:#00329B;text-decoration:underline' onclick='getProcessLog(<%=process.getProcessid() %>);'><%=process.getProcessStatus() %></a></td>
						
						<td><%=process.getUpdatedDate() %></td>
						<%
							String sourceFileName = process.getSourceFileName();
							if(sourceFileName != null && sourceFileName.length() > 16) {
								sourceFileName = sourceFileName.substring(0, 16) + " " + sourceFileName.substring(16, sourceFileName.length());
							}
						%>
						<td><%=sourceFileName %></td>
					</tr>
				
				
				<%} %>
				</tbody>
				</table>
				<input type="hidden" name="sender_email_address" id="sender_email_address">
				<!-- end panel -->
				<div id="dialog_details" title="Loading ..." style='display:none; font-size:11px'> 
					
					<table class="process_details">
						<tr>
							<td colspan="2"><span id="_error_message" style="display:none;color:red"></span></td>
						</tr>
						<tr>
							<td width="40%"><b>School Name :</b></td><td width="60%"><span id="_school_name"><img src="css/ajax-loader.gif"></img></span></td>
						</tr>
						<tr>
							<td><b>Region Name :</b></td><td><span id="_region_name"><img src="css/ajax-loader.gif"></img></span></td>
						</tr>
						<tr>
							<td><b>School email :</b></td><td><span id="_school_email"><img src="css/ajax-loader.gif"></img></span></td>
						</tr>
						<tr>
							<td><b>School Customer Code :</b></td><td><span id="_customer_code"><img src="css/ajax-loader.gif"></img></span></td>
						</tr>
						<tr>
							<td><b>School Structure Element :</b></td><td><span id="_structure_element"><img src="css/ajax-loader.gif"></img></span></td>
						</tr>
						<tr>
							<td><b>Total # of classes :</b></td><td><span id="_class_count"><img src="css/ajax-loader.gif"></img></span></td>
						</tr>
						<tr>
							<td><b>Total # of students :</b></td><td><span id="_stud_count"><img src="css/ajax-loader.gif"></img></span></td>
						</tr>
						<tr>
							<td><b>Total # of users (including teachers) :</b></td><td><span id="_user_count"><img src="css/ajax-loader.gif"></img></span></td>
						</tr>
						<tr>
							<td><b>Generated Login PDF :</b></td><td><span id="_pdf_name"><img src="css/ajax-loader.gif"></img></span></td>
						</tr>
						<tr>
							<td><b>Generated Invitation Letter PDF :</b></td><td><span id="_letter_name"><img src="css/ajax-loader.gif"></img></span></td>
						</tr>
					</table>
					<div class="boxshade">
						<p><span id="_resend_pdf">&nbsp;</span> </p>
						<p><span id="_resend_letter">&nbsp;</span> </p>
						<p><span id="_resend_both">&nbsp;</span> </p>
						<p><span id="_resend_pdf_progress" style="color:green"></span></p>
					</div>
					<!-- <p><span id="_error_message" style="display:none;color:red"></span></p>
					<p><b>School Name :</b> <span id="_school_name"><img src="css/ajax-loader.gif"></img></span></p>
					<p><b>Region Name :</b> <span id="_region_name"><img src="css/ajax-loader.gif"></img></span></p>
					<p><b>School email :</b> <span id="_school_email"><img src="css/ajax-loader.gif"></img></span></p>
					<p><b>School Customer Code :</b> <span id="_customer_code"><img src="css/ajax-loader.gif"></img></span></p>
					<p><b>School Structure Element :</b> <span id="_structure_element"><img src="css/ajax-loader.gif"></img></span></p>
					<p><b>Total # of classes :</b> <span id="_class_count"><img src="css/ajax-loader.gif"></img></span></p>
					<p><b>Total # of students :</b> <span id="_stud_count"><img src="css/ajax-loader.gif"></img></span></p>
					<p><b>Total # of users (including teachers) :</b> <span id="_user_count"><img src="css/ajax-loader.gif"></img></span></p>
					
					<p><b>Generated Login PDF :</b> <span id="_pdf_name"><img src="css/ajax-loader.gif"></img></span></p>
					<p><span id="_resend_pdf">&nbsp;</span> </p>
					<p><span id="_resend_pdf_progress" style="color:green"></span></p> -->
				</div>
				
				<div id="updateEmailBox" title="Update School Email" style='display:none; font-size:11px'> 
					<input type="hidden" name="school_process_id" id="school_process_id">
					<p><b>School Name :</b> <span id="email_school_name">school name</span></p>
					<p><b>Old Email :</b> <span id="old_school_email">old email</span></p>
					<p><b>New Email :</b> <span id="new_school_email"><input type="text" name="school_email" id="school_email" onKeyPress="clearError();"></span></p>
					<p><a id="save_email" class="buttonLink" href="#nogo" onClick="updateEmail();">Save</a>  <span id="save_email_progress">&nbsp;</span></p>
				</div>
				
				<div id='processLogDialog' title='Loading' style='display:none; font-size:10px'>
					<p id="_process_log"><img src="css/ajax-loader.gif"></img><p>
				</div>
				
			</div>

	</div>
</div>
</div>
