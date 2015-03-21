/**
 * @cond
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 * # Copyright (c) 2014-15, Philipp Kraus (philipp.kraus@tu-clausthal.de)               #
 * # This program is free software: you can redistribute it and/or modify               #
 * # it under the terms of the GNU General Public License as                            #
 * # published by the Free Software Foundation, either version 3 of the                 #
 * # License, or (at your option) any later version.                                    #
 * #                                                                                    #
 * # This program is distributed in the hope that it will be useful,                    #
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 * # GNU General Public License for more details.                                       #
 * #                                                                                    #
 * # You should have received a copy of the GNU General Public License                  #
 * # along with this program. If not, see http://www.gnu.org/licenses/                  #
 * ######################################################################################
 * @endcond
 **/

package de.tu_clausthal.in.mec;

import com.google.gson.Gson;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.CReflection;
import net.sf.oval.constraint.Min;
import net.sf.oval.constraint.NotEmpty;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.jxmapviewer.viewer.GeoPosition;
import org.metawidget.inspector.annotation.UiComesAfter;
import org.metawidget.inspector.annotation.UiHidden;
import org.metawidget.inspector.annotation.UiLabel;
import org.metawidget.inspector.annotation.UiLookup;
import org.metawidget.inspector.annotation.UiMasked;
import org.metawidget.inspector.annotation.UiSection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.jar.JarFile;
import java.util.jar.Manifest;


/**
 * singleton class for configuration with a Json file
 */
public class CConfiguration
{

    /**
     * name of the configuration file
     */
    private static final String c_ConfigFilename = "config.json";
    /**
     * singleton instance variable
     */
    private static final CConfiguration c_instance = new CConfiguration();
    private final Map<String, File> m_location = new HashMap()
    {{
            put( "root", new File( System.getProperty( "user.home" ) + File.separator + ".mecsim" ) );
        }};
    /**
     * property that stores the configuration data
     */
    private Data m_data = new Data();
    /**
     * UTF-8 property reader
     */
    private ResourceBundle.Control m_reader = new UTF8Control();
    /**
     * manifest data
     */
    private Map<String, String> m_manifest = new HashMap<>();


    /**
     * private Ctor to avoid manual instantiation with manifest reading
     */
    private CConfiguration()
    {
        try
        {
            Manifest l_manifest = new JarFile( this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath() ).getManifest();
            for ( Map.Entry<Object, Object> l_item : l_manifest.getMainAttributes().entrySet() )
                m_manifest.put( l_item.getKey().toString(), l_item.getValue().toString() );
        }
        catch ( IOException l_exception )
        {
        }

        this.setDefaultDirectories();
    }

    /**
     * singleton get instance method
     *
     * @return configuration instance
     */
    public static CConfiguration getInstance()
    {
        return c_instance;
    }

    /**
     * returns a path relative to the root directory
     *
     * @param p_dir directories
     * @return full file
     */
    private File getBasePath( final String... p_dir )
    {
        if ( !m_location.containsKey( "root" ) )
            throw new IllegalStateException( CCommon.getResourceString( this, "rootnotfound" ) );

        return new File( m_location.get( "root" ) + File.separator + StringUtils.join( p_dir, File.separator ) );
    }

    /**
     * reads the manifest data
     *
     * @return map with string key and value
     */
    public Map<String, String> getManifest()
    {
        return m_manifest;
    }


    /**
     * returns the location of a directory
     *
     * @param p_name    name of the location
     * @param p_varargs path components after the directory
     * @return full directory
     */
    public File getLocation( final String p_name, final String... p_varargs )
    {
        if ( ( p_varargs == null ) || ( p_varargs.length == 0 ) )
            return m_location.get( p_name );

        return new File( m_location.get( p_name ) + File.separator + StringUtils.join( p_varargs, File.separator ) );
    }


