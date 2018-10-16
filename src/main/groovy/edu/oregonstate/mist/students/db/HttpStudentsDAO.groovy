package edu.oregonstate.mist.students.db

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import edu.oregonstate.mist.students.core.AcademicStatus
import edu.oregonstate.mist.students.core.AccountBalance
import edu.oregonstate.mist.students.core.AccountTransactions
import edu.oregonstate.mist.students.core.ClassSchedule
import edu.oregonstate.mist.students.core.GPALevels
import edu.oregonstate.mist.students.core.Grade
import edu.oregonstate.mist.students.core.Holds
import groovy.transform.InheritConstructors
import org.apache.http.HttpHeaders
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.util.EntityUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.core.UriBuilder
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class HttpStudentsDAO {
    private HttpClient httpClient
    private final URI baseURI

    private final String studentsEndpoint = "students"
    private final String accountBalanceEndpoint = "account-balances"
    private final String accountTransactionsEndpoint = "account-transactions"
    private final String academicStandingsEndpoint = "gpa-academic-standings"
    private final String gradesEndpoint = "grades"
    private final String classSchedulesEndpoint = "class-schedules"
    private final String holdsEndpoint = "holds"

    private static Logger logger = LoggerFactory.getLogger(this)

    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())

    HttpStudentsDAO(HttpClient httpClient, String endpoint) {
        this.httpClient = httpClient
        this.baseURI = UriBuilder.fromUri(endpoint).path("/api").build()
    }

    protected AccountBalance getAccountBalance(String id) {
        String response = getResponse("$accountBalanceEndpoint/$id")

        BackendAccountBalance accountBalance = objectMapper.readValue(
                response, BackendAccountBalance)

        AccountBalance.fromBackendAccountBalance(accountBalance)
    }

    protected AccountTransactions getAccountTransactions(String id) {
        String response = getResponse(getStudentsEndpoint(id, accountTransactionsEndpoint))

        List<BackendAccountTransaction> accountTransactions = objectMapper.readValue(
                response, new TypeReference<List<BackendAccountTransaction>>() {})

        AccountTransactions.fromBackendAccountTransactions(accountTransactions)
    }

    protected GPALevels getGPA(String id) {
        String response = getResponse(getAcademicStandingsEndpoint(id))

        def unmappedResponse = objectMapper.readValue(response,
                new TypeReference<List<HashMap>>() {})

        List<BackendGPA> gpas = objectMapper.convertValue(unmappedResponse[0]["levelGPAs"],
                new TypeReference<List<BackendGPA>>() {})

        GPALevels.fromBackendGPA(gpas)
    }

    protected List<AcademicStatus> getAcademicStatus(String id, String term) {
        String response = getResponse(getAcademicStandingsEndpoint(id), term)

        def unmappedResponse = objectMapper.readValue(response,
                new TypeReference<List<HashMap>>() {})

        List<BackendAcademicStanding> academicStanding = objectMapper.convertValue(
                unmappedResponse[0]["studentTermGpaAcademicStandings"],
                new TypeReference<List<BackendAcademicStanding>>() {})

        if (term && academicStanding.size() == 1
                && academicStanding[0].academicStandingTerm != term) {
            // When querying with a valid term that the student doesn't have data for, the backend
            // API will return with a malformed academic standing object. The best way I've found to
            // know that the object is malformed is to compare the term against the term used in the
            // query. If they differ, return an empty object to reflect that the student and term is
            // valid, but no data exists for the given term.
            return []
        }

        academicStanding.collect { AcademicStatus.fromBackendAcademicStanding(it) }
    }

    protected List<Grade> getGrades(String id, String term) {
        String response = getResponse(getStudentsEndpoint(id, gradesEndpoint), term)

        List<BackendGrade> grades = objectMapper.readValue(
                response, new TypeReference<List<BackendGrade>>() {})

        grades.collect { Grade.fromBackendGrade(it) }
    }

    protected List<ClassSchedule> getClassSchedule(String id, String term) {
        String response = getResponse(getStudentsEndpoint(id, classSchedulesEndpoint), term)

        def unmappedResponse = objectMapper.readValue(response,
                new TypeReference<List<HashMap>>() {})

        List<BackendClassSchedule> classSchedule = objectMapper.convertValue(
                unmappedResponse[0]["studentCourseRegistrations"],
                new TypeReference<List<BackendClassSchedule>>() {})

        classSchedule.collect { ClassSchedule.fromBackendClassSchedule(it) }
    }

    protected Holds getHolds(String id) {
        String response = getResponse(getStudentsEndpoint(id, holdsEndpoint))

        List<BackendHold> holds = objectMapper.readValue(response,
                new TypeReference<List<BackendHold>>() {})

        Holds.fromBackendHolds(holds)
    }

    private String getResponse(String endpoint, String term = null) {
        UriBuilder uriBuilder = UriBuilder.fromUri(baseURI)
        uriBuilder.path(endpoint)

        if (term) {
            uriBuilder.queryParam("term", term)
        }

        URI requestURI = uriBuilder.build()

        HttpGet request = new HttpGet(requestURI)
        request.setHeader(HttpHeaders.ACCEPT, "application/json")

        logger.info("Making a request to ${requestURI}")

        HttpResponse response = httpClient.execute(request)

        Integer statusCode = response.getStatusLine().getStatusCode()

        if (statusCode == HttpStatus.SC_OK) {
            logger.info("Successful response from backend data source.")
            EntityUtils.toString(response.entity)
        } else if (statusCode == HttpStatus.SC_NOT_FOUND) {
            EntityUtils.consumeQuietly(response.entity)

            String message = "Student not found"
            logger.info(message)

            throw new StudentNotFoundException(message)
        } else if (statusCode == HttpStatus.SC_BAD_REQUEST) {
            List<String> errorMessages = getBackendErrorMessages(
                    EntityUtils.toString(response.entity))

            logger.info("400 response from backend data source. Error messages: $errorMessages")

            if (errorMessages.contains("Term not found")) {
                String message = "Term: $term is invalid."
                logger.info(message)

                throw new InvalidTermException(message)
            } else {
                String message = "Uncaught error(s) in bad request."
                logger.error(message)

                throw new Exception(message)
            }
        } else {
            String message = "Unexpected response from backend data source. " +
                    "Status code: $statusCode"
            logger.error(message)

            throw new Exception(message)
        }
    }

    private List<String> getBackendErrorMessages(String errorResponse) {
        def unmappedErrors = objectMapper.readValue(errorResponse, new TypeReference<HashMap>() {})

        List<BackendError> errors = objectMapper.convertValue(unmappedErrors["errors"],
                new TypeReference<List<BackendError>>() {})

        errors.collect { it.message }
    }

    private String getAcademicStandingsEndpoint(String id) {
        getStudentsEndpoint(id, academicStandingsEndpoint)
    }

    private String getStudentsEndpoint(String id, String endpoint) {
        "$studentsEndpoint/$id/$endpoint"
    }

    static String getCode(Map<String, String> map) {
        map.get("code")
    }

    static String getDescription(Map<String, String> map) {
        map.get("description")
    }
}

