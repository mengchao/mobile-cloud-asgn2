/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.magnum.mobilecloud.video;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.magnum.mobilecloud.video.client.VideoSvcApi;
import org.magnum.mobilecloud.video.repository.Video;
import org.magnum.mobilecloud.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;

@Controller
public class VideoController {
	
	@Autowired
	VideoRepository videos;
	
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH, method=RequestMethod.GET)
	public @ResponseBody Collection<Video> getAll(){
		return Lists.newArrayList(videos.findAll());
	}
	
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH, method=RequestMethod.POST)
	public @ResponseBody Video save(@RequestBody Video v){
		return videos.save(v);
	}
	
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH + "/{id}", method=RequestMethod.GET)
	public @ResponseBody Video findById(@PathVariable("id") long id, HttpServletResponse response) throws IOException{
		Optional<Video> video =  videos.findById(id);
		
		if (video.isPresent()) {
			return video.get();
		}
		else {
			response.sendError(404);
			return null;
		}
	}
	
	@RequestMapping(value=VideoSvcApi.VIDEO_TITLE_SEARCH_PATH , method=RequestMethod.GET)
	public @ResponseBody Collection<Video> findByName(@RequestParam("title") String title){
		return videos.findByName(title);
	}
	
	@RequestMapping(value=VideoSvcApi.VIDEO_DURATION_SEARCH_PATH , method=RequestMethod.GET)
	public @ResponseBody Collection<Video> findByDurationLessThan(@RequestParam("duration") long duration){
		return videos.findByDurationLessThan(duration);
	}
	
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH + "/{id}/like", method=RequestMethod.POST)
	public void like(@PathVariable("id") long id, HttpServletResponse response, Principal p) throws IOException{
		String username = p.getName();
		
		Video v = findById(id, response);
		
		if (v == null) {
			return;
		}
		
		Set<String> likes = v.getLikedBy();
		
		if (likes.contains(username)) {
			response.sendError(400);
			return;
		}
		
		likes.add(username);
		v.setLikes(v.getLikes()+1);
		save(v);
	}
	
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH + "/{id}/unlike", method=RequestMethod.POST)
	public void unlike(@PathVariable("id") long id, HttpServletResponse response, Principal p) throws IOException{
		String username = p.getName();
		
		Video v = findById(id, response);
		
		if (v == null) {
			return;
		}
		
		Set<String> likes = v.getLikedBy();
		
		if (!likes.contains(username)) {
			response.sendError(400);
			return;
		}
		
		likes.remove(username);
		v.setLikes(v.getLikes()-1);
		save(v);
	}
	
}
