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

package de.tu_clausthal.in.mec.runtime.benchmark;

import de.tu_clausthal.in.mec.CConfiguration;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.apache.commons.lang3.ClassUtils;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Arrays;


/**
 * injection class to build benchmark structure within a class
 *
 * @see https://www.informit.com/guides/content.aspx?g=java&seqNum=589
 */
public final class CInjection implements ClassFileTransformer
{
    /**
     * class pool
     */
    private static final ClassPool c_pool = ClassPool.getDefault();
    /**
     * timer class
     */
    private final CtClass m_timerclass;

    /**
     * ctor
     *
     * @throws NotFoundException is thrown if timer class not found
     */
    public CInjection() throws NotFoundException
    {
        m_timerclass = c_pool.getCtClass( CTimer.class.getCanonicalName() );
    }


    @Override
    public byte[] transform( final ClassLoader p_loader, final String p_classname, final Class<?> p_redefine, final ProtectionDomain p_protecteddomain,
            final byte[] p_binary
    )
    {
        try
        {
            return this.inject( p_classname );
        }
        catch ( final NotFoundException | IOException | CannotCompileException p_exception )
        {
        }
        return p_binary;
    }


    /**
     * inject the class code
     *
     * @param p_classname class name
     * @return byte code
     *
     * @throws NotFoundException class loading error
     * @throws CannotCompileException compiling error
     * @throws IOException io exception
     * @bug class exception
     */
    private byte[] inject( final String p_classname ) throws NotFoundException, IOException, CannotCompileException
    {
        // filtering only package classes - other classes are ignored (throw an exception)
        final String l_classname = p_classname.replace( "/", ClassUtils.PACKAGE_SEPARATOR ).replace( "$", ClassUtils.INNER_CLASS_SEPARATOR );
        if ( !l_classname.startsWith( CConfiguration.getPackage() ) )
            throw new IllegalArgumentException();

        final CtClass l_class = c_pool.getCtClass( l_classname );
        l_class.stopPruning( false );

        Arrays.stream( l_class.getDeclaredMethods() )
              .filter( i -> {
                  try
                  {
                      return i.getAnnotation( IBenchmark.class ) != null;
                  }
                  catch ( final ClassNotFoundException p_exception )
                  {
                      return false;
                  }
              } )
              .forEach( i -> {
                  try
                  {
                      i.addLocalVariable( "l_bechmarktimer", m_timerclass );
                      i.insertBefore( "final l_bechmarktimer = new " + CTimer.class.getCanonicalName() + "().start();" );
                      i.insertAfter( "l_bechmarktimer.stop(\"" + i.getLongName().replace( CConfiguration.getPackage() + ".", "" ) + "\");" );
                  }
                  catch ( final CannotCompileException p_exception )
                  {
                  }
              } );

        l_class.stopPruning( true );
        return l_class.toBytecode();
    }
}
