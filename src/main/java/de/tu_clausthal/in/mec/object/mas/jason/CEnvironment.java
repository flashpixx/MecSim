/**
 * @cond
 * ######################################################################################
 * # GPL License                                                                        #
 * #                                                                                    #
 * # This file is part of the TUC Wirtschaftsinformatik - MecSim                        #
 * * # Copyright (c) 2014-15, Philipp Kraus, <philipp.kraus@tu-clausthal.de>            #
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
 * # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 * ######################################################################################
 * @endcond
 **/

package de.tu_clausthal.in.mec.object.mas.jason;

import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.object.ILayer;
import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.simulation.IStepable;
import de.tu_clausthal.in.mec.ui.CBrowser;
import de.tu_clausthal.in.mec.ui.CFrame;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


/**
 * class of the Jason environment - the task of this class is the communication with outside data (IO with other
 * structure), in this context the environment class encapsulate all behaviour inside, because it will be triggerd from
 * the simulation core thread
 *
 * @see http://jason.sourceforge.net/api/jason/environment/package-summary.html
 */
public class CEnvironment<T extends IStepable> extends IMultiLayer<CAgentArchitecture<T>>
{

    /**
     * global literal storage *
     */
    protected CLiteralStorage m_literals = new CLiteralStorage();
    /**
     * map with actions of the environment *
     */
    protected Map<String, Pair<Method, Object>> m_actions = new HashMap();
    /**
     * agent architecture
     */
    protected CAgentArchitecture<myBindingTest> m_agentarchitecture = null;
    /**
     * browser of the mindinspector - binding to the server port can be done after the first agent is exists
     */
    protected CBrowser m_mindinspector = new CBrowser();


    /** ctor of Jason structure
     *
     * @param p_frame frame object set Jason mindinspector
     * @param p_title title of the mindinspector
     */
    public CEnvironment( CFrame p_frame, String p_title )
    {
        // @todo try to refactor - Jason binds a WebMindInspector on all network interfaces at the port 3272, without any kind of disabeling / modifiying
        // @see https://sourceforge.net/p/jason/svn/1817/tree/trunk/src/jason/architecture/MindInspectorWeb.java
        p_frame.addWidget( "Jason Mindinspector [" + p_title + "]", m_mindinspector );

        m_agentarchitecture = new CAgentArchitecture<myBindingTest>( new myBindingTest() );
        m_actions = m_literals.addObjectMethods( this );
    }

    /**
     * register object methods
     *
     * @param p_object object
     */
    public void registerObjectMethods( Object p_object )
    {
        m_actions.putAll( m_literals.addObjectMethods( p_object ) );
    }

    @Override
    public void step( int p_currentstep, ILayer p_layer )
    {
        // get all data for global perceptions (get analyse function and all properties of the object
        m_literals.addAll( this.analyse() );
        m_literals.addObjectFields( this );
        m_literals.add( "simulationstep", p_currentstep );


        try
        {
            // mind inspector works after an agent exists, so we need
            // to bind the browser after the first agent exists
            m_agentarchitecture.createAgent( "agent" );

            if ( m_agentarchitecture.getAgentNumber() == 1 )
                m_mindinspector.load( "http://localhost:3272" );

        }
        catch ( Exception l_exception )
        {
            CLogger.error( l_exception );
        }
    }


    /**
     * test class for agent method / field binding *
     */
    private class myBindingTest implements IStepable
    {

        @Override
        public Map<String, Object> analyse()
        {
            return null;
        }
    }

}