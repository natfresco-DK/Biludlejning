package ek.dk.biludlejning.model;

public class User {
        protected int id;
        protected String firstName;
        protected String lastName;
        protected String username;
        protected String password;
        protected String email;
        protected String phone;
        protected String role;
        protected boolean active;


    public User(){}

        public User(int id, String firstName, String lastName, String username, String password, String email, String phone, String role, boolean active) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.username = username;
            this.password = password;
            this.email = email;
            this.phone = phone;
            this.role = role;
            this.active = active;
        }

        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }
        public void setFirstname(String firstname) {
            this.firstName = firstname;
        }

        public String getLastName() {
            return lastName;
        }
        public void setLastName(String lastname) {
            this.lastName = lastname;
        }

        public String getUsername() {
            return username;
        }
        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }

        public String getRole() {
            return role;
        }
        public void setRole(String role) {
            this.role = role;
        }

        public String getPhone() {
        return phone;
    }
        public void setPhone(String phone) {
            this.phone = phone;
    }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
}
