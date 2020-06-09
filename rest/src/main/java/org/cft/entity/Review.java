package org.cft.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@Entity
@Table(name = "reviews", schema = "public")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "author_id")
    private Critic author;
    private String subject;
    @Column(columnDefinition = "TEXT")
    private String text;
    private Double rating;
    private LocalDate date;
    @ManyToMany(fetch = FetchType.LAZY)
    @JsonView(value = View.ExtendedView.class)
    private Set<User> usersWhoLike;
}
