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

import java.util.HashMap;
import java.util.Map;


/**
 * class which is responsible for multi language support in the UI
 */
public class CLanguageEnvironment
{
    /**
     * labels of the help menu
     */
    private static final Map<String, String> c_help = new HashMap<String, String>()
    {{
            put( "about", CCommon.getResourceString( CLanguageEnvironment.class, "helpabout" ) );
            put( "userdoc", CCommon.getResourceString( CLanguageEnvironment.class, "helpuserdoc" ) );
            put( "devdoc", CCommon.getResourceString( CLanguageEnvironment.class, "helpdevdoc" ) );
        }};
    /**
     * labels of the MAS menu
     */
    private static final Map<String, String> c_mas = new HashMap<String, String>()
    {{
            put( "jasonmind", CCommon.getResourceString( CLanguageEnvironment.class, "masjasonmind" ) );
            put( "communication", CCommon.getResourceString( CLanguageEnvironment.class, "mascommunication" ) );
        }};
    /**
     * labels of the simulation menu
     */
    private static final Map<String, String> c_simulation = new HashMap<String, String>()
    {{
            put( "start", CCommon.getResourceString( CLanguageEnvironment.class, "simulationstart" ) );
            put( "stop", CCommon.getResourceString( CLanguageEnvironment.class, "simulationstop" ) );
            put( "reset", CCommon.getResourceString( CLanguageEnvironment.class, "simulationreset" ) );
            put( "load", CCommon.getResourceString( CLanguageEnvironment.class, "simulationload" ) );
            put( "save", CCommon.getResourceString( CLanguageEnvironment.class, "simulationsave" ) );
            put( "dialogtitle", CCommon.getResourceString( CLanguageEnvironment.class, "simulationdialogtitle" ) );
        }};
    /**
     * labels of the waypoint menu
     */
    private static final Map<String, String> c_waypoint = new HashMap<String, String>()
    {{
            put( "newpreset", CCommon.getResourceString( CLanguageEnvironment.class, "waypointnewpreset" ) );
            put( "list", CCommon.getResourceString( CLanguageEnvironment.class, "waypointlist" ) );
        }};

    /**
     * returns all static label for the MAS
     *
     * @return map with static labels
     */
    private final Map<String, String> web_static_help()
    {
        return c_help;
    }

    /**
     * returns all static label for the MAS
     *
     * @return map with static labels
     */
    private final Map<String, String> web_static_mas()
    {
        return c_mas;
    }

    /**
     * returns all static label for the simulation
     *
     * @return map with static labels
     */
    private final Map<String, String> web_static_simulation()
    {
        return c_simulation;
    }

    /**
     * returns all static label for the MAS
     *
     * @return map with static labels
     */
    private final Map<String, String> web_static_waypoint()
    {
        return c_waypoint;
    }

}
