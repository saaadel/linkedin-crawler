package saaadel.linkedin.crawler.model.entity;

import javax.persistence.*;
import java.util.Objects;

/**
 * Created by saaadel on 29.03.2017.
 */
@Entity
@Table(name = "companies", schema = "veastecc_companies")
public class Company {
    private Long id;
    private String link;
    private String name;
    private String business;
    private String location;
    private String size;
    private String about;
    private String website;
    private short checked;
    private short processed;

    /**
     * @see #id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    /**
     * @see #id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @see #link
     */
    @Basic
    @Column(name = "link")
    public String getLink() {
        return link;
    }

    /**
     * @see #link
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * @see #name
     */
    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    /**
     * @see #name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @see #business
     */
    @Basic
    @Column(name = "business")
    public String getBusiness() {
        return business;
    }

    /**
     * @see #business
     */
    public void setBusiness(String business) {
        this.business = business;
    }

    /**
     * @see #location
     */
    @Basic
    @Column(name = "location")
    public String getLocation() {
        return location;
    }

    /**
     * @see #location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @see #size
     */
    @Basic
    @Column(name = "size")
    public String getSize() {
        return size;
    }

    /**
     * @see #size
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * @see #about
     */
    @Basic
    @Column(name = "about")
    public String getAbout() {
        return about;
    }

    /**
     * @see #about
     */
    public void setAbout(String about) {
        this.about = about;
    }

    /**
     * @see #website
     */
    @Basic
    @Column(name = "website")
    public String getWebsite() {
        return website;
    }

    /**
     * @see #website
     */
    public void setWebsite(String website) {
        this.website = website;
    }

    /**
     * @see #checked
     */
    @Basic
    @Column(name = "checked")
    public short getChecked() {
        return checked;
    }

    /**
     * @see #checked
     */
    public void setChecked(short checked) {
        this.checked = checked;
    }

    /**
     * @see #processed
     */
    @Basic
    @Column(name = "processed")
    public short getProcessed() {
        return processed;
    }

    /**
     * @see #processed
     */
    public void setProcessed(short processed) {
        this.processed = processed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company that = (Company) o;
        return id == that.id &&
                checked == that.checked &&
                processed == that.processed &&
                Objects.equals(link, that.link) &&
                Objects.equals(name, that.name) &&
                Objects.equals(business, that.business) &&
                Objects.equals(location, that.location) &&
                Objects.equals(size, that.size) &&
                Objects.equals(about, that.about) &&
                Objects.equals(website, that.website);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, link, name, business, location, size, about, website, checked, processed);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Company{");
        sb.append("id=").append(id);
        sb.append(", link='").append(link).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", business='").append(business).append('\'');
        sb.append(", location='").append(location).append('\'');
        sb.append(", size='").append(size).append('\'');
        sb.append(", about='").append(about).append('\'');
        sb.append(", website='").append(website).append('\'');
        sb.append(", checked=").append(checked);
        sb.append(", processed=").append(processed);
        sb.append('}');
        return sb.toString();
    }
}
