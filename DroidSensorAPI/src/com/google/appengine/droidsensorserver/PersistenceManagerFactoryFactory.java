package com.google.appengine.droidsensorserver;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

public abstract class PersistenceManagerFactoryFactory {

	private static final PersistenceManagerFactory SINGLETON = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");
	                                       
	public static PersistenceManagerFactory get(){
		
		return SINGLETON;
	}
	
	public static final PersistenceManager createManager(){
		
		return get().getPersistenceManager();
	}
}