    /**
     * write method of the configuration
     */
    public void write()
    {
        try
        {
            this.createDirectories();
        }
        catch ( Exception l_exception )
        {
            CLogger.error( l_exception.getMessage() );
        }


        try (
                Writer l_writer = new OutputStreamWriter( new FileOutputStream( this.getLocation( "root", c_ConfigFilename ) ), "UTF-8" );
        )
        {
            new Gson().toJson( m_data, l_writer );
        }
        catch ( Exception l_exception )
        {
            CLogger.error( l_exception );
        }
    }


    /**
     * creates the default directories relative to the root dir
     */
    private void setDefaultDirectories()
    {
        for ( String l_item : new String[]{"mas", "jar", "www"} )
            m_location.put( l_item, this.getBasePath( l_item ) );
    }

    /**
     * creates the configuration directories
     */
    private void createDirectories() throws IOException
    {
        for ( File l_dir : m_location.values() )
            if ( !l_dir.exists() && !l_dir.mkdirs() )
                throw new IOException( CCommon.getResourceString( this, "notcreate", l_dir.getAbsolutePath() ) );
    }

    /**
     * reads the configuration within the directory
     */
    public void read()
    {

        // create the configuration directory
        Data l_tmp = null;
        try
        {
            this.createDirectories();
        }
        catch ( Exception l_exception )
        {
            CLogger.error( l_exception.getMessage() );
        }

        final File l_config = this.getLocation( "root", c_ConfigFilename );
        CLogger.info( CCommon.getResourceString( this, "read", l_config ) );

        // read main configuration
        try (
                Reader l_reader = new InputStreamReader( new FileInputStream( l_config ), "UTF-8" );
        )
        {
            l_tmp = new Gson().fromJson( l_reader, Data.class );
        }
        catch ( Exception l_exception )
        {
            CLogger.error( l_exception.getMessage() );
        }

        // check the configuration values and set it
        if ( ( l_tmp == null ) || ( l_tmp.getRestconfig() ) )
            CLogger.warn( CCommon.getResourceString( this, "default" ) );
        else
        {
            if ( l_tmp.UIBindPort < 1 )
            {
                CLogger.warn( CCommon.getResourceString( this, "uibindportdefault" ) );
                l_tmp.UIBindPort = m_data.UIBindPort;
            }
            if ( l_tmp.ViewPoint == null )
            {
                CLogger.warn( CCommon.getResourceString( this, "viewpointdefault" ) );
                l_tmp.ViewPoint = m_data.ViewPoint;
            }
            if ( l_tmp.WindowHeight < 100 )
            {
                CLogger.warn( CCommon.getResourceString( this, "heightdefault" ) );
                l_tmp.WindowHeight = m_data.WindowHeight;
            }
            if ( l_tmp.WindowWidth < 100 )
            {
                CLogger.warn( CCommon.getResourceString( this, "widthdefault" ) );
                l_tmp.WindowWidth = m_data.WindowWidth;
            }
            if ( ( l_tmp.RoutingAlgorithm == null ) || ( l_tmp.RoutingAlgorithm.isEmpty() ) )
            {
                CLogger.warn( CCommon.getResourceString( this, "routingdefault" ) );
                l_tmp.RoutingAlgorithm = m_data.RoutingAlgorithm;
            }
            if ( l_tmp.CellSampling < 1 )
            {
                CLogger.warn( CCommon.getResourceString( this, "cellsamplingdefault" ) );
                l_tmp.CellSampling = m_data.CellSampling;
            }
            if ( l_tmp.ThreadSleepTime < 0 )
            {
                CLogger.warn( CCommon.getResourceString( this, "threadsleepdefault" ) );
                l_tmp.ThreadSleepTime = m_data.ThreadSleepTime;
            }
            if ( l_tmp.RoutingMap == null )
            {
                CLogger.warn( CCommon.getResourceString( this, "routingmapdefault" ) );
                l_tmp.RoutingMap = m_data.RoutingMap;
            }
            if ( l_tmp.Database == null )
            {
                CLogger.warn( CCommon.getResourceString( this, "databasedefault" ) );
                l_tmp.Database = m_data.Database;
            }

            // @deprecated
            if ( l_tmp.Console == null )
            {
                CLogger.warn( CCommon.getResourceString( this, "consoledefault" ) );
                l_tmp.Console = m_data.Console;
            }

            m_data = l_tmp;
        }

        // append all Jar files to the classpath of the system class loader
        try
        {
            URLClassLoader l_classloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            for ( String l_jar : m_location.get( "jar" ).list( new WildcardFileFilter( "*.jar" ) ) )
                CReflection.getClassMethod( l_classloader.getClass(), "addURL", new Class<?>[]{URL.class} ).getHandle().invoke( l_classloader, CCommon.getResource( m_location.get( "jar" ) + File.separator + l_jar ) );
        }
        catch ( Exception l_exception )
        {
            CLogger.error( l_exception );
        }
        catch ( Throwable l_throwable )
        {
            CLogger.error( l_throwable );
        }
    }


