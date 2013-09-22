package com.ouser.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.os.AsyncTask;

import com.ouser.logger.Logger;

public class FileUtil {

	private static final Logger logger = new Logger("FileUtil");

	public static String read(String fileFullName) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileFullName));
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line + "\r\n");
			}
			br.close();

		} catch (FileNotFoundException e) {
			logger.w("FileNotFoundException " + fileFullName);
		} catch (IOException e) {
			logger.w("IOException " + fileFullName);
		}
		return sb.toString().trim();
	}

	public static void write(String fileFullName, String message, boolean append) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(fileFullName, append);
			writer.write(message);
			writer.flush();

		} catch (FileNotFoundException e) {
			logger.w("FileNotFoundException " + fileFullName);
			return;
		} catch (IOException e) {
			logger.w("IOException " + fileFullName);
			return;
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				logger.w("IOException " + fileFullName);
			}
		}
	}

	/**
	 * 将Bitmap转换成字符串
	 */
	public static String toBase64(String fileFullName) {
		String result = null;
		FileInputStream fis = null;
		try {
			File file = new File(fileFullName);
			fis = new FileInputStream(file);
			byte[] buffer = new byte[(int) file.length()];
			fis.read(buffer);
			fis.close();
			result = Base64.encode(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public static void fromBase64(String text, String fileFullName) {
		FileOutputStream fos = null;
		try {
			byte[] buffer = Base64.decode(text);
			FileOutputStream out = new FileOutputStream(fileFullName);
	        out.write(buffer);
	        out.close();
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(fos != null) {
					fos.close();
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void removePathAsync(String path) {
		new FileDeleter().execute(path);
	}

	public static void removePathSync(String path) {
		removePath(path);
	}

	public static String filterName(String value) {
		String ret = value.replaceAll("[: /\\*?\"<>|]*", "");
		if ("".equals(ret)) {
			return "null";
		} else {
			return ret;
		}
	}

	private static void removePath(String value) {
		try {
			File path = new File(value);
			if (!path.exists()) {
				return;
			}
			for (File file : path.listFiles()) {
				file.delete();
			}
			path.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static class FileDeleter extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... arg0) {
			removePath(arg0[0]);
			return null;
		}
	}
}
