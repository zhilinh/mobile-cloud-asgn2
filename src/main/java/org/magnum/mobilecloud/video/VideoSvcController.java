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

import com.google.common.collect.Lists;
import org.magnum.mobilecloud.video.client.VideoSvcApi;
import org.magnum.mobilecloud.video.repository.Video;
import org.magnum.mobilecloud.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Collection;
import java.util.Set;

@Controller
public class VideoSvcController {

    /**
     * You will need to create one or more Spring controllers to fulfill the
     * requirements of the assignment. If you use this file, please rename it
     * to something other than "AnEmptyController"
     * <p>
     * <p>
     * ________  ________  ________  ________          ___       ___  ___  ________  ___  __
     * |\   ____\|\   __  \|\   __  \|\   ___ \        |\  \     |\  \|\  \|\   ____\|\  \|\  \
     * \ \  \___|\ \  \|\  \ \  \|\  \ \  \_|\ \       \ \  \    \ \  \\\  \ \  \___|\ \  \/  /|_
     * \ \  \  __\ \  \\\  \ \  \\\  \ \  \ \\ \       \ \  \    \ \  \\\  \ \  \    \ \   ___  \
     * \ \  \|\  \ \  \\\  \ \  \\\  \ \  \_\\ \       \ \  \____\ \  \\\  \ \  \____\ \  \\ \  \
     * \ \_______\ \_______\ \_______\ \_______\       \ \_______\ \_______\ \_______\ \__\\ \__\
     * \|_______|\|_______|\|_______|\|_______|        \|_______|\|_______|\|_______|\|__| \|__|
     */

    @Autowired
    VideoRepository videoRepository;

    @RequestMapping(value = "/go", method = RequestMethod.GET)
    public @ResponseBody
    String goodLuck() {
        return "Good Luck!";
    }

    @GetMapping(VideoSvcApi.VIDEO_SVC_PATH)
    public @ResponseBody
    Collection<Video> getVideoList() {
        return Lists.newArrayList(videoRepository.findAll());
    }

    @PostMapping(VideoSvcApi.VIDEO_SVC_PATH)
    public @ResponseBody
    Video addVideo(@RequestBody Video video) {
        return videoRepository.save(video);
    }

    @GetMapping(VideoSvcApi.VIDEO_SVC_PATH + "/{id}")
    public @ResponseBody
    Video getVideo(@PathVariable Long id, HttpServletResponse response) {
        Video video = videoRepository.findVideoById(id);
        if (video == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return video;
    }

    @PostMapping(VideoSvcApi.VIDEO_SVC_PATH + "/{id}/like")
    @Transactional
    public void like(@PathVariable Long id, Principal principal, HttpServletResponse response) {
        Video video = videoRepository.findVideoById(id);
        if (video == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Set<String> likedBy = video.getLikedBy();
        if (!likedBy.contains(principal.getName())) {
            likedBy.add(principal.getName());
            video.setLikes(video.getLikes() + 1);
            videoRepository.save(video);
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @PostMapping(VideoSvcApi.VIDEO_SVC_PATH + "/{id}/unlike")
    @Transactional
    public void unlike(@PathVariable Long id, Principal principal, HttpServletResponse response) {
        Video video = videoRepository.findVideoById(id);
        if (video == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Set<String> likedBy = video.getLikedBy();
        if (likedBy.contains(principal.getName())) {
            likedBy.remove(principal.getName());
            video.setLikes(video.getLikes() - 1);
            videoRepository.save(video);
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @GetMapping(VideoSvcApi.VIDEO_TITLE_SEARCH_PATH)
    public @ResponseBody
    Collection<Video> findByTitle(@RequestParam String title) {
        return videoRepository.findVideosByName(title);
    }

    @GetMapping(VideoSvcApi.VIDEO_DURATION_SEARCH_PATH)
    public @ResponseBody
    Collection<Video> findByDurationLessThan(Long duration) {
        return videoRepository.findVideosByDurationLessThan(duration);
    }
}
