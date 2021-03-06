<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<div class="homeMain">
	<h3 class="pageTitle">Manage</h3>
	<div class="pageSection innerTabWrap" id="authLayout">
		<div class="ui-layout-center">
			<div class="Title clearfix">
				<a href="#" id="createNewContent">Create New Content</a>
			</div>
			<div class="body" style="padding: 0 10px; height: 460px; overflow-y:scroll">
			
				<div class="prodMetadataSec" style="height:auto">
					<div class="subnav-header" style="height:9px;">
						<div class="clear"></div>
					</div>
					<div class="subnav"> 
						<ul>
							<li><a href="#auth-tabs-11">User</a></li>
							<li><a href="#auth-tabs-22">Organization</a></li>
						</ul>
					</div>
					<section class="innertab" id="auth-tabs-11" style="padding: 8px;">
						User
					</section>
					<section class="innertab" id="auth-tabs-22" style="padding: 8px;">
						Organization
					</section>
				</div>
					
			
			<form id="saveAuthorForm" name="saveAuthorForm" onsubmit="return false;" class="nomargin">
				<div class="authoringButtons">
					<button value="Save" class="button" id="saveNdConf">Save</button>
					<button value="Edit" class="button" id="editNdConf">Edit</button>
					<button value="Delete" class="button" id="deleteNdConf">Delete</button>
					<button value="Cancel" class="button" id="cancelNdConf">Cancel</button>
				</div>
			</form>
			
		</div>
		</div>
		<div class="ui-layout-east">
			<div class="prodMetadataSec">
				<div class="subnav-header" style="height:9px;">
					<div class="clear"></div>
				</div>
				<div class="subnav"> 
					<ul>
						<li><a href="#auth-tabs-1">Metadata</a></li>
						<li><a href="#auth-tabs-2">Tagged Keywords</a></li>
					</ul>
				</div>
				<section class="innertab" id="auth-tabs-1" style="padding: 8px;">
					<form action="" id="authorMetadataForm">
						<div class="metadata-div">
							<div class="">
								<div class="filterItems">
									<label>Title</label>
									<input type="text" id="auth_meta_title" placeholder="eg. My Title" disabled="disabled" style="background: none;color: #555;"/>
								</div>
								<div class="filterItems">
									<label id="auth_meta_authorLabel" style="display: block;">Author</label>
									<label id="auth_meta_bylineLabel" style="display: none;">Byline</label>
									<input type="text" id="auth_meta_author" placeholder="eg. Abhisek Roy"/>
								</div>
								<div class="filterItems">
									<label>File Name</label>
									<input type="text" id="auth_meta_fileName" placeholder="eg. myFile" disabled="disabled" style="background: none;color: #555;"/>
								</div>
								<div class="filterItems halfArea">
									<label>File Type</label>
									<input type="text" id="auth_meta_fileType" placeholder="" disabled="disabled" style="background: none;color: #555;"/>
								</div>
								<div class="filterItems halfArea">
									<label>Current Version</label>
									<input type="text" id="auth_meta_currVersion" disabled="disabled" style="background: none;color: #555;"/>
								</div>
								<div class="clear"></div>
								<div class="filterItems halfArea">
									<label>Status</label>
									<input type="text" id="auth_meta_status" disabled="disabled" value="Work In Progress" style="background: none;color: #555;"/>
								</div>
								<div class="filterItems halfArea">
									<label>Discipline</label>
									<select id="auth_meta_disciplineOpt" class="nomargin">
										<option value="Civics">Civics</option>
										<option value="Fiction">Fiction</option>
										<option value="Literature">Literature</option>
										<option value="Reading Language">Reading Language</option>
									</select>
								</div>
								<div class="clear"></div>
								<div class="filterItems">
									<label id="auth_meta_programNameLabel" style="display: block;">Program Name</label>
									<label id="auth_meta_datelineLabel" style="display: none;">Dateline</label>
									<select id="auth_meta_programNameOpt" class="nomargin" style="display: block;">
										<option value="Civics Program - 1990-2000">Civics Program - 1990-2000</option>
										<option value="Civics Program - 2001-2010">Civics Program - 2001-2010</option>
									</select>
									<input type="text" id="auth_meta_datelineTextBox" style="display: none;" placeholder="eg. Location"/>
								</div>
								<div class="clear"></div>
								<div class="filterItems">
									<label>Keywords</label>
									<input type="text" id="auth_meta_keywords" placeholder="Keywords One, Keywords Two, ..."/>
								</div>
								<br/>
							</div>
						
						</div>
						<div class="authoringButtons">
							<button value="Save" class="button" id="authoringMetadataSaveRight">Save</button>
							<%-- 
							<button value="Reset" class="button">Reset</button>
							<button value="Cancel" class="button">Cancel</button>
							--%>
							<button id="button-createForumTopic-node2tree" value="Create Discussion Thread" class="hide button slim-button" onclick="return createDiscussionThreadForTopic();">Start Discussion</button>
							<button id="button-editForumTopic-node2tree" value="Edit Discussion Thread" class="hide button slim-button" onclick="return viewDiscussionThreadForTopic();">View Discussion</button>
							<input type="hidden" id="associatedForumId" name="associatedForumId" value="" />
						</div>
					</form>
				</section>
				<section class="innertab" id="auth-tabs-2" style="padding: 8px;">
					<div id="taggedKeywordArea" class="metadata-div">No keywords entered.</div>
					<div class="authoringButtons">
						<button value="Save" class="button" id="removeKeywords">Remove Highlighting</button>
					</div>
				</section>
				<div class="clear"></div>
			</div>
		</div>
	</div>
