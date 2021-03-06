/**
 * @cond LICENSE
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the micro agent-based traffic simulation MecSim of            #
 * # Clausthal University of Technology - Mobile and Enterprise Computing               #
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

package de.tu_clausthal.in.mec.object.waypoint.point;


import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.waypoint.factory.IFactory;
import de.tu_clausthal.in.mec.object.waypoint.generator.IGenerator;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;


/**
 * creates a waypoint with a random target
 */
public abstract class IRandomWayPoint<T, P extends IFactory<T>, N extends IGenerator> extends IWayPointBase<T, P, N>
{

    /**
     * inspector map
     */
    private final Map<String, Object> m_inspect = new HashMap<String, Object>()
    {{
        putAll( IRandomWayPoint.super.inspect() );
    }};
    /**
     * radius around the waypoint *
     */
    private final double m_radius;
    /**
     * random interface
     */
    private final Random m_random = new Random();

    /**
     * ctor
     *
     * @param p_position geoposition of the waypoint
     * @param p_generator generator
     * @param p_factory factory
     * @param p_radius radius around the waypoint
     * @param p_color color
     * @param p_name name
     */
    public IRandomWayPoint( final GeoPosition p_position, final N p_generator, final P p_factory, final double p_radius, final Color p_color,
            final String p_name
    )
    {
        super( p_position, p_generator, p_factory, p_color, p_name );
        m_radius = p_radius;
        m_inspect.put( CCommon.getResourceString( IRandomWayPoint.class, "radius" ), m_radius );
    }

    @Override
    public Collection<Pair<GeoPosition, GeoPosition>> getPath()
    {
        return new HashSet<Pair<GeoPosition, GeoPosition>>()
        {{
            add(
                    new ImmutablePair<GeoPosition, GeoPosition>(
                            getPosition(), new GeoPosition(
                            getPosition().getLatitude() + m_radius * m_random.nextDouble() - m_radius / 2,
                            getPosition().getLongitude() + m_radius * m_random.nextDouble() - m_radius / 2
                    )
                    )
            );
        }};
    }

    @Override
    public Map<String, Object> inspect()
    {
        return m_inspect;
    }

}
