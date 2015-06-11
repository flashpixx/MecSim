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


/**
 * ctor to create the source editor menu
 *
 * @param pc_id ID
 * @param pc_name name of the panel
 * @param pa_panel array with child elements
**/
function Editor( pc_id, pc_name, pa_panel )
{
    Pane.call(this, pc_id, pc_name, pa_panel );

    // array with all file objects in the structure { name: -filename-, config: -key of the configuration object- }
    this.mo_files = [];

    // set the configuration for all file access
    this.mo_configuration = {

        "jason" : {
            reader : "/cagentenvironment/jason/read",
            writer : "/cagentenvironment/jason/write",
            list   : "/cagentenvironment/jason/list"
        }

    }

    // read files on initialisation
    this.readFiles();
}

/** inheritance call **/
Editor.prototype = Object.create(Pane.prototype);


/**
 * @Overwrite
**/
Editor.prototype.getGlobalContent = function()
{
    return Pane.prototype.getGlobalContent.call(this);;
}


/**
 * @Overwrite
**/
Editor.prototype.getContent = function()
{
    return "<p>Editor</p>" + Pane.prototype.getContent.call(this);
}


/**
 * @Overwrite
**/
Editor.prototype.afterDOMAdded = function()
{
    Pane.prototype.afterDOMAdded.call(this);
}


/**
 * read all files from the REST-API
**/
Editor.prototype.readFiles = function()
{
    var self = this;

    jQuery.each(this.mo_configuration, function( pc_configkey, po_config ) {

        MecSim.ajax({

            url     : po_config.list,
            success : function( po_data ) {

                Array.prototype.push.apply( self.mo_files, po_data.agents.convert( function( pc_file ) { return { name : pc_file, config : pc_configkey }; } ) );

            }

        }).done(function() {

            console.log( self.mo_files );

        });

    });
}