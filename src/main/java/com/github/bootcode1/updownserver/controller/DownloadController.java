package com.github.bootcode1.updownserver.controller;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("/download")
public class DownloadController
{
	@GetMapping
	//상황에 맞게 다른 코드
	@ResponseStatus(HttpStatus.OK)
	public StreamingResponseBody doDownload() {
	    return new StreamingResponseBody() {

			@Override
			public void writeTo(OutputStream output) throws IOException
			{
				output.write("stream byte output".getBytes());
			}
	    };
	}
}
