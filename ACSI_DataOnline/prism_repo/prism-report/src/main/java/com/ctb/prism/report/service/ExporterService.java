package com.ctb.prism.report.service;

import java.io.ByteArrayOutputStream;

import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JExcelApiExporterParameter;
import net.sf.jasperreports.engine.export.JRHyperlinkProducerMapFactory;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.util.JRProperties;
import com.ctb.prism.core.util.CustomReportExecutionHyperlinkProducer;

import org.springframework.stereotype.Service;

import com.ctb.prism.core.util.CustomStringUtil;

@Service
public class ExporterService {

	//public static final String MEDIA_TYPE_EXCEL = "application/vnd.ms-excel";
	public static final String MEDIA_TYPE_EXCEL = "application/xls";
	public static final String MEDIA_TYPE_PDF = "application/pdf";
	public static final String EXTENSION_TYPE_EXCEL = "xls";
	public static final String EXTENSION_TYPE_PDF = "pdf";
	
	public HttpServletResponse export(String type, 
			JasperPrint jp, 
			HttpServletResponse response,
			ByteArrayOutputStream baos) {
		
		if (type.equalsIgnoreCase(EXTENSION_TYPE_EXCEL)) {
			// Export to output stream
			exportXls(jp, baos);
			 
			// Set our response properties
			// Here you can declare a custom filename
			String fileName = jp.getName().replace(" ", "_");
			fileName = CustomStringUtil.appendString(fileName, ".", EXTENSION_TYPE_EXCEL);
			//response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			response.setHeader("Content-Disposition",
					CustomStringUtil.appendString("attachment; filename=\"", fileName, "\""));
			
			// Set content type
			response.setContentType(MEDIA_TYPE_EXCEL);
			response.setContentLength(baos.size());
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			
			return response;
		}
		
		if (type.equalsIgnoreCase(EXTENSION_TYPE_PDF)) {
			// Export to output stream
			exportPdf(jp, baos);
			 
			// Set our response properties
			// Here you can declare a custom filename
			String fileName = jp.getName().replace(" ", "_");
			fileName = CustomStringUtil.appendString(fileName, ".", EXTENSION_TYPE_PDF);
			//response.setHeader("Content-Disposition", "attachment; filename="+ fileName);
			response.setHeader("Content-Disposition",
					CustomStringUtil.appendString("attachment; filename=\"", fileName, "\""));
			
			// Set content type
			response.setContentType(MEDIA_TYPE_PDF);
			response.setContentLength(baos.size());
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			
			return response;
			
		} 
		
		throw new RuntimeException("No type set for type " + type);
	}
	
	public void exportXls(JasperPrint jp, ByteArrayOutputStream baos) {
		// Create a JRXlsExporter instance
		JRXlsExporter exporter = new JRXlsExporter();
		setJRProperties();
		 
		// Here we assign the parameters jp and baos to the exporter
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
		 
		// Excel specific parameters
		exporter.setParameter(JRXlsAbstractExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
		exporter.setParameter(JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
		//exporter.setParameter(JRXlsAbstractExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
		exporter.setParameter(JExcelApiExporterParameter.CREATE_CUSTOM_PALETTE, Boolean.TRUE);
		
		// remove hyperlink in exported report
		JRHyperlinkProducerMapFactory producerFactory = new JRHyperlinkProducerMapFactory();
        producerFactory.addProducer("Reference", CustomReportExecutionHyperlinkProducer.getInstance(null));
		exporter.setParameter(JRExporterParameter.HYPERLINK_PRODUCER_FACTORY, producerFactory);
		 
		try {
			exporter.exportReport();
			
		} catch (JRException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void exportPdf(JasperPrint jp, ByteArrayOutputStream baos) {
		// Create a JRXlsExporter instance
		JRPdfExporter exporter = new JRPdfExporter();
		setJRProperties();
		 
		// Here we assign the parameters jp and baos to the exporter
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
		
		// remove hyperlink in exported report
		JRHyperlinkProducerMapFactory producerFactory = new JRHyperlinkProducerMapFactory();
        producerFactory.addProducer("Reference", CustomReportExecutionHyperlinkProducer.getInstance(null));
		exporter.setParameter(JRExporterParameter.HYPERLINK_PRODUCER_FACTORY, producerFactory);
		 
		try {
			exporter.exportReport();
			
		} catch (JRException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void setJRProperties() {
		JRProperties.setProperty("com.jaspersoft.jasperreports.fusion.charts.context.swf.url", "fusion/charts");
		JRProperties.setProperty("com.jaspersoft.jasperreports.fusion.charts.base.swf.url", "fusion/charts");
		JRProperties.setProperty("com.jaspersoft.jasperreports.fusion.maps.context.swf.url", "fusion/maps");
		JRProperties.setProperty("com.jaspersoft.jasperreports.fusion.maps.base.swf.url", "fusion/maps");
		JRProperties.setProperty("com.jaspersoft.jasperreports.fusion.widgets.context.swf.url", "fusion/widgets");
		JRProperties.setProperty("com.jaspersoft.jasperreports.fusion.widgets.base.swf.url", "fusion/widgets");
	}
	
}
