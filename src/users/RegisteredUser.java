package users;

public class RegisteredUser extends User{

	//Member variables
	private String passwordHash;
	
	
	//Constructor
	public RegisteredUser(String fn, String ln, String e, String ph ) {
		super(fn, ln, e);
		passwordHash=ph;
	}

	//Member functions
	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	
	public boolean isPasswordValid(String pwd)
	{
		return true;
	}
	

}
