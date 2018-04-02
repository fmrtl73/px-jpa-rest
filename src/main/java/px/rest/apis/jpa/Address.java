package px.rest.apis.jpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Address{

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	private String line1;
	private String line2;
	private String city;
	private String state;
	private String zipcode;


	protected Address() {}

	/**
	 * Creates a new {@link Address} from the given street, city and country.
	 *
	 * @param line1 must not be {@literal null} or empty.
	 * @param line2 must not be {@literal null} or empty.
	 * @param city must not be {@literal null} or empty.
	 * @param state must not be {@literal null} or empty.
	 * @param zipcode must not be {@literal null} or empty.
	 */
	public Address(String line1, String line2, String city, String state, String zipcode) {
		this.line1=line1;
		this.line2=line2;
		this.city=city;
		this.state=state;
		this.zipcode=zipcode;
	}

  // Line 1 get set
	public String getLine1() {
		return line1;
	}

	public void setLine1(String line1) {
		this.line1 = line1;
	}
	// Line 2 get set
	public String getLine2() {
		return line2;
	}

	public void setLine2(String line2) {
		this.line2 = line2;
	}

	// city get set
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}

	// state get set

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	// zipcode get set
	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

}
