package com.github.bootcode1.updownserver.module;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.ArrayList;
import java.util.List;

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
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.IOUtils;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;

import com.github.bootcode1.updownserver.ServletRequestContextExtend;
import com.github.bootcode1.updownserver.UploadProgressListener;
import com.github.bootcode1.updownserver.config.SiteProperties;
import com.github.bootcode1.updownserver.config.SiteProperties.Order;
import com.github.bootcode1.updownserver.config.StorageProperties;
import com.github.bootcode1.updownserver.hashing.SaveTargetPath;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DirectMultipartUpload extends ServletFileUpload
{
	
	//50MB를 기본값으로 설정
	private static final int DEFAULT_BUFFER_SIZE = 52428800;
	
	private final DiskFileItemFactory factory;
	private final StorageProperties storage;
	private final SiteProperties.Config config;
	private final Path root;
	private final SaveTargetPath targetPath;
	
	private DirectMultipartUpload(SaveTargetPath targetPath, FileItemFactory fileItemFactory,
			StorageProperties storage,
			SiteProperties.Config config) throws FileUploadException
	{
		super(fileItemFactory);
		this.targetPath = targetPath;
		this.storage = storage;
		this.root = Paths.get(storage.getRoot());
		this.factory = (DiskFileItemFactory)fileItemFactory;
		//FIXME 이 Config중 사이트 설정을 관리자기능으로 분리할것
		this.config = config;
		
		
		log.debug("file {}, request {}",config.getFileSizeMaxLong(), config.getSizeMaxLong());
		//파일당 최대 업로드 사이즈
		this.setFileSizeMax(config.getFileSizeMaxLong());
		// 최대 업로드 사이즈 (total)
		this.setSizeMax(config.getSizeMaxLong());
	}

	public DirectMultipartUpload(SaveTargetPath targetPath,
			StorageProperties storage,
			SiteProperties.Config config) throws FileUploadException
	{
		this(targetPath, new DiskFileItemFactory(), storage, config);
	}
	
	public List<FileItem> upload(HttpServletRequest request) throws FileUploadException
	{
		return parseRequest(request);
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
		HttpServletRequest request = ((ServletRequestContextExtend) ctx).getRequest();
		//listener 연결
		this.setProgressListener(new UploadProgressListener());
		//upload 사이즈가 작은 경우 버퍼 사이즈를 줄임
		long contentLengthLong = request.getContentLengthLong();
		int bufferSize = DEFAULT_BUFFER_SIZE;
		if(contentLengthLong > 0 && contentLengthLong < DEFAULT_BUFFER_SIZE)
		{
			bufferSize = (int) contentLengthLong;
		}
		
		//FIXME 차후 관리자 설정으로 변경
//		File repository = this.config.root().resolve(site).toFile();
//		if(!repository.exists())
//			repository.mkdirs();
//		factory.setRepository(repository);
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
			String pluploadFileName = null;
			Integer chunk = 0, chunks = 0;
			boolean isPlupload = false;
			while (iter.hasNext())
			{
				final FileItemStream item = iter.next();
				String fileName = item.getName();
				String name = item.getFieldName();
				//plupload chunk대응
				if("name".equals(name))
				{
					//pluploader 가 전송해주는 파일명
					pluploadFileName = Streams.asString(item.openStream());  
                    continue;
				}
				if("chunk".equals(name))
				{
					chunk = Integer.valueOf(Streams.asString(item.openStream()));  
                    continue;  
				}
				if("chunks".equals(name))
				{
					chunks = Integer.valueOf(Streams.asString(item.openStream()));  
                    continue;  
				}
				if(chunks > 0 && pluploadFileName != null)
				{
					fileName = pluploadFileName;
					pluploadFileName = null;
					isPlupload = true;
				}
				String contentType = item.getContentType();
				if(contentType == null)
				{
					continue;
				}
				System.out.println("itemtype: " + contentType+ " file Name: "+fileName);
				
				//파일명이 없으면 버림
				if(fileName.isEmpty())
					continue;
				fileName = Streams.checkFileName(fileName);
				// 파일명 길이 체크
				if (fileName.length() > storage.getMaxFilenameLength())
				{
					//파일명을 날려버림
//					fileName = fileName.substring(0, this.config.getStorage().getMaxFilenameLength());
					throw new FileUploadException("fileName 길이가 너무 끔");
				}
				if (fileName.length() <  storage.getMinFilenameLength())
				{
					throw new FileUploadException("fileName 길이가 너무 작음");
				}
				if (storage.getRemoveCharFilenameRegex().matcher(fileName).find())
				{
					//불안전 파일명을 제거
					fileName = storage.getRemoveCharFilenameRegex().matcher(fileName).replaceAll("");
				}
				/*if (storage.getDeniedFilenameRegex().matcher(fileName).find())
				{
					//해킹 의심
					throw new FileUploadException("해킹 요건 파일명 거부");
				}*/
				Order fileTypeOrder = this.config.getOrder();
				switch(fileTypeOrder)
				{
				case Allow:
					//FIXME 파일 컨텐츠 타입이나 확장자가 맞으면 허용 그렇지 않으면 에러나 스킵
					break;
				case Deny:
					//FIXME 파일 컨텐츠 타입이나 확장자가 맞지않으면 거부 그렇지 않으면 허용
					break;
				case None:
				default:
					break;
				}
				
				//FIXME 저장할 위치를 지정, 꼭 최종 드라이브에 저장할 것
				Path savaTarget = _makeSavePath(targetPath.path(fileName));
				Path repository = savaTarget.getParent();
				if(!Files.exists(repository))
					Files.createDirectories(repository);
				this.factory.setRepository(repository.toFile());
				
				FileItem fileItem = fac.createItem(item.getFieldName(),	item.getContentType(), item.isFormField(), fileName);
//				fileItem.getContentType();
				log.debug("{}  contentType {} - {}", fileItem.getName(),  fileItem.getContentType(), fileItem.getHeaders());
				
				try
				{
//					UserDefinedFileAttributeView view ;
//					Files.createTempFile(repository, "upload_", ".__temp__", attrs)
					OutputStream outputStream = Files.newOutputStream(savaTarget, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
//					write(item.openStream(), fileItem.getOutputStream(), true, new byte[bufferSize]);
					write(item.openStream(), outputStream, true, new byte[bufferSize]);
						
//					Files.getFileAttributeView(savaTargetFile, Cust, options)
					UserDefinedFileAttributeView view = Files
							.getFileAttributeView(savaTarget,UserDefinedFileAttributeView.class);
					String userChunks = chunk+"/"+chunks;
					
					ByteBuffer buffer = ByteBuffer.wrap(userChunks.getBytes());
					Files.setAttribute(savaTarget, "user:chunks", buffer);
//					Files.setAttribute(savaTarget, "user:type", ByteBuffer.wrap(contentType.getBytes()));
					Object attribute = Files.getAttribute(savaTarget, "user:chunks");
					System.out.println(new String((byte[])attribute));
					//FIXME 파일 최종 기록 
//					fileItem.write("현재 repositor+파일명");
					//ImageIO
					if (chunk == chunks - 1)
				    {
//					     last chunk, upload is done
				    }
					
					items.add(fileItem);
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
	
	private static void append(InputStream inputStream, OutputStream outputStream,
			boolean closeOutputStream, byte[] buffer) throws IOException
	{
	}

	private static long write(InputStream inputStream, OutputStream outputStream,
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
			//FIXME long이 아닌 반환타입 만들어 반환할것
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
	
	private Path _makeSavePath(Path sub) throws FileUploadException
	{
		//경로가지고 장난질 치는거 방지
		Path savePath = root.resolve(sub).toAbsolutePath();
		if (root.getRoot() == null
				|| sub == null
				|| sub.toString().isEmpty()
				|| sub.getRoot() != null
				|| !savePath.normalize().toString().equals(savePath.toString())
				|| !savePath.startsWith(root))
		{
//			/throw new FileUploadException("invalid path:"+savePath);
		}
		return savePath;
	}
	
	/*private Dimension getImageDim(final String path) {
	    Dimension result = null;
	    String suffix = this.getFileSuffix(path);
	    Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
	    if (iter.hasNext()) {
	        ImageReader reader = iter.next();
	        try {
	            ImageInputStream stream = new FileImageInputStream(new File(path));
	            reader.setInput(stream);
	            int width = reader.getWidth(reader.getMinIndex());
	            int height = reader.getHeight(reader.getMinIndex());
	            result = new Dimension(width, height);
	        } catch (IOException e) {
	        	log.debug(e.getMessage());
	        } finally {
	            reader.dispose();
	        }
	    } else {
	        log.debug("No reader found for given format: " + suffix);
	    }
	    return result;
	}*/
	
	/**
	 * 파일 객체에서 확장자를 추출함
	 *
	 * @param file the file
	 * @return the extension
	 */
	private static String getExtension(File file)
	{
		if(file == null)
		{
			throw new IllegalArgumentException("file");
		}
		String fileName = file.toString();
		int i = fileName.lastIndexOf('.');
		if (i > 0 && i < fileName.length() - 1)
		{
			return fileName.substring(i + 1);
		}
		return "";
	}
	
	/**
	 * 일치하는 확장자의 존재 여부
	 *
	 * @param file the file
	 * @param exts the exts
	 * @return true, if is extension
	 */
	private static boolean isExtension(File file, String ... exts)
	{
		String extension = getExtension(file);
		if (extension.equals(""))
			return false;
		for(String _ext: exts)
		{
			if(extension.equalsIgnoreCase(_ext))
				return true;
		}
		return false;
	}
	
	/**
	 * 파일 객체에서 확장자를 추출함
	 *
	 * @param path the path
	 * @return the extension
	 */
	private static String getExtension(Path path)
	{
		if(path == null)
		{
			throw new IllegalArgumentException("path");
		}
		Path file = path.getFileName();
		String fileName = file.toString();
		int i = fileName.lastIndexOf('.');
		if (i > 0 && i < fileName.length() - 1)
		{
			return fileName.substring(i + 1);
		}
		return "";
	}
	
	/**
	 * 일치하는 확장자의 존재 여부
	 *
	 * @param path the file
	 * @param exts the exts
	 * @return true, if is extension
	 */
	private static boolean isExtension(Path path, String ... exts)
	{
		String extension = getExtension(path);
		if (extension.equals(""))
			return false;
		for(String _ext: exts)
		{
			if(extension.equalsIgnoreCase(_ext))
				return true;
		}
		return false;
	}
}
