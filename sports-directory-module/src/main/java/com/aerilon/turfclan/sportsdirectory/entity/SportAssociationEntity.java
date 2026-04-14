package com.aerilon.turfclan.sportsdirectory.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "t_sport_associations", schema = "turfclan_schema")
public class SportAssociationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "selected_sport_experience", nullable = false, length = 100)
    private String selectedSportExperience;

    @Column(name = "sport_category", length = 100)
    private String sportCategory;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "images", columnDefinition = "jsonb")
    private JsonNode images;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "short_name", length = 100)
    private String shortName;

    @Column(name = "board", length = 255)
    private String board;

    @Column(name = "members_number")
    private Integer membersNumber;

    @Column(name = "founded_year")
    private Integer foundedYear;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "location_name", length = 255)
    private String locationName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "map_location", columnDefinition = "jsonb")
    private JsonNode mapLocation;

    @Column(name = "about", columnDefinition = "text")
    private String about;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "achievements", columnDefinition = "jsonb")
    private JsonNode achievements;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "contact_details", columnDefinition = "jsonb")
    private JsonNode contactDetails;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSelectedSportExperience() {
        return selectedSportExperience;
    }

    public void setSelectedSportExperience(String selectedSportExperience) {
        this.selectedSportExperience = selectedSportExperience;
    }

    public String getSportCategory() {
        return sportCategory;
    }

    public void setSportCategory(String sportCategory) {
        this.sportCategory = sportCategory;
    }

    public JsonNode getImages() {
        return images;
    }

    public void setImages(JsonNode images) {
        this.images = images;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public Integer getMembersNumber() {
        return membersNumber;
    }

    public void setMembersNumber(Integer membersNumber) {
        this.membersNumber = membersNumber;
    }

    public Integer getFoundedYear() {
        return foundedYear;
    }

    public void setFoundedYear(Integer foundedYear) {
        this.foundedYear = foundedYear;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public JsonNode getMapLocation() {
        return mapLocation;
    }

    public void setMapLocation(JsonNode mapLocation) {
        this.mapLocation = mapLocation;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public JsonNode getAchievements() {
        return achievements;
    }

    public void setAchievements(JsonNode achievements) {
        this.achievements = achievements;
    }

    public JsonNode getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(JsonNode contactDetails) {
        this.contactDetails = contactDetails;
    }
}
