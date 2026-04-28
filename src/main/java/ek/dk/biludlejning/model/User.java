package ek.dk.biludlejning.model;

public class User {
        protected int id;
        protected String firstname;
        protected String lastname;
        protected String username;
        protected String password;
        protected String email;
        protected String phone;
        protected String role;
        protected boolean active;


    public User(){}

        public User(int id, String firstname, String lastname, String username, String password, String email, String phone, String role,  boolean active) {
            this.id = id;
            this.firstname = firstname;
            this.lastname = lastname;
            this.username = username;
            this.password = password;
            this.email = email;
            this.phone = phone;
            this.role = role;
        }

        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }

        public String getFirstname() {
            return firstname;
        }
        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getLastname() {
            return lastname;
        }
        public void setLastname(String lastname) {
            this.lastname = lastname;
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
