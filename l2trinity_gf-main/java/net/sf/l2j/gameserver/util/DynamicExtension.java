/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.l2j.gameserver.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.l2j.Config;

/**
 * extension loader for l2j
 * @author galun
 * @version $Id: DynamicExtension.java,v 1.3 2006/05/14 17:19:39 galun Exp $
 */
public class DynamicExtension
{
	private static Logger _log = Logger.getLogger(DynamicExtension.class.getCanonicalName());
	private JarClassLoader _classLoader;
	private Properties _prop;
	private ConcurrentHashMap<String, Object> _loadedExtensions;
	private ConcurrentHashMap<String, ExtensionFunction> _getters;
	private ConcurrentHashMap<String, ExtensionFunction> _setters;
	
	/**
	 * create an instance of DynamicExtension
	 * this will be done by GameServer according to the altsettings.properties
	 *
	 */
	private DynamicExtension()
	{
		_getters = new ConcurrentHashMap<String, ExtensionFunction>();
		_setters = new ConcurrentHashMap<String, ExtensionFunction>();
		initExtensions();
	}
	
	/**
	 * get the singleton of DynamicInstance
	 * @return the singleton instance
	 */
	public static DynamicExtension getInstance()
	{
		return SingletonHolder._instance;
	}
	
	/**
	 * get an extension object by class name
	 * @param className he class name as defined in the extension properties
	 * @return the object or null if not found
	 */
	public Object getExtension(String className)
	{
		return _loadedExtensions.get(className);
	}
	
	/**
	 * initialize all configured extensions
	 *
	 */
	public String initExtensions()
	{
		_prop = new Properties();
		String res = "";
		_loadedExtensions = new ConcurrentHashMap<String, Object>();
		try
		{
			_prop.load(new FileInputStream(Config.EXTENSIONS_CONFIG_FILE));
		}
		catch (FileNotFoundException ex)
		{
			_log.info(ex.getMessage() + ": no extensions to load");
		}
		catch (Exception ex)
		{
			_log.log(Level.WARNING, "could not load properties", ex);
		}
		_classLoader = new JarClassLoader();
		for (Object o : _prop.keySet())
		{
			String k = (String) o;
			if (k.endsWith("Class"))
			{
				res += initExtension(_prop.getProperty(k)) + "\n";
			}
		}
		return res;
	}
	
	/**
	 * init a named extension
	 * @param name the class name and optionally a jar file name delimited with a '@' if the jar file is not
	 * in the class path
	 */
	public String initExtension(String name)
	{
		String className = name;
		String[] p = name.split("@");
		String res = name + " loaded";
		if (p.length > 1)
		{
			_classLoader.addJarFile(p[1]);
			className = p[0];
		}
		if (_loadedExtensions.containsKey(className))
			return "already loaded";
		try
		{
			Class<?> extension = Class.forName(className, true, _classLoader);
			Object obj = extension.newInstance();
			extension.getMethod("init", new Class[0]).invoke(obj, new Object[0]);
			_log.info("Extension " + className + " loaded.");
			_loadedExtensions.put(className, obj);
		}
		catch (Exception ex)
		{
			_log.log(Level.WARNING, name, ex);
			res = ex.toString();
		}
		return res;
	}
	
	/**
	 * create a new class loader which resets the cache (jar files and loaded classes)
	 * on next class loading request it will read the jar again
	 */
	protected void clearCache()
	{
		_classLoader = new JarClassLoader();
	}
	
	/**
	 * call unloadExtension() for all known extensions
	 *
	 */
	public String unloadExtensions()
	{
		String res = "";
		for (String e : _loadedExtensions.keySet())
			res += unloadExtension(e) + "\n";
		return res;
	}
	
	/**
	 * get all loaded extensions
	 * @return a String array with the class names
	 */
	public String[] getExtensions()
	{
		String[] l = new String[_loadedExtensions.size()];
		_loadedExtensions.keySet().toArray(l);
		return l;
	}
	
	/**
	 * unload a named extension
	 * @param name the class name and optionally a jar file name delimited with a '@'
	 */
	public String unloadExtension(String name)
	{
		String className = name;
		String[] p = name.split("@");
		if (p.length > 1)
		{
			_classLoader.addJarFile(p[1]);
			className = p[0];
		}
		String res = className + " unloaded";
		try
		{
			Object obj = _loadedExtensions.get(className);
			Class<?> extension = obj.getClass();
			_loadedExtensions.remove(className);
			extension.getMethod("unload", new Class[0]).invoke(obj, new Object[0]);
			_log.info("Extension " + className + " unloaded.");
		}
		catch (Exception ex)
		{
			_log.log(Level.WARNING, "could not unload " + className, ex);
			res = ex.toString();
		}
		return res;
	}
	
	/**
	 * unloads all extensions, resets the cache and initializes all configured extensions
	 *
	 */
	public void reload()
	{
		unloadExtensions();
		clearCache();
		initExtensions();
	}
	
	/**
	 * unloads a named extension, resets the cache and initializes the extension
	 * @param name the class name and optionally a jar file name delimited with a '@' if the jar file is not
	 * in the class path
	 */
	public void reload(String name)
	{
		unloadExtension(name);
		clearCache();
		initExtension(name);
	}
	
	/**
	 * register a getter function given a (hopefully) unique name
	 * @param name the name of the function
	 * @param function the ExtensionFunction implementation
	 */
	public void addGetter(String name, ExtensionFunction function)
	{
		_getters.put(name, function);
	}
	
	/**
	 * deregister a getter function
	 * @param name the name used for registering
	 */
	public void removeGetter(String name)
	{
		_getters.remove(name);
	}
	
	/**
	 * call a getter function registered with DynamicExtension
	 * @param name the function name
	 * @param arg a function argument
	 * @return an object from the extension
	 */
	public Object get(String name, String arg)
	{
		ExtensionFunction func = _getters.get(name);
		if (func != null)
			return func.get(arg);
		return "<none>";
	}
	
	/**
	 * register a setter function given a (hopefully) unique name
	 * @param name the name of the function
	 * @param function the ExtensionFunction implementation
	 */
	public void addSetter(String name, ExtensionFunction function)
	{
		_setters.put(name, function);
	}
	
	/**
	 * deregister a setter function
	 * @param name the name used for registering
	 */
	public void removeSetter(String name)
	{
		_setters.remove(name);
	}
	
	/**
	 * call a setter function registered with DynamicExtension
	 * @param name the function name
	 * @param arg a function argument
	 * @param obj an object to set
	 */
	public void set(String name, String arg, Object obj)
	{
		ExtensionFunction func = _setters.get(name);
		if (func != null)
			func.set(arg, obj);
	}
	
	public JarClassLoader getClassLoader()
	{
		return _classLoader;
	}
	
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final DynamicExtension _instance = new DynamicExtension();
	}
}
