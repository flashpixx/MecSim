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

package de.tu_clausthal.in.mec.ui;

import de.tu_clausthal.in.mec.common.CCommon;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * ui bundle which is responsible for the waypoint-tool settings
 *
 * @todo full refactor
 */
public class CWaypointEnvironment
{
    /**
     * map with default properties
     */
    private final static Map<String, Object> c_defaultproperties = new HashMap<String, Object>()
    {{
            put( "waypointtype", EWayPointType.CarWaypointRandom.m_name );
            put( "radius", 0.75 );
            put( "factory", EFactoryType.DefaultCarFactory.m_name );
            put( "agentprogram", "" );
            put( "generator", EDistributionType.Normal.m_name );
            put( "carcount", 1 );
            put( "generatorinput1", 5 );
            put( "generatorinput2", 1 );
            put( "speedprob", EDistributionType.Normal.m_name );
            put( "speedprobinput1", 50 );
            put( "speedprobinput2", 25 );
            put( "maxspeedprob", EDistributionType.Normal.m_name );
            put( "maxspeedprobinput1", 250 );
            put( "maxspeedprobinput2", 50 );
            put( "accprob", EDistributionType.Normal.m_name );
            put( "accprobinput1", 20 );
            put( "accprobinput2", 5 );
            put( "decprob", EDistributionType.Normal.m_name );
            put( "decprobinput1", 20 );
            put( "decprobinput2", 5 );
            put( "lingerprobinput1", 0 );
            put( "lingerprobinput2", 1 );
            put( "name", CCommon.getResourceString( CWaypointEnvironment.class, "defaulttoolname" ) );
            put( "red", 255 );
            put( "green", 0 );
            put( "blue", 0 );
        }};

    /**
     * map with distribution names
     */
    private final static Map<String, Map<String, String>> c_distribution = new HashMap<String, Map<String, String>>()
    {{
            for ( final EDistributionType l_distribution : EDistributionType.values() )
                put(
                        l_distribution.toString(), new HashMap()
                        {{
                                put( "left", l_distribution.getBoundNames().getLeft() );
                                put( "right", l_distribution.getBoundNames().getRight() );
                                put( "id", l_distribution.name() );
                            }}
                );
        }};

    /**
     * map with factory names
     */
    private final static Map<String, Map<String, Object>> c_factories = new HashMap<String, Map<String, Object>>()
    {{
            for ( final EFactoryType l_factory : EFactoryType.values() )
                put(
                        l_factory.toString(), new HashMap()
                        {{
                                put( "agentprogram", l_factory.getRequireAgent() );
                                put( "id", l_factory.name() );
                            }}
                );
        }};

    /**
     * static label for UI
     */
    private static final Map<String, String> c_label = new HashMap<String, String>()
    {{
            // window title
            put( "name", CCommon.getResourceString( CWaypointEnvironment.class, "wizardwidget" ) );

            // wizard step header
            put( "id_factoryhead", CCommon.getResourceString( CWaypointEnvironment.class, "factorysettings" ) );
            put( "id_basegeneratorhead", CCommon.getResourceString( CWaypointEnvironment.class, "generatorsettings" ) );
            put( "id_carhead", CCommon.getResourceString( CWaypointEnvironment.class, "carsettings" ) );
            put( "id_customhead", CCommon.getResourceString( CWaypointEnvironment.class, "customizing" ) );

            // wizard first step
            put( "label_type", CCommon.getResourceString( CWaypointEnvironment.class, "selectwaypointtype" ) );
            put( "label_radius", CCommon.getResourceString( CWaypointEnvironment.class, "selectwaypointradius" ) );
            put( "label_basedistribution", CCommon.getResourceString( CWaypointEnvironment.class, "selectyourgenerator" ) );
            put( "label_factory", CCommon.getResourceString( CWaypointEnvironment.class, "selectyourfactory" ) );
            put( "label_agent", CCommon.getResourceString( CWaypointEnvironment.class, "selectyouragentprogram" ) );

            // wizard second step
            put( "label_carcount", CCommon.getResourceString( CWaypointEnvironment.class, "selectyourcarcount" ) );

            // wizard third step
            put( "id_speedhead", CCommon.getResourceString( CWaypointEnvironment.class, "speedsettingslabel" ) );
            put( "id_maxspeedhead", CCommon.getResourceString( CWaypointEnvironment.class, "maxspeedsettingslabel" ) );
            put( "id_accelerationhead", CCommon.getResourceString( CWaypointEnvironment.class, "accsettingslabel" ) );
            put( "id_decelerationhead", CCommon.getResourceString( CWaypointEnvironment.class, "decsettingslabel" ) );
            put( "id_lingerhead", CCommon.getResourceString( CWaypointEnvironment.class, "lingerersettingslabel" ) );

            put( "label_linger", CCommon.getResourceString( CWaypointEnvironment.class, "selectlingerprob" ) );
            put( "label_speeddistribution", CCommon.getResourceString( CWaypointEnvironment.class, "selectspeedprob" ) );
            put( "label_maxspeeddistribution", CCommon.getResourceString( CWaypointEnvironment.class, "selectmaxspeedprob" ) );
            put( "label_accelerationdistribution", CCommon.getResourceString( CWaypointEnvironment.class, "selectaccprob" ) );
            put( "label_decelerationdistribution", CCommon.getResourceString( CWaypointEnvironment.class, "selectdecprob" ) );

            // wizard fourth step
            put( "label_name", CCommon.getResourceString( CWaypointEnvironment.class, "selecttoolnamelabel" ) );
            put( "label_color", CCommon.getResourceString( CWaypointEnvironment.class, "selecttoolcolor" ) );

        }};

