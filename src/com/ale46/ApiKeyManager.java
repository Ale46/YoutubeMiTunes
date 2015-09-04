/*
 * Copyright (c) 2015 Ale46.
 *
 * This file is part of YoutubeMiTunes.
 *
 *    YoutubeMiTunes is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     YoutubeMiTunes is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with YoutubeMiTunes.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ale46;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApiKeyManager {
    private static final String KEY_FILE = "keys.properties";
    private Properties prop;
    ApiKeyManager(){
        try {
            prop = new Properties();
            InputStream in = new FileInputStream(System.getProperty("user.home") + "/" + KEY_FILE);
            prop.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getClientID(){
        return prop.getProperty("CLIENT_ID");
    }

    public String getClientSecret(){
        return prop.getProperty("CLIENT_SECRET");
    }

    public String webKey(){
        return prop.getProperty("API_KEY");
    }

}
