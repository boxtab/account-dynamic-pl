package org.webtrader.accountDynamicPL.mariadb;


import org.webtrader.accountDynamicPL.config.Config;
import org.webtrader.accountDynamicPL.log.Log;

abstract public class DBConnection
{
    private static java.sql.Connection instance;

    public static java.sql.Connection getInstance() throws Exception
    {
        if ( instance == null ) {
            Log.write("trying to connect database...");
            instance = createInstance();
            Log.write("database connection OK");
        }

        return instance;
    }

    public static java.sql.Connection createInstance() throws Exception
    {
        try
        {
            return java.sql.DriverManager.getConnection( createURL() );
        }
        catch( Throwable e )
        {
            throw new Exception( "unable to connect to database", e );
        }
    }

    private static String createURL() throws Exception
    {
        Config config = getConfig();

        try
        {
            return
                    "jdbc:mariadb://" +
                            config.getProperty( "DATABASE_HOST" ) +
                            ":" + config.getProperty( "DATABASE_PORT" ) +
                            "/" + config.getProperty( "DATABASE_NAME" ) +
                            "?user=" + config.getProperty( "DATABASE_USERNAME" ) +
                            "&password=" + config.getProperty( "DATABASE_PASSWORD" );
        }
        catch( Exception e )
        {
            throw new Exception( "unable to get config properties", e );
        }
    }

    private static Config getConfig() throws Exception
    {
        try
        {
            return Config.getConfig();
        }
        catch( Exception e )
        {
            throw new Exception( "unable to get Config", e );
        }
    }
}
