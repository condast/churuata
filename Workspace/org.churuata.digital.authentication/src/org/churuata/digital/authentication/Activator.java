package org.churuata.digital.authentication;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    public static String BUNDLE_ID = "org.churuata.digital.authentication";
    
    private static BundleContext defaultContext;
 
	@Override
	public void start(BundleContext context) throws Exception {
      defaultContext = context;
    }

    @Override
	public void stop(BundleContext context) throws Exception {
    }
        
	public static BundleContext getDefault(){
    	return defaultContext;
    }
}