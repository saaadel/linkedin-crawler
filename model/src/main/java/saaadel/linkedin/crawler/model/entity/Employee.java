package saaadel.linkedin.crawler.model.entity;

import javax.persistence.*;
import java.util.Objects;

/**
 * Created by saaadel on 28.03.2017.
 */
@Entity
@Table(name = "employees", schema = "veastecc_companies")
public class Employee {
    private Long id;
    private Long companyId;
    private String link;
    private String name;
    private boolean isPremium;
    private String normalizedTitle;
    private String profileTitle;
    private String profileCompany;
    private String currentTitle;
    private String currentCompany;
    private String location;
    private String connectionsLvl;
    private String connectionsCount;
    private String about;
    private String email;
    private String website;
    private boolean processed;
    private boolean checked;

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
     * @see #companyId
     */
    @Basic
    @Column(name = "company_id")
    public Long getCompanyId() {
        return companyId;
    }

    /**
     * @see #companyId
     */
    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
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
     * @see #isPremium
     */
    @Basic
    @Column(name = "is_premium")
    public boolean getIsPremium() {
        return isPremium;
    }

    /**
     * @see #isPremium
     */
    public void setIsPremium(boolean isPremium) {
        this.isPremium = isPremium;
    }

    /**
     * @see #normalizedTitle
     */
    @Basic
    @Column(name = "normalized_title")
    public String getNormalizedTitle() {
        return normalizedTitle;
    }

    /**
     * @see #normalizedTitle
     */
    public void setNormalizedTitle(String normalizedTitle) {
        this.normalizedTitle = normalizedTitle;
    }

    /**
     * @see #profileTitle
     */
    @Basic
    @Column(name = "profile_title")
    public String getProfileTitle() {
        return profileTitle;
    }

    /**
     * @see #profileTitle
     */
    public void setProfileTitle(String profileTitle) {
        this.profileTitle = profileTitle;
    }

    /**
     * @see #profileCompany
     */
    @Basic
    @Column(name = "profile_company")
    public String getProfileCompany() {
        return profileCompany;
    }

    /**
     * @see #profileCompany
     */
    public void setProfileCompany(String profileCompany) {
        this.profileCompany = profileCompany;
    }

    /**
     * @see #currentTitle
     */
    @Basic
    @Column(name = "current_title")
    public String getCurrentTitle() {
        return currentTitle;
    }

    /**
     * @see #currentTitle
     */
    public void setCurrentTitle(String currentTitle) {
        this.currentTitle = currentTitle;
    }

    /**
     * @see #currentCompany
     */
    @Basic
    @Column(name = "current_company")
    public String getCurrentCompany() {
        return currentCompany;
    }

    /**
     * @see #currentCompany
     */
    public void setCurrentCompany(String currentCompany) {
        this.currentCompany = currentCompany;
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
     * @see #connectionsLvl
     */
    @Basic
    @Column(name = "connections_lvl")
    public String getConnectionsLvl() {
        return connectionsLvl;
    }

    /**
     * @see #connectionsLvl
     */
    public void setConnectionsLvl(String connectionsLvl) {
        this.connectionsLvl = connectionsLvl;
    }

    /**
     * @see #connectionsCount
     */
    @Basic
    @Column(name = "connections_count")
    public String getConnectionsCount() {
        return connectionsCount;
    }

    /**
     * @see #connectionsCount
     */
    public void setConnectionsCount(String connectionsCount) {
        this.connectionsCount = connectionsCount;
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
     * @see #email
     */
    @Basic
    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    /**
     * @see #email
     */
    public void setEmail(String email) {
        this.email = email;
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
     * @see #processed
     */
    @Basic
    @Column(name = "processed")
    public boolean getProcessed() {
        return processed;
    }

    /**
     * @see #processed
     */
    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    /**
     * @see #checked
     */
    @Basic
    @Column(name = "checked")
    public boolean getChecked() {
        return checked;
    }

    /**
     * @see #checked
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return isPremium == employee.isPremium &&
                processed == employee.processed &&
                checked == employee.checked &&
                Objects.equals(id, employee.id) &&
                Objects.equals(companyId, employee.companyId) &&
                Objects.equals(link, employee.link) &&
                Objects.equals(name, employee.name) &&
                Objects.equals(normalizedTitle, employee.normalizedTitle) &&
                Objects.equals(profileTitle, employee.profileTitle) &&
                Objects.equals(profileCompany, employee.profileCompany) &&
                Objects.equals(currentTitle, employee.currentTitle) &&
                Objects.equals(currentCompany, employee.currentCompany) &&
                Objects.equals(location, employee.location) &&
                Objects.equals(connectionsLvl, employee.connectionsLvl) &&
                Objects.equals(connectionsCount, employee.connectionsCount) &&
                Objects.equals(about, employee.about) &&
                Objects.equals(email, employee.email) &&
                Objects.equals(website, employee.website);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, companyId, link, name, isPremium, normalizedTitle, profileTitle, profileCompany, currentTitle, currentCompany, location, connectionsLvl, connectionsCount, about, email, website, processed, checked);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Employee{");
        sb.append("id=").append(id);
        sb.append(", companyId=").append(companyId);
        sb.append(", link='").append(link).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", isPremium=").append(isPremium);
        sb.append(", normalizedTitle='").append(normalizedTitle).append('\'');
        sb.append(", profileTitle='").append(profileTitle).append('\'');
        sb.append(", profileCompany='").append(profileCompany).append('\'');
        sb.append(", currentTitle='").append(currentTitle).append('\'');
        sb.append(", currentCompany='").append(currentCompany).append('\'');
        sb.append(", location='").append(location).append('\'');
        sb.append(", connectionsLvl='").append(connectionsLvl).append('\'');
        sb.append(", connectionsCount='").append(connectionsCount).append('\'');
        sb.append(", about='").append(about).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", website='").append(website).append('\'');
        sb.append(", processed=").append(processed);
        sb.append(", checked=").append(checked);
        sb.append('}');
        return sb.toString();
    }
}
