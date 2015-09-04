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

/**
 * This program will read your iTunes library 
 * @author Spikeles
 */

package com.ale46;
import java.io.File;
import java.io.FileInputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ItunesParser
{
	
	private HashSet<Song> allITunesSongs = new HashSet<Song>();

	

	private void go(final String path) throws Exception
	{
		calcArtists(path);

	}

	/** Helper function to make a NodeList Java generics friendly */
	private ArrayList<Node> asList(final NodeList list)
	{
		final ArrayList<Node> nodeList = new ArrayList<Node>();
		for (int i = 0; i < list.getLength(); i++)
		{
			nodeList.add(list.item(i));
		}
		return nodeList;
	}

	/** Determine the list of artists we are interested in from the iTunes Library */
	private void calcArtists(final String path) throws Exception
	{
		parse(path);
	}

	/** Download the file */
	


	/** Parse the itunes library */
	private void parse(final String file) throws Exception
	{
		final File libraryFile = new File(file);
		//final long length = libraryFile.length();
		//final byte[] data = new byte[(int) length];
		final FileInputStream fis = new FileInputStream(libraryFile);

		/** iTunes library is XML, do we can use the W3C Document classes in Java to parse it */
		final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = builderFactory.newDocumentBuilder();
		System.out.print("Begin parse..");
		final Document document = builder.parse(fis);
		System.out.println("done");

		System.out.println("Begin handle xml..");
		for (final Node node : asList(document.getChildNodes()))
		{
			if ("plist".equals(node.getNodeName()))
			{
				parsePList(node);
			}
		}
		System.out.println("done");
	}

	/** Parse the dictonary(hashmap) */
	private void parseDict(final Node dictNode)
	{
		final HashMap<String, String> dict = new HashMap<String, String>();
		String lastKey = null;

		for (final Node node : asList(dictNode.getChildNodes()))
		{
			if (node.getNodeType() == Node.TEXT_NODE)
			{
				continue;
			}

			if ("key".equals(node.getNodeName()) && lastKey == null)
			{
				lastKey = node.getTextContent();
				continue;
			} else if (lastKey != null)
			{
				if ("dict".equals(node.getNodeName()) && "Tracks".equals(lastKey))
				{
					parseTracks(node);
					lastKey = null;
					continue;
				}
				dict.put(lastKey, node.getTextContent());
				lastKey = null;
				continue;
			} else
			{
				System.out.println("AGH!");
				System.exit(1);
			}
		}
	}

	/** Parse the play list */
	private void parsePList(final Node pnode)
	{
		for (final Node node : asList(pnode.getChildNodes()))
		{
			if ("dict".equals(node.getNodeName()))
			{
				parseDict(node);
			}
		}
	}

	/** Parse the itunes track */
	private void parseTrack(final Node trackNode)
	{
		final HashMap<String, String> dict = new HashMap<String, String>();
		String lastKey = null;

		for (final Node node : asList(trackNode.getChildNodes()))
		{
			if (node.getNodeType() == Node.TEXT_NODE)
			{
				continue;
			}
			if ("key".equals(node.getNodeName()) && lastKey == null)
			{
				lastKey = node.getTextContent();
				continue;
			} else if (lastKey != null)
			{
				dict.put(lastKey, node.getTextContent());
				lastKey = null;
				continue;
			} else
			{
				System.out.println("AGH ParseTrack: LastKey was null! and node wasn't key:" + node.getNodeName());
				System.exit(1);
			}
		}

		if (dict.containsKey("Artist"))
		{
			
			//System.out.println(dict.get("Artist")+" Album-> "+dict.get("Album")+" Title-> "+dict.get("Name")+" Year ->" +dict.get("Year"));
			allITunesSongs.add(new Song(dict.get("Name"),dict.get("Artist"),dict.get("Album"), dict.get("Year")));
		}
	}

	/** Parse the itunes tracks */
	private void parseTracks(final Node tracksNode)
	{
		final HashMap<String, String> dict = new HashMap<String, String>();
		String lastKey = null;

		for (final Node node : asList(tracksNode.getChildNodes()))
		{
			if (node.getNodeType() == Node.TEXT_NODE)
			{
				continue;
			}
			if ("key".equals(node.getNodeName()) && lastKey == null)
			{
				lastKey = node.getTextContent();
				continue;
			} else if (lastKey != null)
			{
				if ("dict".equals(node.getNodeName()))
				{
					parseTrack(node);
					lastKey = null;
					continue;
				}
				dict.put(lastKey, node.getTextContent());
				lastKey = null;
				continue;
			} else
			{
				System.out.println("ARGH ParseTracks!: LastKey was null! and node wasn't key:" + node.getNodeName());
				System.exit(1);
			}
		}
	}
	
	public HashSet<Song> getSong(String library){
		try {
			go(library);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return allITunesSongs;
	}
}
