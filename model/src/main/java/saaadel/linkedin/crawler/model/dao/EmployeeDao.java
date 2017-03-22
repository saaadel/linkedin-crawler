package saaadel.linkedin.crawler.model.dao;

import saaadel.linkedin.crawler.model.entity.Employee;

import javax.persistence.EntityManager;

public class EmployeeDao extends AbstractDao<Employee, Long> {
    public EmployeeDao(EntityManager em) {
        super(em, Employee.class);
    }
}