@InheritConstructors
class HttpStudentsDAOException extends Exception {}

@JsonIgnoreProperties(ignoreUnknown = true)
class BackendError {
    String code
    String message
    String description
}

@JsonIgnoreProperties(ignoreUnknown = true)
class BackendAccountBalance {
    BigDecimal balance
}

@JsonIgnoreProperties(ignoreUnknown = true)
class BackendAccountTransaction {
    BigDecimal amount
    String description
    Instant entryDate
}

@JsonIgnoreProperties(ignoreUnknown = true)
class BackendGPA {
    String gpa
    Integer gpaHours
    String gpaTypeIndicatorDescription
    Integer hoursAttempted
    Integer hoursEarned
    Integer hoursPassed
    String levelDescription
    String qualityPoints
}

@JsonIgnoreProperties(ignoreUnknown = true)
class BackendAcademicStanding {
    String academicStandingDescription
    String academicStandingTerm
    String academicStandingTermDescription
    List<BackendGPA> termGPAs
}

@JsonIgnoreProperties(ignoreUnknown = true)
class BackendGrade {
    String crn
    String gradeFinal
    String gradeInAcadHistory

    String courseSubject
    String courseSubjectDescription

    @JsonProperty("subject")
    private void unpackSubject(Map<String, String> subject) {
        this.courseSubject = HttpStudentsDAO.getCode(subject)
        this.courseSubjectDescription = HttpStudentsDAO.getDescription(subject)
    }

