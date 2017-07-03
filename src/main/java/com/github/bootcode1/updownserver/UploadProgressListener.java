package com.github.bootcode1.updownserver;

import org.apache.commons.fileupload.ProgressListener;

public class UploadProgressListener implements ProgressListener
{
	public UploadProgressListener(/* ETag */)
	{

	}

	@Override
	public void update(long pBytesRead, long pContentLength, int pItems)
	{
		System.out.println("We are currently reading item " + pItems);
		if (pContentLength == -1)
		{
			System.out.println(
					"So far, " + pBytesRead + " bytes have been read.");
		} else
		{
			System.out.println("So far, " + pBytesRead + " of " + pContentLength
					+ " bytes have been read.");
		}
	}

}
