package com.github.bootcode1.updownserver.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(ignoreUnknownFields = true)
public class SiteProperties
{
	
	public enum Order
	{
		None,
		Allow,
		Deny
	}

	private Map<String, Config> site = new HashMap<String, Config>();

	public Map<String, Config> getSite()
	{
		return site;
	}

	public Config getSite(String site)
	{
		Config _site = this.site.get(site);
		if(_site == null)
			throw new UnsupportedOperationException("지원하지 않는 사이트");
		return _site;
	}

	public void setSite(Map<String, Config> sites)
	{
		this.site = sites;
		final Config _default = sites.get("default");
		if (_default != null)
		{
			sites.values().forEach(s -> {
				if (!s.equals(_default))
				{
					if (s.type == null)
					{
						s.type = _default.type;
					}
					if (s.order == null)
					{
						s.order = _default.order;
					}
					if (s.fileSize == null)
					{
						s.fileSize = _default.fileSize;
					}
					if (s.totalSize == null)
					{
						s.totalSize = _default.totalSize;
					}
				}
			});
		}
	}

	public static class Config implements Serializable
	{
		// root: default
		// max: 10M
		// size: 10M
		// allow: gif,jpg,pdf
		// deny: exe, php
		// order: allow

		private static final long serialVersionUID = -5962800720029912756L;
		private String root;
		private String fileSize;
		private String totalSize;
		private String type;
		private Order order;

		public String getRoot()
		{
			return root;
		}

		public void setRoot(String root)
		{
			this.root = root;
		}

		public String getFileSizeMax()
		{
			return fileSize;
		}
		
		public long getFileSizeMaxLong()
		{
			return calculation(fileSize);
		}

		public void setFileSizeMax(String fileSize)
		{
			this.fileSize = fileSize.replace("_", "").trim();
		}

		public String getSizeMax()
		{
			return totalSize;
		}

		public long getSizeMaxLong()
		{
			return calculation(totalSize);
		}
		
		public void setSizeMax(String size)
		{
			this.totalSize = size.replace("_", "").trim();
		}


		public String getType()
		{
			return type;
		}

		public void setType(String type)
		{
			this.type = type;
		}

		public Order getOrder()
		{
			return order;
		}

		public void setOrder(Order order)
		{
			this.order = order;
		}

		public static long calculation(String bytes)
		{
			Pattern p = Pattern.compile("([0-9]+)(\\w*)");
			Matcher matcher = p.matcher(bytes);
			if (matcher.find()) 
			{
				String size = matcher.group(1);
				long _size = Long.parseLong(size);
				String unit = matcher.group(2);
				if(unit == null || unit.isEmpty())
				{
					return _size;
				}else
				{
					String[] s =
							{ "b", "k", "m", "g", "t", "p" };
					unit = unit.toLowerCase();
					for (int i = 0; i < s.length; i++)
					{
						if(unit.startsWith(s[i]))
						{
							return _size * (long)Math.pow(1024, i);
						}
					}
				}
			}
			return 0L;
		}
		
		/*public String byteCalculation(String bytes)
		{
			String retFormat = "0";
			Double size = Double.parseDouble(bytes);

			String[] s =
				{ "bytes", "KB", "MB", "GB", "TB", "PB" };

			if (bytes != "0")
			{
				int idx = (int) Math.floor(Math.log(size) / Math.log(1024));
				DecimalFormat df = new DecimalFormat("#,###.##");
				double ret = ((size / Math.pow(1024, Math.floor(idx))));
				retFormat = df.format(ret) + " " + s[idx];
			} else
			{
				retFormat += " " + s[0];
			}

			return retFormat;
		}*/
		
	}
}
