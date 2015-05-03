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

import de.tu_clausthal.in.mec.object.car.CCarLayer;
import de.tu_clausthal.in.mec.object.car.graph.CGraphHopper;
import de.tu_clausthal.in.mec.object.car.graph.weights.CForbiddenEdges;
import de.tu_clausthal.in.mec.object.waypoint.CCarWayPointLayer;
import de.tu_clausthal.in.mec.runtime.CSimulation;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.input.PanMouseInputListener;

import javax.swing.*;
import java.awt.event.MouseEvent;


/**
 * mouse listener for JxViewer
 */
class COSMMouseListener extends PanMouseInputListener
{

    /**
     * ctor
     *
     * @param p_viewer OSM viewer reference
     */
    public COSMMouseListener( final JXMapViewer p_viewer )
    {
        super( p_viewer );
    }

    /**
     * @bug incomplete - error messages
     */
    @Override
    public void mouseClicked( final MouseEvent p_event )
    {
        if ( ( !SwingUtilities.isLeftMouseButton( p_event ) ) || ( p_event.getClickCount() != 2 ) )
            return;


        final COSMViewer l_viewer = (COSMViewer) p_event.getSource();

        switch ( l_viewer.getCurrentClickableLayer() )
        {
            case Sources:
                CSimulation.getInstance().getWorld().<CCarWayPointLayer>getTyped( "Car WayPoints" ).add(
                        CWaypointEnvironment.getInstance().m_selectedTool.getWaypoint(
                                l_viewer.getViewpointGeoPosition(
                                        p_event.getPoint()
                                )
                        )
                );
                break;


            case ForbiddenEdges:

                // read graph & weight data on-fly, because on loading simulation data the graph instance can be changed
                CSimulation.getInstance().getWorld().<CCarLayer>getTyped( "Cars" ).getGraph().<CForbiddenEdges>getWeight(
                        CGraphHopper.EWeight.ForbiddenEdges
                ).swap();

                l_viewer.repaint();
                break;
        }

    }


    @Override
    public void mouseMoved( final MouseEvent p_event )
    {
        final COSMViewer l_viewer = (COSMViewer) p_event.getSource();
        if ( !l_viewer.getCurrentClickableLayer().equals( COSMViewer.EClickableLayer.ForbiddenEdges ) )
            return;

        // read graph & weight data on-fly, because on loading simulation data the graph instance can be changed
        final CGraphHopper l_graph = CSimulation.getInstance().getWorld().<CCarLayer>getTyped( "Cars" ).getGraph();
        final CForbiddenEdges l_weight = l_graph.<CForbiddenEdges>getWeight( CGraphHopper.EWeight.ForbiddenEdges );

        l_weight.reserve(
                l_graph.getClosestEdge(
                        l_viewer.getViewpointGeoPosition(
                                p_event.getPoint()
                        )
                )
        );

        l_viewer.repaint();
    }

}
