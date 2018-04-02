package px.rest.apis.jpa;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import javax.persistence.ManyToOne;


@Entity
public class Person {

	@Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Long id;
	private String firstName;
	private String lastName;

  @ManyToOne(optional = false, cascade = CascadeType.ALL)
	private Address address;

	protected Person() {}

  public Person(String firstName, String lastName) {
      this.firstName = firstName;
      this.lastName = lastName;
  }

  @Override
  public String toString() {
      return String.format(
              "Person[id=%d, firstName='%s', lastName='%s']",
              id, firstName, lastName);
  }


	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
}