    /**
     * returns the property bundle
     *
     * @return resource bundle
     */
    public ResourceBundle getResourceBundle()
    {
        if ( m_data.Language != null )
            switch ( m_data.Language )
            {
                case "en":
                    Locale.setDefault( Locale.ENGLISH );
                    break;
                case "de":
                    Locale.setDefault( Locale.GERMANY );
                    break;
            }

        return ResourceBundle.getBundle( "language.locals", m_reader );
    }


    /**
     * sets the config dir
     *
     * @param p_dir directory
     */
    public void setConfigDir( final File p_dir )
    {
        m_location.put( "root", p_dir );
        this.setDefaultDirectories();
        try
        {
            this.createDirectories();
        }
        catch ( Exception l_exception )
        {
            CLogger.error( l_exception.getMessage() );
        }
    }


    /**
     * returns the configuration data
     *
     * @return returns the configuration data
     */
    public Data get()
    {
        return m_data;
    }

    /**
     * class for storing the configuration
     *
     * @see http://metawidget.sourceforge.net/doc/reference/en/html/ch04s03.html
     */
    public class Data
    {
        /**
         * bind port of the web UI
         */
        private int UIBindPort = 9876;
        /**
         * cell size for sampling
         */
        private int CellSampling = 2;
        /**
         * flag to reset the configuration
         */
        private boolean resetconfig = false;
        /**
         * flag to reset the UI
         */
        private boolean resetui = false;
        /**
         * geo position object of the start viewpoint
         */
        private GeoPosition ViewPoint = new GeoPosition( 51.8089, 10.3412 );
        /**
         * zoom level of the viewpoint on the start point
         */
        private int Zoom = 4;
        /**
         * thread sleep time in milliseconds
         */
        private int ThreadSleepTime = 25;
        /**
         * window width
         */
        private int WindowWidth = 1684;
        /**
         * window height
         */
        private int WindowHeight = 1024;
        /**
         * geo map for graph
         */
        private RoutingMap RoutingMap = new RoutingMap();
        /**
         * graph algorithm: astar & astarbi (A* algorithm), dijkstra, dijkstrabi, dijkstraOneToMany (Dijkstra
         * algorithm)
         */
        private String RoutingAlgorithm = "astarbi";
        /**
         * language code
         */
        private String Language = null;
        /**
         * database driver (optional)
         */
        private DatabaseDriver Database = new DatabaseDriver();
        /**
         * console definition
         *
         * @deprecated
         */
        private ConsoleData Console = new ConsoleData();


        @UiSection("General")
        @UiLabel("UI language")
        @UiLookup({"en", "de"})
        @NotEmpty
        public String getLanguage()
        {
            return ( Language == null ) ? "en" : Language;
        }

        public void setLanguage( String p_value )
        {
            Language = p_value;
        }


        @UiLabel("UI Bind Port")
        @UiComesAfter("language")
        @Min(1024)
        public int getUibindport()
        {
            return UIBindPort;
        }

        public void setUibindport( int p_value )
        {
            UIBindPort = Math.max( 1024, Math.abs( p_value ) );
        }


