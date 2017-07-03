package in.saram.service;

import org.apache.catalina.connector.Request;

public class MultipartRequest extends Request
{
	protected Request request;

	public MultipartRequest(Request request)
	{
		this.request = request;
	}
}
