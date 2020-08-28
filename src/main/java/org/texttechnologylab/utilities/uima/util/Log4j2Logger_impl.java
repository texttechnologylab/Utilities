/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.texttechnologylab.utilities.uima.util;

import org.apache.uima.internal.util.I18nUtil;
import org.apache.uima.resource.ResourceManager;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;

import java.io.OutputStream;
import java.io.PrintStream;
import java.text.MessageFormat;

/**
 * UIMA Logging interface implementation for Log4j2
 */
public class Log4j2Logger_impl implements Logger {

	private static final String EXCEPTION_MESSAGE = "Exception occurred";

	/**
	 * logger object from the underlying Log4j logging framework
	 */
	private org.apache.logging.log4j.Logger logger = null;

	/**
	 * ResourceManager whose extension ClassLoader will be used to locate the
	 * message digests. Null will cause the ClassLoader to default to
	 * this.class.getClassLoader().
	 */
	private ResourceManager mResourceManager = null;

	/**
	 * create a new LogWrapper class for the specified source class
	 *
	 * @param component specified source class
	 */
	private Log4j2Logger_impl(Class<?> component) {
		super();

		if (component != null) {
			logger = org.apache.logging.log4j.LogManager.getLogger(component.getName());
		} else {
			logger = org.apache.logging.log4j.LogManager.getLogger("org.apache.uima");
		}
	}

	/**
	 * create a new LogWrapper object with the default logger from the Log4j
	 * logging framework
	 */
	private Log4j2Logger_impl() {
		this(null);
	}

	/**
	 * Creates a new Log4jLogger instance for the specified source class
	 *
	 * @param component current source class
	 * @return Logger returns the JSR47Logger object for the specified class
	 */
	public static synchronized Logger getInstance(Class<?> component) {
		return new Log4j2Logger_impl(component);
	}

	/**
	 * Creates a new Log4jLogger instance with the default Log4j framework logger
	 *
	 * @return Logger returns the JSR47Logger object with the default Log4j
	 * framework logger
	 */
	public static synchronized Logger getInstance() {
		return new Log4j2Logger_impl();
	}

	/**
	 * Logs a message with level INFO.
	 *
	 * @param aMessage the message to be logged
	 * @deprecated use new function with log level
	 */
	@Deprecated
	public void log(String aMessage) {
		if (isLoggable(Level.INFO)) {
			if (aMessage == null || aMessage.equals(""))
				return;

			String[] sourceInfo = getStackTraceInfo(new Throwable());

			org.apache.logging.log4j.LogManager.getLogger(sourceInfo[0]).log(org.apache.logging.log4j.Level.INFO, aMessage);
		}
	}

	/**
	 * Logs a message with a message key and the level INFO
	 *
	 * @see org.apache.uima.util.Logger#log(java.lang.String, java.lang.String,
	 * java.lang.Object[])
	 * @deprecated use new function with log level
	 */
	@Deprecated
	public void log(String aResourceBundleName, String aMessageKey,
					Object[] aArguments) {
		if (isLoggable(Level.INFO)) {
			if (aMessageKey == null || aMessageKey.equals(""))
				return;

			String[] sourceInfo = getStackTraceInfo(new Throwable());
			org.apache.logging.log4j.LogManager.getLogger(sourceInfo[0]).log(
					org.apache.logging.log4j.Level.INFO,
					I18nUtil.localizeMessage(aResourceBundleName, aMessageKey,
							aArguments, getExtensionClassLoader()));
		}
	}

	/**
	 * Logs an exception with level INFO
	 *
	 * @param aException the exception to be logged
	 * @deprecated use new function with log level
	 */
	@Deprecated
	public void logException(Exception aException) {
		if (isLoggable(Level.INFO)) {
			if (aException == null)
				return;

			String[] sourceInfo = getStackTraceInfo(new Throwable());

			// log exception
			org.apache.logging.log4j.LogManager.getLogger(sourceInfo[0]).log(
					org.apache.logging.log4j.Level.INFO, EXCEPTION_MESSAGE, aException);
		}
	}

	/**
	 * @see org.apache.uima.util.Logger#setOutputStream(java.io.OutputStream)
	 * @deprecated use external configuration possibility
	 */
	@Deprecated
	public void setOutputStream(OutputStream out) {
		throw new UnsupportedOperationException(
				"Method setOutputStream(OutputStream out) not supported");
	}

