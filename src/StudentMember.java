public class StudentMember extends Member {

    public StudentMember(String name, String memberID) {
        super(name, memberID);
    }

    @Override
    public double calculateFee(int lateDays) {
        if (lateDays < 0) lateDays = 0;  // negatif olmz
        return lateDays * 1.0; // student discount.
    }

    @Override
    public String toString() {
        return "StudentMember{name='" + getName() + "', id='" + getID() + "'}";
    }
}
