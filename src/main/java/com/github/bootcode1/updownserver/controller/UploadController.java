package com.github.bootcode1.updownserver.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.github.bootcode1.updownserver.config.SiteProperties;
import com.github.bootcode1.updownserver.config.StorageProperties;
import com.github.bootcode1.updownserver.hashing.SaveTargetPath;
import com.github.bootcode1.updownserver.model.UploadedFileItem;
import com.github.bootcode1.updownserver.module.DirectMultipartUpload;

//https://stackoverflow.com/questions/32782026/springboot-large-streaming-file-upload-using-apache-commons-fileupload
@Controller
/// {directory:[a-z_/]+}
@RequestMapping("/upload/{site}")
public class UploadController
{
	@Autowired 
	private SiteProperties config;
	
	@Autowired 
	private StorageProperties storage;

	/*
	 * @PostMapping(consumes = { "multipart/form-data"}, produces =
	 * MediaType.APPLICATION_JSON_VALUE)
	 * 
	 * @ResponseStatus(HttpStatus.CREATED) public @ResponseBody ProgressInfo
	 * doUpload(@RequestParam("file") List<MultipartFile>
	 * files, @PathVariable("site") String site ,RedirectAttributes
	 * redirectAttributes) { for (MultipartFile file : files) {
	 * System.out.println("upload "+file.getName()+
	 * " "+file.getOriginalFilename()); try { // file.getInputStream();
	 * file.transferTo(new
	 * File("D:/tmp/storage/root/saramin/"+file.getOriginalFilename())); } catch
	 * (IllegalStateException | IOException e) { // TODO Auto-generated catch
	 * block e.printStackTrace(); } } System.out.println(site + "  "+ files);
	 * return new ProgressInfo(); }
	 */

	// @PostMapping(consumes = { "multipart/form-data" }, produces =
	// MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PostMapping(consumes =
		{ "multipart/form-data" })
	@ResponseStatus(HttpStatus.CREATED)
	public List<UploadedFileItem> doUpload2(HttpServletRequest request, @PathVariable String site,
			@RequestParam(required = true) String path)
	{
		// fileItemFactory.setRepository(repository);
		// upload.setProgressListener(new UploadProgressListener());
		try
		{
			DirectMultipartUpload upload = new DirectMultipartUpload(new SaveTargetPath(){

				@Override
				public Path path(String fileName)
				{
//					fileName.substring(0,1);
//					fileName.substring(1,1);
//					fileName.substring(2,1);
//					Path path2 = Paths.get(path).resolve(fileName);
					return Paths.get(fileName);
				}
				
			}, storage, config.getSite(site));
			// 개별 업로드 사이즈
			//3749645500
//			upload.setFileSizeMax(3749645);
			// 최대 업로드 사이즈 (total)
//			upload.setSizeMax(0);
			List<FileItem> items = upload.upload(request);
			
			for (FileItem item : items)
			{
				String string = item.toString();
				System.out.println(string);
//				Files.move(source, target, options)
			}
		} catch (FileUploadException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// return upload;

		System.out.println("?");
		return null;
		// try
		// {
		// request.getInputStream().close();
		// } catch (IOException e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// System.out.println(request);
		// System.out.println(file);
	}

	
	
	@GetMapping
	public String listUploadedFiles(@PathVariable String site,
			@RequestParam(required = true) String path  
			) throws IOException
	{
		System.out.println(config);
		// model.addAttribute("files", storageService
		// .loadAll()
		// .map(path ->
		// MvcUriComponentsBuilder
		// .fromMethodName(FileUploadController.class, "serveFile",
		// path.getFileName().toString())
		// .build().toString())
		// .collect(Collectors.toList()));

		return "uploadform";
	}
	
	@GetMapping("/2")
	public String listUploadedFiles2(@PathVariable String site,
			@RequestParam(required = true) String path  
			) throws IOException
	{
		System.out.println(config);
		// model.addAttribute("files", storageService
		// .loadAll()
		// .map(path ->
		// MvcUriComponentsBuilder
		// .fromMethodName(FileUploadController.class, "serveFile",
		// path.getFileName().toString())
		// .build().toString())
		// .collect(Collectors.toList()));

		return "uploadform2";
	}
}
