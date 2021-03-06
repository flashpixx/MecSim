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

import com.graphhopper.util.EdgeIteratorState;
import de.tu_clausthal.in.mec.runtime.IVoidSteppable;
import de.tu_clausthal.in.mec.ui.COSMViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

import java.util.Map;


/**
 * car interface for defining the car structure
 */
public interface ICar extends Painter<COSMViewer>, IVoidSteppable
{


    /**
     * define an individual acceleration
     *
     * @return number (greater than zero) in m/sec^2
     */
    int getAcceleration();

    /**
     * returns the current speed of the car
     *
     * @return speed in km/h
     */
    int getCurrentSpeed();


    /**
     * sets the current speed of the care
     *
     * @param p_speed speed value in km/h
     */
    void setCurrentSpeed( int p_speed ) throws IllegalArgumentException;

    /**
     * define an individual deceleration
     *
     * @return number (greater than zero) in m/sec^2
     */
    int getDeceleration();

    /**
     * get current edge object on the graph
     *
     * @return edge
     */
    EdgeIteratorState getEdge();

    /**
     * returns the current geo position of the car
     *
     * @return geoposition
     */
    GeoPosition getGeoposition();

    /**
     * returns a probability for lingering
     *
     * @return double value in [0,1]
     */
    double getLingerProbability();

    /**
     * returns the maximum speed of the car
     *
     * @return speed value in km/h
     */
    int getMaximumSpeed();

    /**
     * returns the current predecessor of the car and the distance
     *
     * @return predecessor car object and its distance in m
     */
    Map<Double, ICar> getPredecessor();

    /**
     * returns the current predecessor of the car and the distance
     *
     * @param p_count number of predecessors
     * @return predecessor car object and its distance in meter
     */
    Map<Double, ICar> getPredecessor( int p_count );

    /**
     * boolean method, that returns true, if the car has reached its end
     *
     * @return boolean for reaching the end
     */
    boolean hasEndReached();

}
