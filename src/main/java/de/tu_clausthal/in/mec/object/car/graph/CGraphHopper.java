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

package de.tu_clausthal.in.mec.object.car.graph;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.Path;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.Weighting;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.EdgeIteratorState;
import de.tu_clausthal.in.mec.CConfiguration;
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.car.ICar;
import de.tu_clausthal.in.mec.object.car.graph.weights.CSpeedUp;
import de.tu_clausthal.in.mec.object.car.graph.weights.CSpeedUpTrafficJam;
import de.tu_clausthal.in.mec.object.car.graph.weights.CTrafficJam;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jxmapviewer.viewer.GeoPosition;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * class for create a graph structure of OSM data, the class downloads the data and creates edges and verticies
 *
 * @see http://graphhopper.com/
 */
public class CGraphHopper extends GraphHopper
{

    /**
     * map with edge-cell connection
     */
    protected final Map<Integer, CEdge<ICar, Object>> m_edgecell = new ConcurrentHashMap<>();
    /**
     * set with listerner of the edges
     */
    protected final Set<IAction<ICar, Object>> m_edgelister = new HashSet<>();


    /**
     * private ctor of the singleton structure
     */
    public CGraphHopper()
    {
        this( null );
    }

    /**
     * private ctor do add different weights for routing
     *
     * @param p_weights weight name
     * @see https://github.com/graphhopper/graphhopper/issues/111
     */
    public CGraphHopper( final String p_weights )
    {
        if ( ( p_weights != null ) && ( !p_weights.isEmpty() ) )
            this.setCHShortcuts( p_weights );

        // define graph location (use configuration)
        final File l_graphlocation = CConfiguration.getInstance().getConfigDir( "graphs", CConfiguration.getInstance().get().getRoutingmap().getName().replace( '/', '_' ) );
        CLogger.out( CCommon.getResourceString( this, "path", l_graphlocation.getAbsolutePath() ) );

        // convert OSM or load the graph
        if ( !this.load( l_graphlocation.getAbsolutePath() ) )
        {
            CLogger.info( CCommon.getResourceString( this, "notloaded" ) );
            final File l_osm = this.downloadOSMData();

            this.setGraphHopperLocation( l_graphlocation.getAbsolutePath() );
            this.setOSMFile( l_osm.getAbsolutePath() );
            this.setEncodingManager( new EncodingManager( "CAR" ) );
            this.importOrLoad();

            l_osm.delete();
        }

        CLogger.out( CCommon.getResourceString( this, "loaded" ) );
    }


    /**
     * creates a list of list of edge between two geopositions
     *
     * @param p_start start geoposition
     * @param p_end   end geoposition
     * @return list of list of edges
     */
    public final List<List<EdgeIteratorState>> getRoutes( final GeoPosition p_start, final GeoPosition p_end )
    {
        return this.getRoutes( p_start, p_end, Integer.MAX_VALUE );
    }


    /**
     * creates a list of list of edge between two geopositions
     *
     * @param p_start     start geoposition
     * @param p_end       end geoposition
     * @param p_maxroutes max. number of paths
     * @return list of list of edges
     */
    public final List<List<EdgeIteratorState>> getRoutes( final GeoPosition p_start, final GeoPosition p_end, final int p_maxroutes )
    {
        // calculate routes
        final GHRequest l_request = new GHRequest( p_start.getLatitude(), p_start.getLongitude(), p_end.getLatitude(), p_end.getLongitude() );
        l_request.setAlgorithm( CConfiguration.getInstance().get().getRoutingalgorithm() );

        final GHResponse l_result = this.route( l_request );
        if ( !l_result.getErrors().isEmpty() )
        {
            for ( Throwable l_msg : l_result.getErrors() )
                CLogger.error( l_msg.getMessage() );
            throw new IllegalArgumentException( CCommon.getResourceString( this, "grapherror" ) );
        }


        // get all paths
        final List<Path> l_routes = this.getPaths( l_request, l_result );
        if ( l_routes.size() == 0 )
            return null;

        // create edge list with routes
        final List<List<EdgeIteratorState>> l_paths = new ArrayList<>();
        for ( Path l_path : l_routes )
        {
            if ( l_paths.size() >= p_maxroutes )
                return l_paths;

            // we must delete the first and last element, because the items are "virtual"
            final List<EdgeIteratorState> l_edgelist = l_path.calcEdges();
            if ( l_edgelist.size() < 3 )
                continue;

            l_edgelist.remove( 0 );
            l_edgelist.remove( l_edgelist.size() - 1 );

            l_paths.add( l_edgelist );
        }

        return l_paths;
    }


    /**
     * returns the closest edge(s) of a geo position
     *
     * @param p_position geo position
     * @return ID of the edge
     */
    public final int getClosestEdge( final GeoPosition p_position )
    {
        final QueryResult l_result = this.getLocationIndex().findClosest( p_position.getLatitude(), p_position.getLongitude(), EdgeFilter.ALL_EDGES );
        return l_result.getClosestEdge().getEdge();
    }


