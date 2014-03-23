/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * Messages.java is PROPRIETARY/CONFIDENTIAL built in 10:14:35 PM, Mar 23, 2014.
 * Use is subject to license terms.
 */
package com.frank.sys;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * The messages bundler.
 * <p>
 * In this bundler, the messages can get from this bundle.
 * </p>
 * 
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class Messages extends ResourceBundle
{
	/**
	 * The inner bundle.
	 */
	protected ResourceBundle	bundle;

	/**
	 * Construct an instance of <tt>Messages</tt>.
	 * 
	 * @param baseName
	 *            the base name of the resource bundle, a fully qualified class
	 *            name
	 * @exception java.lang.NullPointerException
	 *                if <code>baseName</code> is <code>null</code>
	 * @exception MissingResourceException
	 *                if no resource bundle for the specified base name can be
	 *                found
	 */
	public Messages(String baseName)
	{
		bundle = ResourceBundle.getBundle(baseName);
	}

	/**
	 * Returns an enumeration of the keys.
	 * 
	 * @return an <code>Enumeration</code> of the keys contained in
	 *         this <code>ResourceBundle</code> and its parent bundles.
	 * @see java.util.ResourceBundle#getKeys()
	 */
	@Override
	public Enumeration<String> getKeys()
	{
		return bundle.getKeys();
	}

	/**
	 * Returns the message which is bounded with class type.
	 * 
	 * @param c
	 *            the class type
	 * @param key
	 *            the key of the message
	 * @return the message
	 */
	public String getString(Class c, String key)
	{
		return getString(String.format("%s.%s", c.getName(), key));
	}

	/**
	 * Return the formatted message according to the desired key.
	 * 
	 * @param c
	 *            the class type
	 * @param key
	 *            the key for the desired object
	 * @param args
	 *            Arguments referenced by the format specifiers in the format
	 *            string. If there are more arguments than format specifiers,
	 *            the extra arguments are ignored. The number of arguments is
	 *            variable and may be zero. The maximum number of arguments is
	 *            limited by the maximum dimension of a Java array as defined by
	 *            <cite>The Java&trade; Virtual Machine Specification</cite>.
	 * @return the message
	 */
	public String getString(Class c, String key, Object... args)
	{
		return String.format(getString(c, key), args);
	}

	/**
	 * Returns the message for the desired object.
	 * 
	 * @param obj
	 *            the specified object
	 * @param key
	 *            the key for the desired object
	 * @return the message
	 */
	public String getString(Object obj, String key)
	{
		return getString(obj.getClass(), key);
	}

	/**
	 * Returns the formatted message according to the specified object and the
	 * bundle key.
	 * 
	 * @param obj
	 *            the specified object
	 * @param key
	 *            the desired key
	 * @param args
	 *            Arguments referenced by the format specifiers in the format
	 *            string. If there are more arguments than format specifiers,
	 *            the extra arguments are ignored. The number of arguments is
	 *            variable and may be zero. The maximum number of arguments is
	 *            limited by the maximum dimension of a Java array as defined by
	 *            <cite>The Java&trade; Virtual Machine Specification</cite>.
	 * @return the formatted message
	 */
	public String getString(Object obj, String key, Object... args)
	{
		return String.format(getString(obj, key), args);
	}

	/**
	 * Returns the formatted message.
	 * 
	 * @param format
	 *            A format string
	 * @param args
	 *            Arguments referenced by the format specifiers in the format
	 *            string. If there are more arguments than format specifiers,
	 *            the extra arguments are ignored. The number of arguments is
	 *            variable and may be zero. The maximum number of arguments is
	 *            limited by the maximum dimension of a Java array as defined by
	 *            <cite>The Java&trade; Virtual Machine Specification</cite>.
	 * @return the formatted message
	 */
	public String getString(String key, Object... args)
	{
		return String.format(getString(key), args);
	}

	/**
	 * Gets an object for the given key from this resource bundle.
	 * Returns null if this resource bundle does not contain an
	 * object for the given key.
	 * 
	 * @param key
	 *            the key for the desired object
	 * @exception NullPointerException
	 *                if <code>key</code> is <code>null</code>
	 * @return the object for the given key, or <code>null</code>
	 * @see java.util.ResourceBundle#handleGetObject(java.lang.String)
	 */
	@Override
	protected Object handleGetObject(String key)
	{
		return bundle.getString(key);
	}
}
