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
import java.lang.String;

public class YoutubeSong {
	
	public String songID,songName;
	
	public YoutubeSong(String songID, String songName){
		this.songID = songID;
		this.songName = songName;
	}

}
