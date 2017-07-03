package com.github.bootcode1.updownserver.module;

import javax.servlet.http.HttpServletRequest;

import com.github.bootcode1.updownserver.config.SiteProperties;
import com.github.bootcode1.updownserver.config.StorageProperties;
import com.github.bootcode1.updownserver.hashing.SaveTargetPath;
import org.apache.commons.fileupload.FileUploadException;

public class Uploader
{
	public Uploader(SaveTargetPath targetPath,
			StorageProperties storage,
			SiteProperties.Config config) throws FileUploadException
	{
//		this(targetPath, new DiskFileItemFactory(), storage, config);
	}
	
	public void upload(HttpServletRequest request)
	{
		
	}
}
