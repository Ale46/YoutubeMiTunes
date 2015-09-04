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

import com.google.api.client.googleapis.json.GoogleJsonResponseException;

import com.google.api.client.http.HttpResponseException;
import com.google.api.services.youtube.YouTube;


import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;

import java.util.List;


class YoutubeSearch {

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private YouTube youtube;
    private String apiKey;

    YoutubeSearch(String apikey){
        this.apiKey = apikey;
    }

    public List<SearchResult> search(String keyword) throws HttpResponseException {

        try {
            // This object is used to make YouTube Data API requests. The last
            // argument is required, but since we don't need anything
            // initialized when the HttpRequest is initialized, we override
            // the interface and provide a no-op function.

            youtube = new YouTube.Builder(YoutubeAuth.HTTP_TRANSPORT, YoutubeAuth.JSON_FACTORY, request -> {
            }).setApplicationName("youtube-cmdline-search-sample").build();

            // Prompt the user to enter a query term.
            //String queryTerm = getInputQuery();

            // Define the API request for retrieving search results.
            YouTube.Search.List search = youtube.search().list("id,snippet");

            // Set your developer key from the Google Developers Console for
            // non-authenticated requests. See:
            // https://console.developers.google.com/
            
            search.setKey(apiKey);

            search.setQ(keyword);

            // Restrict the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            search.setType("video");

            // To increase efficiency, only retrieve the fields that the
            // application uses.
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet)");

            long NUMBER_OF_VIDEOS_RETURNED = 25;
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

            // Call the API and print results.
            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();
            if (searchResultList != null) {
                //prettyPrint(searchResultList.iterator(), queryTerm);
                //prettyPrint(searchResultList.iterator());
                return searchResultList;
            }
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

}