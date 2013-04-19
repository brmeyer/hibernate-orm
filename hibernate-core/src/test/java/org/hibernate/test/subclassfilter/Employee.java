// $Id: Employee.java 5899 2005-02-24 20:08:04Z steveebersole $
package org.hibernate.test.subclassfilter;
import javax.persistence.Column;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of Employee.
 *
 * @author Steve Ebersole
 */
public class Employee extends Person {
	@Column(name="`title`")
    private String title;
	private String department;
	private Employee manager;
	private Set minions = new HashSet();

	public Employee() {
	}

	public Employee(String name) {
		super( name );
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public Employee getManager() {
		return manager;
	}

	public void setManager(Employee manager) {
		this.manager = manager;
	}

	public Set getMinions() {
		return minions;
	}

	public void setMinions(Set minions) {
		this.minions = minions;
	}
}
