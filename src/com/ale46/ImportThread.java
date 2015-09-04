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
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.util.List;
import java.util.concurrent.Callable;

import com.google.api.services.youtube.model.SearchResult;




public class ImportThread implements Callable<List<SearchResult>> {

	private String call, query, apiKey;
	
	public ImportThread(String call, String query, String apiKey){
		this.call = call;
		this.query = query;
		this.apiKey = apiKey;
	}
	
	

	public List<SearchResult> search() throws Exception {
		YoutubeSearch yts = new YoutubeSearch(apiKey);
		return yts.search(query);

	}
	

	@Override
	public List<SearchResult> call() throws Exception {
		
		if (call.equals("search"))
			return search();
		return null;
	}
	
}
