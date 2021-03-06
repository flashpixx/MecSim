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

package de.tu_clausthal.in.mec.runtime.message;


import de.tu_clausthal.in.mec.common.CCommon;
import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.runtime.CSimulation;

import java.util.Set;


/**
 * participant class for event messager
 */
public final class CParticipant implements IParticipant
{

    /**
     * owner object *
     */
    private final IReceiver m_owner;

    /**
     * ctor to registerObject an object *
     *
     * @param p_owner owner of the message
     */
    public CParticipant( final IReceiver p_owner )
    {
        if ( p_owner == null )
            throw new IllegalArgumentException( CCommon.getResourceString( this, "ownernull" ) );

        m_owner = p_owner;
        CSimulation.getInstance().getMessageSystem().register( this.getReceiverPath(), this );
    }

    @Override
    public final CPath getReceiverPath()
    {
        return m_owner.getReceiverPath();
    }

    @Override
    public final void receiveMessage( final Set<IMessage> p_messages )
    {
        m_owner.receiveMessage( p_messages );
    }

    /**
     * release *
     */
    public final void release()
    {
        CSimulation.getInstance().getMessageSystem().unregister( m_owner.getReceiverPath(), this );
    }

    @Override
    public final void sendMessage( final CPath p_path, final IMessage p_message )
    {
        CSimulation.getInstance().getMessageSystem().pushMessage( p_path, p_message );
    }
}
