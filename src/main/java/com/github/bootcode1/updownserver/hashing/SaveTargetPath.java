package com.github.bootcode1.updownserver.hashing;

import java.nio.file.Path;

@FunctionalInterface
public interface SaveTargetPath
{
	//파일명과 패스를 가지고 새로운 패스 유도
 	Path path(String fileName);
}
