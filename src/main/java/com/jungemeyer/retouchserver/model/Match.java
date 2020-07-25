package com.jungemeyer.retouchserver.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    @ManyToOne
    private Image winner;

    @ManyToOne
    private Image looser;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date")
    private Date created;

    public Match() {
    }

    public Match(Image winner, Image looser) {
        this.winner = winner;
        this.looser = looser;
    }

    public Image getWinner() {
        return winner;
    }

    public Image getLooser() {
        return looser;
    }

    @Override
    public String toString() {
        return "Match{" +
                "id=" + id +
                ", winner=" + winner +
                ", looser=" + looser +
                ", created=" + created +
                '}';
    }
}
