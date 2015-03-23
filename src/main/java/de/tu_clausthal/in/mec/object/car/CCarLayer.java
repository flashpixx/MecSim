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

package de.tu_clausthal.in.mec.object.car;

import com.graphhopper.routing.util.Weighting;
import de.tu_clausthal.in.mec.object.IMultiLayer;
import de.tu_clausthal.in.mec.object.car.drivemodel.CNagelSchreckenberg;
import de.tu_clausthal.in.mec.object.car.drivemodel.IDriveModel;
import de.tu_clausthal.in.mec.object.car.graph.CGraphHopper;
import de.tu_clausthal.in.mec.simulation.CSimulation;
import de.tu_clausthal.in.mec.simulation.IReturnSteppableTarget;
import de.tu_clausthal.in.mec.simulation.ISerializable;
import de.tu_clausthal.in.mec.ui.COSMViewer;
import de.tu_clausthal.in.mec.ui.CSwingWrapper;
import org.jxmapviewer.painter.Painter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * defines the layer for cars
 */
public class CCarLayer extends IMultiLayer<ICar> implements IReturnSteppableTarget<ICar>, ISerializable
{
    /**
     * serialize version ID *
     */
    private static final long serialVersionUID = 1L;
    /**
     * driving model list
     */
    protected static IDriveModel[] s_drivemodellist = {new CNagelSchreckenberg()};
    /**
     * data structure - not serializable
     */
    protected final transient List<ICar> m_data = new LinkedList<>();
    /**
     * map of analyse call
     */
    protected final transient Map<String, Object> m_analyse = new HashMap<>();
    /**
     * driving model
     */
    protected IDriveModel m_drivemodel = s_drivemodellist[0];
    /**
     * graph
     */
    protected transient CGraphHopper m_graph = new CGraphHopper();


    /**
     * returns the graph of the layer
     *
     * @return graph object
     */
    public final CGraphHopper getGraph()
    {
        return m_graph;
    }

    /**
     * returns this list of all weights
     *
     * @return weight name
     */
    public final String[] getGraphWeight()
    {
        return m_graph.getWeightingList();
    }

    /**
     * checks if a weight is active
     *
     * @param p_weight weight name
     * @return bool active flag
     */
    public final boolean isActiveWeight( final String p_weight )
    {
        return m_graph.isActiveWeight( p_weight );
    }

    /**
     * enable / disable weight
     *
     * @param p_weight weight name
     */
    public final void enableDisableGraphWeight( final String p_weight )
    {
        if ( this.isActiveWeight( p_weight ) )
            m_graph.disableWeight( p_weight );
        else
            m_graph.enableWeight( p_weight );
    }


    /**
     * returns a graph weight
     *
     * @param p_weight weight name
     * @return weight object or null
     */
    public final Weighting getGraphWeight( final String p_weight )
    {
        return m_graph.getWeight( p_weight );
    }


    /**
     * sets the drive model
     *
     * @param p_model model
     */
    public final void setDriveModel( final String p_model )
    {
        for ( IDriveModel l_model : s_drivemodellist )
            if ( p_model.equals( l_model.getName() ) )
                m_drivemodel = l_model;
    }


    /**
     * returns the name of the driving model
     *
     * @return name
     */
    public final String getDrivingModel()
    {
        return m_drivemodel.getName();
    }

    /**
     * returns a list with all driving model names
     *
     * @return list
     */
    public final String[] getDrivingModelList()
    {
        int i = 0;
        final String[] l_list = new String[s_drivemodellist.length];
        for ( IDriveModel l_model : s_drivemodellist )
            l_list[i++] = l_model.getName();

        return l_list;
    }

    @Override
    public final int getCalculationIndex()
    {
        return 1;
    }

    @Override
    public final void beforeStepObject( final int p_currentstep, final ICar p_object )
    {
        m_drivemodel.update( p_currentstep, this.m_graph, p_object );
    }

    @Override
    public final void afterStepObject( final int p_currentstep, final ICar p_object )
    {
        // if a car has reached its end, remove it
        if ( p_object.hasEndReached() )
        {
            super.remove( p_object );
            p_object.release();
        }

        // repaint the OSM viewer (supress flickering)
        if ( CSimulation.getInstance().hasUI() )
            CSimulation.getInstance().getUI().<CSwingWrapper<COSMViewer>>getTab( "OSM" ).getComponent().repaint();
    }

    @Override
    public final Map<String, Object> analyse()
    {
        m_analyse.put( "car count", this.getGraph().getNumberOfObjects() );
        return m_analyse;
    }

    @Override
    public final void release()
    {
        super.clear();
        m_graph.clear();
    }

    @Override
    public final void onDeserializationInitialization()
    {
        if ( CSimulation.getInstance().hasUI() )
            CSimulation.getInstance().getUI().<CSwingWrapper<COSMViewer>>getTab( "OSM" ).getComponent().getCompoundPainter().removePainter( (Painter) this );
    }

    @Override
    public final void onDeserializationComplete()
    {
        if ( CSimulation.getInstance().hasUI() )
            CSimulation.getInstance().getUI().<CSwingWrapper<COSMViewer>>getTab( "OSM" ).getComponent().getCompoundPainter().addPainter( (Painter) this );
    }

    @Override
    public final void push( final Collection<ICar> p_data )
    {
        super.addAll( p_data );
    }


    /**
     * write call of serialize interface
     *
     * @param p_stream stream
     * @throws IOException throws the exception on loading data
     */
    private void writeObject( final ObjectOutputStream p_stream ) throws IOException
    {
        p_stream.defaultWriteObject();

        // write active graph weights
        p_stream.writeObject( m_graph.getActiveWeights() );
    }

    /**
     * read call of serialize interface
     *
     * @param p_stream stream
     * @throws IOException            throws exception on loading the data
     * @throws ClassNotFoundException throws exception on deserialization error
     */
    private void readObject( final ObjectInputStream p_stream ) throws IOException, ClassNotFoundException
    {
        p_stream.defaultReadObject();

        m_graph = new CGraphHopper();
        m_graph.disableWeight();
        for ( String l_weight : (String[]) p_stream.readObject() )
            m_graph.enableWeight( l_weight );
    }
}
