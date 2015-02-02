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

package de.tu_clausthal.in.mec.object.mas.jason.action;


import jason.asSemantics.*;
import jason.asSyntax.Term;


/**
 * empty action to overwrite default behaviour
 */
public class CInternalEmpty extends DefaultInternalAction
{

    /**
     * default result value *
     */
    protected boolean m_result = true;
    /**
     * minimum number of arguments *
     */
    protected int m_minimumarguments = super.getMinArgs();
    /**
     * maximum number of arguments *
     */
    protected int m_maximumarguments = super.getMaxArgs();


    /**
     * default ctor
     */
    public CInternalEmpty()
    {
    }


    /**
     * ctor
     *
     * @param p_min    minimum of arguments
     * @param p_max    maximum of arguments
     * @param p_result result value
     */
    public CInternalEmpty( int p_min, int p_max, boolean p_result )
    {
        m_minimumarguments = Math.abs( p_min );
        m_maximumarguments = Math.abs( p_max );
        m_result = p_result;
    }


    /**
     * ctor
     *
     * @param p_min minimum of arguments
     * @param p_max maximum of arguments
     */
    public CInternalEmpty( int p_min, int p_max )
    {
        this( p_min, p_max, true );
    }


    /**
     * ctor
     *
     * @param p_result result value
     */
    public CInternalEmpty( boolean p_result )
    {
        this( 0, 0, p_result );
    }


    @Override
    public int getMinArgs()
    {
        return m_minimumarguments;
    }

    @Override
    public int getMaxArgs()
    {
        return m_maximumarguments;
    }

    @Override
    public Object execute( TransitionSystem p_ts, Unifier p_unifier, Term[] p_args ) throws Exception
    {
        return m_result;
    }

}