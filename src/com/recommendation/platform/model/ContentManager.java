package com.recommendation.platform.model;

// Inheritance: extends abstract User
public class ContentManager extends User {

    private String department;
    private int coursesManaged;

    public ContentManager(int userId, String name, String email, String password, String department) {
        super(userId, name, email, password);
        this.department     = department;
        this.coursesManaged = 0;
    }

    // Polymorphism: overrides abstract method
    @Override
    public UserRole getRole() { return UserRole.CONTENT_MANAGER; }

    @Override
    public String getDashboardInfo() {
        return "=== Content Manager Dashboard ===\n"
             + "Name      : " + getName() + "\n"
             + "Email     : " + getEmail() + "\n"
             + "Department: " + department + "\n"
             + "Courses Managed: " + coursesManaged;
    }

    public String getDepartment()          { return department; }
    public int    getCoursesManaged()      { return coursesManaged; }
    public void   incrementCoursesManaged(){ coursesManaged++; }
    public void   setDepartment(String d)  { this.department = d; }
}
