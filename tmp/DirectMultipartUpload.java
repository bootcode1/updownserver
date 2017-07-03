package in.saram.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;

import in.saram.service.config.StorageProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DirectMultipartUpload extends ServletFileUpload
{
	
	//20MB를 기본값으로 설정
	private static final int DEFAULT_BUFFER_SIZE = 20971520;
	
	private final DiskFileItemFactory factory;
	private final MultipartConfigElement multipartConfig;
	private final StorageProperties config;
	
	public DirectMultipartUpload(FileItemFactory fileItemFactory,
			MultipartConfigElement multipartConfig, StorageProperties config)
	{
		super(fileItemFactory);
		this.multipartConfig = multipartConfig;
		this.config = config;
		//다른 팩토리가 필요하면 여기서 분기 처리
		this.factory = (DiskFileItemFactory)fileItemFactory;
	}


	@Override
    public List<FileItem> parseRequest(HttpServletRequest request) throws FileUploadException 
	{
        return parseRequest(new ServletRequestContextExtend(request));
    }
	
	@Override
	public List<FileItem> parseRequest(RequestContext ctx)
			throws FileUploadException
	{
//		org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
		HttpServletRequest request = ((ServletRequestContextExtend) ctx).getRequest();
		String requestURI = request.getRequestURI();
		//FIXME 설정 파일로 뺄것
		if(!requestURI.startsWith("/upload"))
		{
			return null;
		}
		String[] uri = requestURI.split("/");
		//saramin or otwojob
		String site ="";
		try{
			
			site = uri[2];
		}catch(Exception e){
			System.out.println(e);
			return null;
		}
		
//		ServletContext context = request.getServletContext();
//		System.out.println(context);
		//listener 연결
		this.setProgressListener(new UploadProgressListener());
		//upload 사이즈가 작은 경우 버퍼 사이즈를 줄임
		long contentLengthLong = request.getContentLengthLong();
		int bufferSize = DEFAULT_BUFFER_SIZE;
		if(contentLengthLong > 0 && contentLengthLong < DEFAULT_BUFFER_SIZE)
		{
			bufferSize = (int) contentLengthLong;
		}
		
		String pathInfo = request.getPathInfo();
		String contentType = ctx.getContentType();
		System.out.println(contentType);
		
		
		String queryString = request.getQueryString();
		log.debug(requestURI + " " +site+ " "+queryString);
		
		
		//FIXME 차후 관리자 설정으로 변경
		/*File repository = this.config.root().resolve(site).toFile();
		if(!repository.exists())
			repository.mkdirs();
		factory.setRepository(repository);*/
//		repository = (File) request.getAttribute("javax.servlet.context.tempdir");
//		factory.setRepository(repository);
		
		List<FileItem> items = new ArrayList<>();
		boolean successful = false;
		try
		{
			FileItemIterator iter = getItemIterator(ctx);
			FileItemFactory fac = getFileItemFactory();
			if (fac == null)
			{
				throw new NullPointerException(
						"No FileItemFactory has been set.");
			}
			while (iter.hasNext())
			{
				final FileItemStream item = iter.next();
				final String fileName = item.getName();
				System.out.println("---> "+item.getFieldName() + " "+fileName);
				// final String fileName =
				// ((FileItemIteratorImpl.FileItemStreamImpl) item).name;
				FileItem fileItem = fac.createItem(item.getFieldName(),	item.getContentType(), item.isFormField(), fileName);
				items.add(fileItem);
				try
				{
//					Streams.copy(item.openStream(), fileItem.getOutputStream(),
//							true);
					copy(item.openStream(), fileItem.getOutputStream(),	true, new byte[bufferSize]);
				} catch (FileUploadIOException e)
				{
					throw (FileUploadException) e.getCause();
				} catch (IOException e)
				{
					throw new IOFileUploadException(
							String.format("Processing of %s request failed. %s",
									MULTIPART_FORM_DATA, e.getMessage()),
							e);
				}
				final FileItemHeaders fih = item.getHeaders();
				fileItem.setHeaders(fih);
			}
			successful = true;
			return items;
		} catch (FileUploadIOException e)
		{
			throw (FileUploadException) e.getCause();
		} catch (IOException e)
		{
			throw new FileUploadException(e.getMessage(), e);
		} finally
		{
			if (!successful)
			{
				for (FileItem fileItem : items)
				{
					try
					{
						fileItem.delete();
					} catch (Exception ignored)
					{
						// ignored TODO perhaps add to tracker delete failure
						// list somehow?
					}
				}
			}
		}
	}

	public static long copy(InputStream inputStream, OutputStream outputStream,
			boolean closeOutputStream, byte[] buffer) throws IOException
	{
		OutputStream out = outputStream;
		BufferedInputStream in = new BufferedInputStream(inputStream);
		in.mark(0);
		
		AutoDetectParser parser = new AutoDetectParser();
	    Detector detector = parser.getDetector();
	    Metadata md = new Metadata();
//	    md.add(Metadata.RESOURCE_NAME_KEY, theFileName);
	    MediaType mediaType = detector.detect(in, md);
	    System.out.println(mediaType);
	    in.reset();
		try
		{
			long total = 0;
			for (;;)
			{
				int res = in.read(buffer);
				if (res == -1)
				{
					break;
				}
				if (res > 0)
				{
					total += res;
					if (out != null)
					{
						out.write(buffer, 0, res);
					}
				}
			}
			if (out != null)
			{
				if (closeOutputStream)
				{
					out.close();
				} else
				{
					out.flush();
				}
				out = null;
			}
			in.close();
			in = null;
			return total;
		} finally
		{
			IOUtils.closeQuietly(in);
			if (closeOutputStream)
			{
				IOUtils.closeQuietly(out);
			}
//			bis.close();
		}
	}
}
