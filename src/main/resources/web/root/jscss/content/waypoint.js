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


"use strict";

//@todo fix HTML5 data attributes


/**
 * ctor to create the waypoint menu
 *
 * @param pc_id ID
 * @param pc_name name of the panel
 * @param pa_panel array with child elements
**/
function Waypoint( pc_id, pc_name, pa_panel )
{
    Pane.call(this, pc_id, pc_name, pa_panel );
}

/** inheritance call **/
Waypoint.prototype = Object.create(Pane.prototype);



/**
 * @Overwrite
**/
Pane.prototype.getGlobalCSS = function()
{
}


/**
 * @Overwrite
**/
Waypoint.prototype.getContent = function()
{
    return '<button id = "' + this.generateSubID("newpreset") + '" >Create new preset</button >' +
           '<button id = "' + this.generateSubID("list") + '" >Show Waypoint List</button >' +
           Pane.prototype.getContent.call(this);

    // @todo add default preset list
}


/**
 * @Overwrite
**/
Waypoint.prototype.afterDOMAdded = function()
{
    Pane.prototype.afterDOMAdded.call(this);
    var self = this;

    ["newpreset", "list"].forEach( function(pc_item) {

        // @todo action bind with button().click( function() {} )
        jQuery( self.generateSubID( pc_item, "#" ) ).button();

    });

}