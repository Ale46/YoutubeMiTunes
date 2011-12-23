/*******************************************************************************
 * Copyright (c) 2011 Ale46.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
public class GrooveSong {
	
	public String songID,songName,artistID,artistName,albumID,albumName,artFileName,trackNum;
	
	public GrooveSong(String songID, String songName, String artistID, String artistName,  String albumID,  String albumName, String artFileName, String trackNum){
		this.songID = songID;
		this.songName = songName;
		this.albumID = albumID;
		this.albumName = albumName;
		this.artistID = artistID;
		this.artistName = artistName;
		this.artFileName  = artFileName;
		this.trackNum = trackNum;
	}

}
