package com.jungemeyer.retouchserver.model;

import com.jungemeyer.retouchserver.controller.ImageController;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "image")
public class Image {

    @Id
    private String id;

    @OneToMany(mappedBy = "winner")
    private Set<Match> win;

    @OneToMany(mappedBy = "looser")
    private Set<Match> loose;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_date")
    private Date lastUpdated;

    private double elo;

    private long random;

    public Image() {
        this.elo = 1000.0;
    }

    public Image(String id, long random) {
        this.id = id;
        this.elo = 1000.0;
        this.random = random;
    }

    public String getId() {
        return id;
    }

    public double getElo() {
        return elo;
    }

    public void winAgainst(Image looser) {

        double e_winner = 1 / (1 + Math.pow(10, (this.elo - looser.elo) / 400.0));
        double e_looser = 1 / (1 + Math.pow(10, (looser.elo - this.elo) / 400.0));

        this.elo = (this.elo + (20 * (1.0 - e_winner)));
        looser.elo = (looser.elo + (-20 * e_looser));

        this.random += ImageController.getRandomNumberInRange(0,500);
        looser.random += ImageController.getRandomNumberInRange(0,500);
    }
}
