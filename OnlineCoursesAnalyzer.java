import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * This is just a demo for you, please run it on JDK17 (some statements may be not allowed in lower version).
 * This is just a demo, and you can extend and implement functions
 * based on this demo, or implement it in a different way.
 */
public class OnlineCoursesAnalyzer {

    List<Course> courses = new ArrayList<>();

    public OnlineCoursesAnalyzer(String datasetPath) {
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader(datasetPath, StandardCharsets.UTF_8));
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] info = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1);
                Course course = new Course(info[0], info[1], new Date(info[2]), info[3], info[4], info[5],
                        Integer.parseInt(info[6]), Integer.parseInt(info[7]), Integer.parseInt(info[8]),
                        Integer.parseInt(info[9]), Integer.parseInt(info[10]), Double.parseDouble(info[11]),
                        Double.parseDouble(info[12]), Double.parseDouble(info[13]), Double.parseDouble(info[14]),
                        Double.parseDouble(info[15]), Double.parseDouble(info[16]), Double.parseDouble(info[17]),
                        Double.parseDouble(info[18]), Double.parseDouble(info[19]), Double.parseDouble(info[20]),
                        Double.parseDouble(info[21]), Double.parseDouble(info[22]));
                courses.add(course);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //1
    public Map<String, Integer> getPtcpCountByInst() {
        Map<String,Integer> ptcp = courses.stream().collect(Collectors.groupingBy(Course::getInstitution,
            Collectors.summingInt(Course::getParticipants)));
        return ptcp;
    }

    //2
    public Map<String, Integer> getPtcpCountByInstAndSubject() {
//        Stream<String> ptcp = courses.stream().map(course -> course.getInstitution() + "-" +
//            course.getSubject());
//        Map<String,Integer> a = courses.stream().collect(Collectors.groupingBy(course -> ptcp.toList().get(
//            courses.indexOf(course)),Collectors.summingInt(Course::getParticipants)));
        Map<String,Integer> ptcp = courses.stream().collect(Collectors.groupingBy(Course::getInstructorsAndSubject,
            Collectors.summingInt(Course::getParticipants)));
        Map<String, Integer> result1 = new LinkedHashMap<String, Integer>();

        ptcp.entrySet().stream().sorted(Map.Entry.<String,Integer>comparingByValue().reversed())
            .forEachOrdered(x -> result1.put(x.getKey(), x.getValue()));
        return result1;
    }

    //3
    public Map<String, List<List<String>>> getCourseListOfInstructor() {
//        String[] a = courses.get(1).getEachInstructor();
        Map<String,List<List<String>>> instructors = new HashMap<>();
        for (int i = 0; i < courses.size(); i++) {
            Course cur = courses.get(i);
            List<String> a = cur.getEachInstructor();
            for (int j = 0; j < a.size(); j++) {
                if (instructors.containsKey(a.get(j))){
                    if (a.size() == 1){
                        if (!instructors.get(a.get(j)).get(0).contains(cur.getTitle()))
                        instructors.get(a.get(j)).get(0).add(cur.getTitle());
                    }
                    else {
                        if (!instructors.get(a.get(j)).get(1).contains(cur.getTitle()))
                            instructors.get(a.get(j)).get(1).add(cur.getTitle());
                    }
                }else {
                    List<String> list0 = new ArrayList<>();
                    List<String> list1 = new ArrayList<>();
                    if (a.size() == 1)
                        list0.add(cur.getTitle());
                    else list1.add(cur.getTitle());
                    List<List<String>> b = new ArrayList<>();
                    b.add(list0);b.add(list1);
                    instructors.put(a.get(j),b);
                }
            }
        }
        instructors.forEach((s, lists) -> Collections.sort(lists.get(0)));
        instructors.forEach((s, lists) -> Collections.sort(lists.get(1)));
        return instructors;
    }


    //4
    public List<String> getCourses(int topK, String by) {
        List<Course> list = new ArrayList<>();
        if (by.equals("hours")){
            list = courses.stream().sorted(Comparator.comparing(Course::getTotalHours).reversed()).distinct().toList();
        }
        else {
            list = courses.stream().sorted(Comparator.comparing(Course::getParticipants).reversed()).distinct().toList();
        }
        List<String> result = new ArrayList<>();
        for (int i = 0; i < topK; i++) {
            result.add(list.get(i).getTitle());
        }

        return result;
    }

    //5
    public List<String> searchCourses(String courseSubject, double percentAudited, double totalCourseHours) {
        List<String> tmp = courses.stream().map(course -> course.getTitle().toLowerCase(Locale.ROOT)).toList();
        List<Course> courseList = courses.stream().
            filter(course -> course.getSubject().toLowerCase(Locale.ROOT).contains(courseSubject.toLowerCase(
                Locale.ROOT))
            && course.getPercentAudited() >= percentAudited && course.getTotalHours() <= totalCourseHours)
            .sorted(Comparator.comparing(Course::getTitle))
            .distinct().toList();
        List<String> result = new ArrayList<>();
        for (Course course : courseList) {
            result.add(course.getTitle());
        }
        return result;
    }

    //6
    public List<String> recommendCourses(int age, int gender, int isBachelorOrHigher) {
//        double avgMedianAge = courses.stream().mapToDouble(Course::getMedianAge).average().getAsDouble();
//        double avgMale = courses.stream().mapToDouble(Course::getPercentMale).average().getAsDouble();
//        double avgBachelor = courses.stream().mapToDouble(Course::getPercentDegree).average().getAsDouble();
        Map<String,Double> courseListAge = courses.stream()
            .collect(Collectors.groupingBy(Course::getNumber,
            Collectors.averagingDouble(Course::getMedianAge)));
        Map<String,Double> courseListMale = courses.stream()
            .collect(Collectors.groupingBy(Course::getNumber,
            Collectors.averagingDouble(Course::getPercentMale)));
        Map<String,Double> courseListDegree = courses.stream()
            .collect(Collectors.groupingBy(Course::getNumber,
            Collectors.averagingDouble(Course::getPercentDegree)));
        Map<String,String> courseLatest = courses.stream().sorted(Comparator.comparing(Course::getLaunchDate).reversed())
            .collect(Collectors.groupingBy(Course::getNumber,Collectors.collectingAndThen(Collectors.toList(), courses1 -> courses1.get(0).getTitle())));
        Map<String,Double> similarities = new HashMap<>();
        for (int i = 0; i < courses.size(); i++) {
            String key = courses.get(i).getNumber();
            double avgMedianAge = courseListAge.get(key);
            double avgMale = courseListMale.get(key);
            double avgBachelor = courseListDegree.get(key);
            double similarity = Math.pow((age - avgMedianAge),2) + Math.pow((100*gender - avgMale),2)
                + Math.pow((100*isBachelorOrHigher - avgBachelor),2);
            similarities.put(key,similarity);
        }
//        double similarity = Math.sqrt(age - avgMedianAge) + Math.sqrt(100 - avgMale) - Math.sqrt(100 - avgBachelor);
        List<String> resultList = new ArrayList<>();
        similarities.entrySet().stream().sorted(Map.Entry.<String,Double>comparingByValue()
                .thenComparing(stringDoubleEntry -> courseLatest.get(stringDoubleEntry.getKey())))
            .distinct().forEachOrdered(x -> resultList.add(x.getKey()));
        List<String> result = new ArrayList<>();
        for (int i = 0; i < resultList.size(); i++) {
            result.add(courseLatest.get(resultList.get(i)));
        }
        List<String> re = result.stream().distinct().toList();
        List<String> result1 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            result1.add(re.get(i));
        }
        return result1;
    }

}

