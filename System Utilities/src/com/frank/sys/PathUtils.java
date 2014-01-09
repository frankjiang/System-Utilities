/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * PathUtil.java is built in 2013-2-14.
 */
package com.frank.sys;

import java.io.File;
import java.io.IOException;

/**
 * The utilities about system file and its path.
 * 
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class PathUtils
{
	/**
	 * Returns the file separator of the current system.
	 * 
	 * @return the file separator
	 */
	public static String getFileSeparator()
	{
		return System.getProperty("file.separator");//$NON-NLS-1$
	}

	/**
	 * Returns the directory of current project in the format of
	 * "Disk:/Directory/".
	 * 
	 * @return the directory of current project
	 */
	public static String getCurrentDirectory()
	{
		return System.getProperty("user.dir")//$NON-NLS-1$
				+ System.getProperty("file.separator");//$NON-NLS-1$
	}

	/**
	 * Create the specified file if it not exists.
	 * 
	 * @param file
	 *            the specified file
	 * @return true if the file exists or created successfully, false otherwise.
	 * @throws IOException
	 *             If an I/O error occurred
	 */
	public static boolean createFileIfNotExist(File file) throws IOException
	{
		if (file.exists())
			return true;
		else
		{
			File parent = file.getParentFile();
			if (parent != null && parent.mkdirs())
				return file.createNewFile();
			else
				return false;
		}
	}

	/**
	 * Translate the specified string to UNICODE presented code string.
	 * 
	 * @param s
	 *            the specified string
	 * @return UNICODE presented code string
	 */
	public static String toUnicodeSymbols(String s)
	{
		StringBuffer sb = new StringBuffer();
		char a[] = s.toCharArray();
		for (char c : a)
			if (c < 256)
				sb.append(c);
			else if (c < 4096)
				sb.append(String.format("\\u0%h", (int) c));//$NON-NLS-1$
			else
				sb.append(String.format("\\u%h", (int) c));//$NON-NLS-1$
		return sb.toString();
	}
}
