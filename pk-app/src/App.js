import { BrowserRouter, Route, Routes } from "react-router-dom";
import Header from "./components/layout/Header";
import Footer from "./components/layout/Footer";
import Home from "./components/Home";
import 'bootstrap/dist/css/bootstrap.min.css';
import { Container } from "react-bootstrap";
import Login from "./components/Login";
import Register from "./components/Register";

import cookie from 'react-cookies';
import PatientDashboard from "./components/PatientDashboard";



import { useReducer } from "react";
import { MyUserReducer } from "./components/reducers/MyUserReducer";
import { MyUserContext } from "./config/MyContexts";
import BookingPage from "./components/BookingPage";
import MedicalRecordsPage from "./components/MedicalRecordsPage";
import ProfileEditPage from "./components/ProfileEditPage";
import PatientBillsPage from "./components/PatientBillsPage";

import DoctorAppointments from "./components/DoctorAppointments";
import DoctorPatients from "./components/DoctorPatients";

import PatientNotifications from "./components/PatientNotifications";
import PrescriptionPage from "./components/PrescriptionPage";
import MedicalRecordForm from "./components/MedicalRecordForm";

const App = () => {
 // Khởi tạo state người dùng và giỏ hàng với useReducer
 // MyUserReducer và MyCartReducer sẽ quản lý logic cập nhật state
 const [user, dispatch] = useReducer(MyUserReducer, cookie.load("user") || null);
 

 return (
   // Sử dụng Provider để cung cấp giá trị cho toàn bộ ứng dụng
   <MyUserContext.Provider value={[user, dispatch]}>
     
       <BrowserRouter>
         <div className="App d-flex flex-column min-vh-100">
           <Header />

           <main className="flex-grow-1">
             <Container>
               <Routes>
                 <Route path="/" element={<Home />} />
                 <Route path="/login" element={<Login />} />
                 <Route path="/register" element={<Register />} />
                 <Route path="/booking" element={<BookingPage />} />
                 <Route path="/bills" element={<PatientBillsPage />} />
                  <Route path="/medical-records" element={<MedicalRecordsPage />} />
                  <Route path="/profile/edit" element={<ProfileEditPage />} />
                 <Route path="/profile" element={<PatientDashboard />} />
                 <Route path="/doctors/my-appointments" element={<DoctorAppointments />} />
                 <Route path="/patients" element={<DoctorPatients />} />
                <Route path="/notifications" element={<PatientNotifications />} />
                <Route path="/prescriptions" element={<PrescriptionPage />} />
                 <Route path="/doctor/create-medical-record" element={<MedicalRecordForm />} />
               </Routes>
             </Container>
           </main>
           
           <Footer />
         </div>
       </BrowserRouter>
     
   </MyUserContext.Provider>
 );
}

export default App;
