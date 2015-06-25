/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * SuffixFilter.java is PROPRIETARY/CONFIDENTIAL built in 10:47:44 PM, Jul 13,
 * 2015.
 * Use is subject to license terms.
 */
package com.frank.sys;

import java.io.File;
import java.io.FilenameFilter;

/**
 * The suffix filter.
 * <p>
 * Implemenetation of a {@link FilenameFilter} that filters the files according
 * to their suffix.
 * </p>
 * 
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class SuffixFilter implements FilenameFilter
{
	/**
	 * The accepted filename suffix.
	 */
	protected String[]	suffix;

	/**
	 * Construct an instance of <tt>SuffixFilter</tt>.
	 * 
	 * @param suffix
	 *            the accpeted filename suffix
	 */
	public SuffixFilter(String... suffix)
	{
		if (suffix.length == 0)
			throw new IllegalArgumentException("The suffix is not specified!");
	}

	/**
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	@Override
	public boolean accept(File dir, String name)
	{
		boolean b = false;
		name = name.toLowerCase();
		for (String s : suffix)
			if (name.endsWith(s))
			{
				b = true;
				break;
			}
		return b;
	}

	/**
	 * Returns a image file filter whose suffix is ".jpg", ".png", ".bmp",
	 * ".ppm", ".jpeg" or ".gif".
	 * 
	 * @return a image file filter
	 */
	public static SuffixFilter getImageFileFilter()
	{
		return new SuffixFilter(".jpg", ".png", ".bmp", ".ppm", ".jpeg", ".gif");
	}
}
