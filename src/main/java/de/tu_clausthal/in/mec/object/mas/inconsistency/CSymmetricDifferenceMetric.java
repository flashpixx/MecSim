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

package de.tu_clausthal.in.mec.object.mas.inconsistency;

import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.object.mas.IAgent;
import de.tu_clausthal.in.mec.object.mas.general.ILiteral;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * metric on collections returns the size of symmetric difference
 */
public class CSymmetricDifferenceMetric<T extends IAgent> extends IDefaultMetric<T>
{
    /**
     * ctor
     *
     * @param p_paths path list
     */
    public CSymmetricDifferenceMetric( final CPath... p_paths )
    {
        super( p_paths );
    }

    /**
     * copy ctor
     *
     * @param p_metric metric
     */
    public CSymmetricDifferenceMetric( final IDefaultMetric<T> p_metric )
    {
        super( p_metric );
    }

    @Override
    public double calculate( final T p_first, final T p_second )
    {
        // equal objects create zero value
        if ( p_first.equals( p_second ) )
            return 0;

        final Set<ILiteral<?>> l_firstLiterals = new HashSet<>();
        final Set<ILiteral<?>> l_secondLiterals = new HashSet<>();


        for ( final CPath l_path : m_paths )
        {
            l_firstLiterals.addAll( p_first.getBeliefs().getLiterals( l_path ) );
            l_secondLiterals.addAll( p_second.getBeliefs().getLiterals( l_path ) );
        }


        // get union
        final Set<ILiteral<?>> l_aggregate = new HashSet<ILiteral<?>>()
        {{
                addAll( l_firstLiterals );
                addAll( l_secondLiterals );
            }};

        // difference of contradiction is the sum of difference of contradictions on each belief-base (closed-world-assumption)
        return new Double( ( ( l_aggregate.size() - l_firstLiterals.size() ) + ( l_aggregate.size() - l_secondLiterals.size() ) ) );
    }

}
