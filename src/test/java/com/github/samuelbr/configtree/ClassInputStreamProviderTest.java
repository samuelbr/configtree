package com.github.samuelbr.configtree;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class ClassInputStreamProviderTest {

	@Test
	public void testForDirectory() throws IOException {
		String testDirectoryPath = getTestFilePath("directory");
		ClassInputStreamProvider provider = new ClassInputStreamProvider(testDirectoryPath);

		assertEquals(6, count(provider));
	}
	
	@Test
	public void testForJar() throws IOException {
		String testDirectoryPath = getTestFilePath("javax.servlet-api-3.1.0.jar");
		ClassInputStreamProvider provider = new ClassInputStreamProvider(testDirectoryPath);

		assertEquals(6, count(provider));
		
	}
	
	private String getTestFilePath(String path) {
		ClassLoader classLoader = ClassInputStreamProviderTest.class.getClassLoader();
		
		return classLoader.getResource("classInputStreamProvider/"+path).getFile();
	}
	
	private int count(ClassInputStreamProvider provider) throws IOException {
		
		int count = 0;
		while (provider.hasMore()) {
			InputStream is = provider.getNext();
			is.close();
			
			count++;
		}
		return count;
	}
	
}