    /**
     * waypoint layer to edit makrov chain
     */
    //private static final CCarWayPointLayer m_waypointLayer = CSimulation.getInstance().getWorld().<CCarWayPointLayer>getTyped( "Car WayPoints" );

    /**
     * returns all static label information
     *
     * @return map with labels
     */
    private final Map<String, String> web_static_label()
    {
        return c_label;
    }


    /**
     * list all possible distribution types
     *
     * @return map with names of distribution
     */
    private final Map<String, Map<String, String>> web_static_listdistribution()
    {
        return c_distribution;
    }

    /**
     * list all possible factory types
     *
     * @return
     */
    private final Map<String, Map<String, Object>> web_static_listfactory()
    {
        return c_factories;
    }

    /**
     * list all possible waypoint types
     *
     * @return
     * @todo refactor
     */
    private final List<String> web_static_listwaypointtype()
    {
        List<String> l_waypointtypes = new ArrayList<>();

        for ( final EWayPointType l_waypoint : EWayPointType.values() )
            l_waypointtypes.add( l_waypoint.m_name );

        return l_waypointtypes;
    }


    /**
     * enum for waypoint types
     */
    private enum EWayPointType
    {
        CarWaypointRandom( CCommon.getResourceString( EWayPointType.class, "carwaypointrandom" ) ),
        CayWaypointPath( CCommon.getResourceString( EWayPointType.class, "carwaypointpath" ) );

        /**
         * name of this waypoint type
         */
        private final String m_name;

        /**
         * ctor
         *
         * @param p_name
         */
        private EWayPointType( final String p_name )
        {
            this.m_name = p_name;
        }

        @Override
        public String toString()
        {
            return m_name;
        }

    }

    /**
     * enum for factory type
     */
    private enum EFactoryType
    {
        DefaultCarFactory( CCommon.getResourceString( EFactoryType.class, "defaultcarfactory" ), false ),
        DefaultAgentCarFactory( CCommon.getResourceString( EFactoryType.class, "defaultagentcarfactory" ), true );

        /**
         * name of this factory type
         */
        private final String m_name;

        /**
         * variable indicate if this factory type require an agent program
         */
        private final boolean m_requireAgent;

        /**
         * ctor
         *
         * @param p_name
         * @param p_requireAgent
         */
        private EFactoryType( final String p_name, final boolean p_requireAgent )
        {
            this.m_name = p_name;
            this.m_requireAgent = p_requireAgent;
        }

        public boolean getRequireAgent()
        {
            return m_requireAgent;
        }

        @Override
        public String toString()
        {
            return m_name;
        }


    }

    /**
     * enum for generator type
     */
    private enum EDistributionType
    {
        Normal(
                CCommon.getResourceString( EDistributionType.class, "normaldistribution" ), CCommon.getResourceString(
                EDistributionType.class, "normaldistributionleft"
        ), CCommon.getResourceString( EDistributionType.class, "normaldistributionright" )
        ),
        Uniform(
                CCommon.getResourceString( EDistributionType.class, "uniformdistribution" ), CCommon.getResourceString(
                EDistributionType.class, "uniformdistributionleft"
        ), CCommon.getResourceString( EDistributionType.class, "uniformdistributionright" )
        ),
        Exponential(
                CCommon.getResourceString( EDistributionType.class, "exponentialdistribution" ), CCommon.getResourceString(
                EDistributionType.class, "exponentialdistributionleft"
        ), CCommon.getResourceString( EDistributionType.class, "exponentialdistributionright" )
        ),
        Profile(
                CCommon.getResourceString( EDistributionType.class, "profiledistribution" ), CCommon.getResourceString(
                EDistributionType.class, "profiledistributionleft"
        ), CCommon.getResourceString( EDistributionType.class, "profiledistributionright" )
        );

        private final Pair<String, String> m_boundname;
        /**
         * name of this distribution type
         */
        private final String m_name;


        /**
         * ctor
         *
         * @param p_name
         */
        private EDistributionType( final String p_name, final String m_left, final String m_right )
        {
            m_name = p_name;
            m_boundname = new ImmutablePair<>( m_left, m_right );
        }

        /**
         * @return
         */
        public Pair<String, String> getBoundNames()
        {
            return m_boundname;
        }

        @Override
        public String toString()
        {
            return m_name;
        }
    }

}
