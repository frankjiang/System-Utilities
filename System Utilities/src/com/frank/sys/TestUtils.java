/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved. PathUtil.java is built in 2012-11-15.
 */
package com.frank.sys;

import java.util.concurrent.TimeUnit;

/**
 * The testing utilities for software development.
 * <p>
 * In this class, a collection of testing utilities is designed, including
 * program running timer.
 * </p>
 * 
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class TestUtils
{
	/**
	 * The program running timer.
	 * 
	 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
	 * @version 1.0.0
	 */
	public final static class Timer
	{
		/**
		 * The accuracy of the timer.
		 */
		public static final TimeUnit	sourceUnit	= TimeUnit.NANOSECONDS;
		/**
		 * The time of beginning to record.
		 */
		private long					begin;

		/**
		 * Construct a timer.
		 */
		Timer()
		{
			begin = 0;
		}

		/**
		 * Notify the timer and start to record the time.
		 */
		public void start()
		{
			begin = System.nanoTime();
		}

		/**
		 * Returns the time spent according to the system clock. The time spent
		 * value will be in the default time unit - milliseconds
		 * {@linkplain TimeUnit#MILLISECONDS}.
		 * 
		 * @return the time spent
		 */
		public long getTime()
		{
			return getTime(TimeUnit.MILLISECONDS);
		}

		/**
		 * Returns the time spent according to the system clock. The time spent
		 * value will be in the specified time unit.
		 * 
		 * @param timeunit
		 *            the specified time unit
		 * @return the time spent
		 */
		public long getTime(java.util.concurrent.TimeUnit timeunit)
		{
			return timeunit.convert(System.nanoTime() - begin, sourceUnit);
		}

		/**
		 * Display the time spent in console lines with default time unit.
		 */
		public void display()
		{
			System.out.printf("TIME = %d %s\n", getTime(),
					sourceUnit.toString());
		}

		/**
		 * Display the time spent in console lines with specified time unit.
		 * 
		 * @param timeunit
		 *            the specified time unit
		 */
		public void display(java.util.concurrent.TimeUnit timeunit)
		{
			System.out.printf("TIME = %d %s\n", getTime(timeunit),
					timeunit.toString());
		}
	}

	/**
	 * Returns an instance of the timer.
	 * 
	 * @return the timer
	 */
	public static Timer getTimer()
	{
		return new Timer();
	}
}
