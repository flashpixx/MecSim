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

package de.tu_clausthal.in.mec.object.source;

import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.object.car.CCarLayer;
import de.tu_clausthal.in.mec.object.car.ICar;
import de.tu_clausthal.in.mec.object.source.factory.ICarFactory;
import de.tu_clausthal.in.mec.object.source.factory.IFactory;
import de.tu_clausthal.in.mec.object.source.generator.IGenerator;
import de.tu_clausthal.in.mec.object.source.sourcetarget.CComplexTarget;
import de.tu_clausthal.in.mec.runtime.CSimulation;
import de.tu_clausthal.in.mec.runtime.IReturnSteppable;
import de.tu_clausthal.in.mec.runtime.IReturnSteppableTarget;
import de.tu_clausthal.in.mec.ui.COSMViewer;
import de.tu_clausthal.in.mec.ui.IInspector;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultWaypointRenderer;
import org.jxmapviewer.viewer.GeoPosition;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;


/**
 * abstract class for sources (handles common tasks)
 */
public abstract class ISource<T> extends IInspector implements IReturnSteppable<T>, Painter<COSMViewer>, Serializable
{

    /**
     * serialize version ID *
     */
    private static final long serialVersionUID = 1L;
    /**
     * image of the waypoint
     */
    private transient BufferedImage m_image;
    /**
     * waypoint color
     */
    private Color m_color = Color.BLACK;
    /**
     * last zoom (if the zoom changed the image need to be resized)
     */
    private int m_lastZoom = 0;
    /**
     * position of the source within the map
     */
    private GeoPosition m_position;
    /**
     * generator of this source
     */
    private IGenerator m_generator;
    /**
     * factory of this source
     */
    private IFactory<T> m_factory;
    /**
     * ComplexTarget of this source
     */
    private CComplexTarget m_complexTarget = new CComplexTarget();
    /**
     * map with targets
     */
    private transient Collection<IReturnSteppableTarget<T>> m_target = new HashSet()
    {{
            add( CSimulation.getInstance().getWorld().<CCarLayer>getTyped( "Cars" ) );
        }};


    @Override
    public final Collection<IReturnSteppableTarget<T>> getTargets()
    {
        return m_target;
    }

    @Override
    public final Map<String, Object> analyse()
    {
        return null;
    }

    @Override
    public final void onClick( final MouseEvent p_event, final JXMapViewer p_viewer )
    {
        if ( m_position == null )
            return;

        final Point2D l_point = p_viewer.getTileFactory().geoToPixel( m_position, p_viewer.getZoom() );
        final Ellipse2D l_circle = new Ellipse2D.Double(
                l_point.getX() - p_viewer.getViewportBounds().getX(), l_point.getY() - p_viewer.getViewportBounds().getY(), this.iconsize( p_viewer ),
                this.iconsize(
                        p_viewer
                )
        );

        if ( l_circle.contains( p_event.getX(), p_event.getY() ) )
            CSimulation.getInstance().getUIComponents().getInspector().set( this );
    }

    @Override
    public final void paint( final Graphics2D p_graphic, final COSMViewer p_viewer, final int p_width, final int p_height )
    {
        if ( m_image == null )
            return;

        //If the Zoom changed Calculate the new Image and Scale
        if ( p_viewer.getZoom() != m_lastZoom )
        {
            int l_newWidth = 20;
            int l_newHeight = 34;

            l_newHeight = (int) ( l_newHeight * this.iconscale( p_viewer ) );
            l_newWidth = (int) ( l_newWidth * this.iconscale( p_viewer ) );

            this.setImage( l_newWidth, l_newHeight );
        }

        final Point2D l_point = p_viewer.getTileFactory().geoToPixel( m_position, p_viewer.getZoom() );
        p_graphic.drawImage( m_image, (int) l_point.getX() - m_image.getWidth() / 2, (int) l_point.getY() - m_image.getHeight(), null );
    }

