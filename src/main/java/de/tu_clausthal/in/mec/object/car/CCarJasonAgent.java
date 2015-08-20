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
import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.object.mas.CFieldFilter;
import de.tu_clausthal.in.mec.object.mas.CMethodFilter;
import de.tu_clausthal.in.mec.object.mas.generic.IBeliefBase;
import de.tu_clausthal.in.mec.object.mas.generic.IBeliefBaseMask;
import de.tu_clausthal.in.mec.object.mas.generic.ILiteral;
import de.tu_clausthal.in.mec.object.mas.generic.implementation.CBeliefBase;
import de.tu_clausthal.in.mec.object.mas.generic.implementation.IOneTimeStorage;
import de.tu_clausthal.in.mec.object.mas.jason.belief.CLiteral;
import de.tu_clausthal.in.mec.object.mas.jason.belief.CMask;
import de.tu_clausthal.in.mec.runtime.CSimulation;
import de.tu_clausthal.in.mec.runtime.message.IMessage;
import de.tu_clausthal.in.mec.runtime.message.IReceiver;
import jason.JasonException;
import jason.asSyntax.Literal;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * agent car
 *
 * @bug refactor ctor (reduce parameter)
 */
public class CCarJasonAgent extends CDefaultCar implements IReceiver
{
    /**
     * agent object *
     */
    @CFieldFilter.CAgent( bind = false )
    private final Set<de.tu_clausthal.in.mec.object.mas.jason.CAgent> m_agents = new HashSet<>();
    /**
     * inspector map
     */
    @CFieldFilter.CAgent( bind = false )
    private final Map<String, Object> m_inspect = new HashMap<String, Object>()
    {{
            putAll( CCarJasonAgent.super.inspect() );
        }};
    /**
     * internal receiver path
     */
    @CFieldFilter.CAgent( bind = false )
    private final CPath m_objectpath;
    /**
     * traffic environment belief base
     */
    @CFieldFilter.CAgent( bind = false )
    private final IBeliefBase<Literal> m_trafficbeliefbase = new CBeliefBase<>( new CTrafficStorage() );

    /**
     * ctor
     *
     * @param p_route driving route
     * @param p_speed initial speed
     * @param p_maxspeed maximum speed
     * @param p_acceleration acceleration
     * @param p_deceleration decceleration
     * @param p_lingerprobability linger probability
     * @param p_objectname name of the object within the simulation
     * @param p_agent ASL / agent name     *
     * @throws JasonException throws on Jason error
     */
    public CCarJasonAgent( final ArrayList<Pair<EdgeIteratorState, Integer>> p_route, final int p_speed, final int p_maxspeed, final int p_acceleration,
            final int p_deceleration, final double p_lingerprobability, final String p_objectname, final String p_agent
    ) throws JasonException

    {
        this(
                p_route, p_speed, p_maxspeed, p_acceleration, p_deceleration, p_lingerprobability,
                p_objectname, new HashSet<String>()
                {{
                        add( p_agent );
                    }}
        );
    }

    /**
     * @param p_route driving route
     * @param p_speed initial speed
     * @param p_maxspeed maximum speed
     * @param p_acceleration acceleration
     * @param p_deceleration decceleration
     * @param p_lingerprobability linger probability
     * @param p_objectname name of the object within the simulation
     * @param p_agent set with ASL / agent name
     * @throws JasonException throws on Jason error
     */
    public CCarJasonAgent( final ArrayList<Pair<EdgeIteratorState, Integer>> p_route, final int p_speed, final int p_maxspeed, final int p_acceleration,
            final int p_deceleration, final double p_lingerprobability, final String p_objectname, final Set<String> p_agent
    ) throws JasonException
    {
        super( p_route, p_speed, p_maxspeed, p_acceleration, p_deceleration, p_lingerprobability );
        m_objectpath = new CPath( "traffic", "car", CSimulation.getInstance().generateObjectName( p_objectname, this ) );
        for ( final String l_item : p_agent )
            this.bind( l_item );
    }

    @Override
    public CPath getReceiverPath()
    {
        return m_objectpath;
    }

    @Override
    public void receiveMessage( final Set<IMessage> p_messages )
    {
    }

