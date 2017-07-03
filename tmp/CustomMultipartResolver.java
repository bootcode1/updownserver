package in.saram.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

public class CustomMultipartResolver extends CommonsMultipartResolver {  
    
    @Autowired  
    private CustomProgressListener progressListener;  
       
    public void setFileUploadProgressListener(CustomProgressListener progressListener){  
        this.progressListener = progressListener;  
    }  
      
    public void setFileSizeMax(long fileSizeMax) {  
        getFileUpload().setFileSizeMax(fileSizeMax);  
    }  
    
    @Override  
    public MultipartParsingResult parseRequest(HttpServletRequest request) throws MultipartException {  
        String encoding = determineEncoding(request);  
        FileUpload fileUpload = prepareFileUpload(encoding);  
          
        progressListener.setSession(request.getSession());  
        fileUpload.setProgressListener(progressListener);  
          
        try {  
            List<FileItem> fileItems = ((ServletFileUpload) fileUpload).parseRequest(request);  
            return parseFileItems(fileItems, encoding);  
        }  
        catch (FileUploadBase.SizeLimitExceededException ex) {  
            throw new MaxUploadSizeExceededException(fileUpload.getSizeMax(), ex);  
        }  
        catch (FileUploadBase.FileSizeLimitExceededException ex) {  
            throw new MaxUploadSizeExceededException(fileUpload.getFileSizeMax(), ex);  
        }  
        catch (FileUploadException ex) {  
            throw new MultipartException("Could not parse multipart servlet request", ex);  
        }  
    }  
  
}  