    /**
     * creates the image
     */
    public void setImage()
    {
        if ( m_color == null )
            return;

        try
        {
            final BufferedImage l_image = ImageIO.read( DefaultWaypointRenderer.class.getResource( "/images/standard_waypoint.png" ) );

            // modify blue value to the color of the waypoint
            m_image = new BufferedImage( l_image.getColorModel(), l_image.copyData( null ), l_image.isAlphaPremultiplied(), null );
            for ( int i = 0; i < l_image.getHeight(); i++ )
                for ( int j = 0; j < l_image.getWidth(); j++ )
                {
                    final Color l_color = new Color( l_image.getRGB( j, i ) );
                    if ( l_color.getBlue() > 0 )
                        m_image.setRGB( j, i, m_color.getRGB() );
                }

        }
        catch ( final Exception l_exception )
        {
            CLogger.warn( l_exception );
        }
    }

    /**
     * creates an image with a specific scale
     *
     * @param p_width image width
     * @param p_height image height
     */
    public void setImage( final int p_width, final int p_height )
    {
        if ( m_color == null )
            return;

        try
        {
            BufferedImage l_image = ImageIO.read( DefaultWaypointRenderer.class.getResource( "/images/standard_waypoint.png" ) );
            l_image = this.getScaledImage( l_image, p_width, p_height );

            // modify blue value to the color of the waypoint
            m_image = new BufferedImage( l_image.getColorModel(), l_image.copyData( null ), l_image.isAlphaPremultiplied(), null );
            for ( int i = 0; i < l_image.getHeight(); i++ )
                for ( int j = 0; j < l_image.getWidth(); j++ )
                {
                    final Color l_color = new Color( l_image.getRGB( j, i ) );
                    if ( l_color.getBlue() > 0 )
                        m_image.setRGB( j, i, m_color.getRGB() );
                }

        }
        catch ( final Exception l_exception )
        {
            CLogger.warn( l_exception );
        }
    }

    /**
     * method to scale a buffered image
     *
     * @param p_source Image which should be scaled
     * @param p_width new Width
     * @param p_height new Height
     * @return new Image
     */
    public BufferedImage getScaledImage( final BufferedImage p_source, final int p_width, final int p_height )
    {
        final BufferedImage l_newimage = new BufferedImage( p_width, p_height, BufferedImage.TRANSLUCENT );
        final Graphics2D l_graphics = l_newimage.createGraphics();
        l_graphics.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );
        l_graphics.drawImage( p_source, 0, 0, p_width, p_height, null );
        l_graphics.dispose();
        return l_newimage;
    }

    /**
     * read call of serialize interface
     *
     * @param p_stream stream
     * @throws IOException throws exception on loading the data
     * @throws ClassNotFoundException throws exception on deserialization error
     */
    private void readObject( final ObjectInputStream p_stream ) throws IOException, ClassNotFoundException
    {
        p_stream.defaultReadObject();

        m_position = new GeoPosition( p_stream.readDouble(), p_stream.readDouble() );
        this.setImage();
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

        p_stream.writeDouble( m_position.getLatitude() );
        p_stream.writeDouble( m_position.getLongitude() );
    }

    /**
     * returns the position of the source
     *
     * @return geoposition of the source
     */
    public final GeoPosition getPosition()
    {
        return this.m_position;
    }

    /**
     * set the position of a source
     *
     * @param p_position geoposition where the source should be placed
     */
    public final void setPosition( final GeoPosition p_position )
    {
        this.m_position = p_position;
    }

    /**
     * returns the color of the source
     *
     * @return color of the source
     */
    public Color getColor()
    {
        return this.m_color;
    }

    /**
     * set a color for this source
     *
     * @param p_color new Color
     */
    public void setColor( final Color p_color )
    {
        this.m_color = p_color;
    }

    /**
     * return the generator of the source
     *
     * @return generator object of this source
     * @deprecated
     */
    public IGenerator getGenerator(){
        return m_generator;
    }

    /**
     * set a new generator
     *
     * @param p_generator new generator
     */
    public void setGenerator(IGenerator p_generator){
        if(p_generator != null)
            this.m_generator = p_generator;
    }

    /**
     * return the factory of the source
     *
     * @return factory object of this source
     */
    public IFactory<T> getFactory(){
        return m_factory;
    }

    /**
     * set a new factory
     *
     * @param p_factory
     */
    public void setFactory(IFactory<T> p_factory){
        if(p_factory == null)
            return;

        this.m_factory = p_factory;
        this.setColor( m_factory.getColor() );
    }

    /**
     * return the ComplexTarget of the source
     *
     * @return ComplexTarget of the source
     * @deprecated
     */
    public CComplexTarget getComplexTarget(){
        return this.m_complexTarget;
    }

}