        @UiLabel("Reset configuration")
        @UiComesAfter("uibindport")
        public boolean getRestconfig()
        {
            return resetconfig;
        }

        public void setRestconfig( boolean p_value )
        {
            resetconfig = p_value;
        }


        @UiLabel("Reset UI configuration")
        @UiComesAfter("restconfig")
        public boolean getResetui()
        {
            return resetui;
        }

        public void setResetui( boolean p_value )
        {
            resetui = p_value;
        }


        @UiSection("Traffic Graph")
        @UiComesAfter("resetui")
        @UiLabel("Cell Size (in metre)")
        @Min(1)
        public int getCellsampling()
        {
            return CellSampling;
        }

        public void setCellsampling( int p_value )
        {
            CellSampling = Math.max( p_value, 1 );
            ;
        }


        @UiComesAfter("cellsampling")
        @UiLabel("Routing algorithm")
        @UiLookup({"astar", "astarbi", "dijkstra", "dijkstrabi", "dijkstraOneToMany"})
        @NotEmpty
        public String getRoutingalgorithm()
        {
            return RoutingAlgorithm;
        }

        public void setRoutingalgorithm( String p_value )
        {
            RoutingAlgorithm = p_value;
        }


        @UiSection("Openstreetmap")
        @UiComesAfter("routingalgorithm")
        @UiLabel("")
        public RoutingMap getRoutingmap()
        {
            return RoutingMap;
        }

        public void setRoutingmap( RoutingMap p_value )
        {
            RoutingMap = p_value;
        }


        @UiSection("Console")
        @UiComesAfter("routingmap")
        @UiLabel("")
        public ConsoleData getConsole()
        {
            return Console;
        }

        public void setConsole( ConsoleData p_value )
        {
            Console = p_value;
        }


        @UiSection("Analysis Database")
        @UiComesAfter("console")
        @UiLabel("")
        public DatabaseDriver getDatabase()
        {
            return Database;
        }

        public void setDatabase( DatabaseDriver p_value )
        {
            Database = p_value;
        }

        @UiHidden
        public GeoPosition getViewpoint()
        {
            return ViewPoint;
        }

        public void setViewpoint( GeoPosition p_value )
        {
            ViewPoint = p_value;
        }

        @UiHidden
        public int getZoom()
        {
            return Zoom;
        }

        public void setZoom( int p_value )
        {
            Zoom = Math.max( p_value, 1 );
        }

        @UiHidden
        public int getThreadsleeptime()
        {
            return ThreadSleepTime;
        }

        public void setThreadsleeptime( int p_value )
        {
            ThreadSleepTime = Math.max( p_value, 0 );
        }

        @UiHidden
        public int getWindowwidth()
        {
            return WindowWidth;
        }

        public void setWindowwidth( int p_value )
        {
            WindowWidth = Math.max( p_value, 150 );
        }

        @UiHidden
        public int getWindowheight()
        {
            return WindowHeight;
        }

        public void setWindowheight( int p_value )
        {
            WindowHeight = Math.max( p_value, 150 );
        }

        /**
         * class of the console configuration
         *
         * @deprecated
         */
        public class ConsoleData
        {
            /**
             * maximum char number on each line *
             */
            private int LineBuffer = 120;
            /**
             * maximum line numbers *
             */
            private int LineNumber = 120;


            @UiLabel("Number Lines")
            public int getLinenumber()
            {
                return LineNumber;
            }

            public void setLinenumber( int p_value )
            {
                LineNumber = Math.max( p_value, 1 );
            }

            @UiLabel("Character Line Buffer")
            @UiComesAfter("linenumber")
            public int getLinebuffer()
            {
                return LineBuffer;
            }

            public void setLinebuffer( int p_value )
            {
                LineBuffer = Math.max( p_value, 1 );
            }
        }

        /**
         * class of the routing map
         */
        public class RoutingMap implements Serializable
        {
            /**
             * serialize version ID *
             */
            private static final long serialVersionUID = 1L;
            /**
             * download URL
             */
            private String url = "http://download.geofabrik.de/europe/germany/niedersachsen-latest.osm.pbf";
            /**
             * flag for reimport
             */
            private boolean reimport = false;

