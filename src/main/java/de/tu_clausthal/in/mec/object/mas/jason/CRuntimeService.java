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


import jason.JasonException;
import jason.architecture.AgArch;
import jason.asSemantics.Agent;
import jason.mas2j.ClassParameters;
import jason.runtime.RuntimeServicesInfraTier;
import jason.runtime.Settings;

import java.util.List;
import java.util.Set;


/**
 * Jason runtime service
 *
 * @see http://jason.sourceforge.net/api/jason/runtime/RuntimeServicesInfraTier.html
 */
public class CRuntimeService implements RuntimeServicesInfraTier
{


    @Override
    public String createAgent( String agName, String agSource, String agClass, List<String> archClasses, ClassParameters bbPars, Settings stts ) throws Exception
    {
        return null;
    }

    @Override
    public void startAgent( String s )
    {

    }

    @Override
    public AgArch clone( Agent agent, List<String> list, String s ) throws JasonException
    {


        return null;
    }

    @Override
    public boolean killAgent( String agName, String byAg )
    {

        return false;
    }

    @Override
    public Set<String> getAgentsNames()
    {
        return null;
    }

    @Override
    public int getAgentsQty()
    {
        return 0;
    }

    @Override
    public void stopMAS() throws Exception
    {
    }
}