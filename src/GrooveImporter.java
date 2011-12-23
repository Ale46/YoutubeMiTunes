/*******************************************************************************
 * Copyright (c) 2011 Ale46.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
import java.io.File;
import java.io.IOException;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


import jgroove.json.CountryUtil;


import com.google.gson.stream.MalformedJsonException;


public class GrooveImporter implements Callable<ArrayList<Object>>{

	
	private ArrayList<GrooveSong> toAdd = new ArrayList<GrooveSong>();
	private  ArrayList<Song> noMatch =  new ArrayList<Song>();
	private HashSet<Song> parsed;
	private ArrayList<String> local = new ArrayList<String> ();
	private String playlist;
	
	public GrooveImporter(String playlist){
		this.playlist = playlist;
	}
	private static int getLevenshteinDistance(String s, String t) {
		if (s == null || t == null) {
			throw new IllegalArgumentException("Strings must not be null");
		}

		int n = s.length(); // length of s
		int m = t.length(); // length of t

		if (n == 0) {
			return m;
		} else if (m == 0) {
			return n;
		}

		if (n > m) {
			// swap the input strings to consume less memory
			String tmp = s;
			s = t;
			t = tmp;
			n = m;
			m = t.length();
		}

		int p[] = new int[n+1]; //'previous' cost array, horizontally
		int d[] = new int[n+1]; // cost array, horizontally
		int _d[]; //placeholder to assist in swapping p and d

		// indexes into strings s and t
		int i; // iterates through s
		int j; // iterates through t

		char t_j; // jth character of t

		int cost; // cost

		for (i = 0; i<=n; i++) {
			p[i] = i;
		}

		for (j = 1; j<=m; j++) {
			t_j = t.charAt(j-1);
			d[0] = j;

			for (i=1; i<=n; i++) {
				cost = s.charAt(i-1)==t_j ? 0 : 1;
				// minimum of cell to the left+1, to the top+1, diagonally left and up +cost
				d[i] = Math.min(Math.min(d[i-1]+1, p[i]+1),  p[i-1]+cost);
			}

			// copy current distance counts to 'previous row' distance counts
			_d = p;
			p = d;
			d = _d;
		}

		// our last action in the above loop was to switch d and p, so p now 
		// actually has the most recent cost counts
		return p[n];
	}




	public void start() throws IOException,MalformedJsonException {
	
		ItunesParser it = new ItunesParser();
		//final String userDir = System.getenv("USERPROFILE");
		//String path = userDir + "\\Music\\iTunes\\test.xml";
		String path = playlist;
		if (!new File(path).exists()){
			GrooveMyTunes.textArea.append("Can't find " + path + "\n");
			GrooveMyTunes.textArea.setCaretPosition(GrooveMyTunes.textArea.getDocument().getLength());
			return;
		}
		
		GrooveMyTunes.textArea.append("Using iTunes Library:" + path+"\n");
		GrooveMyTunes.textArea.setCaretPosition(GrooveMyTunes.textArea.getDocument().getLength());
		parsed =  it.getSong(path);
		Iterator<Song> itt = parsed.iterator();
		CountryUtil.initCountryCode();
		GrooveMyTunes.textArea.append("Init , wait..\n");

		int count = 0;
		GrooveMyTunes.textArea.append("TOTAL parsed songs: "+parsed.size()+"\n");
		GrooveMyTunes.textArea.setCaretPosition(GrooveMyTunes.textArea.getDocument().getLength());
		while(itt.hasNext()) {
			//int per = (count+1)*100/parsed.size();
			GrooveMyTunes.textArea.append("Testing: "+(count+1) +" / " +parsed.size()+"\n");
			GrooveMyTunes.textArea.setCaretPosition(GrooveMyTunes.textArea.getDocument().getLength());
			//System.out.println("###"+per+"%");
			Song song =itt.next();
			String searchKey =  (song.getName()+" "+song.getArtist());
			searchKey = searchKey.replaceAll("[^\\p{ASCII}]", "");
			//System.out.println(searchKey);
			ExecutorService executor = Executors.newFixedThreadPool(1);
			Future<HashMap<String, String>[]> task = executor.submit(new GrooveThread("search",(searchKey)));


			HashMap<String, String>[] array = null;
			try {
				array = task.get();
				int betterDistance=100;
				String so = null;
				if (array==null){
					GrooveMyTunes.textArea.append("No Match for: "+searchKey+"\n");
					noMatch.add(song);
					continue;
				}
				int pos = 0;

				for (int i = 0;i<array.length;i++){
					//JSonsong ss = new Gson().fromJson(array.get(i).toString(), JSonsong.class);
					

					so = (array[i].get("SongName")+ " " + array[i].get("ArtistName"));// + " "+ ss.AlbumName);
					int distance = getLevenshteinDistance(searchKey, so.toLowerCase());
					//System.out.println("Distance between: " + searchKey.toLowerCase() + " and "+ so+" is:"+distance);
					if (distance == 0){
						GrooveMyTunes.textArea.append("100% match for:"+searchKey + " is "+ so+"\n");
						GrooveMyTunes.textArea.setCaretPosition(GrooveMyTunes.textArea.getDocument().getLength());
					//	System.out.println(array[count].get("SongName") + " " + array[count].get("ArtistName") +" "+array[count].get("AlbumName"));
						betterDistance = 0;
						
						toAdd.add(i,new GrooveSong(array[i].get("SongID"),array[i].get("SongName"),array[i].get("ArtistID"),array[i].get("ArtistName"),array[i].get("AlbumID"),array[i].get("AlbumName"),array[i].get("CoverArtFilename"),array[i].get("TrackNum")));
						local.add(i,so);
					//	JGroovex.userAddSongsToLibrary(array[i].get("SongID"),array[i].get("SongName"),array[i].get("ArtistID"),array[i].get("ArtistName"),array[i].get("AlbumID"),array[i].get("AlbumName"),array[i].get("CoverArtFilename"),array[i].get("TrackNum"));
						break;
					}else if (distance<betterDistance){
						betterDistance = distance;
						pos = i;
					}
				}
				if (betterDistance!=100 && betterDistance!=0){
					GrooveMyTunes.textArea.append(("Best match for:" +searchKey+ " is "+ so +"\n"));
					GrooveMyTunes.textArea.setCaretPosition(GrooveMyTunes.textArea.getDocument().getLength());
					toAdd.add(toAdd.size(),new GrooveSong(array[pos].get("SongID"),array[pos].get("SongName"),array[pos].get("ArtistID"),array[pos].get("ArtistName"),array[pos].get("AlbumID"),array[pos].get("AlbumName"),array[pos].get("CoverArtFilename"),array[pos].get("TrackNum")));
					local.add(so);
				//	System.out.println(array[count].get("SongName")+ " " + array[count].get("ArtistName") +" "+array[count].get("AlbumName"));
					//JGroovex.userAddSongsToLibrary(array[pos].get("SongID"),array[pos].get("SongName"),array[pos].get("ArtistID"),array[pos].get("ArtistName"),array[pos].get("AlbumID"),array[pos].get("AlbumName"),array[pos].get("CoverArtFilename"),array[pos].get("TrackNum"));
				}
				else if (betterDistance == 100){
					GrooveMyTunes.textArea.append(("No match for: " +searchKey+"\n"));
					GrooveMyTunes.textArea.setCaretPosition(GrooveMyTunes.textArea.getDocument().getLength());
				}
		
			} catch (InterruptedException e) {

				e.printStackTrace();
			} catch (ExecutionException e) {
				/*if (toAdd.size()>0)
					for (int k = 0;k<toAdd.size();k++){		
						JGroovex.userAddSongsToLibrary(toAdd.get(k).songID, toAdd.get(k).songName, toAdd.get(k).albumID, toAdd.get(k).albumName, toAdd.get(k).artistID, toAdd.get(k).artistName, toAdd.get(k).artFileName, toAdd.get(k).trackNum);
					}*/
				System.out.println("Banned..");
				//System.exit(-1);

			}
			count++;
			
			
		}
	}




	@Override
	public ArrayList<Object> call() throws Exception {
		ArrayList<Object> ret = new ArrayList<Object>();
		start();
		GrooveMyTunes.textArea.append(("No match for: " +noMatch.size()+"\n"));
		GrooveMyTunes.textArea.append(("Match for: " +toAdd.size()+"\n"));
		GrooveMyTunes.textArea.setCaretPosition(GrooveMyTunes.textArea.getDocument().getLength());
		ret.add(toAdd);
		ret.add(local);
		return ret;
	}
	
	public HashSet<Song> getParsed(){

		return parsed;
	}

}
