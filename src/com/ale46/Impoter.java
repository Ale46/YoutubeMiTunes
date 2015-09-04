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
import java.io.File;
import java.io.IOException;
import java.lang.Exception;
import java.lang.IllegalArgumentException;
import java.lang.Math;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.System;
import java.util.*;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchResult;
import com.google.gson.stream.MalformedJsonException;


public class Impoter implements Callable<ArrayList<Object>>{

	
	private ArrayList<YoutubeSong> toAdd = new ArrayList<YoutubeSong>();
	private  ArrayList<Song> noMatch =  new ArrayList<Song>();
	private HashSet<Song> parsed;
	private ArrayList<String> local = new ArrayList<String> ();
	private String playlist;
	
	public Impoter(String playlist){
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
			YoutubeMiTunes.textArea.append("Can't find " + path + "\n");
			YoutubeMiTunes.textArea.setCaretPosition(YoutubeMiTunes.textArea.getDocument().getLength());
			return;
		}
		
		YoutubeMiTunes.textArea.append("Using iTunes Library:" + path+"\n");
		YoutubeMiTunes.textArea.setCaretPosition(YoutubeMiTunes.textArea.getDocument().getLength());
		parsed =  it.getSong(path);
		Iterator<Song> itt = parsed.iterator();
		YoutubeMiTunes.textArea.append("Init , wait..\n");

		int count = 0;
		YoutubeMiTunes.textArea.append("TOTAL parsed songs: "+parsed.size()+"\n");
		YoutubeMiTunes.textArea.setCaretPosition(YoutubeMiTunes.textArea.getDocument().getLength());
		ApiKeyManager key = new ApiKeyManager();
		while(itt.hasNext()) {
			//int per = (count+1)*100/parsed.size();
			YoutubeMiTunes.textArea.append("Testing: "+(count+1) +" / " +parsed.size()+"\n");
			YoutubeMiTunes.textArea.setCaretPosition(YoutubeMiTunes.textArea.getDocument().getLength());
			//System.out.println("###"+per+"%");
			Song song =itt.next();
			YoutubeMiTunes.textArea.append("Testing: "+song.getName()+" "+song.getArtist()+"\n");
			String searchKey =  (song.getName()+" "+song.getArtist());
			searchKey = searchKey.replaceAll("[^\\p{ASCII}]", "");
			ExecutorService executor = Executors.newFixedThreadPool(1);

            Future<List<SearchResult>> task = executor.submit(new ImportThread("search", searchKey, key.webKey()));

            Iterator<SearchResult> iteratorSearchResults = null;

			try {
                iteratorSearchResults = task.get().iterator();
				int betterDistance=100;
				String so = null;
				if (!iteratorSearchResults.hasNext()){
					YoutubeMiTunes.textArea.append("No Match for: "+searchKey+"\n");
					noMatch.add(song);
					continue;
				}
                SearchResult bestMatch= null;
                while (iteratorSearchResults.hasNext()) {

                    SearchResult singleVideo = iteratorSearchResults.next();
                    ResourceId rId = singleVideo.getId();

                    // Confirm that the result represents a video. Otherwise, the
                    // item will not contain a video ID.
                    if (rId.getKind().equals("youtube#video")) {

                        so = singleVideo.getSnippet().getTitle();
                        int distance = getLevenshteinDistance(searchKey.toLowerCase(), so.toLowerCase());
                        //System.out.println("Distance between: " + searchKey.toLowerCase() + " and "+ so+" is:"+distance);
                        if (distance == 0){
                            YoutubeMiTunes.textArea.append("100% match for:"+searchKey + " and "+ so+"\n");
                            YoutubeMiTunes.textArea.setCaretPosition(YoutubeMiTunes.textArea.getDocument().getLength());
                            betterDistance = 0;
                            toAdd.add(new YoutubeSong(rId.getVideoId(),singleVideo.getSnippet().getTitle()));
                            local.add(song.getName()+" - "+song.getArtist());
                            break;
                        }else if (distance<betterDistance){
                            betterDistance = distance;
                            bestMatch = singleVideo;
                        }
                    }

                }
                SearchResult singleVideo = bestMatch;
                ResourceId rId = singleVideo.getId();
                if (betterDistance!=100 && betterDistance!=0 && singleVideo != null){
                    YoutubeMiTunes.textArea.append(("Best match for:" +searchKey+ " is "+ singleVideo.getSnippet().getTitle() +"\n"));
                    YoutubeMiTunes.textArea.setCaretPosition(YoutubeMiTunes.textArea.getDocument().getLength());
                    toAdd.add(new YoutubeSong(rId.getVideoId(), singleVideo.getSnippet().getTitle()));
                    local.add(song.getName()+" - "+song.getArtist());
                }
                else if (betterDistance == 100 || singleVideo == null){
                    YoutubeMiTunes.textArea.append(("No match for: " +searchKey+"\n"));
                    YoutubeMiTunes.textArea.setCaretPosition(YoutubeMiTunes.textArea.getDocument().getLength());
                }

			} catch (java.lang.InterruptedException e) {

				e.printStackTrace();
			} catch (ExecutionException e) {
				System.out.println(e);
			}
			count++;
			
			
		}
	}




	@Override
	public ArrayList<Object> call() throws Exception {
		ArrayList<Object> ret = new ArrayList<Object>();
		start();
		YoutubeMiTunes.textArea.append(("No match for: " +noMatch.size()+"\n"));
		YoutubeMiTunes.textArea.append(("Match for: " +toAdd.size()+"\n"));
		YoutubeMiTunes.textArea.setCaretPosition(YoutubeMiTunes.textArea.getDocument().getLength());
		ret.add(0, toAdd);
		ret.add(1, local);
		return ret;
	}
	
	public HashSet<Song> getParsed(){

		return parsed;
	}

}
