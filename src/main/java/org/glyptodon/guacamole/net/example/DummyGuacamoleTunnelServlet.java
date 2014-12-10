package org.glyptodon.guacamole.net.example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.glyptodon.guacamole.GuacamoleException;
import org.glyptodon.guacamole.net.GuacamoleSocket;
import org.glyptodon.guacamole.net.GuacamoleTunnel;
import org.glyptodon.guacamole.net.InetGuacamoleSocket;
import org.glyptodon.guacamole.protocol.ConfiguredGuacamoleSocket;
import org.glyptodon.guacamole.protocol.GuacamoleConfiguration;
import org.glyptodon.guacamole.servlet.GuacamoleHTTPTunnelServlet;
import org.glyptodon.guacamole.servlet.GuacamoleSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

//import org.json.simple.JSONObject;

/*
 *  Guacamole - Clientless Remote Desktop
 *  Copyright (C) 2010  Michael Jumper
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

public class DummyGuacamoleTunnelServlet extends GuacamoleHTTPTunnelServlet {

    @Override
    protected GuacamoleTunnel doConnect(HttpServletRequest request) throws GuacamoleException {

        HttpSession httpSession = request.getSession(true);
        JSONObject jsonObject = null;
        // testing
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
            System.out.println("*** Body: "+jb.toString());
        } catch (Exception e) { 
            //System.out.println(e.printStackTrace()); 
            System.out.println("*** Error");
        }

        try {
            jsonObject = new JSONObject(jb.toString());
            System.out.println("****");
            System.out.println(jsonObject.getString("initial-program"));
            /*System.out.println(jsonObject.getString("remote-app"));
            System.out.println(jsonObject.getString("remote-app-dir"));
            System.out.println(jsonObject.getString("remote-app-args"));*/
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // try {
        //     JSONObject jsonObject = JSONObject.fromObject(jb.toString());
        // } catch (ParseException e) {
        //     // crash and burn
        //     throw new IOException("Error parsing JSON request string");
        // }

        // guacd connection information
        String hostname = "localhost";
        int port = 4822;

        // VNC connection information
        GuacamoleConfiguration config = new GuacamoleConfiguration();
        config.setProtocol("vnc");
        config.setParameter("hostname", "localhost");
        config.setParameter("port", "5901");
        config.setParameter("username", "root");
        config.setParameter("password", "welcome");
        // try {
        //     config.setParameter("initial-program", jsonObject.getString("initial-program"));
        //     config.setParameter("remote-app", jsonObject.getString("remote-app"));
        //     config.setParameter("remote-app-dir", jsonObject.getString("remote-app-dir"));
        //     config.setParameter("remote-app-args", jsonObject.getString("remote-app-args"));
        // } catch (JSONException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }

        // Connect to guacd, proxying a connection to the VNC server above
        GuacamoleSocket socket = new ConfiguredGuacamoleSocket(
                new InetGuacamoleSocket(hostname, port),
                config
        );

        // Create tunnel from now-configured socket
        GuacamoleTunnel tunnel = new GuacamoleTunnel(socket);

        // Attach tunnel
        GuacamoleSession session = new GuacamoleSession(httpSession);
        session.attachTunnel(tunnel);

        return tunnel;

    }

}
