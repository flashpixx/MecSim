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

package de.tu_clausthal.in.mec.object.waypoint.factory;

import com.graphhopper.util.EdgeIteratorState;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.car.ICar;
import de.tu_clausthal.in.mec.object.mas.jason.IEnvironment;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jxmapviewer.viewer.GeoPosition;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * class to generate Jason cars
 */
public class CDefaultAgentCarFactory extends CDefaultCarFactory
{

    /**
     * name of the ASL file
     */
    private String m_aslName;
    /**
     * inspect data
     */
    private final Map<String, Object> m_inspect = new HashMap()
    {{
            put( CCommon.getResourceString( IFactory.class, "factoryname" ), this );
            put( CCommon.getResourceString( CDefaultAgentCarFactory.class, "factoryasl" ), m_aslName );
        }};


    /**
     * ctor
     *
     * @param p_aslName name of the asl file
     */
    public CDefaultAgentCarFactory( final String p_aslName )
    {
        super();
        if ( ( p_aslName == null ) || ( p_aslName.isEmpty() ) )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "aslnotnull" ) );

        this.m_aslName = p_aslName;
    }

    @Override
    public Set<ICar> generate( final Collection<Pair<GeoPosition, GeoPosition>> p_waypoints, final int p_count )
    {
        final ArrayList<Pair<EdgeIteratorState, Integer>> l_cells = this.generateRouteCells( p_waypoints );
        final Set<ICar> l_set = new HashSet<>();
        for ( int i = 0; i < p_count; i++ )
            l_set.add( new de.tu_clausthal.in.mec.object.car.CCarJasonAgent( l_cells, m_aslName ) );
        return l_set;
    }

    @Override
    public Map<String, Object> inspect()
    {
        return m_inspect;
    }

    /**
     * read call of serialize interface
     *
     * @param p_stream stream
     * @throws java.io.IOException throws exception on reading
     * @throws ClassNotFoundException throws on deserialization
     */
    private void readObject( final ObjectInputStream p_stream ) throws IOException, ClassNotFoundException
    {
        p_stream.defaultReadObject();

        // read the ASL file from the stream
        final String l_aslname = m_aslName;
        final String l_asldata = (String) p_stream.readObject();
        File l_output = IEnvironment.getAgentFile( l_aslname );

        try
        {
            if ( !l_output.exists() )
                FileUtils.write( l_output, l_asldata );
            else if ( !CCommon.getHash( l_output, "MD5" ).equals( CCommon.getHash( l_asldata, "MD5" ) ) )
            {
                l_output = IEnvironment.getAgentFile( FilenameUtils.getBaseName( l_aslname ) + "_0" );
                for ( int i = 1; l_output.exists(); i++ )
                {
                    m_aslName = FilenameUtils.getBaseName( l_aslname ) + "_" + i;
                    l_output = IEnvironment.getAgentFile( m_aslName );
                }
                FileUtils.write( l_output, l_asldata );
            }
        }
        catch ( final Exception l_exception )
        {
        }
    }

    /**
     * write call of serialize interface
     *
     * @param p_stream stream
     * @throws IOException throws the exception on loading data
     */
    private void writeObject( final ObjectOutputStream p_stream ) throws IOException
    {
        p_stream.defaultWriteObject();

        // write the ASL file to the stream
        p_stream.writeObject( new String( Files.readAllBytes( Paths.get( IEnvironment.getAgentFile( m_aslName ).toString() ) ) ) );
    }
}
