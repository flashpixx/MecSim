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

package de.tu_clausthal.in.mec.ui.web;

import de.tu_clausthal.in.mec.common.CCommon;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * definition of virtual location *
 */
public class CVirtualDirectory implements IVirtualLocation
{

    /**
     * markdown renderer
     */
    private final CMarkdownRenderer m_markdown;
    /**
     * URI pattern *
     */
    private String m_uri = "/";
    /**
     * virtual-location-directory *
     */
    private URL m_directory = null;
    /**
     * index file *
     */
    private String m_index = "";

    /**
     * ctor
     *
     * @param p_directory base directory of the physical directory
     * @param p_index     index file
     */
    public CVirtualDirectory( final URL p_directory, final String p_index )
    {
        if ( ( p_index == null ) || ( p_index.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "indexempty" ) );
        if ( p_directory == null )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "directorynotexists", p_directory ) );

        m_index = p_index;
        m_directory = p_directory;
        m_markdown = null;
    }

    /**
     * ctor
     *
     * @param p_directory base directory of the physical directory
     * @param p_index     index file
     * @param p_uri       regular expression of the URI
     */
    public CVirtualDirectory( final URL p_directory, final String p_index, final String p_uri )
    {
        if ( ( p_index == null ) || ( p_index.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "indexempty" ) );
        if ( p_directory == null )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "directorynotexists", p_directory ) );
        if ( ( p_uri == null ) || ( p_uri.isEmpty() ) || ( !p_uri.endsWith( "/" ) ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "trailingslashempty", p_uri ) );

        m_index = p_index;
        m_directory = p_directory;
        m_uri = p_uri;
        m_markdown = null;
    }


    /**
     * ctor
     *
     * @param p_directory base directory of the physical directory
     * @param p_index     index file
     * @param p_uri       regular expression of the URI
     * @param p_markdown  markdown renderer
     */
    public CVirtualDirectory( final URL p_directory, final String p_index, final String p_uri, final CMarkdownRenderer p_markdown )
    {
        if ( ( p_index == null ) || ( p_index.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "indexempty" ) );
        if ( p_directory == null )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "directorynotexists", p_uri ) );
        if ( ( p_uri == null ) || ( p_uri.isEmpty() ) || ( !p_uri.endsWith( "/" ) ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "trailingslashempty", p_uri ) );

        m_index = p_index;
        m_directory = p_directory;
        m_uri = p_uri;
        m_markdown = p_markdown;
    }


    @Override
    public boolean match( final String p_uri )
    {
        return p_uri.startsWith( m_uri );
    }


    @Override
    public URL getFile( final String p_uri ) throws MalformedURLException
    {
        final String[] l_parts = p_uri.split( "/" );
        if ( ( l_parts.length == 0 ) || ( !l_parts[l_parts.length - 1].contains( "." ) ) )
            return new URL( m_directory.toString() + "/" + m_index );


        return new URL( m_directory.toString() + "/" + p_uri.replace( m_uri, "" ) );
    }

    @Override
    public CMarkdownRenderer getMarkDownRenderer()
    {
        return m_markdown;
    }


    @Override
    public final int hashCode()
    {
        return m_uri.hashCode();
    }


    @Override
    public final boolean equals( final Object p_object )
    {
        if ( p_object instanceof CVirtualLocation )
            return this.hashCode() == p_object.hashCode();

        return false;
    }

}