class Course {
    String institution;
    String number;
    Date launchDate;
    String title;
    String instructors;
    String subject;
    int year;
    int honorCode;
    int participants;
    int audited;
    int certified;
    double percentAudited;
    double percentCertified;
    double percentCertified50;
    double percentVideo;
    double percentForum;
    double gradeHigherZero;
    double totalHours;
    double medianHoursCertification;
    double medianAge;
    double percentMale;
    double percentFemale;
    double percentDegree;

    public Course(String institution, String number, Date launchDate,
                  String title, String instructors, String subject,
                  int year, int honorCode, int participants,
                  int audited, int certified, double percentAudited,
                  double percentCertified, double percentCertified50,
                  double percentVideo, double percentForum, double gradeHigherZero,
                  double totalHours, double medianHoursCertification,
                  double medianAge, double percentMale, double percentFemale,
                  double percentDegree) {
        this.institution = institution;
        this.number = number;
        this.launchDate = launchDate;
        if (title.startsWith("\"")) title = title.substring(1);
        if (title.endsWith("\"")) title = title.substring(0, title.length() - 1);
        this.title = title;
        if (instructors.startsWith("\"")) instructors = instructors.substring(1);
        if (instructors.endsWith("\"")) instructors = instructors.substring(0, instructors.length() - 1);
        this.instructors = instructors;
        if (subject.startsWith("\"")) subject = subject.substring(1);
        if (subject.endsWith("\"")) subject = subject.substring(0, subject.length() - 1);
        this.subject = subject;
        this.year = year;
        this.honorCode = honorCode;
        this.participants = participants;
        this.audited = audited;
        this.certified = certified;
        this.percentAudited = percentAudited;
        this.percentCertified = percentCertified;
        this.percentCertified50 = percentCertified50;
        this.percentVideo = percentVideo;
        this.percentForum = percentForum;
        this.gradeHigherZero = gradeHigherZero;
        this.totalHours = totalHours;
        this.medianHoursCertification = medianHoursCertification;
        this.medianAge = medianAge;
        this.percentMale = percentMale;
        this.percentFemale = percentFemale;
        this.percentDegree = percentDegree;
    }

    public String getInstitution() {
        return institution;
    }

    public String getNumber() {
        return number;
    }

    public Date getLaunchDate() {
        return launchDate;
    }

    public String getTitle() {
        return title;
    }

    public String getInstructors() {
        return instructors;
    }

    public List<String> getEachInstructor(){
        return Arrays.stream(getInstructors().split(", ")).toList();
    }

    public String getInstructorsAndSubject(){
        return getInstitution() + "-" + getSubject();
    }

    public String getSubject() {
        return subject;
    }

    public int getYear() {
        return year;
    }

    public int getHonorCode() {
        return honorCode;
    }

    public int getParticipants() {
        return participants;
    }

    public int getAudited() {
        return audited;
    }

    public int getCertified() {
        return certified;
    }

    public double getPercentAudited() {
        return percentAudited;
    }

    public double getPercentCertified() {
        return percentCertified;
    }

    public double getPercentCertified50() {
        return percentCertified50;
    }

    public double getPercentVideo() {
        return percentVideo;
    }

    public double getPercentForum() {
        return percentForum;
    }

    public double getGradeHigherZero() {
        return gradeHigherZero;
    }

    public double getTotalHours() {
        return totalHours;
    }

    public double getMedianHoursCertification() {
        return medianHoursCertification;
    }

    public double getMedianAge() {
        return medianAge;
    }

    public double getPercentMale() {
        return percentMale;
    }

    public double getPercentFemale() {
        return percentFemale;
    }

    public double getPercentDegree() {
        return percentDegree;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Course course = (Course) o;
        return Objects.equals(title, course.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}