	/**
	 * @see org.apache.uima.util.Logger#setOutputStream(java.io.PrintStream)
	 * @deprecated use external configuration possibility
	 */
	@Deprecated
	public void setOutputStream(PrintStream out) {
		throw new UnsupportedOperationException(
				"Method setOutputStream(PrintStream out) not supported");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.uima.util.Logger#isLoggable(org.apache.uima.util.Level)
	 */
	public boolean isLoggable(Level level) {
		org.apache.logging.log4j.Level log4jLevel = getLog4jLevel(level);

		return logger.isEnabled(log4jLevel);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.uima.util.Logger#setLevel(org.apache.uima.util.Level)
	 */
	public void setLevel(Level level) {
		// get corresponding Log4j level
		org.apache.logging.log4j.Level log4jLevel = getLog4jLevel(level);

		logger.atLevel(log4jLevel);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.uima.util.Logger#log(org.apache.uima.util.Level,
	 *      java.lang.String)
	 */
	public void log(Level level, String aMessage) {
		if (isLoggable(level)) {
			if (aMessage == null || aMessage.equals(""))
				return;

			org.apache.logging.log4j.Level log4jLevel = getLog4jLevel(level);

			logger.log(log4jLevel, aMessage);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.uima.util.Logger#log(org.apache.uima.util.Level,
	 *      java.lang.String, java.lang.Object)
	 */
	public void log(Level level, String aMessage, Object param1) {
		if (isLoggable(level)) {
			if (aMessage == null || aMessage.equals(""))
				return;
			org.apache.logging.log4j.Level log4jLevel = getLog4jLevel(level);

			logger.log(log4jLevel, MessageFormat.format(aMessage,
					new Object[]{param1}));
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.uima.util.Logger#log(org.apache.uima.util.Level,
	 *      java.lang.String, java.lang.Object[])
	 */
	public void log(Level level, String aMessage, Object[] params) {
		if (isLoggable(level)) {
			if (aMessage == null || aMessage.equals(""))
				return;

			// get corresponding Log4j level
			org.apache.logging.log4j.Level log4jLevel = getLog4jLevel(level);

			logger.log(log4jLevel, MessageFormat.format(aMessage, params));

		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.uima.util.Logger#log(org.apache.uima.util.Level,
	 *      java.lang.String, java.lang.Throwable)
	 */
	public void log(Level level, String aMessage, Throwable thrown) {
		if (isLoggable(level)) {
			org.apache.logging.log4j.Level log4jLevel = getLog4jLevel(level);

			if (aMessage != null && !aMessage.equals("")) {
				// get corresponding Log4j level

				logger.log(log4jLevel, aMessage, thrown);
			}

			if (thrown != null && (aMessage == null || aMessage.equals(""))) {
				// get corresponding Log4j level
				// log exception
				logger.log(log4jLevel, EXCEPTION_MESSAGE, thrown);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.uima.util.Logger#logrb(org.apache.uima.util.Level,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.Object)
	 */
	public void logrb(Level level, String sourceClass, String sourceMethod,
					  String bundleName, String msgKey, Object param1) {
		if (isLoggable(level)) {
			if (msgKey == null || msgKey.equals(""))
				return;

			if (sourceClass == null) {
				sourceClass = "";
			}

			// get corresponding Log4j level
			org.apache.logging.log4j.Level log4jLevel = getLog4jLevel(level);

			logger.log(log4jLevel, I18nUtil.localizeMessage(bundleName, msgKey,
					new Object[]{param1}, getExtensionClassLoader()));
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.uima.util.Logger#logrb(org.apache.uima.util.Level,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.Object[])
	 */
	public void logrb(Level level, String sourceClass, String sourceMethod,
					  String bundleName, String msgKey, Object[] params) {
		if (isLoggable(level)) {
			if (msgKey == null || msgKey.equals(""))
				return;
			if (sourceClass == null) {
				sourceClass = "";
			}

			// get corresponding Log4j level
			org.apache.logging.log4j.Level log4jLevel = getLog4jLevel(level);

			logger.log(log4jLevel, I18nUtil.localizeMessage(bundleName, msgKey,
					params, getExtensionClassLoader()));
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.uima.util.Logger#logrb(org.apache.uima.util.Level,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.Throwable)
	 */
	public void logrb(Level level, String sourceClass, String sourceMethod,
					  String bundleName, String msgKey, Throwable thrown) {
		if (isLoggable(level)) {
			org.apache.logging.log4j.Level log4jLevel = getLog4jLevel(level);

			if (sourceClass == null) {
				sourceClass = "";
			}

			if (msgKey != null && !msgKey.equals("")) {
				// get corresponding Log4j level
				org.apache.logging.log4j.LogManager.getLogger(sourceClass).log(
						log4jLevel,
						I18nUtil.localizeMessage(bundleName, msgKey, null,
								getExtensionClassLoader()), thrown);
			}

			if (thrown != null && (msgKey == null || msgKey.equals(""))) {

				// log exception
				org.apache.logging.log4j.LogManager.getLogger(sourceClass).log(log4jLevel,
						EXCEPTION_MESSAGE, thrown);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.uima.util.Logger#logrb(org.apache.uima.util.Level,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String)
	 */
	public void logrb(Level level, String sourceClass, String sourceMethod,
					  String bundleName, String msgKey) {
		if (isLoggable(level)) {

			if (msgKey == null || msgKey.equals(""))
				return;

			if (sourceClass == null) {
				sourceClass = "";
			}
			// get corresponding Log4j level
			org.apache.logging.log4j.Level log4jLevel = getLog4jLevel(level);
			org.apache.logging.log4j.LogManager.getLogger(sourceClass).log(log4jLevel,
					I18nUtil.localizeMessage(bundleName, msgKey, null, getExtensionClassLoader()));

			// logger.log(log4jLevel, sourceClass + sourceMethod +
			// I18nUtil.localizeMessage(bundleName, msgKey, null,
			// getExtensionClassLoader()));
		}
	}

	public void log(String wrapperFQCN, Level level, String message, Throwable thrown) {
		// get corresponding Log4j level
		org.apache.logging.log4j.Level log4jLevel = getLog4jLevel(level);
		logger.log(log4jLevel, message, thrown);
//     logger.log(wrapperFQCN, log4jLevel, message, thrown);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.uima.util.Logger#setResourceManager(org.apache.uima.resource.ResourceManager)
	 */
	public void setResourceManager(ResourceManager resourceManager) {
		mResourceManager = resourceManager;
	}

	/**
	 * Gets the extension ClassLoader to used to locate the message digests. If
	 * this returns null, then message digests will be searched for using
	 * this.class.getClassLoader().
	 */
	private ClassLoader getExtensionClassLoader() {
		if (mResourceManager == null)
			return null;
		else
			return mResourceManager.getExtensionClassLoader();
	}

	/**
	 * log4j level mapping to UIMA level mapping. SEVERE (highest value) ->
	 * SEVERE WARNING -%gt; WARNING INFO -%gt; INFO CONFIG -%gt; CONFIG FINE -%gt; FINE FINER ->
	 * FINER FINEST (lowest value) -%gt; FINEST OFF -%gt; OFF ALL -%gt; ALL
	 *
	 * @param level uima level
	 * @return Level - corresponding JSR47 level
	 */
	private org.apache.logging.log4j.Level getLog4jLevel(Level level) {
		switch (level.toInteger()) {
			case org.apache.uima.util.Level.OFF_INT:
				return org.apache.logging.log4j.Level.OFF;
			case org.apache.uima.util.Level.SEVERE_INT:
				return org.apache.logging.log4j.Level.ERROR;
			case org.apache.uima.util.Level.WARNING_INT:
				return org.apache.logging.log4j.Level.WARN;
			case org.apache.uima.util.Level.INFO_INT:
				return org.apache.logging.log4j.Level.INFO;
			case org.apache.uima.util.Level.CONFIG_INT:
				return org.apache.logging.log4j.Level.INFO;
			case org.apache.uima.util.Level.FINE_INT:
				return org.apache.logging.log4j.Level.DEBUG;
			case org.apache.uima.util.Level.FINER_INT:
				return org.apache.logging.log4j.Level.ALL;
			case org.apache.uima.util.Level.FINEST_INT:
				return org.apache.logging.log4j.Level.ALL;
			default: // for all other cases return Level.ALL
				return org.apache.logging.log4j.Level.ALL;
		}
	}

	/**
	 * returns the method name and the line number if available
	 *
	 * @param thrown the thrown
	 * @return String[] - fist element is the source class, second element is the
	 * method name with linenumber if available
	 */
	private String[] getStackTraceInfo(Throwable thrown) {
		StackTraceElement[] stackTraceElement = thrown.getStackTrace();

		String sourceMethod = "";
		String sourceClass = "";
		int lineNumber = 0;
		try {
			lineNumber = stackTraceElement[1].getLineNumber();
			sourceMethod = stackTraceElement[1].getMethodName();
			sourceClass = stackTraceElement[1].getClassName();
		} catch (Exception ex) {
			// do nothing, use the initialized string members
		}

		if (lineNumber > 0) {
			StringBuffer buffer = new StringBuffer(25);
			buffer.append(sourceMethod);
			buffer.append('(');
			buffer.append(lineNumber);
			buffer.append(')');
			sourceMethod = buffer.toString();
		}

		return new String[]{sourceClass, sourceMethod};
	}
}
