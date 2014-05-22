/**
 ######################################################################################
 # GPL License                                                                        #
 #                                                                                    #
 # This file is part of the TUC Wirtschaftsinformatik - Fortgeschrittenenprojekt      #
 # Copyright (c) 2014, Philipp Kraus, <philipp.kraus@tu-clausthal.de>                 #
 # This program is free software: you can redistribute it and/or modify               #
 # it under the terms of the GNU General Public License as                            #
 # published by the Free Software Foundation, either version 3 of the                 #
 # License, or (at your option) any later version.                                    #
 #                                                                                    #
 # This program is distributed in the hope that it will be useful,                    #
 # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 # GNU General Public License for more details.                                       #
 #                                                                                    #
 # You should have received a copy of the GNU General Public License                  #
 # along with this program. If not, see <http://www.gnu.org/licenses/>.               #
 ######################################################################################
 **/

package de.tu_clausthal.in.winf.objects.norms;

import de.tu_clausthal.in.winf.mas.norm.IInstitution;
import de.tu_clausthal.in.winf.mas.norm.INorm;
import de.tu_clausthal.in.winf.mas.norm.INormCheckResult;
import de.tu_clausthal.in.winf.objects.ICar;


/**
 * norm for speed check
 */
public class CNormSpeed implements INorm<ICar> {

    /* maximum speed value for check **/
    private int m_maxspeed = Integer.MAX_VALUE;
    /**
     * dedicated institution *
     */
    private IInstitution<ICar> m_institution = null;

    /**
     * ctor
     *
     * @param p_institution defines the institution
     */
    public CNormSpeed(IInstitution<ICar> p_institution) {
        m_institution = p_institution;
    }


    /**
     * ctor
     *
     * @param p_institution defines institution
     * @param p_maxspeed    defines maximum allowed speed
     */
    public CNormSpeed(IInstitution<ICar> p_institution, int p_maxspeed) {
        m_institution = p_institution;
        m_maxspeed = p_maxspeed;
    }

    @Override
    public INormCheckResult check(ICar p_object) {
        return new CNormResultSpeed(p_object.getCurrentSpeed() <= m_maxspeed);
    }

    @Override
    public IInstitution<ICar> getInstitution() {
        return m_institution;
    }

    /**
     * inner class to represent the norm result
     */
    public class CNormResultSpeed implements INormCheckResult<Boolean> {

        /**
         * satisfiable boolean *
         */
        private boolean m_match = false;


        /**
         * ctor to create the value
         *
         * @param p_match value
         */
        public CNormResultSpeed(boolean p_match) {
            m_match = p_match;
        }

        @Override
        public double getWeight() {
            return 1;
        }

        @Override
        public Boolean getResult() {
            return false;
        }
    }
}
