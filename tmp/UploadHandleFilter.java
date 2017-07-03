package in.saram.service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.AsyncContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.core.ApplicationPart;
import org.apache.tomcat.util.http.Parameters.FailReason;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import lombok.extern.slf4j.Slf4j;

//http://dev.anyframejava.org/docs/anyframe/plugin/optional/fileupload/1.1.0/reference/htmlsingle/fileupload.html#fileupload_ref_size_limit
@Slf4j
public class UploadHandleFilter  extends OncePerRequestFilter 
{

	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		HttpServletRequest requestToUse = request;

		//if ("POST".equals(request.getMethod()) && request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE) == null)
		if(DirectMultipartUpload.isMultipartContent(request))
		{
			
			String contentType = request.getContentType();
            if (contentType == null) {
                contentType = "";
            }
            int semicolon = contentType.indexOf(';');
            if (semicolon >= 0) {
                contentType = contentType.substring(0, semicolon).trim();
            } else {
                contentType = contentType.trim();
            }

            if ("multipart/form-data".equals(contentType)) 
            {
            	String pathInfo = request.getPathInfo();
            	System.out.println(pathInfo);
            	String requestURI = request.getRequestURI();
            	System.out.println(requestURI);
            	RequestFacade requestFacade = (RequestFacade)request;

            	try
				{
					Field field = RequestFacade.class.getDeclaredField("request");
					Field field2 = Request.class.getDeclaredField("parts");
					field.setAccessible(true);
					field2.setAccessible(true);
					Request rawRequest = (Request) field.get(requestFacade);
					System.out.println(rawRequest);
					Context context = rawRequest.getContext();
					Connector connector = rawRequest.getConnector();
					MultipartConfigElement mce = rawRequest.getWrapper().getMultipartConfigElement();
					if (mce == null) {
			            if(context.getAllowCasualMultipartParsing()) {
			                mce = new MultipartConfigElement(null,
			                                                 connector.getMaxPostSize(),
			                                                 connector.getMaxPostSize(),
			                                                 connector.getMaxPostSize());
			            }
			        }
					String locationStr = mce.getLocation();
					System.out.println(locationStr);
					
					DiskFileItemFactory factory = new DiskFileItemFactory();
					ServletContext servletContext = request.getServletContext();
					File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
					factory.setRepository(repository);
//					MultipartDirectUpload upload = new MultipartDirectUpload(factory);
//					List<FileItem> items = upload.parseRequest(new ServletRequestContext(rawRequest));
					
//					Collection<Part> parts = (Collection<Part>)field2.get(rawRequest);
//					for (FileItem item : items) {
//	                    ApplicationPart part = new ApplicationPart(item, repository);
//	                    parts.add(part);
//					}
					
				} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//            	request.startAsync();
//            	AsyncContext context = request.getAsyncContext();
//            	System.out.println(context);
//            	Context context = request
//                MultipartConfigElement mce = getWrapper().getMultipartConfigElement();
            	

//                if (mce == null) {
//                    if(context.getAllowCasualMultipartParsing()) {
//                        mce = new MultipartConfigElement(null,
//                                                         connector.getMaxPostSize(),
//                                                         connector.getMaxPostSize(),
//                                                         connector.getMaxPostSize());
//                    } else {
//                        if (explicit) {
//                            partsParseException = new IllegalStateException(
//                                    sm.getString("coyoteRequest.noMultipartConfig"));
//                            return;
//                        } else {
//                            parts = Collections.emptyList();
//                            return;
//                        }
//                    }
//                }
//            	// Create a new file upload handler
//                DiskFileItemFactory factory = new DiskFileItemFactory();
//                try {
//                    factory.setRepository(location.getCanonicalFile());
//                } catch (IOException ioe) {
////                    parameters.setParseFailedReason(FailReason.IO_ERROR);
////                    partsParseException = ioe;
//                    return;
//                }
//                factory.setSizeThreshold(mce.getFileSizeThreshold());
//
//                ServletFileUpload upload = new ServletFileUpload();
//                upload.setFileItemFactory(factory);
//                upload.setFileSizeMax(mce.getMaxFileSize());
//                upload.setSizeMax(mce.getMaxRequestSize());
                
//            	Collection<Part> parts = request.getParts();
            	
//                parseParts(false);
//                success = true;
//                return;
//            	System.out.println("성공 "+ parts);
            }
            
//			request.getParts()
//			String paramValue = request.getParameter(this.methodParam);
//			if (StringUtils.hasLength(paramValue)) {
////				requestToUse = new HttpMethodRequestWrapper(request, paramValue);
//			}
		}

		filterChain.doFilter(requestToUse, response);
	}
	
	/*@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException
	{
		try 
		{
			if ("POST".equals(request.getMethod()) && request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE) == null) {
				String paramValue = request.getParameter(this.methodParam);
				if (StringUtils.hasLength(paramValue)) {
				}
			}
//			org.apache.tomcat.util.http.fileupload.FileUploadBase;
			HttpServletRequest _request = (HttpServletRequest) request;
			Collection<Part> parts = _request.getParts();
			for (Part part : parts) {
				String disposition = part.getHeader("content-disposition");
				String filename = part.getName();
				log.debug(disposition + " "+ filename);
//				if (filename == null) {
//					filename = extractFilenameWithCharset(disposition);
//				}
//				if (filename != null) {
//					files.add(part.getName(), new StandardMultipartFile(part, filename));
//				}
//				else {
//					this.multipartParameterNames.add(part.getName());
//				}
			}
//			request.
			chain.doFilter(request, response);
		} catch (Exception ex) {
			request.setAttribute("errorMessage", ex);
			request.getRequestDispatcher("/WEB-INF/views/jsp/error.jsp")
                               .forward(request, response);
		}
	}*/

	@Override
	public void destroy()
	{
		// TODO Auto-generated method stub
		
	}

}
