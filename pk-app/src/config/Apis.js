import axios from "axios";
import cookie from "react-cookies";

const BASE_URL = process.env.REACT_APP_BASE_URL || "http://localhost:8080/App/api";
const HOST = process.env.REACT_APP_HOST || "http://localhost:8080";

export const endpoints = {
    login: "/login",
    register: "/users",
    profile: "/profile",
    "update-profile": (userId) => `/users/${userId}`,
    "current-patient": "/patients/me",
    medicines: "/medicines",
    doctors: "/doctors",
    specialties: "/doctors/specialties",
    appointments: "/appointments",
    "my-appointments-patient": "/patients/my-appointments",
    "my-appointments-doctor": "/doctors/my-appointments",
    "confirm-appointment": (id) => `/doctors/appointments/${id}/confirm`,
    "doctor-patients": "/doctors/patients",
    "create-medical-record": "/medical-records",
    "medical-records-patient": (patientId) => `/patients/${patientId}/medical-records`,
    "medical-records-history": (patientId) => `/medical-records/by-patient/${patientId}`,
    "patient-medical-history": (patientId) => `/patients/${patientId}/medical-records`,
    "my-bills": "/patients/my-bills",
    "patient-bills": (patientId) => `/patients/${patientId}/bills`,
    "my-notifications": "/notifications/my-notifications",
    "create-prescription": "/prescriptions/create",
    "pay-appointment-deposit": (appointmentId) => `/payments/appointments/${appointmentId}/deposit`,
    "pay-bill": (billId) => `/payments/bills/${billId}/pay`,
    "vnpay-url": (billId) => `/payments/vnpay-url/${billId}`,
    "vnpay-url-by-appointment": (appointmentId) => `/payments/vnpay-url/by-appointment/${appointmentId}`,
};

export const getImageUrl = (path) => {
    const defaultAvt = "https://www.gravatar.com/avatar/00000000000000000000000000000000?d=mp&f=y";
    if (!path || path === "null" || path === "") return defaultAvt;
    if (typeof path === "string" && (path.startsWith("http://") || path.startsWith("https://"))) {
        return path;
    }
    return `${HOST}${path}`;
};

export const authApis = () => {
    return axios.create({
        baseURL: BASE_URL,
        headers: {
            Authorization: `Bearer ${cookie.load("token")}`,
        },
    });
};

export default axios.create({
    baseURL: BASE_URL,
});
