package com.github.bootcode1.updownserver.config;

import java.io.Serializable;
import java.util.regex.Pattern;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * upload된 파일의 저장소 관련 설정
 * 주의 할것은 temp와 root가 같은 드라이브여야 함
 * 업로드된 파일을 rename할 경우 같은 드라이브가 아니면 move가 되어서
 * 속도 저하 발생
 */
@ConfigurationProperties(prefix= "storage")
public class StorageProperties implements Serializable
{
	
	private static final long serialVersionUID = -2835124345618786833L;
	private String root;
	private Pattern remove_char_filename_regex;
	private Pattern denied_filename_regex;
	private int min_filename_length;
	private int max_filename_length;
	
	public String getRoot()
	{
		return root;
	}

	public void setRoot(String root)
	{
		this.root = root;
	}

	public Pattern getRemoveCharFilenameRegex()
	{
		return remove_char_filename_regex;
	}

	public void setRemoveCharFilenameRegex(
			Pattern remove_char_filename_regex)
	{
		this.remove_char_filename_regex = remove_char_filename_regex;
	}

	public Pattern getDeniedFilenameRegex()
	{
		return denied_filename_regex;
	}

	public void setDeniedFilenameRegex(Pattern denied_filename_regex)
	{
		this.denied_filename_regex = denied_filename_regex;
	}

	public int getMinFilenameLength()
	{
		return min_filename_length;
	}

	public void setMinFilenameLength(int min_filename_length)
	{
		this.min_filename_length = min_filename_length;
	}

	public int getMaxFilenameLength()
	{
		return max_filename_length;
	}

	public void setMaxFilenameLength(int max_filename_length)
	{
		this.max_filename_length = max_filename_length;
	}
}
