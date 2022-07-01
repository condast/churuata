package org.churuata.caminantes;

import java.util.HashMap;
import java.util.Map;

import org.churuata.caminantes.entries.BannerEntryPoint;
import org.churuata.caminantes.entries.BasicEntryPoint;
import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.client.WebClient;


public class BasicApplication implements ApplicationConfiguration {

	public static final String S_CHURUATA_VARIANT = "churuata";

    public void configure(Application application) {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(WebClient.PAGE_TITLE, "Los Caminantes");
        application.addEntryPoint("/banner", BannerEntryPoint.class, properties);
        application.addEntryPoint("/home", BasicEntryPoint.class, properties);
    }

}
