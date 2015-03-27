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
 **/

package de.tu_clausthal.in.mec.object.car;

import com.graphhopper.util.EdgeIteratorState;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.car.graph.CEdge;
import de.tu_clausthal.in.mec.object.car.graph.CGraphHopper;
import de.tu_clausthal.in.mec.simulation.CSimulation;
import de.tu_clausthal.in.mec.ui.COSMViewer;
import de.tu_clausthal.in.mec.ui.inspector.IInspector;
import org.apache.commons.lang3.tuple.Pair;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * class for a default car *
 */
public class CDefaultCar extends IInspector implements ICar
{

    /**
     * random interface
     */
    protected Random m_random = new Random();
    /**
     * geo position of the start
     */
    protected GeoPosition m_StartPosition = null;
    /**
     * geo position of the end
     */
    protected GeoPosition m_EndPosition = null;
    /**
     * current speed
     */
    protected int m_speed = 0;
    /**
     * maximum speed definition
     */
    protected int m_maxSpeed = 200;
    /**
     * linger probability value
     */
    protected double m_LingerProbability = 0;
    /**
     * cell structure of the route
     */
    protected ArrayList<Pair<EdgeIteratorState, Integer>> m_route = null;
    /**
     * current position on the route
     */
    protected int m_routeindex = 0;
    /**
     * boolean flag for end reached
     */
    protected boolean m_endReached = false;
    /**
     * individual acceleration
     */
    protected int m_acceleration = 1;
    /**
     * individual deceleration *
     */
    protected int m_deceleration = 1;
    /**
     * reference to the graph
     */
    protected CGraphHopper m_graph = ( (CCarLayer) CSimulation.getInstance().getWorld().get( "Cars" ) ).getGraph();