</div>

<!------------------------------------------------------   Dialog Area ------------------------------------------------------------->
<div id="authoringDialog" title="Choose Template" style="display: none;">
	<div class="metaWrap">
		<div class="">
			<div class="filterItems">
				<select id="auth_content_type">
					<option value="book_article">Book Article</option>
					<option value="journal_article">Journal Article</option>
					<option value="newsletter_article">Newsletter Article</option>
				</select>
			</div>
			<div class="filterItems">
				<label>Discipline</label>
				<select id="disciplineOpt" class="nomargin">
					<option value="Civics">Civics</option>
					<option value="Fiction">Fiction</option>
					<option value="Literature">Literature</option>
					<option value="Reading Language">Reading Language</option>
				</select>
			</div>
			
			<div class="filterItems">
				<label id="programNameLabel" style="display: block;">Program Name</label>
				<label id="datelineLabel" style="display: none;">Dateline</label>
				<select id="programNameOpt" class="nomargin" style="display: block;">
					<option value="Civics Program - 1990-2000">Civics Program - 1990-2000</option>
					<option value="Civics Program - 2001-2010">Civics Program - 2001-2010</option>
				</select>
				<input type="text" id="datelineTextBox" style="display: none;" placeholder="eg. Location"/>
			</div>
			
			<div class="filterItems" style="position:relative !important">
				<label class="mandatory">Title</label>
				<input type="text" id="auth_content_title" placeholder="eg. My Title"/>
			</div>
			<div class="filterItems halfArea">
				<label id="authorLabel" style="display: block;">Author</label>
				<label id="bylineLabel" style="display: none;">Byline</label>
				<input type="text" id="auth_content_authorName" placeholder="eg. Abhisek Roy"/>
			</div>
			<div class="filterItems halfArea" style="position:relative !important">
				<label class="mandatory">File Name</label>
				<input type="text" id="auth_content_fileName" placeholder="eg. myFile"/>
			</div>
			<div class="clear"></div>
			<div class="filterItems">
				<label>Keywords</label>
				<input type="text" id="auth_content_keywords" placeholder="Keywords One, Keywords Two, ..."/>
			</div>
		</div>
	</div>
</div>

<div id="authoringRenditionDialog" title="Rendition Type" style="display: none;">

	<div class="filterItems full">
		<label>Rendition Type</label>
		<form id="renditionSelection" action="">
			<div class="full">
				<input type="radio" class="radioInp" name="auth_rendType"
					id="auth_rendTypeHtml" value="html" checked="checked"/> HTML
			</div>
			<div class="full">
				<input type="radio" class="radioInp" name="auth_rendType"
					id="auth_rendTypePdf" value="pdf" /> PDF
			</div>
		</form>
		<div class="clear"></div>
	</div>
</div>
<div id="auth-dialog-confirm" class="confirm" style="display: none;">
	<div class="confDialogTitle">Confirmation</div>
	You have unsaved content! Are you sure, you want to continue?
</div>


