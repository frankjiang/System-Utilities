/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * FileIterator.java is PROPRIETARY/CONFIDENTIAL built in 4:45:15 PM, Jul 24,
 * 2016.
 * Use is subject to license terms.
 */

package com.frank.sys;

import java.io.File;
import java.io.FileFilter;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * The file iterator class.
 * <p>
 * The iterator can iterate the specified file from the specified root. If
 * recursive iteration is need, just accept directories in the
 * {@linkplain FileFilter}.
 * </p>
 * 
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class FileIterator implements Iterator<File>
{
	/**
	 * The file stack.
	 */
	protected Stack<File>	stack;

	/**
	 * The file filter.
	 */
	protected FileFilter	filter;

	/**
	 * Construct an instance of <tt>FileIterator</tt>.
	 */
	public FileIterator(File root, FileFilter filter)
	{
		if (filter == null)
			throw new NullPointerException("The file filter is null.");
		this.filter = filter;
		stack = new Stack<>();
		stack.add(root);
	}

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext()
	{
		if (stack.isEmpty())
			return false;
		File top = stack.peek();
		if (top.isDirectory())
		{
			boolean flag = false;
			while (!stack.isEmpty() && !flag)
			{
				top = stack.pop();
				for (File file : top.listFiles(filter))
				{
					stack.push(file);
					if (file.isFile())
						flag = true;
				}
			}
			return flag;
		}
		else
			return true;
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	@Override
	public File next()
	{
		File top;
		try
		{
			top = stack.pop();
		}
		catch (EmptyStackException e)
		{
			throw new NoSuchElementException("There is no more files.");
		}
		if (top.isDirectory())
		{
			while (top.isDirectory())
			{
				for (File file : top.listFiles(filter))
					stack.push(file);
				if (stack.isEmpty())
					throw new NoSuchElementException("There is no more files");
				top = stack.pop();
			}
			return top;
		}
		else
			return top;
	}

}
