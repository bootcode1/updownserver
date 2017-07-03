package in.saram.service;


import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
//import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import in.saram.service.config.StorageProperties;

//https://stackoverflow.com/questions/30356367/spring-mvc-handling-partial-file-upload
public class DirectMultipartUploadResolver extends CommonsMultipartResolver
{
	public static MultipartConfigElement MULTIPART_CONFIG;
	public static StorageProperties CONFIG;

	
	
//	public DirectMultipartUploadResolver() {
//		super();
//	}
	
//	public DirectMultipartUploadResolver(ServletContext servletContext) {
//		this();
//		setServletContext(servletContext);
//		super.setResolveLazily(true);
//	}

	
	public DirectMultipartUploadResolver(ServletContext servletContext)
	{
		super(servletContext);
	}
	
	protected DiskFileItemFactory newFileItemFactory() 
	{
		return new DiskFileItemFactory();
	}

	@Override
	protected FileUpload newFileUpload(FileItemFactory fileItemFactory) {
		DirectMultipartUpload upload = new DirectMultipartUpload(fileItemFactory, MULTIPART_CONFIG, CONFIG);
		return upload;
	}
	
//	@Override
//	public void cleanupMultipart(MultipartHttpServletRequest request) {
//	    File tempDir = (File) servletContext.getAttribute(ServletContext.TEMPDIR);
//	    File[] files = tempDir.listFiles();
//	    for(int i=0; i< files.length; i++) {
//	        //LOGGER.debug("filename: " + files[i].getName() + ", size: " + files[i].length());
//	    	System.out.println("filename: " + files[i].getName() + ", size: " + files[i].length());
//	    }
//	}
}