    String courseNumber
    String courseTitle
    String sequenceNumber

    String term
    String termDescription

    @JsonProperty("term")
    private void unpackTerm(Map<String, String> term) {
        this.term = HttpStudentsDAO.getCode(term)
        this.termDescription = HttpStudentsDAO.getDescription(term)
    }

    String classFormat
    String classFormatDescription

    @JsonProperty("classFormat")
    private void unpackClassFormat(Map<String, String> classFormat) {
        this.classFormat = HttpStudentsDAO.getCode(classFormat)
        this.classFormatDescription = HttpStudentsDAO.getDescription(classFormat)
    }

    Integer creditHour

    String registrationStatus

    @JsonProperty("registrationStatus")
    private void unpackRegistrationStatus(Map<String, String> registrationStatus) {
        this.registrationStatus = HttpStudentsDAO.getDescription(registrationStatus)
    }

    String courseLevel

    @JsonProperty("level")
    private void unpackCourseLevel(Map<String, String> courseLevel) {
        this.courseLevel = HttpStudentsDAO.getDescription(courseLevel)
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class BackendClassSchedule {
    String academicYear
    String academicYearDescription
    String courseReferenceNumber
    String subject
    String subjectDescription
    String courseNumber
    String courseTitle
    String sequenceNumber
    String term
    String termDescription
    String scheduleDescription
    String scheduleType
    Integer creditHour

    String registrationStatus

    @JsonProperty("courseRegistrationStatus")
    private void unpackRegistrationStatus(Map<String, String> registrationStatus) {
        this.registrationStatus = HttpStudentsDAO.getDescription(registrationStatus)
    }

    String gradingModeDescription

    Boolean continuingEducation

    @JsonProperty("level")
    private void unpackContinuingEducation(Map<String, Boolean> continuingEducation) {
        this.continuingEducation = continuingEducation.get("ceuInd")
    }

    List<BackendFaculty> faculty
    List<BackendMeetingTime> meetingTimes
}

@JsonIgnoreProperties(ignoreUnknown = true)
class BackendFaculty {
    String bannerId
    String displayName
    String emailAddress
    Boolean primaryIndicator
}

@JsonIgnoreProperties(ignoreUnknown = true)
class BackendMeetingTime {
    LocalDate startDate
    LocalDate endDate

    private static DateTimeFormatter backendTimeFormat = DateTimeFormatter.ofPattern("HHmm")

    @JsonIgnore
    LocalTime beginTime

    @JsonProperty("beginTime")
    private void setBeginTime(String beginTime) {
        this.beginTime = LocalTime.parse(beginTime, backendTimeFormat)
    }

    @JsonIgnore
    LocalTime endTime

    @JsonProperty("endTime")
    private void setEndTime(String endTime) {
        this.endTime = LocalTime.parse(endTime, backendTimeFormat)
    }

    String room
    String building
    String buildingDescription
    String campusDescription
    BigDecimal hoursWeek
    Integer creditHourSession
    String meetingScheduleType
    Boolean sunday
    Boolean monday
    Boolean tuesday
    Boolean wednesday
    Boolean thursday
    Boolean friday
    Boolean saturday
}

@JsonIgnoreProperties(ignoreUnknown = true)
class BackendHold {
    LocalDate fromDate
    LocalDate toDate
    String holdTypeDescription
    List<String> processAffectedDescription
    String reason
}