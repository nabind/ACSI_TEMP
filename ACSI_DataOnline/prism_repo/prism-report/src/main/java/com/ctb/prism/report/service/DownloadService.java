package com.ctb.prism.report.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DownloadService {

	protected static Logger logger = Logger.getLogger("service");

	@Autowired
	private ExporterService exporter;
	
	@Autowired
	private TokenService tokenService;
	
	public void download(String type, String token, HttpServletResponse response, JasperPrint jp) throws JRException {
		 
		// 1. Add report parameters
		HashMap<String, Object> params = new HashMap<String, Object>(); 
		params.put("Title", jp.getName().replace(" ", "_"));
		 
		/*// 2.  Retrieve template
		InputStream reportStream = this.getClass().getResourceAsStream(TEMPLATE); 
		 
		// 3. Convert template to JasperDesign
		JasperDesign jd = JRXmlLoader.load(reportStream);
		 
		// 4. Compile design to JasperReport
		JasperReport jr = JasperCompileManager.compileReport(jd);
		 
		// 5. Create the JasperPrint object
		// Make sure to pass the JasperReport, report parameters, and data source
		JasperPrint jp = JasperFillManager.fillReport(jr, params, datasource.getDataSource());*/
		 
		// 6. Create an output byte stream where data will be written
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		 
		// 7. Export report
		exporter.export(type, jp, response, baos);
		 
		// 8. Write to reponse stream
		write(token, response, baos);
	}
	
	/**
	* Writes the report to the output stream
	*/
	private void write(String token, HttpServletResponse response,
			ByteArrayOutputStream baos) {
		ServletOutputStream outputStream = null;
		try {
			logger.debug(baos.size());
			
			// Retrieve output stream
			outputStream = response.getOutputStream();
			// Write to output stream
			baos.writeTo(outputStream);
			
			// Remove download token
			tokenService.remove(token);
			
		} catch (Exception e) {
			logger.error("Unable to write report to the output stream");
			throw new RuntimeException(e);
		} finally {
			if(baos != null)
				try { 
					baos.flush();
					baos.close(); 
				} catch (IOException e1) {}
			if(outputStream != null) {
				// Flush the stream
				try { 
					outputStream.flush();
					outputStream.close(); 
				} catch (IOException e) {}
			}
		}
	}
}
