/**
 * @cond LICENSE
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
 */

package de.tu_clausthal.in.mec.ui.web;

import de.tu_clausthal.in.mec.CBootstrap;
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.CReflection;
import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil2;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWebSocketServer;
import fi.iki.elonen.WebSocket;
import org.apache.commons.io.FilenameUtils;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Collection;
import java.util.Map;


/**
 * class of websocket & http server
 */
public class CServer
{
    /**
     * seperator
     */
    private static final String c_seperator = "/";
    /**
     * web-static prefix
     */
    private static final String c_webstatic = "web_static_";
    /**
     * web-dynamic prefix
     */
    private static final String c_webdynamic = "web_dynamic_";
    /**
     * web-uribase name
     */
    private static final String c_weburibase = "web_uribase";
    /**
     * virtual-locations
     */
    private final CVirtualLocation m_virtuallocation;
    /**
     * http server
     */
    private final CHTTPServer m_httpserver;
    /**
     * websocket server
     */
    private final CWebSocketServer m_wsserver;


    /**
     * ctor - starts the HTTP server
     *
     * @param p_host bind hostname
     * @param p_httpport http bind port
     * @param p_websocketport websocket bind port
     * @param p_default default location
     */
    public CServer( final String p_host, final int p_httpport, final int p_websocketport, final CVirtualDirectory p_default )
    {
        if ( p_httpport == p_websocketport )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "portequal" ) );

        m_virtuallocation = new CVirtualLocation( p_default );
        m_wsserver = new CWebSocketServer( p_host, p_websocketport );
        m_httpserver = new CHTTPServer( p_host, p_httpport );

        CBootstrap.afterServerInit( this );
    }


    /**
     * returns the virtual-location object
     *
     * @return location
     */
    public final CVirtualLocation getVirtualLocation()
    {
        return m_virtuallocation;
    }


    /**
     * registerObject an object for the UI
     *
     * @param p_object object, all methods with the name "ui_" are registered
     * @note a class / object need to implemente methods with the prefix "web_static_" or "web_dynamic_", which is bind
     * to the server with the URL part "/classname/[basepart/]methodname" (without prefix). The methods "String
     * web_uribase"
     * returns the basepart of the URI
     * @bug incomplete
     */
    public final void registerObject( final Object p_object )
    {
        // URI begin
        final String l_uriclass = c_seperator + p_object.getClass().getSimpleName().toLowerCase() + c_seperator;

        // get methods
        for ( Map.Entry<String, CReflection.CMethod> l_method : CReflection.getClassMethods(
                p_object.getClass(), new CReflection.IMethodFilter()
                {
                    @Override
                    public boolean filter( final java.lang.reflect.Method p_method )
                    {
                        return ( p_method.getName().toLowerCase().startsWith(
                                c_webstatic
                        ) || p_method.getName().toLowerCase().startsWith(
                                c_webdynamic
                        ) ) && ( !Modifier.isStatic( p_method.getModifiers() ) );
                    }
                }
        ).entrySet() )
        {
            this.bindStaticMethod( p_object, l_method.getValue(), l_uriclass );
            this.bindDynamicMethod( p_object, l_method.getValue(), l_uriclass );
        }
    }


    /**
     * static method binding
     *
     * @param p_object bind object
     * @param p_method method object
     * @param p_uriclass class name
     */
    private void bindStaticMethod( final Object p_object, final CReflection.CMethod p_method, final String p_uriclass )
    {
        String l_methodname = p_method.getMethod().getName().toLowerCase();
        if ( !l_methodname.contains( c_webstatic ) )
            return;
        l_methodname = l_methodname.replace( c_webstatic, "" );
        if ( l_methodname.isEmpty() )
            return;

        m_virtuallocation.add( new CVirtualStaticMethod( p_object, p_method, p_uriclass + this.getURIBase( p_object ) + l_methodname ) );
    }


    /**
     * dynamic method binding
     *
     * @param p_object bind object
     * @param p_method method object
     * @param p_uriclass class name
     */
    private void bindDynamicMethod( final Object p_object, final CReflection.CMethod p_method, final String p_uriclass )
    {
        String l_methodname = p_method.getMethod().getName().toLowerCase();
        if ( !l_methodname.contains( c_webdynamic ) )
            return;
        l_methodname = l_methodname.replace( c_webdynamic, "" );
        if ( l_methodname.isEmpty() )
            return;

        m_virtuallocation.add( new CVirtualDynamicMethod( p_object, p_method, p_uriclass + this.getURIBase( p_object ) + l_methodname ) );
    }


    /**
     * returns URI base if exists
     *
     * @param p_object object
     * @return empty string or URI
     */
    private String getURIBase( final Object p_object )
    {
        // try to read basepart
        String l_uribase = "";
        try
        {
            final Object l_data = CReflection.getClassMethod( p_object.getClass(), c_weburibase ).getMethod().invoke( p_object );
            if ( l_data instanceof String )
                l_uribase = l_data.toString().toLowerCase();

            if ( ( l_uribase != null ) && ( !l_uribase.isEmpty() ) )
            {
                if ( l_uribase.startsWith( c_seperator ) )
                    l_uribase = l_uribase.substring( 1 );
                if ( !l_uribase.endsWith( c_seperator ) )
                    l_uribase += c_seperator;
            }
        }
        catch ( final IllegalArgumentException | IllegalAccessException | InvocationTargetException l_exception )
        {
        }

        return l_uribase;
    }


    /**
     * adds a new virtual file to the server with existance checking
     *
     * @param p_source relative source path
     * @param p_uri URI
     */
    public final void registerVirtualFile( final String p_source, final String p_uri )
    {
        try
        {
            final URL l_url = CCommon.getResourceURL( p_source );
            if ( l_url != null )
                m_virtuallocation.add( new CVirtualFile( l_url, p_uri ) );
        }
        catch ( final IllegalArgumentException l_exception )
        {
            CLogger.error( l_exception );
        }
    }


    /**
     * adds a new virtual directory to the server with existance checking
     *
     * @param p_source source file object
     * @param p_index index file
     * @param p_uri URI
     */
    public final void registerVirtualDirectory( final File p_source, final String p_index, final String p_uri )
    {
        this.registerVirtualDirectory( p_source, p_index, p_uri, null );
    }


    /**
     * adds a new virtual directory to the server with existance checking
     *
     * @param p_source source file object
     * @param p_index index file
     * @param p_uri URI
     * @param p_markdown markdown renderer
     */
    public final void registerVirtualDirectory( final File p_source, final String p_index, final String p_uri, final CMarkdownRenderer p_markdown )
    {
        try
        {
            if ( p_source != null )
                m_virtuallocation.add( new CVirtualDirectory( CCommon.getResourceURL( p_source ), p_index, p_uri, p_markdown ) );
        }
        catch ( final IllegalArgumentException l_exception )
        {
            CLogger.error( l_exception );
        }
    }

    /**
     * adds a new virtual directory to the server with existance checking
     *
     * @param p_source relative source path
     * @param p_index index file
     * @param p_uri URI
     */
    public final void registerVirtualDirectory( final String p_source, final String p_index, final String p_uri )
    {
        this.registerVirtualDirectory( p_source, p_index, p_uri, null );
    }

    /**
     * adds a new virtual directory to the server with existance checking
     *
     * @param p_source relative source path
     * @param p_index index file
     * @param p_uri URI
     * @param p_markdown markdown renderer
     */
    public final void registerVirtualDirectory( final String p_source, final String p_index, final String p_uri, final CMarkdownRenderer p_markdown )
    {
        this.registerVirtualDirectory( new File( p_source ), p_index, p_uri, p_markdown );
    }


    /**
     * HTTP server to handle static content
     */
    private class CHTTPServer extends NanoHTTPD
    {
        /**
         * markdown processor
         */
        private final PegDownProcessor m_markdown = new PegDownProcessor( Extensions.ALL );
        /**
         * mimetype detector
         */
        private final MimeUtil2 m_mimetype = new MimeUtil2();


        /**
         * ctor
         *
         * @param p_host bind host name
         * @param p_port bind port
         */
        public CHTTPServer( final String p_host, final int p_port )
        {
            super( p_host, p_port );

            for ( String l_detector : new String[]{
                    "eu.medsea.mimeutil.detector.MagicMimeMimeDetector", "eu.medsea.mimeutil.detector.ExtensionMimeDetector",
                    "eu.medsea.mimeutil.detector.TextMimeDetector"
            } )
                m_mimetype.registerMimeDetector( l_detector );


            try
            {
                this.start();
            }
            catch ( final IOException l_exception )
            {
                CLogger.error( l_exception );
            }

            CLogger.info( CCommon.getResourceString( this, "bind", p_host, p_port ) );
        }


        @Override
        public final Response serve( final IHTTPSession p_session )
        {
            try
            {
                // get location
                final IVirtualLocation l_location = m_virtuallocation.get( p_session );
                if ( l_location instanceof CVirtualStaticMethod )
                    return this.setDefaultHeader( this.getVirtualStaticMethod( l_location, p_session ), p_session );

                return this.setDefaultHeader( this.getVirtualDirFile( l_location, p_session ), p_session );
            }
            catch ( final Throwable l_throwable )
            {
                CLogger.error( l_throwable );
                return this.setDefaultHeader( new Response( Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "ERROR 500\n" + l_throwable ), p_session );
            }
        }

        /**
         * set default header items
         *
         * @param p_response response
         * @param p_session session
         * @return modified response
         */
        private Response setDefaultHeader( final Response p_response, final IHTTPSession p_session )
        {
            p_response.addHeader( "Location", p_session.getUri() );
            p_response.addHeader( "Expires", "-1" );

            // is needed for ajax request
            // @see http://stackoverflow.com/questions/10548883/request-header-field-authorization-is-not-allowed-error-tastypie
            p_response.addHeader( "Access-Control-Allow-Origin", "*" );
            p_response.addHeader( "Access-Control-Allow-Methods", "POST,GET,OPTIONS,PUT" );
            p_response.addHeader( "Access-Control-Allow-Headers", "Origin,Content-Type,Accept" );

            return p_response;
        }

        /**
         * generates HTTP response of a static method calls
         *
         * @param p_location location object
         * @param p_session session object
         * @return response
         * @throws Throwable on error
         */
        private Response getVirtualStaticMethod( final IVirtualLocation p_location, final IHTTPSession p_session ) throws Throwable
        {
            CLogger.info( p_session.getUri() );
            return p_location.<Response>get( p_session );
        }

        /**
         * generates HTTP response of file & directory calls
         *
         * @param p_location location object
         * @param p_session session object
         * @return response
         * @throws Throwable on error
         */
        private Response getVirtualDirFile( final IVirtualLocation p_location, final IHTTPSession p_session ) throws Throwable
        {
            final Response l_response;
            final URL l_physicalfile = p_location.<URL>get( p_session );
            final String l_mimetype = this.getMimeType( l_physicalfile );
            CLogger.info( p_session.getUri() + "   " + l_physicalfile + "   " + l_mimetype );

            switch ( FilenameUtils.getExtension( l_physicalfile.toString() ) )
            {
                case "htm":
                case "html":
                    l_response = new Response(
                            Response.Status.OK, l_mimetype + "; charset=" + ( new InputStreamReader(
                            l_physicalfile.openStream()
                    ).getEncoding() ), l_physicalfile.openStream()
                    );
                    break;

                case "md":
                    if ( p_location.getMarkDownRenderer() != null )
                        l_response = new Response(
                                Response.Status.OK, p_location.getMarkDownRenderer().getMimeType(), p_location.getMarkDownRenderer().getHTML(
                                m_markdown, l_physicalfile
                        )
                        );
                    else
                        l_response = new Response( Response.Status.OK, l_mimetype, l_physicalfile.openStream() );
                    break;

                default:
                    l_response = new Response( Response.Status.OK, l_mimetype, l_physicalfile.openStream() );
            }

            return l_response;
        }

        /**
         * reads the mime-type of an URL - first try to detect a mime-type which has no "application" prefix
         */
        private String getMimeType( final URL p_url )
        {
            final Collection l_types = m_mimetype.getMimeTypes( p_url );
            if ( l_types.size() == 1 )
                return l_types.iterator().next().toString();

            for ( Object l_item : l_types )
            {
                final MimeType l_type = (MimeType) l_item;
                if ( !l_type.toString().startsWith( "application/" ) )
                    return l_type.toString();
            }
            return MimeUtil2.UNKNOWN_MIME_TYPE.toString();
        }
    }


    /**
     * WebSocket server to handle dynamic content
     */
    private class CWebSocketServer extends NanoWebSocketServer
    {

        /**
         * ctor
         *
         * @param p_host bind host name
         * @param p_port bind port
         */
        public CWebSocketServer( final String p_host, final int p_port )
        {
            super( p_host, p_port );

            try
            {
                this.start();
            }
            catch ( final IOException l_exception )
            {
                CLogger.error( l_exception );
            }

            CLogger.info( CCommon.getResourceString( this, "bind", p_host, p_port ) );
        }


        @Override
        public WebSocket openWebSocket( final IHTTPSession p_handshake )
        {
            try
            {

                // get location
                final IVirtualLocation l_location = m_virtuallocation.get( p_handshake );
                if ( l_location != null )
                    return l_location.<WebSocket>get( p_handshake );

            }
            catch ( final Throwable l_throwable )
            {
                CLogger.error( l_throwable );
            }

            return super.openWebSocket( p_handshake );
        }
    }

}