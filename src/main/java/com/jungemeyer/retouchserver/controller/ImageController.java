package com.jungemeyer.retouchserver.controller;

import com.jungemeyer.retouchserver.model.Image;
import com.jungemeyer.retouchserver.model.Match;
import com.jungemeyer.retouchserver.repository.ImageRepository;
import com.jungemeyer.retouchserver.repository.MatchRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class ImageController {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    private boolean alternate = true;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    MatchRepository matchRepository;

    @CrossOrigin
    @RequestMapping(value = "/announce_winner", method = RequestMethod.POST)
    public synchronized void announceWinner(@RequestBody Map<String, String> json) {
        System.out.println("New winner");

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        Image winner = imageRepository.findById(json.get("winner")).get();
        Image looser = imageRepository.findById(json.get("looser")).get();
        winner.winAgainst(looser);
        imageRepository.save(winner);
        imageRepository.save(looser);

        Match match = new Match(winner, looser);
        matchRepository.save(match);

    }


    @GetMapping(
            value = "/image",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public @ResponseBody
    byte[] getImageWithMediaType(@RequestParam String id) throws IOException {
        InputStream in = getClass()
                .getResourceAsStream("../../../../faces/female/" + id + ".jpg");
        return IOUtils.toByteArray(in);
    }


    @CrossOrigin
    @RequestMapping(value = "/random_pair", method = RequestMethod.GET)
    public Image[] random_pair() {
        Image image1;
        Image image2;
        if (false) {
            image1 = imageRepository.findFirstByOrderByLastUpdatedAsc();
            image2 = imageRepository.findFirstByOrderByRandom();
        } else {
            List<Image> all = imageRepository.findAll();

            int rand = getRandomNumberInRange(0, all.size() - 1);

            image1 = all.get(rand);
            image2 = all.get(rand + 1);
        }

        alternate = !alternate;

        if (image1.getId().equals(image2.getId())) {
            image2 = imageRepository.findFirstByOrderByEloAsc();
        }

        Image[] array = {image1, image2};

        return array;

    }

    @RequestMapping(value = "/fill")
    public void fill() {
        File f = new File("/home/ubuntu/RetouchServer/src/main/resources/faces/female");

        var pathnames = f.list();

        for (String pathname : pathnames) {
            String file = pathname.substring(0, pathname.length() - 4);

            Image image = new Image(file, getRandomNumberInRange(0, 5000));
            imageRepository.save(image);
        }
    }


    public static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}
