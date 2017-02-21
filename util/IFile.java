/*
 * @project Tools
 * @package util
 * @file IFile.java
 * @author oliver.xyy
 * @pubTime 2016-12-23 16:59:08
 */
package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.apache.tools.zip.*;//need apache ant jar

/**
 * The Class IFile.
 */
public class IFile {
	/**
	 * Instantiates a new i file.
	 */
	public IFile() {}
	
	/**
	 * 新建文件.
	 *
	 * @param pathname 文件路径名
	 * @param fileContent the file content
	 * @return true, if successful
	 * @exception In linux system, system can not 
	 * use createNewFile() and mkdir()/mkdirs() to create file/folder with same 
	 * filename in same parent directory.
	 */
	public static File newFile(String pathname) {
		File file = new File(pathname);
		if(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
		return file;
	}
	
	/**
	 * 新建文件夹.
	 *
	 * @param path the path
	 * @param folderName the folder name
	 * @return true, if successful
	 * @exception In linux system, system can not 
	 * use createNewFile() and mkdir()/mkdirs() to create file/folder with same 
	 * filename in same parent directory. Waiting to explore the reason.
	 */
	public static boolean newFolder(String pathname) {
		File file = new File(pathname);
		return file.exists()&&file.isDirectory()?false:file.mkdirs()?true:false;
	}
	
	/**
	 * 读取文件.
	 *
	 * @param pathname 文件路径名
	 * @return the list
	 */
	public static List<String> read(String pathname) {
		BufferedReader reader = null;
		List<String> fileContent = new ArrayList<String>();
		try {
	        reader = new BufferedReader(new FileReader(new File(pathname)));
            String tmp_str = null;
            while ((tmp_str = reader.readLine()) != null) {
            	fileContent.add(tmp_str);
            }
		} catch (Exception ex) {
			return null;
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				return null;
			}
        }
		return fileContent;
	}
	
