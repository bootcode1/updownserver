package com.github.bootcode1.updownserver;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.servlet.ServletRequestContext;

public class ServletRequestContextExtend extends ServletRequestContext
{

	private final HttpServletRequest request;

	public ServletRequestContextExtend(final HttpServletRequest request)
	{
		super(request);
		this.request = request;
	}

	public HttpServletRequest getRequest()
	{
		return this.request;
	}
}
