package in.saram.service;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

public class DirectMultipartHttpServletRequest extends StandardMultipartHttpServletRequest
{

	private Set<String> multipartParameterNames;
	
	public DirectMultipartHttpServletRequest(HttpServletRequest request) throws MultipartException
	{
		super(request);
//		CommonsMultipartResolver
	}
	

//	private void parseRequest(HttpServletRequest request) {
//		try {
//			Collection<Part> parts = request.getParts();
//			this.multipartParameterNames = new LinkedHashSet<String>(parts.size());
//			MultiValueMap<String, MultipartFile> files = new LinkedMultiValueMap<String, MultipartFile>(parts.size());
//			for (Part part : parts) {
//				String disposition = part.getHeader(CONTENT_DISPOSITION);
//				String filename = extractFilename(disposition);
//				if (filename == null) {
//					filename = extractFilenameWithCharset(disposition);
//				}
//				if (filename != null) {
//					files.add(part.getName(), new StandardMultipartFile(part, filename));
//				}
//				else {
//					this.multipartParameterNames.add(part.getName());
//				}
//			}
//			setMultipartFiles(files);
//		}
//		catch (Throwable ex) {
//			throw new MultipartException("Could not parse multipart servlet request", ex);
//		}
//	}
}
