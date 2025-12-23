public class Member {

    private final String name;
    private final String memberID;

    public Member(String name, String memberID) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Member name cannot be empty");
        }
        if (memberID == null || memberID.isEmpty()) {
            throw new IllegalArgumentException("Member ID cannot be empty");
        }

        this.name = name;
        this.memberID = memberID;
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return memberID;
    }

    public double calculateFee(int lateDays) {
        if (lateDays < 0) lateDays = 0;  // negatif olamz
        return lateDays * 2.0;
    }

    @Override
    public String toString() {
        return "Member{" +
                "name='" + name + '\'' +
                ", memberID='" + memberID + '\'' +
                '}';
    }
}
