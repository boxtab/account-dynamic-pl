package org.webtrader.accountDynamicPL.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config extends Properties
{
    final private static String CONFIG_PROPERTIES_FILENAME = "account-dynamic-pl.properties";

    private static Config instance;

    public static Config getConfig()
    {
        if ( instance == null ) {
            instance = createInstance();
        }

        return instance;
    }

    private static Config createInstance()
    {
        Config config = new Config();

        try ( FileInputStream fis = new FileInputStream( CONFIG_PROPERTIES_FILENAME ) )
        {
            config.load( fis );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "unable to read " + CONFIG_PROPERTIES_FILENAME + " file", e );
        }

        return config;
    }

    public String getProperty( String key )
    {
        String property = super.getProperty( key );

        if ( property == null )
        {
            throw new RuntimeException( "unable to find property " + key + " at " + CONFIG_PROPERTIES_FILENAME );
        }

        return property;
    }
}
