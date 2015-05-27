package com.github.samuelbr.configtree;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.VFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class ClassInputStreamProvider {

	private static final Logger LOG = LoggerFactory.getLogger(ClassInputStreamProvider.class);
	
	private static final String CLASS_EXTENSION = "class";
	
	private List<String> paths;
	
	private List<FileObject> currentObjects;
	
	private FileSystemManager fileSystemManager;
	
	public ClassInputStreamProvider(String ... paths) {
		this.paths = Lists.newArrayList(paths);
		currentObjects = Lists.newArrayListWithExpectedSize(1000);
	}
	
	public boolean hasMore() throws FileSystemException {
		if (fileSystemManager == null) {
			fileSystemManager = VFS.getManager();
		}
		
		if (!currentObjects.isEmpty()) {
			return true;
		}
		
		walkForNewFileObjects();
		
		return !currentObjects.isEmpty();
	}
	
	public InputStream getNext() throws FileSystemException {
		Preconditions.checkState(!currentObjects.isEmpty());
		
		FileObject fileObject = currentObjects.remove(0);
		
		return fileObject.getContent().getInputStream();
	}
	
	private void walkForNewFileObjects() throws FileSystemException {
		if (paths.isEmpty()) {
			return;
		}
		
		String newPath = paths.remove(0);
		
		FileObject fileObject = fileSystemManager.resolveFile(newPath);
		walkFileObject(fileObject);
	}
	
	private void walkFileObject(FileObject fileObject) throws FileSystemException {
		FileType type = fileObject.getType();
		
		if (type.hasChildren()) {
			for (FileObject child: fileObject.getChildren()) {
				try {
					walkFileObject(child);
				} catch (FileSystemException e) {
					LOG.error("Cannot process FileObject", e);
				}
			}
		}
		
		FileName fileName = fileObject.getName();
		
		if (type.hasContent() && CLASS_EXTENSION.equalsIgnoreCase(fileName.getExtension())) {
			currentObjects.add(fileObject);
		}
	}
	
	
}
