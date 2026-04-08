 package com.polycampus.android.common;

/**
 * Unified API Configuration for PolyCampus
 * Centralized endpoint management for Student and Teacher portals.
 */
public class Urls {
    // --- Base Configurations ---
    public static final String BASE_URL = "http://192.168.0.110:80/PolyCampusAPI/";
    public static final String TEACHER_BASE_URL = BASE_URL + "Teacher/";
    
    // --- File & Document Directories ---
    public static final String ONLINE_DOC_DIR = TEACHER_BASE_URL + "doc/";
    public static final String IMAGE_ASSET_DIR = BASE_URL + "image/";
    public static final String NOTICE_IMAGE_DIR = BASE_URL + "notic_img/"; // Adjust prefix if needed

    // ==========================================
    // 🎓 STUDENT PORTAL ENDPOINTS
    // ==========================================
    
    // --- Authentication & Account ---
    public static final String loginWebService = BASE_URL + "PolyUserLogin.php";
    public static final String registerUserWebService = BASE_URL + "Polyuserregisterdetail.php";
    public static final String forgetPasswordWebService = BASE_URL + "PolyuserForgetPassword.php";
    public static final String sendOtpEmail = BASE_URL + "sendOtpEmail.php";
    
    // --- Profile Management ---
    public static final String getMyDetailsWebService = BASE_URL + "PolyMyDetails.php";
    public static final String updateProfileWebservice = BASE_URL + "PolyupdateProfile.php";
    
    // --- Academic & Content ---
    public static final String getAllCategoryWebService = BASE_URL + "PolygetAllCategoryDetails.php";
    public static final String getAllSubjectDetailsWebService = BASE_URL + "PolygetAllSubjectDetails.php";
    public static final String getSubjectwiseStudyMaterial = BASE_URL + "PolysubjectWiseStudyMaterial.php";
    public static final String urlMyStudyMaterial = BASE_URL + "getStudyMaterial.php";
    public static final String getNotice = BASE_URL + "getNotice.php";

    // --- Services & Certificates ---
    public static final String bonafiedDetails = BASE_URL + "PolybonafiedDetails.php";
    public static final String tcDetails = BASE_URL + "PolytcDetails.php";

    // --- Student Attendance ---
    public static final String checkAttendanceDoneOrNot = BASE_URL + "checkAttendanceDoneOrNot.php";
    public static final String urlGetSujects = BASE_URL + "get_subjects.php";
    public static final String urlGetSubjectwiseAttendance = BASE_URL + "getSubjectwiseAttendance.php";
    public static final String urlGetSubjectwisePresentyCount = BASE_URL + "getSubjectwisePresentyCount.php";
    public static final String urlApplyLeave = BASE_URL + "apply_leave.php";
    public static final String urlGetMyLeaves = BASE_URL + "get_my_leave_requests.php";

    // ==========================================
    // 👨‍🏫 TEACHER PORTAL ENDPOINTS
    // ==========================================

    // --- Authentication ---
    public static final String urlLoginTeacher = TEACHER_BASE_URL + "login_teacher.php";

    // --- Student Management ---
    public static final String urlGetAllStudent = TEACHER_BASE_URL + "getAllStudent.php";
    public static final String urldeleteStudent = TEACHER_BASE_URL + "deleteStudent.php";
    public static final String urlGetStudentDatewiseAttendance = TEACHER_BASE_URL + "getStudentDatewiseAttendance.php";

    // --- Attendance Control ---
    public static final String urlAddAttendance = TEACHER_BASE_URL + "addAttendance.php";
    public static final String urlDeleteAttendance = TEACHER_BASE_URL + "deleteAttendance.php";
    public static final String urlGetLeaveRequests = TEACHER_BASE_URL + "get_leave_requests.php";
    public static final String urlUpdateLeaveStatus = TEACHER_BASE_URL + "update_leave_status.php";
    public static final String urlGetPendingAttendance = BASE_URL + "getPendingAttendance.php";
    public static final String urlAddPendingAttendance = BASE_URL + "addPendingAttendance.php";

    // --- Content & Notice Management ---
    public static final String addNotice = BASE_URL + "addNotice.php";
    public static final String NoticImg = BASE_URL + "NoticImg.php";
    public static final String urlAddStudyMaterial = TEACHER_BASE_URL + "addStudyMaterial.php";
    public static final String urlAddStudyMaterialDoc = TEACHER_BASE_URL + "addStudyMaterialDoc.php";

    // ==========================================
    // 🏛️ ADMIN (HOD) PORTAL ENDPOINTS
    // ==========================================
    public static final String ADMIN_BASE_URL = BASE_URL + "Admin/";
    public static final String urlGetAllTeacher = ADMIN_BASE_URL + "getAllTeacher.php";
    public static final String urlAddTeacher = ADMIN_BASE_URL + "addTeacher.php";
    public static final String urlDeleteTeacher = ADMIN_BASE_URL + "deleteTeacher.php";
    public static final String urlUpdateTeacherSubjects = ADMIN_BASE_URL + "updateTeacherSubjects.php";
    
    // Academic Curriculum Management
    public static final String urlAddSubject = ADMIN_BASE_URL + "addSubject.php";
    public static final String urlDeleteSubject = ADMIN_BASE_URL + "deleteSubject.php";
    public static final String urlGetSubjectsByFilter = ADMIN_BASE_URL + "getSubjectsByFilter.php";
    public static final String urlGetAllSubjectsMaster = ADMIN_BASE_URL + "getSubjectsByFilter.php"; // Reuse filter for all
    
    public static final String urlGetAllStudentByYear = ADMIN_BASE_URL + "getAllStudentByYear.php";
    public static final String urlUploadMarks = TEACHER_BASE_URL + "uploadMarks.php";

    // 🛡️ Principal HOD Management Endpoints
    public static final String urlGetHODList = ADMIN_BASE_URL + "getHODList.php";
    public static final String urlAddHOD = ADMIN_BASE_URL + "addHOD.php";
    public static final String urlDeleteHOD = ADMIN_BASE_URL + "deleteHOD.php";
    public static final String urlResetHODCredentials = ADMIN_BASE_URL + "resetHODCredentials.php";
    public static final String urlChangeAdminPassword = ADMIN_BASE_URL + "changeHODPassword.php";

    // Redundant aliases preserved for backward compatibility
    public static final String addNoticeByTeacher = addNotice;
    public static final String addNoticeImage = NoticImg;
    public static final String webServiceAddress = BASE_URL;
    public static final String OnlineDocAddress = ONLINE_DOC_DIR;
    public static final String imageFolderWebService = IMAGE_ASSET_DIR;
    public static final String webServiceTeacherAddress = TEACHER_BASE_URL;
}
