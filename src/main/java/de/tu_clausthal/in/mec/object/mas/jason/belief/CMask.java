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

package de.tu_clausthal.in.mec.object.mas.jason.belief;


import de.tu_clausthal.in.mec.common.CPath;
import de.tu_clausthal.in.mec.object.mas.general.IBeliefBase;
import de.tu_clausthal.in.mec.object.mas.general.IBeliefBaseMask;
import de.tu_clausthal.in.mec.object.mas.general.ILiteral;
import jason.asSemantics.Agent;
import jason.asSemantics.Unifier;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import jason.asSyntax.PredicateIndicator;
import jason.bb.BeliefBase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Iterator;


/**
 * beliefbase mask that represent the Jason beliefbase
 */
public class CMask extends de.tu_clausthal.in.mec.object.mas.general.implementation.CMask<Literal> implements BeliefBase
{

    /**
     * ctor
     *
     * @param p_name name of the mask
     * @param p_beliefbase reference to the beliefbase context
     * @param p_separator path separator
     */
    public CMask( final String p_name, final IBeliefBase<Literal> p_beliefbase, final String p_separator )
    {
        super( p_name, p_beliefbase );
        m_pathseparator = p_separator;
    }

    /**
     * ctor
     *
     * @param p_name name of the mask
     * @param p_beliefbase reference to the beliefbase context
     * @param p_parent reference to the parent mask
     * @param p_separator path separator
     */
    public CMask( final String p_name, final IBeliefBase<Literal> p_beliefbase, final IBeliefBaseMask<Literal> p_parent, final String p_separator
    )
    {
        super( p_name, p_beliefbase, p_parent );
        m_pathseparator = p_separator;
    }

    @Override
    public void init( final Agent p_agent, final String[] p_args )
    {

    }

    @Override
    public void stop()
    {

    }

    @Override
    public boolean add( final Literal p_literal )
    {
        return this.add( 0, p_literal );
    }

    @Override
    public boolean add( final int p_index, final Literal p_literal )
    {
        CPath l_path = null;
        Literal l_literal = null;
        this.cloneLiteral( p_literal, l_path, l_literal );

        this.add( l_path.getSubPath( 0, l_path.size() - 1 ), new CLiteral( l_literal ) );
        return true;
    }

    @Override
    public Iterator<Literal> iterator()
    {
        return this.getLiteralIterator( this.iteratorLiteral() );
    }

    @Override
    public Iterator<Literal> getAll()
    {
        // deprecated
        return this.iterator();
    }

    @Override
    public Iterator<Literal> getCandidateBeliefs( final PredicateIndicator p_predicateIndicator )
    {
        return this.getLiteralIterator( this.getLiterals( this.splitPath( p_predicateIndicator.getFunctor() ) ).values().iterator() );
    }

    @Override
    public Iterator<Literal> getCandidateBeliefs( final Literal p_literal, final Unifier p_unifier )
    {
        if ( p_literal.isVar() )
            return this.iterator();

        return this.getLiteralIterator( this.getLiterals( this.splitPath( p_literal.getFunctor() ) ).values().iterator() );
    }

    @Override
    public Iterator<Literal> getRelevant( final Literal p_literal )
    {
        // deprecated
        return getCandidateBeliefs( p_literal, null );
    }

    @Override
    public Literal contains( final Literal p_literal )
    {
        final CPath l_path = this.splitPath( p_literal.getFunctor() );
        if ( this.containsLiteral( l_path ) )
            return this.getLiterals( l_path ).values().iterator().next().getLiteral();

        return null;
    }

    @Override
    public Iterator<Literal> getPercepts()
    {
        return this.getLiteralIterator( this.iteratorLiteral() );
    }

    @Override
    public boolean remove( final Literal p_literal )
    {
        CPath l_path = null;
        Literal l_literal = null;
        this.cloneLiteral( p_literal, l_path, l_literal );

        this.remove( l_path, new CLiteral( l_literal ) );
        return true;
    }

    @Override
    public boolean abolish( final PredicateIndicator p_predicateIndicator )
    {
        // remove masks and literals
        this.remove( this.splitPath( p_predicateIndicator.getFunctor() ) );
        return true;
    }

    @Override
    public Element getAsDOM( final Document p_document )
    {
        final Element l_beliefs = (Element) p_document.createElement( "beliefs" );
        for ( final Literal l_item : this )
            l_beliefs.appendChild( l_item.getAsDOM( p_document ) );

        return l_beliefs;
    }

    @Override
    public BeliefBase clone()
    {
        return this;
    }

    @Override
    public IBeliefBaseMask<Literal> clone( final IBeliefBaseMask<Literal> p_parent )
    {
        return new CMask( m_name, m_beliefbase, p_parent, m_pathseparator );
    }

    /**
     * get literal iterator
     *
     * @return iterator
     */
    private Iterator<Literal> getLiteralIterator( final Iterator<ILiteral<Literal>> p_iterator )
    {
        return new Iterator<Literal>()
        {
            @Override
            public boolean hasNext()
            {
                return p_iterator.hasNext();
            }

            @Override
            public Literal next()
            {
                // literal with path
                return p_iterator.next().getLiteral();
            }
        };
    }

    /**
     * splits the literal functor to a path
     *
     * @param p_functor literal
     * @return path
     */
    private CPath splitPath( final String p_functor )
    {
        return CPath.createPath( m_pathseparator, p_functor );
    }

    /**
     * clones a literal with path
     *
     * @param p_literal input literal
     * @param p_path returning literal path
     * @param p_output output / modified literal
     */
    private void cloneLiteral( final Literal p_literal, CPath p_path, Literal p_output )
    {
        p_path = this.splitPath( p_literal.getFunctor() );

        p_output = ASSyntax.createLiteral( !p_literal.negated(), p_path.getSuffix() );
        p_output.addAnnot( p_literal.getAnnots() );
        p_output.addTerms( p_literal.getTerms() );
        p_output.addSource( p_literal.getSources() );
    }

}