    @Override
    @CMethodFilter.CAgent( bind = false )
    public final Map<String, Object> inspect()
    {
        return m_inspect;
    }

    @Override
    @CMethodFilter.CAgent( bind = false )
    public final void release()
    {
        super.release();
        for ( final de.tu_clausthal.in.mec.object.mas.jason.CAgent l_agent : m_agents )
        {
            l_agent.release();
            CSimulation.getInstance().getWorld().<IMultiLayer>getTyped( "Jason Car Agents" ).remove( l_agent );
        }
    }

    @Override
    @CMethodFilter.CAgent( bind = false )
    public void step( final int p_currentstep, final ILayer p_layer ) throws Exception
    {
        // check speed, because agent can modify the speed value and the value should always in range
        // [0,max-speed] other values are declared as final member
        m_speed = Math.min( Math.max( 0, m_speed ), m_maxspeed );
        super.step( p_currentstep, p_layer );
    }

    /**
     * binds an agent with the name
     *
     * @param p_asl ASL / agent name
     * @throws JasonException throws on Jason error
     */
    @CMethodFilter.CAgent( bind = false )
    private void bind( final String p_asl ) throws JasonException
    {
        final de.tu_clausthal.in.mec.object.mas.jason.CAgent l_agent = new de.tu_clausthal.in.mec.object.mas.jason.CAgent(
                m_objectpath.append( p_asl ), p_asl, this
        );
        m_inspect.put( CCommon.getResourceString( this, "agent", l_agent.getName() ), l_agent.getSource() );

        // add local beliefbase to the agent
        l_agent.getBeliefBase().add( new CMask( "mytraffic", m_trafficbeliefbase, "_" ) );

        // add agent to layer and internal set
        CSimulation.getInstance().getWorld().<IMultiLayer>getTyped( "Jason Car Agents" ).add( l_agent );
        m_agents.add( l_agent );
    }

    /**
     * returns the predecessor with name for communication
     *
     * @param p_count number of predecessors
     * @return map with distance and map with name and can communicate
     */
    private Map<Double, Map<String, Object>> getPredecessorWithName( final int p_count )
    {
        final Map<Double, Map<String, Object>> l_predecessor = new HashMap<>();

        for ( final Map.Entry<Double, ICar> l_item : this.getPredecessor( p_count ).entrySet() )
        {
            final ICar l_car = l_item.getValue();
            final boolean l_isagent = l_car instanceof CCarJasonAgent;

            l_predecessor.put(
                    l_item.getKey(),
                    CCommon.getMap(
                            "name", l_isagent ? ( (CCarJasonAgent) l_car ).getReceiverPath().toString() : l_car.toString(),
                            "isagent", l_isagent
                    )
            );
        }

        return l_predecessor;
    }


    /**
     * private class for definined a belief storage to use "traffic content information"
     *
     * @bug inconsistency value works with one agent only
     */
    private class CTrafficStorage extends IOneTimeStorage<ILiteral<Literal>, IBeliefBaseMask<Literal>>
    {
        /**
         * name of the inconsistency belief
         **/
        private static final String c_inconsistencyname = "inconsistency";
        /**
         * name of the predecessor
         **/
        private static final String c_predecessor = "predecessor";

        @Override
        protected void updating()
        {
            this.add( de.tu_clausthal.in.mec.object.mas.jason.CCommon.getLiteral( c_predecessor, CCarJasonAgent.this.getPredecessorWithName( 1 ) ) );

            for ( final de.tu_clausthal.in.mec.object.mas.jason.CAgent l_agent : m_agents )
                this.add(
                        de.tu_clausthal.in.mec.object.mas.jason.CCommon.getLiteral(
                                c_inconsistencyname,
                                CSimulation.getInstance().getWorld().<CCarJasonAgentLayer>getTyped( "Jason Car Agents" ).getInconsistencyValue( l_agent )
                        )
                );
        }

        /**
         * adds an element to the storage
         *
         * @param p_literal literal
         */
        private void add( final Literal p_literal )
        {
            final Set<ILiteral<Literal>> l_elements = m_multielements.getOrDefault( p_literal.getFunctor(), new HashSet<>() );
            l_elements.add( new CLiteral( p_literal ) );
            m_multielements.put( p_literal.getFunctor(), l_elements );
        }

    }
}