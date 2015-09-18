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

package de.tu_clausthal.in.mec.object.mas.jason;

import de.tu_clausthal.in.mec.CLogger;
import de.tu_clausthal.in.mec.common.CReflection;
import de.tu_clausthal.in.mec.object.mas.IAgentTemplateFactory;
import jason.JasonException;
import jason.architecture.AgArch;
import jason.asSemantics.Agent;
import jason.asSemantics.GoalListenerForMetaEvents;
import jason.asSemantics.TransitionSystem;
import jason.bb.BeliefBase;

import java.io.File;
import java.util.ArrayList;


/**
 * Jason agent template factory
 */
public class CAgentTemplateFactory extends IAgentTemplateFactory<Agent, Object>
{
    /**
     * internal agent architecture which is used on template instantiation
     **/
    private final AgArch m_architecture = new AgArch();


    @Override
    @SuppressWarnings( "unchecked" )
    protected Agent clone( final Agent p_agent, final Object... p_any ) throws Exception
    {
        return new CAgent( p_agent, (AgArch) p_any[0], (BeliefBase) p_any[1] );
    }


    @Override
    protected final Agent create( final File p_source ) throws JasonException
    {
        // build the agent with Jason default behaviour
        return Agent.create( m_architecture, Agent.class.getCanonicalName(), null, p_source.toString(), null );
    }


    /**
     * template class to initialize the agent manually on cloning
     * process to handle Jason stdlib internal action includes
     *
     * @note we do the initialization process manually, because some internal
     * actions are removed from the default behaviour
     * @see http://jason.sourceforge.net/api/jason/asSemantics/TransitionSystem.html
     */
    public static class CAgent extends Agent
    {

        /**
         * ctor
         *
         * @note adaptet from Agent.clone
         * @param p_template template agent
         * @param p_architecture architecture
         */
        public CAgent( final Agent p_template, final AgArch p_architecture, final BeliefBase p_beliefbase )
        {
            // --- initialize the agent, that is used within the simulation context ---
            // Jason design bug, the setLogger method need an agent architecture see
            // http://sourceforge.net/p/jason/svn/1834/tree//trunk/src/jason/asSemantics/Agent.java#l357 and not the logger itself
            this.setLogger( p_architecture );
            this.setASLSrc( p_template.getASLSrc() );
            this.setTS( new TransitionSystem( this, null, null, p_architecture ) );
            this.setBB( p_beliefbase );
            this.setPL( p_template.getPL().clone() );
            this.initDefaultFunctions();

            if ( p_template.getPL().hasMetaEventPlans() )
                this.getTS().addGoalListener( new GoalListenerForMetaEvents( this.getTS() ) );

            // --- internal data structure are private, so set with reflection ---
            try
            {
                CReflection.getClassField( this.getClass(), "initialGoals" ).getSetter().invoke( this, new ArrayList<>() );
                CReflection.getClassField( this.getClass(), "initialBels" ).getSetter().invoke( this, new ArrayList<>() );

                // create internal actions map - reset the map and overwrite not useable actions with placeholder
                CReflection.getClassField( this.getClass(), "internalActions" ).getSetter().invoke( this, IEnvironment.INTERNALACTION );
            }
            catch ( final JasonException l_exception )
            {
                CLogger.error( l_exception );
            }
            catch ( final Throwable l_throwable )
            {
                CLogger.error( l_throwable );
            }

            // --- initialize ---
            this.initAg();
        }

    }
}