            /**
             * name of the map
             */
            private String name = "europe/germany/lowersaxony";


            @UiLabel("Unique name of the OpenStreetMap data")
            public String getName()
            {
                return name;
            }

            public void setName( String p_value )
            {
                name = p_value;
            }


            @UiLabel("Download URL of the OpenStreetMap PBF file")
            @UiComesAfter("name")
            public String getUrl()
            {
                return url;
            }

            public void setUrl( String p_value )
            {
                url = p_value;
            }


            @UiLabel("Reimport graph data")
            @UiComesAfter("url")
            public boolean getReimport()
            {
                return reimport;
            }

            public void setReimport( boolean p_value )
            {
                reimport = p_value;
            }
        }


        /**
         * class of the database driver
         */
        public class DatabaseDriver
        {
            /**
             * enable / disable without removing settings *
             */
            private boolean active = false;
            /**
             * driver *
             */
            private String driver = null;

            /**
             * server url *
             */
            private String server = null;

            /**
             * login user name *
             */
            private String username = null;

            /**
             * login password *
             */
            private String password = null;
            /**
             * table prefix
             */
            private String tableprefix = null;


            @UiLabel("Enable")
            public boolean isActive()
            {
                return active;
            }

            public void setActive( boolean p_value )
            {
                active = p_value;
            }

            @UiLabel("Database Driver")
            @UiComesAfter("active")
            public String getDriver()
            {
                return driver;
            }

            public void setDriver( String p_value )
            {
                driver = p_value;
            }


            @UiLabel("Server URL")
            @UiComesAfter("driver")
            public String getServer()
            {
                return server;
            }

            public void setServer( String p_value )
            {
                server = p_value;
            }


            @UiLabel("Login Username")
            @UiComesAfter("server")
            public String getUsername()
            {
                return username;
            }

            public void setUsername( String p_value )
            {
                username = p_value;
            }


            @UiLabel("Login Password")
            @UiComesAfter("username")
            @UiMasked
            public String getPassword()
            {
                return password;
            }

            public void setPassword( String p_value )
            {
                password = p_value;
            }


            @UiLabel("Table Prefix")
            @UiComesAfter("password")
            public String getTableprefix()
            {
                return tableprefix;
            }

            public void setTableprefix( String p_value )
            {
                tableprefix = p_value;
            }

            /**
             * checks if the server can connect
             *
             * @return boolean flag
             */
            @UiHidden
            public boolean isConnectable()
            {
                return ( active ) && ( driver != null ) && ( !driver.isEmpty() ) && ( server != null ) && ( !server.isEmpty() );
            }
        }

    }


    /**
     * class to read UTF-8 encoded property file
     *
     * @note Java default encoding for property files is ISO-Latin-1
     */
    protected class UTF8Control extends ResourceBundle.Control
    {

        public ResourceBundle newBundle( final String p_basename, final Locale p_locale, final String p_format, final ClassLoader p_loader, final boolean p_reload ) throws IllegalAccessException, InstantiationException, IOException
        {
            InputStream l_stream = null;
            final String l_resource = this.toResourceName( this.toBundleName( p_basename, p_locale ), "properties" );

            if ( !p_reload )
                l_stream = p_loader.getResourceAsStream( l_resource );
            else
            {

                final URL l_url = p_loader.getResource( l_resource );
                if ( l_url == null )
                    return null;

                final URLConnection l_connection = l_url.openConnection();
                if ( l_connection == null )
                    return null;

                l_connection.setUseCaches( false );
                l_stream = l_connection.getInputStream();
            }

            try
            {
                return new PropertyResourceBundle( new InputStreamReader( l_stream, "UTF-8" ) );

            }
            catch ( Exception l_exception )
            {
            }
            finally
            {
                l_stream.close();
            }

            return null;
        }
    }

}
