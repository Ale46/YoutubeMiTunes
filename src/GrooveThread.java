/*******************************************************************************
 * Copyright (c) 2011 Ale46.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 ******************************************************************************/
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Callable;

import jgroove.JGroovex;




public class GrooveThread implements Callable<HashMap<String, String>[]>{

	private String call,query;
	
	public GrooveThread(String call, String query){
		this.call = call;
		this.query = query;
	}
	
	
	public HashMap<String, String>[] top() throws Exception {
		return JGroovex.getPopularSongs(query).result.Songs;
		
	}

	public HashMap<String, String>[] search() throws Exception {
		
		return JGroovex.getSearchResults(query,"Songs").result.result;	
	}
	
	public HashMap<String,String>[] getPlaylist() throws IOException{
		
		return JGroovex.getPlaylistSongs(query, 0, false).result.Songs;
	}
	
	public HashMap<String,String>[] getAlbum() throws IOException{
	
		return JGroovex.getAlbumSongs(query, 0, false).result.songs;
	}

	@Override
	public HashMap<String, String>[] call() throws Exception {
		
		if (call.equals("search"))
			return search();
		if (call.equals("top"))
			return top();
		if (call.contains("playlist"))
			return (getPlaylist());
		if (call.contains("album"))
			return (getAlbum());
		return null;
	}
	
}
