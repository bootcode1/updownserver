package com.github.bootcode1.updownserver.hashing;

import java.nio.file.Path;

public class SaraminSavePath implements SaveTargetPath
{
	private Path sub;

	public SaraminSavePath(Path sub){
		this.sub = sub;
	}
	
	@Override
	public Path path(String fileName)
	{
		return sub.resolve(fileName);
	}
}
