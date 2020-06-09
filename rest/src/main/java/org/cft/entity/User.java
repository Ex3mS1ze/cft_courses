package org.cft.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "users", schema = "public")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String email;
    private String firstName;
    private String secondName;
    private LocalDateTime registrationDate;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_to_liked_review",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "review_id")
    )
    @JsonView(value = View.ExtendedView.class)
    private Set<Review> likedReviews;
    @OneToOne(
            mappedBy = "user",
            cascade = {CascadeType.REMOVE, CascadeType.ALL},
            fetch = FetchType.LAZY)
    @JsonView(value = View.ExtendedView.class)
    private UserDetailsImpl userDetails;
}
