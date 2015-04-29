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

package de.tu_clausthal.in.mec.object.car;


import com.graphhopper.util.EdgeIteratorState;
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.object.mas.jason.CAgent;
import de.tu_clausthal.in.mec.object.mas.jason.action.CMethodBind;
import de.tu_clausthal.in.mec.runtime.CSimulation;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * agent car
 *
 * @bug check agent name / structure
 */
public class CCarJasonAgent extends CDefaultCar
{

    /**
     * list of forbidden names *
     */
    private static final Set<String> c_forbidden = new HashSet()
    {{
            add( "m_agent" );
            add( "m_route" );
            add( "m_graph" );
            add( "m_inspect" );
            add( "m_random" );
            add( "m_endReached" );
            add( "m_routeindex" );
            add( "m_EndPosition" );
            add( "m_StartPosition" );
            add( "release" );
            add( "paint" );
            add( "step" );
            add( "inspect" );
            add( "onClick" );
        }};


    /**
     * agent object *
     */
    private CAgent<CDefaultCar> m_agent;


    /**
     * ctor to create the initial values
     *
     * @param p_route driving route
     * @param p_asl agent ASL file
     */
    public CCarJasonAgent( final ArrayList<Pair<EdgeIteratorState, Integer>> p_route, final String p_asl )
    {
        super( p_route );

        try
        {

            m_agent = new CAgent<>( new CPath( this.getClass().getSimpleName() + "@" + this.hashCode(), "agent" ), p_asl );

            // add the belief bind to the agent
            final de.tu_clausthal.in.mec.object.mas.jason.belief.CFieldBind l_belief = new de.tu_clausthal.in.mec.object.mas.jason.belief.CFieldBind(
                    "self", this, c_forbidden
            );
            m_agent.getBelief().add( l_belief );

            // add the method bind to the agent
            final CMethodBind l_method = new CMethodBind( "self", this );
            l_method.getForbidden( "self" ).addAll( c_forbidden );
            m_agent.getActions().put( "invoke", l_method );

            // add the set bind to the agent
            final de.tu_clausthal.in.mec.object.mas.jason.action.CFieldBind l_set = new de.tu_clausthal.in.mec.object.mas.jason.action.CFieldBind(
                    "self", this, c_forbidden
            );
            m_agent.getActions().put( "set", l_set );


            // add agent to layer
            CSimulation.getInstance().getWorld().<IMultiLayer>getTyped( "Jason Car Agents" ).add( m_agent );

        }
        catch ( final Exception l_exception )
        {
            CLogger.error( l_exception );
        }
    }

    @Override
    public final void release()
    {
        super.release();
        if ( m_agent != null )
            m_agent.release();
    }

    @Override
    public final Map<String, Object> inspect()
    {
        final Map<String, Object> l_map = super.inspect();
        if ( m_agent == null )
            return l_map;

        l_map.put( CCommon.getResourceString( this, "asl" ), m_agent.getSource() );
        l_map.put( CCommon.getResourceString( this, "cycle" ), m_agent.getCycle() );
        l_map.put( CCommon.getResourceString( this, "agent" ), m_agent.getName() );
        return l_map;
    }
}