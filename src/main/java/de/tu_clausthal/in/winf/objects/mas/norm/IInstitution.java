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

package de.tu_clausthal.in.winf.objects.mas.norm;


import java.io.Serializable;
import java.util.Collection;

/**
 * interface of an institution, an institution
 * is a collection of norms, collection of other institutions with additional aspects
 */
public interface IInstitution<T> extends Collection<INorm>, Serializable {


    /** checks an object
     *
     * @param p_object object
     */
    public void check( T p_object );


    /** returns the range of the workspace of the institution
     *
     * @return rangecollection
     */
    public IRangeCollection<T> getRange();


}
