package com.jungemeyer.retouchserver.controller;

import com.jungemeyer.retouchserver.model.Image;
import com.jungemeyer.retouchserver.model.Match;
import com.jungemeyer.retouchserver.repository.ImageRepository;
import com.jungemeyer.retouchserver.repository.MatchRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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

        String path = "female/";

        if (imageRepository.findById(id).get().getGender().equals("m")) {
            path = "male/";
        }

        InputStream in = getClass()
                .getResourceAsStream("../../../../faces/" + path + id + ".png");
        return IOUtils.toByteArray(in);
    }


    @CrossOrigin
    @RequestMapping(value = "/random_pair", method = RequestMethod.GET)
    public Image[] random_pair(@RequestParam String gender) {
        Image image1;
        Image image2;
        if (alternate) {
            List<Image> all = imageRepository.findAllByGenderOrderByEloAsc(gender);

            int rand = getRandomNumberInRange(0, all.size() - 1);

            image1 = all.get(rand);
            image2 = all.get(rand + 1);
        } else {
            List<Image> all = imageRepository.findAllByGender(gender);

            int rand = getRandomNumberInRange(0, all.size() - 1);

            image1 = imageRepository.findFirstByGenderOrderByLastUpdatedAsc(gender);
            image2 = imageRepository.findFirstByGenderOrderByRandom(gender);
        }

        alternate = !alternate;

        if (image1.getId().equals(image2.getId())) {
            image2 = imageRepository.findFirstByGenderOrderByEloAsc(gender);
        }

        Image[] array = {image1, image2};

        return array;

    }

    @Autowired
    private Environment env;

    @RequestMapping(value = "/fill")
    public void fill() {

        String path = env.getProperty("face-location.path");
        System.out.println(path);

        File f = new File(path + "faces/female");
        var pathnames = f.list();

        for (String pathname : pathnames) {
            String file = pathname.substring(0, pathname.length() - 4);

            Image image = new Image(file, "f", getRandomNumberInRange(0, 5000));
            imageRepository.save(image);
        }


        f = new File(path + "faces/male");
        pathnames = f.list();

        for (String pathname : pathnames) {
            String file = pathname.substring(0, pathname.length() - 4);

            Image image = new Image(file, "m", getRandomNumberInRange(0, 5000));
            imageRepository.save(image);
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/leaderboard")
    public Map<String, List<String>> leaderboard() {
        List<Image> men = imageRepository.findAllByGenderOrderByEloDesc("m");
        List<String> idMen = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            idMen.add(men.get(i).getId());
        }

        List<Image> women = imageRepository.findAllByGenderOrderByEloDesc("f");
        List<String> idWomen = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            idWomen.add(women.get(i).getId());
        }

        Map<String, List<String>> map = new HashMap<>();

        map.put("men", idMen);
        map.put("women", idWomen);

        return map;

    }


    public static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}
