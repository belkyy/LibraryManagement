public class StudentMember extends Member {

    public StudentMember(String name, String memberID) {
        super(name, memberID);
    }

    @Override
    public double calculateFee(int lateDays) {
        if (lateDays < 0) lateDays = 0;  // negatif gecikmeyi engelle
        return lateDays * 1.0; // öğrenci için indirimli ücret
    }

    @Override
    public String toString() {
        return "StudentMember{name='" + getName() + "', id='" + getID() + "'}";
    }
}