    /**
     * returns the max. speed of an edge
     *
     * @param p_edge edge ID
     * @return speed
     */
    public final double getEdgeSpeed( final EdgeIteratorState p_edge )
    {
        if ( p_edge == null )
            return Double.POSITIVE_INFINITY;

        return this.getGraph().getEncodingManager().getEncoder( "CAR" ).getSpeed( p_edge.getFlags() );
    }


    /**
     * returns an iterator state of an edge
     *
     * @param p_edgeid edge ID
     * @return iterator
     */
    public final EdgeIteratorState getEdgeIterator( final int p_edgeid )
    {
        return this.getGraph().getEdgeProps( p_edgeid, Integer.MIN_VALUE );
    }


    /**
     * returns the ID of the closest node
     *
     * @param p_position geo position
     * @return ID of the node
     */
    public final int getClosestNode( final GeoPosition p_position )
    {
        final QueryResult l_result = this.getLocationIndex().findClosest( p_position.getLatitude(), p_position.getLongitude(), EdgeFilter.ALL_EDGES );
        return l_result.getClosestNode();
    }


    /**
     * creates the full path of cells with the edge value
     *
     * @param p_route edge list
     * @return list with pair of edge and cell position
     */
    public final ArrayList<Pair<EdgeIteratorState, Integer>> getRouteCells( final List<EdgeIteratorState> p_route )
    {
        final ArrayList<Pair<EdgeIteratorState, Integer>> l_list = new ArrayList<>();

        for ( EdgeIteratorState l_edge : p_route )
            for ( int i = 0; i < this.getEdge( l_edge ).getEdgeCells(); i++ )
                l_list.add( new ImmutablePair<>( l_edge, i ) );

        return l_list;
    }


    /**
     * clears all edges
     */
    public final synchronized void clear()
    {
        for ( Map.Entry<Integer, CEdge<ICar, Object>> l_item : m_edgecell.entrySet() )
            l_item.getValue().clear();
    }

    /**
     * number of cars
     *
     * @return number of cars on the graph
     */
    public final synchronized int getNumberOfObjects()
    {
        int l_count = 0;
        for ( CEdge l_item : m_edgecell.values() )
            l_count += l_item.getNumberOfObjects();
        return l_count;
    }


    /**
     * adds an edge listener
     *
     * @note listener object will be set at the edge instantiation process
     */
    public final synchronized void addEdgeListener( final IAction<ICar, Object> p_listener )
    {
        m_edgelister.add( p_listener );
    }

    /**
     * returns the linkage between edge and car
     *
     * @param p_edgestate edge object
     * @return linkage object
     * @note listener object will be set at the edge instantiation process
     */
    public final synchronized CEdge getEdge( final EdgeIteratorState p_edgestate )
    {
        CEdge l_edge = m_edgecell.get( p_edgestate.getEdge() );
        if ( l_edge == null )
        {
            l_edge = new CEdge( p_edgestate );
            l_edge.addListener( m_edgelister );
            m_edgecell.put( l_edge.getEdgeID(), l_edge );
        }

        return l_edge;
    }


    /**
     * downloads the OSM data
     *
     * @return download file with full path
     */
    private final File downloadOSMData()
    {
        try
        {
            final File l_output = File.createTempFile( "mecsim", ".osm.pbf" );
            final URL l_url = new URL( CConfiguration.getInstance().get().getRoutingmap().getUrl() );

            CLogger.out( CCommon.getResourceString( this, "download", l_url, l_output ) );

            final ReadableByteChannel l_channel = Channels.newChannel( l_url.openStream() );
            final FileOutputStream l_stream = new FileOutputStream( l_output );
            l_stream.getChannel().transferFrom( l_channel, 0, Long.MAX_VALUE );

            return l_output;
        }
        catch ( Exception l_exception )
        {
            CLogger.error( l_exception.getMessage() );
        }
        return null;

    }

    /**
     * returns a string list with weightening names
     *
     * @return string list
     */
    public final String[] getWeightingList()
    {
        return new String[]{"Default", "TrafficJam + SpeedUp", "SpeedUp", "TrafficJam"};
    }

    @Override
    // TODO: discuss translation of menu labels of these menu items
    public final Weighting createWeighting( final String p_weighting, final FlagEncoder p_encoder )
    {
        if ( "TrafficJam + SpeedUp".equalsIgnoreCase( p_weighting ) )
            return new CSpeedUpTrafficJam( this, p_encoder );

        if ( "SpeedUp".equalsIgnoreCase( p_weighting ) )
            return new CSpeedUp( p_encoder );

        if ( "TrafficJam".equalsIgnoreCase( p_weighting ) )
            return new CTrafficJam( this );

        return super.createWeighting( p_weighting, p_encoder );
    }

}