	/**
	 * 读写文件.
	 *
	 * @param pathname 文件路径名
	 * @param content 写入的内容
	 * @param flag true:append false:rewrite
	 * @return true, if successful
	 */
	public static boolean write(String pathname, String content, boolean flag) {
		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			newFile(pathname);
			fw = new FileWriter(pathname, flag);
			pw = new PrintWriter(fw);
			pw.println(content);
			return true;
		} catch (Exception ex) {
			return false;
		} finally {
			pw.close();
			try { fw.close(); } catch (IOException e) { return false; }
		}
	}

	/**
	 * 移动文件操作.
	 *
	 * @param oldPath  原始路径
	 * @param newPath  目标路径
	 * @return true, if successful
	 */
	public static boolean moveFile(String oldPath, String newPath) {
		File oFile = new File(oldPath);
		File nFile = new File(newPath);
		oFile.renameTo(nFile);
		return !oFile.exists()&nFile.exists();
	}

	/**
	 * 删除指定文件.
	 *
	 * @param pathname 目标文件路径
	 * @return true, if successful
	 */
	public static boolean delFile(String pathname) {
		try {
			File file = new File(pathname);
			return file.exists()?file.delete():false;
		} catch (Exception ex) {
			return false;
		}
	}
	
	/**
	 * 删除文件夹.
	 *
	 * @param folderPath 文件夹路径
	 * @return true, if successful
	 */
	public static boolean delFolder(String folderPath) {
		List<File> fileList = new ArrayList<File>();
		getAllFiles(fileList, folderPath);
		try {
			for(int i = fileList.size()-1;i>=0;i--){
				fileList.get(i).delete();
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 获取指定目录下的所有文件及其目录.
	 *
	 * @param fileList 存储文件路径名的list
	 * @param path 文件路径
	 * @return the all files
	 */
	public static List<File> getAllFiles(List<File> fileList, String path) {
		File file = new File(path);
		fileList.add(file);
		File[] files = file.listFiles();
		if (files != null) { 
			for (int i = 0; i < files.length; i++) {
				fileList.add(files[i]);
				if (files[i].isDirectory()) {
					getAllFiles(fileList,files[i].toString());
				}
			}
			return fileList;
		} else {
			return null;//when file is not exist
		}
	}

	/**
	 * 复制文件.
	 *
	 * @param oldPathName 原始路径名
	 * @param newPathName 目标路径名
	 * @return true, if successful
	 */
	public static boolean copyFile(String oldPathName, String newPathName) {
		InputStream inStream = null;
		FileOutputStream outStream = null;
		try {
			int index = 0;
			File oFile = new File(oldPathName);
			if (oFile.exists()) {
				inStream = new FileInputStream(oldPathName);// 读入文件
				outStream = new FileOutputStream(newPathName); // 写入文件
				byte[] buffer = new byte[1024];
				while ((index = inStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, index);
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			try {
				inStream.close();
				outStream.close();
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
	}

	/**
	 * 复制文件夹.
	 *
	 * @param oldPath 原始路径名
	 * @param newPath 目标路径名
	 * @return true, if successful
	 */
	public static boolean copyFolder(String oldPath, String newPath) {
		try {
			List<File> fileList = new ArrayList<File>();
			getAllFiles(fileList, oldPath);
			for(int i = 0;i<fileList.size();i++){
				if(fileList.get(i).isDirectory()){
					new File(fileList.get(i).getPath().replace(oldPath, newPath)).mkdirs();
				}else{
					copyFile(fileList.get(i).getPath(),fileList.get(i).getPath().replace(oldPath, newPath));
				}
			}
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * 获取文件夹的大小.
	 *
	 * @param file  当前文件夹
	 * @return long 文件夹大小
	 */
	public static long getFolderSize(File file) {
		long size = 0;
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				size = size + getFolderSize(files[i]);
			} else {
				size = size + files[i].length();
			}
		}
		return size;
	}

	/**
	 * 获取相对路径.
	 *
	 * @param rootPath 根目录
	 * @param path 文件目录
	 * @return 相对路径
	 */
	public static String getRelativePath(String rootPath, String path) {
		int index = rootPath.length();
		String relativePath = path.substring(index, path.length());
		return relativePath;
	}
	
	/**
	 * 压缩文件.
	 *
	 * @param pathname the path name
	 * @param zipfile  目标压缩文件
	 * @return true, if successful
	 */
	public static boolean zipFiles(String pathname, File zipfile) {
		File file = new File(pathname);
		File[] files = file.listFiles();
		byte[] buf = new byte[1024];
		ZipOutputStream out = null;
		try {
			out = new ZipOutputStream(new FileOutputStream(zipfile));
			for (int i = 0; i < files.length; i++) {
				FileInputStream in = new FileInputStream(files[i]);
				out.putNextEntry(new ZipEntry(files[i].getName()));
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.closeEntry();
				in.close();
			}
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
	}

	/**
	 * 解压缩选中文件.
	 *
	 * @param unZipfileName  目标压缩文件
	 * @return true, if successful
	 */
	public static boolean unzipFiles(String unZipfileName) {
		FileOutputStream fileOut = null;
		File file = null;
		InputStream inputStream = null;
		ZipFile zipFile = null;
		byte[] buf = new byte[1024];
		int readedBytes;
		try {
			zipFile = new ZipFile(unZipfileName);
			for (Enumeration<ZipEntry> entries = zipFile.getEntries(); entries.hasMoreElements();) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				file = new File(entry.getName());
				if (entry.isDirectory()) {
					file.mkdirs();
				} else {
					File parent = file.getParentFile();
					if (!parent.exists()) {
						parent.mkdirs();
					}
					inputStream = zipFile.getInputStream(entry);
					fileOut = new FileOutputStream(file);
					while ((readedBytes = inputStream.read(buf)) > 0) {
						fileOut.write(buf, 0, readedBytes);
					}
					fileOut.close();
					inputStream.close();
				}
			}
			return true;
		} catch (IOException ioe) {
			return false;
		} finally {
			try {
				zipFile.close();
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
	}
}
