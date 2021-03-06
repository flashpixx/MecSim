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

package de.tu_clausthal.in.mec.object.car;


import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.object.mas.IAgent;
import de.tu_clausthal.in.mec.object.mas.inconsistency.CInconsistencyLayer;
import de.tu_clausthal.in.mec.object.mas.jason.CAgent;
import de.tu_clausthal.in.mec.object.mas.jason.IEnvironment;


/**
 * layer for car agents
 */
public final class CCarJasonAgentLayer extends IEnvironment<CDefaultCar>
{
    /**
     * reference to the inconsitency layer
     */
    private final CInconsistencyLayer m_inconsistencyLayer;

    /**
     * ctor
     *
     * @param p_inconsistencyLayer inconsistency layer
     */
    public CCarJasonAgentLayer( final CInconsistencyLayer p_inconsistencyLayer )
    {
        m_inconsistencyLayer = p_inconsistencyLayer;
    }

    @Override
    public final void afterStepObject( final int p_currentstep, final CAgent<CDefaultCar> p_object
    )
    {

    }

    @Override
    public final void beforeStepObject( final int p_currentstep, final CAgent<CDefaultCar> p_object
    )
    {

    }

    @Override
    public int getCalculationIndex()
    {
        return 100;
    }

    @Override
    public boolean add( final CAgent<CDefaultCar> p_value )
    {
        return super.add( p_value ) &&
               m_inconsistencyLayer.add( p_value );
    }

    @Override
    public boolean remove( final Object p_object )
    {
        return super.remove( p_object ) &&
               m_inconsistencyLayer.remove( (IAgent) p_object );
    }

    @Override
    public final String toString()
    {
        return CCommon.getResourceString( this, "name" );
    }

    /**
     * returns the inconsistency value of an object
     *
     * @return inconsistency value
     */
    public Double getInconsistencyValue( final CAgent<CDefaultCar> p_value )
    {
        return m_inconsistencyLayer.getInconsistencyValue( p_value );
    }

}
