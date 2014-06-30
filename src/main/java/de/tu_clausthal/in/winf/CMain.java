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

package de.tu_clausthal.in.winf;

import de.tu_clausthal.in.winf.ui.CFrame;
import org.apache.commons.cli.*;

import javax.swing.*;
import java.io.File;


/**
 * main class of the application
 *
 * @note Main must be started with option "-Xmx2g", because we need memory to create graph structure
 */
public class CMain {

    /**
     * main program
     *
     * @param p_args commandline arguments
     */
    public static void main(String[] p_args) throws Exception {

        // --- define CLI options --------------------------------------------------------------------------------------
        Options l_clioptions = new Options();
        l_clioptions.addOption("help", false, "shows this help");
        l_clioptions.addOption("configuration", true, "configuration directory");

        CommandLineParser l_parser = new BasicParser();
        CommandLine l_cli = l_parser.parse(l_clioptions, p_args);

        // --- process CLI arguments -----------------------------------------------------------------------------------
        if (l_cli.hasOption("help")) {
            HelpFormatter l_formatter = new HelpFormatter();
            l_formatter.printHelp((new java.io.File(CMain.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName()), l_clioptions);
            System.exit(0);
        }

        // read the configuration directory (default ~/.tucwinf)
        File l_config = new File(System.getProperty("user.home") + File.separator + ".tucwinf");
        if (l_cli.hasOption("configuration"))
            l_config = new File(l_cli.getOptionValue("configuration"));


        // --- create configuration ------------------------------------------------------------------------------------
        CConfiguration.getInstance().setConfigDir(l_config);
        CConfiguration.getInstance().read();
        CBootstrap.ConfigIsLoaded(CConfiguration.getInstance());


        // --- invoke UI -----------------------------------------------------------------------------------------------
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                CFrame l_frame = new CFrame();
                l_frame.setTitle("TU-Clausthal MEC - Traffic Simulation");
                l_frame.setVisible(true);
            }
        });
    }

}
