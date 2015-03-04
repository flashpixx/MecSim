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

package de.tu_clausthal.in.mec.object.source;

import de.tu_clausthal.in.mec.object.car.ICar;
import de.tu_clausthal.in.mec.object.source.generator.IGenerator;
import de.tu_clausthal.in.mec.simulation.IReturnSteppable;
import de.tu_clausthal.in.mec.ui.COSMViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.io.Serializable;


/**
 * factory interface of car source - defines a source
 */
public interface ISource extends IReturnSteppable<ICar>, Painter<COSMViewer>, Serializable
{

    /**
     * returns the position of the source
     *
     * @return geoposition of the source
     */
    public GeoPosition getPosition();

    /**
     * Method to get the actual Color of this Source
     * @return
     */
    public Color getColor();

    /**
     * Method to set the Color of this Source
     * @param p_color
     */
    public void setColor(Color p_color);

    /**
     * Method to get the Generator of an Source (null if there is no Generator)
     * @return
     */
    public IGenerator getGenerator();

    /**
     * Method to set a Generator in a Source
     */
    public void setGenerator(IGenerator p_generator);

    /**
     * Method to remove the Generator (Set m_generator to null)
     */
    public void removeGenerator();
}