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

package de.tu_clausthal.in.mec.ui.inspector;

import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.ui.IUIListener;

import java.util.HashMap;
import java.util.Map;


/**
 * global interface of the simulation with mouse event handler
 */
public abstract class IInspector extends IUIListener
{

    /**
     * inspect variable *
     */
    private Map<String, Object> m_inspect = new HashMap<>();


    /**
     * returns a map to inspect current data of the car
     *
     * @return map with name and value
     */
    public Map<String, Object> inspect()
    {
        m_inspect.put( CCommon.getResourceString(IInspector.class, "classname"), CCommon.removePackageName( this.getClass().getName() ) );
        m_inspect.put( CCommon.getResourceString(IInspector.class, "objectid"), this.hashCode() );

        return m_inspect;
    }

}