    /**
     * ctor to create the initial values
     *
     * @param p_StartPosition start positions (position of the source)
     */
    public CDefaultCar( final GeoPosition p_StartPosition )
    {
        if ( p_StartPosition == null )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "startnull" ) );

        m_StartPosition = p_StartPosition;
        m_LingerProbability = m_random.nextDouble();
        while ( m_speed < 50 ) m_speed = m_random.nextInt( m_maxSpeed );
        m_acceleration = m_random.nextInt( 40 ) + 20;
        m_deceleration = m_random.nextInt( 40 ) + 20;

        // we try to find a route within the geo data, so we get a random end position and try to calculate a
        // route between start and end position, so if an exception is cached, we create a new end position
        while ( true ) try
        {

            m_EndPosition = new GeoPosition( m_StartPosition.getLatitude() + m_random.nextDouble() - 0.1, m_StartPosition.getLongitude() + m_random.nextDouble() - 0.1 );
            final List<List<EdgeIteratorState>> l_route = m_graph.getRoutes( m_StartPosition, m_EndPosition, 1 );

            if ( ( l_route != null ) && ( l_route.size() > 0 ) )
            {
                m_route = m_graph.getRouteCells( l_route.get( 0 ) );
                break;
            }

        }
        catch ( final Exception l_exception )
        {
        }

    }

    @Override
    public final int getMaximumSpeed()
    {
        return m_maxSpeed;
    }

    @Override
    public final int getCurrentSpeed()
    {
        return m_speed;
    }

    @Override
    public final void setCurrentSpeed( final int p_speed )
    {
        m_speed = p_speed;
    }

    @Override
    public final double getLingerProbability()
    {
        return m_LingerProbability;
    }

    @Override
    public final void reroute()
    {
        this.reroute( m_EndPosition );
    }

    @Override
    public final void reroute( final GeoPosition p_position )
    {
        m_EndPosition = p_position;

        final List<List<EdgeIteratorState>> l_route = m_graph.getRoutes( this.getGeoposition(), m_EndPosition, 1 );
        if ( ( l_route != null ) && ( l_route.size() > 0 ) )
        {
            if ( m_routeindex < m_route.size() - 1 ) m_route.subList( m_routeindex + 1, m_route.size() ).clear();
            m_route.addAll( m_graph.getRouteCells( l_route.get( 0 ) ) );
        }
    }

    @Override
    public final GeoPosition getGeoposition()
    {
        final EdgeIteratorState l_edgeid = this.getEdge();
        if ( l_edgeid == null ) return null;

        return m_graph.getEdge( l_edgeid ).getGeoposition( this );
    }

    @Override
    public final boolean hasEndReached()
    {
        return m_endReached;
    }

    @Override
    public final Map<Integer, ICar> getPredecessor()
    {
        return this.getPredecessor( 1 );
    }

    @Override
    public final Map<Integer, ICar> getPredecessor( final int p_count )
    {
        final Map<Integer, ICar> l_predecessordistance = new HashMap<>();

        // we get the nearest predecessor within the speed range (performance boost)
        for ( int i = m_routeindex + 1; ( i <= m_routeindex + m_speed ) && ( i < m_route.size() ) && ( l_predecessordistance.size() < p_count ); i++ )
        {
            final ICar l_object = (ICar) m_graph.getEdge( m_route.get( i ).getLeft() ).getObject( m_route.get( i ).getRight() );
            if ( l_object != null ) l_predecessordistance.put( i - m_routeindex, l_object );
        }

        return l_predecessordistance;
    }

    @Override
    public final EdgeIteratorState getEdge()
    {
        return this.getEdge( m_routeindex );
    }

    @Override
    public final int getAcceleration()
    {
        return m_acceleration;
    }

    @Override
    public final int getDeceleration()
    {
        return m_deceleration;
    }

    /**
     * returns the edge from an index
     *
     * @param p_index index
     * @return null or edge
     */
    private final EdgeIteratorState getEdge( final int p_index )
    {
        if ( m_route == null ) return null;

        return p_index < m_route.size() ? m_route.get( p_index ).getLeft() : null;
    }

    /**
     * @bug UI frame
     */
    @Override
    public final void onClick( final MouseEvent p_event, final JXMapViewer p_viewer )
    {
        final GeoPosition l_position = this.getGeoposition();
        if ( l_position == null ) return;

        final int l_zoom = this.iconsize( p_viewer );
        final Point2D l_point = p_viewer.getTileFactory().geoToPixel( l_position, p_viewer.getZoom() );
        final Ellipse2D l_circle = new Ellipse2D.Double( l_point.getX() - p_viewer.getViewportBounds().getX(), l_point.getY() - p_viewer.getViewportBounds().getY(), l_zoom, l_zoom );

        //if ( l_circle.contains( p_event.getX(), p_event.getY() ) )
        //    ( (CInspector) CSimulation.getInstance().getUIServer().getWidget( "Inspector" ) ).set( this );
    }

    @Override
    public void release()
    {
        super.release();
        final CEdge l_edge = m_graph.getEdge( this.getEdge() );
        if ( l_edge != null ) l_edge.removeObject( this );
    }

    @Override
    public Map<String, Object> inspect()
    {
        final Map<String, Object> l_map = super.inspect();

        l_map.put( CCommon.getResourceString( CDefaultCar.class, "currentspeed" ), m_speed );
        l_map.put( CCommon.getResourceString( CDefaultCar.class, "maximumspeed" ), m_maxSpeed );
        l_map.put( CCommon.getResourceString( CDefaultCar.class, "acceleration" ), m_acceleration );
        l_map.put( CCommon.getResourceString( CDefaultCar.class, "deceleration" ), m_deceleration );
        l_map.put( CCommon.getResourceString( CDefaultCar.class, "startposition" ), m_StartPosition );
        l_map.put( CCommon.getResourceString( CDefaultCar.class, "endposition" ), m_EndPosition );
        l_map.put( CCommon.getResourceString( CDefaultCar.class, "streetname" ), m_route.get( m_routeindex ).getLeft().getName() );
        l_map.put( CCommon.getResourceString( CDefaultCar.class, "currentgeoposition" ), this.getGeoposition() );

        return l_map;
    }

    /**
     * @todo draw route (solid for driven way, dashed for driving way)
     */
    @Override
    public final void paint( final Graphics2D p_graphic, final COSMViewer p_viewer, final int p_width, final int p_height )
    {
        final GeoPosition l_position = this.getGeoposition();
        if ( l_position == null ) return;

        final int l_zoom = this.iconsize( p_viewer );
        final Point2D l_point = p_viewer.getTileFactory().geoToPixel( l_position, p_viewer.getZoom() );

        // speed limit color defined with http://wiki.openstreetmap.org/wiki/File:Speed_limit_Germany.png
        p_graphic.setColor( Color.DARK_GRAY );
        if ( m_speed >= 50 ) p_graphic.setColor( Color.MAGENTA );
        if ( m_speed >= 60 ) p_graphic.setColor( Color.PINK );
        if ( m_speed >= 80 ) p_graphic.setColor( Color.BLUE );
        if ( m_speed >= 100 ) p_graphic.setColor( Color.CYAN );
        if ( m_speed >= 130 ) p_graphic.setColor( Color.RED );

        p_graphic.fillOval( (int) l_point.getX(), (int) l_point.getY(), l_zoom, l_zoom );
    }


    @Override
    public final void step( final int p_currentstep, final ILayer p_layer ) throws Exception
    {

        // if the car is at the end
        if ( this.hasEndReached() ) return;

        // if the car reaches the end
        int l_speed = this.getCurrentSpeed();
        if ( m_routeindex + l_speed >= m_route.size() )
        {
            m_endReached = true;
            l_speed = m_route.size() - m_routeindex - 1;
        }


        // if the route index equal to zero, push it car on the first item or wait until it is free
        if ( m_routeindex == 0 )
        {

            if ( !m_graph.getEdge( m_route.get( l_speed ).getLeft() ).isEmpty( m_route.get( l_speed ).getRight().intValue() ) )
                return;

            try
            {
                m_graph.getEdge( m_route.get( l_speed ).getLeft() ).setObject( this, m_route.get( l_speed ).getRight().intValue() );
                m_routeindex += l_speed;
            }
            catch ( final IllegalAccessException l_exception )
            {
            }

        }
        else
        {

            try
            {
                m_graph.getEdge( m_route.get( m_routeindex ).getLeft() ).removeObject( this );
                m_graph.getEdge( m_route.get( m_routeindex + l_speed ).getLeft() ).setObject( this, m_route.get( m_routeindex + l_speed ).getRight() );
                m_routeindex += l_speed;
            }
            catch ( final IllegalAccessException l_exception )
            {
                m_graph.getEdge( m_route.get( m_routeindex ).getLeft() ).removeObject( this );
            }

        }

    }

    @Override
    public final Map<String, Object> analyse()
    {
        return null;
    }
}
