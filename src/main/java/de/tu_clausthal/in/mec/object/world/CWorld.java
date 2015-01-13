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

package de.tu_clausthal.in.mec.object.world;

import de.tu_clausthal.in.mec.CBootstrap;
import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.object.ILayer;

import java.io.Serializable;
import java.util.*;


/**
 * world layer collection
 */
public class CWorld implements Map<String, ILayer>, Serializable
{

    /**
     * map with layer
     */
    protected Map<String, ILayer> m_layer = new HashMap();

    /**
     * ctor
     */
    public CWorld()
    {
        CBootstrap.AfterWorldInit( this );
    }

    @Override
    public int size()
    {
        return m_layer.size();
    }

    @Override
    public boolean isEmpty()
    {
        return m_layer.isEmpty();
    }

    @Override
    public boolean containsKey( Object key )
    {
        return m_layer.containsKey( key );
    }

    @Override
    public boolean containsValue( Object value )
    {
        return m_layer.containsValue( value );
    }

    @Override
    public ILayer get( Object key )
    {
        ILayer l_layer = m_layer.get( key );
        CLogger.warn( "layer [" + l_layer.toString() + "] not exists", l_layer == null );
        return l_layer;
    }

    @Override
    public ILayer put( String key, ILayer value )
    {
        return m_layer.put( key, value );
    }

    @Override
    public ILayer remove( Object key )
    {
        return m_layer.remove( key );
    }

    @Override
    public void putAll( Map<? extends String, ? extends ILayer> m )
    {
        m_layer.putAll( m );
    }

    @Override
    public void clear()
    {
        m_layer.clear();
    }

    @Override
    public Set<String> keySet()
    {
        return m_layer.keySet();
    }

    @Override
    public Collection<ILayer> values()
    {
        return m_layer.values();
    }

    @Override
    public Set<Entry<String, ILayer>> entrySet()
    {
        return m_layer.entrySet();
    }
}